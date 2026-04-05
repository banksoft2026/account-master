package com.banking.cbs.account.common.client;

import com.banking.cbs.account.common.exception.CbsException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEntityClient {

    private final RestTemplate restTemplate;

    @Value("${integration.customer-entity.base-url}")
    private String baseUrl;

    // ── Inner DTOs ────────────────────────────────────────────────────────────

    @Data
    public static class EntitySummary {
        private String entityId;
        private String entityStatus;
        private String kybStatus;
    }

    @Data
    public static class EntityComplianceDto {
        private Boolean sanctionsHit;
        private String nextReviewDate;
    }

    @Data
    public static class LinkDto {
        private String customerId;
        private Boolean isAuthorisedSignatory;
        private Boolean isActive;
    }

    @Data
    public static class EntityDocumentDto {
        private String docType;
        private String docStatus;
    }

    // ── Client Methods ────────────────────────────────────────────────────────

    public EntitySummary getEntity(String entityId) {
        try {
            String url = baseUrl + "/v1/entities/" + entityId + "/summary";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            EntitySummary summary = new EntitySummary();
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    summary.setEntityId((String) data.get("entityId"));
                    summary.setEntityStatus((String) data.get("entityStatus"));
                    summary.setKybStatus((String) data.get("kybStatus"));
                }
            }
            return summary;
        } catch (ResourceAccessException ex) {
            log.error("Customer/Entity service is not reachable: {}", ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        } catch (Exception ex) {
            log.error("Error calling customer-entity service for entity {}: {}", entityId, ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        }
    }

    public EntityComplianceDto getCompliance(String entityId) {
        try {
            String url = baseUrl + "/v1/entities/" + entityId + "/compliance";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            EntityComplianceDto dto = new EntityComplianceDto();
            if (response != null && response.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    Object sanctionsHit = data.get("sanctionsHit");
                    dto.setSanctionsHit(sanctionsHit != null ? (Boolean) sanctionsHit : false);
                    Object nextReview = data.get("nextReviewDate");
                    dto.setNextReviewDate(nextReview != null ? nextReview.toString() : null);
                }
            }
            return dto;
        } catch (ResourceAccessException ex) {
            log.error("Customer/Entity service is not reachable: {}", ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        } catch (Exception ex) {
            log.error("Error calling customer-entity service for compliance {}: {}", entityId, ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        }
    }

    @SuppressWarnings("unchecked")
    public List<LinkDto> getEntityLinks(String entityId) {
        try {
            String url = baseUrl + "/v1/entities/" + entityId + "/customers";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<LinkDto> links = new java.util.ArrayList<>();
            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
                if (dataList != null) {
                    for (Map<String, Object> item : dataList) {
                        LinkDto link = new LinkDto();
                        link.setCustomerId((String) item.get("customerId"));
                        Object isAuth = item.get("isAuthorisedSignatory");
                        link.setIsAuthorisedSignatory(isAuth != null ? (Boolean) isAuth : false);
                        Object isActive = item.get("isActive");
                        link.setIsActive(isActive != null ? (Boolean) isActive : false);
                        links.add(link);
                    }
                }
            }
            return links;
        } catch (ResourceAccessException ex) {
            log.error("Customer/Entity service is not reachable: {}", ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        } catch (Exception ex) {
            log.error("Error calling customer-entity service for links {}: {}", entityId, ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        }
    }

    @SuppressWarnings("unchecked")
    public List<EntityDocumentDto> getEntityDocuments(String entityId) {
        try {
            String url = baseUrl + "/v1/entities/" + entityId + "/documents";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<EntityDocumentDto> docs = new java.util.ArrayList<>();
            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
                if (dataList != null) {
                    for (Map<String, Object> item : dataList) {
                        EntityDocumentDto doc = new EntityDocumentDto();
                        doc.setDocType((String) item.get("docType"));
                        doc.setDocStatus((String) item.get("docStatus"));
                        docs.add(doc);
                    }
                }
            }
            return docs;
        } catch (ResourceAccessException ex) {
            log.error("Customer/Entity service is not reachable: {}", ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        } catch (Exception ex) {
            log.error("Error calling customer-entity service for documents {}: {}", entityId, ex.getMessage());
            throw CbsException.serviceUnavailable("ENTITY_SERVICE_UNAVAILABLE",
                    "Customer/Entity service is not reachable");
        }
    }
}
