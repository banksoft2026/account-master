package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountBalance;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class AccountBalanceResponse {

    private String balanceId;
    private String accountId;
    private BigDecimal ledgerBalance;
    private BigDecimal availableBalance;
    private BigDecimal earmarkedAmount;
    private BigDecimal uncollectedAmount;
    private BigDecimal overdraftUtilised;
    private BigDecimal overdraftLimit;
    private String currencyCode;
    private LocalDate balanceDate;
    private Instant valueDate;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer version;

    private Instant balanceAsAt;
    private Long balanceVersion;
    private Long txnSequence;
    private BigDecimal accruedInterestCr;
    private BigDecimal accruedInterestDr;
    private BigDecimal floatAmount;
    private Short earmarkCount;
    private Short uncollectedCount;

    public static AccountBalanceResponse from(AccountBalance b) {
        AccountBalanceResponse r = new AccountBalanceResponse();
        r.setBalanceId(b.getBalanceId());
        r.setAccountId(b.getAccountId());
        r.setLedgerBalance(b.getLedgerBalance());
        r.setAvailableBalance(b.getAvailableBalance());
        r.setEarmarkedAmount(b.getEarmarkedAmount());
        r.setUncollectedAmount(b.getUncollectedAmount());
        r.setOverdraftUtilised(b.getOverdraftUtilised());
        r.setOverdraftLimit(b.getOverdraftLimit());
        r.setCurrencyCode(b.getCurrencyCode());
        r.setBalanceDate(b.getBalanceDate());
        r.setValueDate(b.getValueDate());
        r.setCreatedAt(b.getCreatedAt());
        r.setUpdatedAt(b.getUpdatedAt());
        r.setVersion(b.getVersion());
        r.setBalanceAsAt(b.getBalanceAsAt());
        r.setBalanceVersion(b.getBalanceVersion());
        r.setTxnSequence(b.getTxnSequence());
        r.setAccruedInterestCr(b.getAccruedInterestCr());
        r.setAccruedInterestDr(b.getAccruedInterestDr());
        r.setFloatAmount(b.getFloatAmount());
        r.setEarmarkCount(b.getEarmarkCount());
        r.setUncollectedCount(b.getUncollectedCount());
        return r;
    }
}
