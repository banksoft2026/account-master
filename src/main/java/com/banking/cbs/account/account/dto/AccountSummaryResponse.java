package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountMaster;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountSummaryResponse {

    private String accountId;
    private String accountNumber;
    private String accountType;
    private String accountStatus;
    private String currencyCode;
    private BigDecimal ledgerBalance;
    private BigDecimal availableBalance;
    private String accountName;
    private String entityId;
    private String branchCode;
    private String productId;
    private String productCode;

    public static AccountSummaryResponse from(AccountMaster m) {
        AccountSummaryResponse r = new AccountSummaryResponse();
        r.setAccountId(m.getAccountId());
        r.setAccountNumber(m.getAccountNumber());
        r.setAccountType(m.getAccountType());
        r.setAccountStatus(m.getAccountStatus());
        r.setCurrencyCode(m.getCurrencyCode());
        r.setLedgerBalance(m.getLedgerBalance());
        r.setAvailableBalance(m.getAvailableBalance());
        r.setAccountName(m.getAccountName());
        r.setEntityId(m.getEntityId());
        r.setBranchCode(m.getBranchCode());
        r.setProductId(m.getProductId());
        r.setProductCode(m.getProductCode());
        return r;
    }
}
