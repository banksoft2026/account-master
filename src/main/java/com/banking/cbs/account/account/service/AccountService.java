package com.banking.cbs.account.account.service;

import com.banking.cbs.account.account.dto.*;
import com.banking.cbs.account.account.entity.*;
import com.banking.cbs.account.account.repository.*;
import com.banking.cbs.account.common.client.CbsMaintenanceClient;
import com.banking.cbs.account.common.client.CustomerEntityClient;
import com.banking.cbs.account.common.client.CustomerEntityClient.*;
import com.banking.cbs.account.common.exception.CbsException;
import com.banking.cbs.account.product.entity.AccountProduct;
import com.banking.cbs.account.product.entity.ProductParameter;
import com.banking.cbs.account.product.repository.AccountProductRepository;
import com.banking.cbs.account.product.repository.ProductParameterRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private static final Map<String, Set<String>> STATUS_TRANSITIONS = new HashMap<>();

    static {
        STATUS_TRANSITIONS.put("PENDING_ACTIVATION", Set.of("ACTIVE"));
        STATUS_TRANSITIONS.put("ACTIVE",    Set.of("DORMANT", "FROZEN", "SUSPENDED", "CLOSED"));
        STATUS_TRANSITIONS.put("DORMANT",   Set.of("ACTIVE", "CLOSED"));
        STATUS_TRANSITIONS.put("FROZEN",    Set.of("ACTIVE", "SUSPENDED", "CLOSED"));
        STATUS_TRANSITIONS.put("SUSPENDED", Set.of("ACTIVE", "CLOSED"));
        STATUS_TRANSITIONS.put("CLOSED",    Set.of());
        STATUS_TRANSITIONS.put("UNCLAIMED", Set.of());
    }

    private final AccountMasterRepository accountRepo;
    private final AccountParametersRepository parametersRepo;
    private final AccountParameterOverrideRepository overrideRepo;
    private final AccountBalanceRepository balanceRepo;
    private final AccountEarmarkRepository earmarkRepo;
    private final AccountLedgerRepository ledgerRepo;
    private final AccountProductRepository productRepo;
    private final ProductParameterRepository productParamRepo;
    private final CbsMaintenanceClient cbsClient;
    private final CustomerEntityClient entityClient;

    // ── Eligibility Validation ────────────────────────────────────────────────

    @Data
    static class EligibilityCheck {
        private final String checkName;
        private final boolean passed;
        private final String currentValue;
        private final String requiredValue;
        private final String details;
    }

    private void validateEntityEligibility(String entityId) {
        List<EligibilityCheck> failed = new ArrayList<>();

        EntitySummary entity = entityClient.getEntity(entityId);

        if (!"ACTIVE".equals(entity.getEntityStatus())) {
            failed.add(new EligibilityCheck("ENTITY_STATUS", false,
                    entity.getEntityStatus(), "ACTIVE", "Entity must be in ACTIVE status"));
        }

        if (!"VERIFIED".equals(entity.getKybStatus())) {
            failed.add(new EligibilityCheck("KYB_STATUS", false,
                    entity.getKybStatus(), "VERIFIED", "KYB must be VERIFIED"));
        }

        EntityComplianceDto compliance = entityClient.getCompliance(entityId);

        if (Boolean.TRUE.equals(compliance.getSanctionsHit())) {
            failed.add(new EligibilityCheck("SANCTIONS", false,
                    "SANCTIONS_HIT", "NO_SANCTIONS", "Entity has a sanctions hit"));
        }

        List<LinkDto> links = entityClient.getEntityLinks(entityId);
        boolean hasAuthSignatory = links.stream()
                .anyMatch(l -> Boolean.TRUE.equals(l.getIsAuthorisedSignatory())
                        && Boolean.TRUE.equals(l.getIsActive()));
        if (!hasAuthSignatory) {
            failed.add(new EligibilityCheck("AUTHORISED_SIGNATORY", false,
                    "NONE", "REQUIRED", "At least one active authorised signatory is required"));
        }

        List<EntityDocumentDto> docs = entityClient.getEntityDocuments(entityId);
        List<String> mandatoryDocs = List.of(
                "CERTIFICATE_OF_INCORPORATION",
                "MEMORANDUM_OF_ASSOCIATION",
                "ARTICLES_OF_ASSOCIATION");
        for (String docType : mandatoryDocs) {
            boolean verified = docs.stream()
                    .anyMatch(d -> docType.equals(d.getDocType()) && "VERIFIED".equals(d.getDocStatus()));
            if (!verified) {
                failed.add(new EligibilityCheck("MANDATORY_DOCS", false,
                        "MISSING_OR_UNVERIFIED", "VERIFIED",
                        "Mandatory document not verified: " + docType));
            }
        }

        if (compliance.getNextReviewDate() != null) {
            try {
                LocalDate reviewDate = LocalDate.parse(compliance.getNextReviewDate().substring(0, 10));
                if (!reviewDate.isAfter(LocalDate.now())) {
                    failed.add(new EligibilityCheck("REVIEW_OVERDUE", false,
                            compliance.getNextReviewDate(), "FUTURE_DATE",
                            "Compliance review is overdue"));
                }
            } catch (Exception ex) {
                log.warn("Could not parse nextReviewDate: {}", compliance.getNextReviewDate());
            }
        }

        if (!failed.isEmpty()) {
            String details = failed.stream()
                    .map(c -> c.getCheckName() + ": " + c.getDetails())
                    .collect(Collectors.joining("; "));
            throw CbsException.unprocessable("ENTITY_ELIGIBILITY_FAILED",
                    "Entity eligibility checks failed: " + details);
        }
    }

    // ── Account Opening ───────────────────────────────────────────────────────

    @Transactional
    public AccountResponse openAccount(AccountOpenRequest req) {
        AccountProduct product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + req.getProductId()));

        if ("RETIRED".equals(product.getLifecycleStatus())) {
            throw CbsException.unprocessable("PRODUCT_RETIRED",
                    "Product is RETIRED and cannot accept new accounts");
        }
        if (!"ACTIVE".equals(product.getLifecycleStatus())) {
            throw CbsException.unprocessable("PRODUCT_NOT_ACTIVE",
                    "Product must be ACTIVE to open accounts. Current status: " + product.getLifecycleStatus());
        }

        if (!product.isAllowCorporate()) {
            throw CbsException.unprocessable("PRODUCT_NOT_FOR_CORPORATE",
                    "This product does not allow corporate accounts");
        }

        validateEntityEligibility(req.getMaster().getEntityId());

        boolean branchActive = cbsClient.isBranchActive(req.getMaster().getBranchCode());
        if (!branchActive) {
            log.warn("Branch {} is not active in CBS Maintenance (non-blocking warning)", req.getMaster().getBranchCode());
        }

        // Generate account number
        String accountNumber = cbsClient.generateAccountNumber()
                .orElseGet(() -> "ACC-" + System.currentTimeMillis());

        // Validate and process parameter overrides
        List<ProductParameter> productParams = productParamRepo.findByProductId(product.getProductId());
        Map<String, ProductParameter> paramMap = productParams.stream()
                .collect(Collectors.toMap(ProductParameter::getParamKey, p -> p));

        List<String> overrideErrors = new ArrayList<>();
        for (ParameterOverrideRequest overReq : req.getParameterOverrides()) {
            ProductParameter pp = paramMap.get(overReq.getParamKey());
            if (pp == null) {
                overrideErrors.add("Unknown param key: " + overReq.getParamKey());
                continue;
            }
            if (!pp.isOverridableAtAccount()) {
                overrideErrors.add("Parameter not overridable at account level: " + overReq.getParamKey());
                continue;
            }
            String validationError = validateParamValue(pp, overReq.getValue());
            if (validationError != null) {
                overrideErrors.add(overReq.getParamKey() + ": " + validationError);
            }
        }
        if (!overrideErrors.isEmpty()) {
            throw CbsException.unprocessable("PARAMETER_OVERRIDE_VALIDATION_FAILED",
                    "Parameter override errors: " + String.join("; ", overrideErrors));
        }

        // Build and save AccountMaster
        AccountMaster account = AccountMaster.builder()
                .accountNumber(accountNumber)
                .accountType(product.getAccountType())
                .productId(product.getProductId())
                .productCode(product.getProductCode())
                .currencyCode(product.getCurrencyCode())
                .accountName(req.getMaster().getAccountName())
                .shortName(req.getMaster().getShortName())
                .customerId(req.getMaster().getCustomerId())
                .entityId(req.getMaster().getEntityId())
                .branchCode(req.getMaster().getBranchCode())
                .purposeCode(req.getMaster().getPurposeCode())
                .riskCategory(req.getMaster().getRiskCategory() != null ? req.getMaster().getRiskCategory() : "LOW")
                .ownershipType(req.getMaster().getOwnershipType() != null ? req.getMaster().getOwnershipType() : "SOLE")
                .relationshipManagerId(req.getMaster().getRelationshipManagerId())
                .accountStatus("ACTIVE")
                .openingDate(LocalDate.now())
                .createdBy(req.getMaster().getCreatedBy())
                .build();
        account = accountRepo.save(account);
        final String accountId = account.getAccountId();

        // Build and save AccountParameters (product defaults merged with overrides)
        Map<String, String> overrideMap = new HashMap<>();
        for (ParameterOverrideRequest ov : req.getParameterOverrides()) {
            overrideMap.put(ov.getParamKey(), ov.getValue());
        }

        String overdraftLimitStr = resolveParamValue(paramMap, overrideMap, "overdraft_limit", "0");
        BigDecimal overdraftLimit = parseSafeBigDecimal(overdraftLimitStr, BigDecimal.ZERO);

        String dailyDebitStr = resolveParamValue(paramMap, overrideMap, "daily_debit_limit", "1000000");
        String dailyCreditStr = resolveParamValue(paramMap, overrideMap, "daily_credit_limit", "5000000");
        String singleTxnStr = resolveParamValue(paramMap, overrideMap, "single_txn_limit", "500000");
        String creditRateStr = resolveParamValue(paramMap, overrideMap, "credit_interest_rate", "0.0");

        AccountParameters params = AccountParameters.builder()
                .accountId(accountId)
                .productId(product.getProductId())
                .creditInterestRate(parseSafeBigDecimal(creditRateStr, BigDecimal.ZERO))
                .debitInterestRate(BigDecimal.ZERO)
                .penaltyRate(BigDecimal.ZERO)
                .overdraftLimit(overdraftLimit)
                .dailyDebitLimit(parseSafeBigDecimal(dailyDebitStr, new BigDecimal("1000000")))
                .dailyCreditLimit(parseSafeBigDecimal(dailyCreditStr, new BigDecimal("5000000")))
                .singleTxnLimit(parseSafeBigDecimal(singleTxnStr, new BigDecimal("500000")))
                .overdraftAllowed(overdraftLimit.compareTo(BigDecimal.ZERO) > 0)
                .effectiveFrom(Instant.now())
                .build();
        parametersRepo.save(params);

        // Save parameter overrides
        for (ParameterOverrideRequest ov : req.getParameterOverrides()) {
            ProductParameter pp = paramMap.get(ov.getParamKey());
            AccountParameterOverride override = AccountParameterOverride.builder()
                    .accountId(accountId)
                    .paramKey(ov.getParamKey())
                    .originalProductValue(pp != null ? pp.getDefaultValue() : "")
                    .overriddenValue(ov.getValue())
                    .overrideReason(ov.getOverrideReason())
                    .approvedBy(ov.getApprovedBy())
                    .approvalRef(ov.getApprovalRef())
                    .isActive(true)
                    .effectiveFrom(Instant.now())
                    .build();
            overrideRepo.save(override);
        }

        // Initialize AccountBalance
        AccountBalance balance = AccountBalance.builder()
                .accountId(accountId)
                .ledgerBalance(BigDecimal.ZERO)
                .availableBalance(overdraftLimit) // available = overdraft limit (credit line) when balance=0
                .earmarkedAmount(BigDecimal.ZERO)
                .uncollectedAmount(BigDecimal.ZERO)
                .overdraftUtilised(BigDecimal.ZERO)
                .overdraftLimit(overdraftLimit)
                .currencyCode(product.getCurrencyCode())
                .balanceDate(LocalDate.now())
                .valueDate(Instant.now())
                .createdAt(Instant.now())
                .build();
        balanceRepo.save(balance);

        // Sync balance cache on AccountMaster
        account.setOverdraftLimitCached(overdraftLimit);
        account.setAvailableBalance(overdraftLimit);
        account = accountRepo.save(account);

        return buildFullResponse(account);
    }

    // ── Account Read ──────────────────────────────────────────────────────────

    public AccountResponse getAccount(String accountId) {
        AccountMaster account = loadAccount(accountId);
        return buildFullResponse(account);
    }

    public AccountSummaryResponse getAccountSummary(String accountId) {
        return AccountSummaryResponse.from(loadAccount(accountId));
    }

    // ── Account Update ────────────────────────────────────────────────────────

    @Transactional
    public AccountResponse updateAccountMaster(String accountId, AccountMasterUpdateRequest req) {
        AccountMaster account = loadAccount(accountId);
        if (req.getAccountName() != null) account.setAccountName(req.getAccountName());
        if (req.getShortName() != null) account.setShortName(req.getShortName());
        if (req.getPurposeCode() != null) account.setPurposeCode(req.getPurposeCode());
        if (req.getRiskCategory() != null) account.setRiskCategory(req.getRiskCategory());
        if (req.getRelationshipManagerId() != null) account.setRelationshipManagerId(req.getRelationshipManagerId());
        if (req.getUpdatedBy() != null) account.setUpdatedBy(req.getUpdatedBy());
        account.setUpdatedAt(Instant.now());
        account = accountRepo.save(account);
        return buildFullResponse(account);
    }

    @Transactional
    public AccountResponse updateStatus(String accountId, String newStatus, String reason) {
        AccountMaster account = loadAccount(accountId);
        String currentStatus = account.getAccountStatus();

        if ("CLOSED".equals(currentStatus) || "UNCLAIMED".equals(currentStatus)) {
            throw CbsException.unprocessable("TERMINAL_STATUS",
                    "Account is in terminal " + currentStatus + " status");
        }

        Set<String> allowed = STATUS_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw CbsException.unprocessable("INVALID_STATUS_TRANSITION",
                    "Cannot transition from " + currentStatus + " to " + newStatus);
        }

        account.setAccountStatus(newStatus);
        if ("CLOSED".equals(newStatus)) {
            account.setClosingDate(LocalDate.now());
            account.setCloseReason(reason);
        }
        if ("FROZEN".equals(newStatus)) {
            account.setFreezeReason(reason);
        }
        if ("ACTIVE".equals(newStatus) && "FROZEN".equals(currentStatus)) {
            account.setFreezeReason(null);
        }
        account.setUpdatedAt(Instant.now());
        account = accountRepo.save(account);
        return buildFullResponse(account);
    }

    public List<AccountResponse> listAccounts(String productId, String customerId,
                                               String entityId, String status, String branchCode) {
        List<AccountMaster> accounts = accountRepo.findAll();
        return accounts.stream()
                .filter(a -> productId == null || a.getProductId().equals(productId))
                .filter(a -> customerId == null || a.getCustomerId().equals(customerId))
                .filter(a -> entityId == null || a.getEntityId().equals(entityId))
                .filter(a -> status == null || a.getAccountStatus().equals(status))
                .filter(a -> branchCode == null || a.getBranchCode().equals(branchCode))
                .map(this::buildFullResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse softClose(String accountId, String reason) {
        return updateStatus(accountId, "CLOSED", reason);
    }

    // ── Parameters ────────────────────────────────────────────────────────────

    public AccountParametersResponse getParameters(String accountId) {
        loadAccount(accountId);
        AccountParameters params = parametersRepo.findByAccountId(accountId)
                .orElseThrow(() -> CbsException.notFound("PARAMS_NOT_FOUND",
                        "Account parameters not found for: " + accountId));
        AccountParametersResponse resp = AccountParametersResponse.from(params);

        // Build source map
        List<AccountParameterOverride> activeOverrides = overrideRepo.findByAccountIdAndIsActiveTrue(accountId);
        Map<String, AccountParameterOverride> overrideMap = activeOverrides.stream()
                .collect(Collectors.toMap(AccountParameterOverride::getParamKey, o -> o));

        // Add entries to sourceMap for known parameters
        addToSourceMap(resp.getSourceMap(), "overdraft_limit",
                params.getOverdraftLimit().toPlainString(), overrideMap);
        addToSourceMap(resp.getSourceMap(), "daily_debit_limit",
                params.getDailyDebitLimit().toPlainString(), overrideMap);
        addToSourceMap(resp.getSourceMap(), "daily_credit_limit",
                params.getDailyCreditLimit().toPlainString(), overrideMap);
        addToSourceMap(resp.getSourceMap(), "single_txn_limit",
                params.getSingleTxnLimit().toPlainString(), overrideMap);
        addToSourceMap(resp.getSourceMap(), "credit_interest_rate",
                params.getCreditInterestRate().toPlainString(), overrideMap);

        return resp;
    }

    private void addToSourceMap(Map<String, Map<String, Object>> sourceMap, String paramKey,
                                  String value, Map<String, AccountParameterOverride> overrideMap) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("value", value);
        if (overrideMap.containsKey(paramKey)) {
            AccountParameterOverride ov = overrideMap.get(paramKey);
            entry.put("source", "ACCOUNT_OVERRIDE");
            entry.put("overrideId", ov.getOverrideId());
            entry.put("approvedBy", ov.getApprovedBy());
        } else {
            entry.put("source", "PRODUCT_DEFAULT");
        }
        sourceMap.put(paramKey, entry);
    }

    // ── Overrides ─────────────────────────────────────────────────────────────

    public List<OverrideResponse> listOverrides(String accountId) {
        loadAccount(accountId);
        return overrideRepo.findByAccountIdAndIsActiveTrue(accountId).stream()
                .map(OverrideResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public OverrideResponse applyOverride(String accountId, ParameterOverrideRequest req) {
        AccountMaster account = loadAccount(accountId);

        List<ProductParameter> productParams = productParamRepo.findByProductId(account.getProductId());
        Map<String, ProductParameter> paramMap = productParams.stream()
                .collect(Collectors.toMap(ProductParameter::getParamKey, p -> p));

        ProductParameter pp = paramMap.get(req.getParamKey());
        if (pp == null) {
            throw CbsException.unprocessable("UNKNOWN_PARAM_KEY",
                    "Parameter key not found on product: " + req.getParamKey());
        }
        if (!pp.isOverridableAtAccount()) {
            throw CbsException.unprocessable("PARAM_NOT_OVERRIDABLE",
                    "Parameter is not overridable at account level: " + req.getParamKey());
        }
        String error = validateParamValue(pp, req.getValue());
        if (error != null) {
            throw CbsException.unprocessable("PARAM_VALUE_INVALID", error);
        }

        // Expire existing active override
        overrideRepo.findByAccountIdAndParamKeyAndIsActiveTrue(accountId, req.getParamKey())
                .ifPresent(existing -> {
                    existing.setActive(false);
                    existing.setEffectiveTo(Instant.now());
                    overrideRepo.save(existing);
                });

        // Save new override
        AccountParameterOverride override = AccountParameterOverride.builder()
                .accountId(accountId)
                .paramKey(req.getParamKey())
                .originalProductValue(pp.getDefaultValue())
                .overriddenValue(req.getValue())
                .overrideReason(req.getOverrideReason())
                .approvedBy(req.getApprovedBy())
                .approvalRef(req.getApprovalRef())
                .isActive(true)
                .effectiveFrom(Instant.now())
                .build();
        override = overrideRepo.save(override);

        // Re-resolve AccountParameters
        reResolveParameters(accountId, account.getProductId());

        // If overdraft_limit changed, recalculate balance
        if ("overdraft_limit".equals(req.getParamKey())) {
            recalculateBalanceForOverdraftChange(accountId, parseSafeBigDecimal(req.getValue(), BigDecimal.ZERO));
        }

        return OverrideResponse.from(override);
    }

    @Transactional
    public void revertOverride(String accountId, String paramKey) {
        AccountMaster account = loadAccount(accountId);

        AccountParameterOverride override = overrideRepo
                .findByAccountIdAndParamKeyAndIsActiveTrue(accountId, paramKey)
                .orElseThrow(() -> CbsException.notFound("OVERRIDE_NOT_FOUND",
                        "No active override found for param key: " + paramKey));

        override.setActive(false);
        override.setEffectiveTo(Instant.now());
        overrideRepo.save(override);

        reResolveParameters(accountId, account.getProductId());

        if ("overdraft_limit".equals(paramKey)) {
            List<ProductParameter> productParams = productParamRepo.findByProductId(account.getProductId());
            String defaultOverdraftStr = productParams.stream()
                    .filter(p -> "overdraft_limit".equals(p.getParamKey()))
                    .map(ProductParameter::getDefaultValue)
                    .findFirst().orElse("0");
            recalculateBalanceForOverdraftChange(accountId,
                    parseSafeBigDecimal(defaultOverdraftStr, BigDecimal.ZERO));
        }
    }

    // ── Balance ───────────────────────────────────────────────────────────────

    public AccountBalanceResponse getBalance(String accountId) {
        loadAccount(accountId);
        AccountBalance balance = balanceRepo.findByAccountId(accountId)
                .orElseThrow(() -> CbsException.notFound("BALANCE_NOT_FOUND",
                        "Balance not found for account: " + accountId));
        return AccountBalanceResponse.from(balance);
    }

    public AccountSummaryResponse getBalanceSummary(String accountId) {
        return AccountSummaryResponse.from(loadAccount(accountId));
    }

    // ── Earmarks ──────────────────────────────────────────────────────────────

    @Transactional
    public EarmarkResponse placeEarmark(String accountId, EarmarkRequest req) {
        loadAccount(accountId);
        AccountBalance balance = balanceRepo.findByAccountId(accountId)
                .orElseThrow(() -> CbsException.notFound("BALANCE_NOT_FOUND",
                        "Balance not found for account: " + accountId));

        if (balance.getAvailableBalance().compareTo(req.getEarmarkAmount()) < 0) {
            throw CbsException.unprocessable("INSUFFICIENT_AVAILABLE_BALANCE",
                    "Insufficient available balance. Available: " + balance.getAvailableBalance()
                            + ", Requested: " + req.getEarmarkAmount());
        }

        AccountEarmark earmark = AccountEarmark.builder()
                .accountId(accountId)
                .earmarkAmount(req.getEarmarkAmount())
                .earmarkType(req.getEarmarkType())
                .referenceId(req.getReferenceId())
                .description(req.getDescription())
                .isActive(true)
                .createdBy(req.getCreatedBy())
                .build();
        earmark = earmarkRepo.save(earmark);

        // Update balance
        balance.setEarmarkedAmount(balance.getEarmarkedAmount().add(req.getEarmarkAmount()));
        balance.setAvailableBalance(balance.getAvailableBalance().subtract(req.getEarmarkAmount()));
        balance.setUpdatedAt(Instant.now());
        balanceRepo.save(balance);

        // Sync cache
        syncBalanceCache(accountId, balance);

        return EarmarkResponse.from(earmark);
    }

    @Transactional
    public void releaseEarmark(String accountId, String earmarkId) {
        AccountEarmark earmark = earmarkRepo.findById(earmarkId)
                .orElseThrow(() -> CbsException.notFound("EARMARK_NOT_FOUND",
                        "Earmark not found: " + earmarkId));
        if (!earmark.getAccountId().equals(accountId)) {
            throw CbsException.badRequest("EARMARK_ACCOUNT_MISMATCH",
                    "Earmark does not belong to account: " + accountId);
        }
        if (!earmark.isActive()) {
            throw CbsException.unprocessable("EARMARK_ALREADY_RELEASED",
                    "Earmark is already released");
        }

        earmark.setActive(false);
        earmark.setEffectiveTo(Instant.now());
        earmarkRepo.save(earmark);

        AccountBalance balance = balanceRepo.findByAccountId(accountId)
                .orElseThrow(() -> CbsException.notFound("BALANCE_NOT_FOUND",
                        "Balance not found for account: " + accountId));
        balance.setEarmarkedAmount(balance.getEarmarkedAmount().subtract(earmark.getEarmarkAmount()));
        balance.setAvailableBalance(balance.getAvailableBalance().add(earmark.getEarmarkAmount()));
        balance.setUpdatedAt(Instant.now());
        balanceRepo.save(balance);

        syncBalanceCache(accountId, balance);
    }

    // ── Ledger ────────────────────────────────────────────────────────────────

    public Page<AccountLedger> getLedger(String accountId, LocalDate from, LocalDate to, Pageable pageable) {
        loadAccount(accountId);
        if (from != null && to != null) {
            return ledgerRepo.findByAccountIdAndValueDateBetweenOrderByValueDateDescPostingDateDesc(
                    accountId, from, to, pageable);
        }
        return ledgerRepo.findByAccountIdOrderByValueDateDescPostingDateDesc(accountId, pageable);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private AccountMaster loadAccount(String accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> CbsException.notFound("ACCOUNT_NOT_FOUND",
                        "Account not found: " + accountId));
    }

    private AccountResponse buildFullResponse(AccountMaster account) {
        AccountResponse resp = AccountResponse.from(account);

        parametersRepo.findByAccountId(account.getAccountId())
                .ifPresent(params -> resp.setParameters(AccountParametersResponse.from(params)));

        List<OverrideResponse> overrides = overrideRepo
                .findByAccountIdAndIsActiveTrue(account.getAccountId()).stream()
                .map(OverrideResponse::from)
                .collect(Collectors.toList());
        resp.setOverrides(overrides);

        balanceRepo.findByAccountId(account.getAccountId())
                .ifPresent(bal -> resp.setBalance(AccountBalanceResponse.from(bal)));

        return resp;
    }

    private void syncBalanceCache(String accountId, AccountBalance balance) {
        accountRepo.findById(accountId).ifPresent(account -> {
            account.setLedgerBalance(balance.getLedgerBalance());
            account.setAvailableBalance(balance.getAvailableBalance());
            account.setEarmarkedAmount(balance.getEarmarkedAmount());
            account.setUncollectedAmount(balance.getUncollectedAmount());
            account.setOverdraftUtilised(balance.getOverdraftUtilised());
            account.setOverdraftLimitCached(balance.getOverdraftLimit());
            account.setUpdatedAt(Instant.now());
            accountRepo.save(account);
        });
    }

    private void reResolveParameters(String accountId, String productId) {
        List<ProductParameter> productParams = productParamRepo.findByProductId(productId);
        Map<String, String> productDefaults = productParams.stream()
                .collect(Collectors.toMap(ProductParameter::getParamKey, ProductParameter::getDefaultValue));

        List<AccountParameterOverride> activeOverrides = overrideRepo.findByAccountIdAndIsActiveTrue(accountId);
        Map<String, String> overrideMap = activeOverrides.stream()
                .collect(Collectors.toMap(AccountParameterOverride::getParamKey,
                        AccountParameterOverride::getOverriddenValue));

        parametersRepo.findByAccountId(accountId).ifPresent(params -> {
            String overdraftStr = overrideMap.getOrDefault("overdraft_limit",
                    productDefaults.getOrDefault("overdraft_limit", "0"));
            params.setOverdraftLimit(parseSafeBigDecimal(overdraftStr, BigDecimal.ZERO));
            params.setOverdraftAllowed(params.getOverdraftLimit().compareTo(BigDecimal.ZERO) > 0);

            String debitStr = overrideMap.getOrDefault("daily_debit_limit",
                    productDefaults.getOrDefault("daily_debit_limit", "1000000"));
            params.setDailyDebitLimit(parseSafeBigDecimal(debitStr, new BigDecimal("1000000")));

            String creditRateStr = overrideMap.getOrDefault("credit_interest_rate",
                    productDefaults.getOrDefault("credit_interest_rate", "0.0"));
            params.setCreditInterestRate(parseSafeBigDecimal(creditRateStr, BigDecimal.ZERO));

            params.setUpdatedAt(Instant.now());
            parametersRepo.save(params);
        });
    }

    private void recalculateBalanceForOverdraftChange(String accountId, BigDecimal newOverdraftLimit) {
        balanceRepo.findByAccountId(accountId).ifPresent(balance -> {
            BigDecimal oldLimit = balance.getOverdraftLimit();
            BigDecimal delta = newOverdraftLimit.subtract(oldLimit);
            balance.setOverdraftLimit(newOverdraftLimit);
            balance.setAvailableBalance(balance.getAvailableBalance().add(delta));
            balance.setUpdatedAt(Instant.now());
            balanceRepo.save(balance);
            syncBalanceCache(accountId, balance);
        });

        accountRepo.findById(accountId).ifPresent(account -> {
            account.setOverdraftLimitCached(newOverdraftLimit);
            accountRepo.save(account);
        });
    }

    private String resolveParamValue(Map<String, ProductParameter> paramMap,
                                      Map<String, String> overrideMap,
                                      String paramKey, String fallback) {
        if (overrideMap.containsKey(paramKey)) return overrideMap.get(paramKey);
        if (paramMap.containsKey(paramKey)) return paramMap.get(paramKey).getDefaultValue();
        return fallback;
    }

    private BigDecimal parseSafeBigDecimal(String val, BigDecimal fallback) {
        if (val == null) return fallback;
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String validateParamValue(ProductParameter pp, String value) {
        String type = pp.getValueType();
        if (type == null) return null;

        if (type.equals("DECIMAL") || type.equals("RATE")) {
            try {
                BigDecimal val = new BigDecimal(value);
                if (type.equals("RATE") && (val.compareTo(BigDecimal.ZERO) < 0
                        || val.compareTo(new BigDecimal("100")) > 0)) {
                    return "RATE value must be between 0 and 100";
                }
                if (pp.getMinValue() != null) {
                    BigDecimal min = new BigDecimal(pp.getMinValue());
                    if (val.compareTo(min) < 0) return "Value " + val + " is below minimum " + min;
                }
                if (pp.getMaxValue() != null) {
                    BigDecimal max = new BigDecimal(pp.getMaxValue());
                    if (val.compareTo(max) > 0) return "Value " + val + " exceeds maximum " + max;
                }
            } catch (NumberFormatException e) {
                return "Value must be a valid decimal number";
            }
        } else if (type.equals("INTEGER")) {
            try {
                long val = Long.parseLong(value);
                if (pp.getMinValue() != null) {
                    long min = Long.parseLong(pp.getMinValue());
                    if (val < min) return "Value " + val + " is below minimum " + min;
                }
                if (pp.getMaxValue() != null) {
                    long max = Long.parseLong(pp.getMaxValue());
                    if (val > max) return "Value " + val + " exceeds maximum " + max;
                }
            } catch (NumberFormatException e) {
                return "Value must be a valid integer";
            }
        } else if (type.equals("BOOLEAN")) {
            if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                return "Value must be 'true' or 'false'";
            }
        } else if (type.startsWith("ENUM:")) {
            String[] allowed = type.substring(5).split(",");
            boolean found = Arrays.asList(allowed).contains(value);
            if (!found) {
                return "Value must be one of: " + type.substring(5);
            }
        }
        return null;
    }
}
