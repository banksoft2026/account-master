package com.banking.cbs.account.common.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CbsMaintenanceClient {

    private final RestTemplate restTemplate;

    @Value("${integration.cbs-maintenance.base-url}")
    private String baseUrl;

    /**
     * Generate next account number via CBS Maintenance numbering scheme.
     * Returns generatedNumber from the response, or empty if service is unavailable.
     */
    public Optional<String> generateAccountNumber() {
        try {
            String url = baseUrl + "/v1/config/numbering/ACCOUNT/next";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.containsKey("generatedNumber")) {
                    return Optional.of((String) data.get("generatedNumber"));
                }
            }
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("CBS Maintenance service unavailable for account number generation: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if a branch is active in CBS Maintenance. Graceful fallback: returns true if service unavailable.
     */
    public boolean isBranchActive(String branchCode) {
        try {
            String url = baseUrl + "/v1/config/branches?institutionId=DEFAULT&status=ACTIVE";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                java.util.List<Map<String, Object>> branches =
                        (java.util.List<Map<String, Object>>) response.get("data");
                if (branches != null) {
                    return branches.stream()
                            .anyMatch(b -> branchCode.equals(b.get("branchCode")));
                }
            }
            return true; // graceful fallback
        } catch (Exception ex) {
            log.warn("CBS Maintenance service unavailable for branch check ({}): {}", branchCode, ex.getMessage());
            return true; // graceful fallback — do not block account opening
        }
    }

    /**
     * Check if a currency is active in CBS Maintenance. Graceful fallback: returns true if service unavailable.
     */
    public boolean isCurrencyActive(String currencyCode) {
        try {
            String url = baseUrl + "/v1/config/currencies/" + currencyCode;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    return "ACTIVE".equals(data.get("status"));
                }
            }
            return true; // graceful fallback
        } catch (Exception ex) {
            log.warn("CBS Maintenance service unavailable for currency check ({}): {}", currencyCode, ex.getMessage());
            return true; // graceful fallback — do not block account opening
        }
    }
}
