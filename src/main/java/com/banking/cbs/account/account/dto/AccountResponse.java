package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountMaster;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class AccountResponse {

    private String accountId;
    private String accountNumber;
    private String accountType;
    private String productId;
    private String productCode;
    private String currencyCode;
    private String accountName;
    private String shortName;
    private String customerId;
    private String entityId;
    private String branchCode;
    private String purposeCode;
    private String riskCategory;
    private String ownershipType;
    private String relationshipManagerId;
    private String accountStatus;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String closeReason;
    private BigDecimal ledgerBalance;
    private BigDecimal availableBalance;
    private BigDecimal earmarkedAmount;
    private BigDecimal overdraftLimitCached;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private Integer version;

    private AccountParametersResponse parameters;
    private List<OverrideResponse> overrides;
    private AccountBalanceResponse balance;

    public static AccountResponse from(AccountMaster m) {
        AccountResponse r = new AccountResponse();
        r.setAccountId(m.getAccountId());
        r.setAccountNumber(m.getAccountNumber());
        r.setAccountType(m.getAccountType());
        r.setProductId(m.getProductId());
        r.setProductCode(m.getProductCode());
        r.setCurrencyCode(m.getCurrencyCode());
        r.setAccountName(m.getAccountName());
        r.setShortName(m.getShortName());
        r.setCustomerId(m.getCustomerId());
        r.setEntityId(m.getEntityId());
        r.setBranchCode(m.getBranchCode());
        r.setPurposeCode(m.getPurposeCode());
        r.setRiskCategory(m.getRiskCategory());
        r.setOwnershipType(m.getOwnershipType());
        r.setRelationshipManagerId(m.getRelationshipManagerId());
        r.setAccountStatus(m.getAccountStatus());
        r.setOpeningDate(m.getOpeningDate());
        r.setClosingDate(m.getClosingDate());
        r.setCloseReason(m.getCloseReason());
        r.setLedgerBalance(m.getLedgerBalance());
        r.setAvailableBalance(m.getAvailableBalance());
        r.setEarmarkedAmount(m.getEarmarkedAmount());
        r.setOverdraftLimitCached(m.getOverdraftLimitCached());
        r.setCreatedBy(m.getCreatedBy());
        r.setCreatedAt(m.getCreatedAt());
        r.setUpdatedBy(m.getUpdatedBy());
        r.setUpdatedAt(m.getUpdatedAt());
        r.setVersion(m.getVersion());
        return r;
    }
}
