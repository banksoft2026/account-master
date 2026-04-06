package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountUncollected;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class UncollectedResponse {

    private String uncollectedId;
    private String accountId;
    private String instrumentType;
    private String instrumentRef;
    private String presentingBank;
    private String presentingBankBic;
    private BigDecimal instrumentAmount;
    private String currencyCode;
    private String collectionStatus;
    private LocalDate presentedDate;
    private LocalDate expectedClearanceDate;
    private LocalDate actualClearanceDate;
    private BigDecimal clearedAmount;
    private BigDecimal returnedAmount;
    private String returnReason;
    private String clearingRef;
    private String sourceTxnId;
    private String clearedTxnId;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer version;

    public static UncollectedResponse from(AccountUncollected u) {
        UncollectedResponse r = new UncollectedResponse();
        r.setUncollectedId(u.getUncollectedId());
        r.setAccountId(u.getAccountId());
        r.setInstrumentType(u.getInstrumentType());
        r.setInstrumentRef(u.getInstrumentRef());
        r.setPresentingBank(u.getPresentingBank());
        r.setPresentingBankBic(u.getPresentingBankBic());
        r.setInstrumentAmount(u.getInstrumentAmount());
        r.setCurrencyCode(u.getCurrencyCode());
        r.setCollectionStatus(u.getCollectionStatus());
        r.setPresentedDate(u.getPresentedDate());
        r.setExpectedClearanceDate(u.getExpectedClearanceDate());
        r.setActualClearanceDate(u.getActualClearanceDate());
        r.setClearedAmount(u.getClearedAmount());
        r.setReturnedAmount(u.getReturnedAmount());
        r.setReturnReason(u.getReturnReason());
        r.setClearingRef(u.getClearingRef());
        r.setSourceTxnId(u.getSourceTxnId());
        r.setClearedTxnId(u.getClearedTxnId());
        r.setCreatedBy(u.getCreatedBy());
        r.setCreatedAt(u.getCreatedAt());
        r.setUpdatedAt(u.getUpdatedAt());
        r.setVersion(u.getVersion());
        return r;
    }
}
