package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountParameters;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
public class AccountParametersResponse {

    private String paramId;
    private String accountId;
    private String productId;
    private BigDecimal creditInterestRate;
    private BigDecimal debitInterestRate;
    private BigDecimal penaltyRate;
    private String interestAccrualFreq;
    private String interestPaymentFreq;
    private BigDecimal overdraftLimit;
    private BigDecimal dailyDebitLimit;
    private BigDecimal dailyCreditLimit;
    private BigDecimal singleTxnLimit;
    private boolean overdraftAllowed;
    private boolean sweepEnabled;
    private boolean autoRenewal;
    private boolean statementsEnabled;
    private Short statementDay;
    private String statementFrequency;
    private String specialInstruction;
    private String chargeSchemeRef;
    private Instant effectiveFrom;
    private Integer version;

    // source map: paramKey -> {value, source, overrideId?, approvedBy?}
    private Map<String, Map<String, Object>> sourceMap = new HashMap<>();

    public static AccountParametersResponse from(AccountParameters p) {
        AccountParametersResponse r = new AccountParametersResponse();
        r.setParamId(p.getParamId());
        r.setAccountId(p.getAccountId());
        r.setProductId(p.getProductId());
        r.setCreditInterestRate(p.getCreditInterestRate());
        r.setDebitInterestRate(p.getDebitInterestRate());
        r.setPenaltyRate(p.getPenaltyRate());
        r.setInterestAccrualFreq(p.getInterestAccrualFreq());
        r.setInterestPaymentFreq(p.getInterestPaymentFreq());
        r.setOverdraftLimit(p.getOverdraftLimit());
        r.setDailyDebitLimit(p.getDailyDebitLimit());
        r.setDailyCreditLimit(p.getDailyCreditLimit());
        r.setSingleTxnLimit(p.getSingleTxnLimit());
        r.setOverdraftAllowed(p.isOverdraftAllowed());
        r.setSweepEnabled(p.isSweepEnabled());
        r.setAutoRenewal(p.isAutoRenewal());
        r.setStatementsEnabled(p.isStatementsEnabled());
        r.setStatementDay(p.getStatementDay());
        r.setStatementFrequency(p.getStatementFrequency());
        r.setSpecialInstruction(p.getSpecialInstruction());
        r.setChargeSchemeRef(p.getChargeSchemeRef());
        r.setEffectiveFrom(p.getEffectiveFrom());
        r.setVersion(p.getVersion());
        return r;
    }
}
