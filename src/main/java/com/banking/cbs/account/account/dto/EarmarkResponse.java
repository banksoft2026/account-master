package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountEarmark;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class EarmarkResponse {

    private String earmarkId;
    private String accountId;
    private BigDecimal earmarkAmount;
    private String earmarkType;
    private String referenceId;
    private String description;
    private boolean isActive;
    private Instant effectiveFrom;
    private Instant effectiveTo;
    private String createdBy;
    private Instant createdAt;

    private String earmarkRef;
    private String sourceModule;
    private String earmarkReason;
    private String earmarkStatus;
    private Instant expiryAt;
    private BigDecimal releasedAmount;
    private Instant releasedAt;
    private String releasedBy;
    private String releaseReason;
    private String sourceTxnId;

    public static EarmarkResponse from(AccountEarmark e) {
        EarmarkResponse r = new EarmarkResponse();
        r.setEarmarkId(e.getEarmarkId());
        r.setAccountId(e.getAccountId());
        r.setEarmarkAmount(e.getEarmarkAmount());
        r.setEarmarkType(e.getEarmarkType());
        r.setReferenceId(e.getReferenceId());
        r.setDescription(e.getDescription());
        r.setActive(e.isActive());
        r.setEffectiveFrom(e.getEffectiveFrom());
        r.setEffectiveTo(e.getEffectiveTo());
        r.setCreatedBy(e.getCreatedBy());
        r.setCreatedAt(e.getCreatedAt());
        r.setEarmarkRef(e.getEarmarkRef());
        r.setSourceModule(e.getSourceModule());
        r.setEarmarkReason(e.getEarmarkReason());
        r.setEarmarkStatus(e.getEarmarkStatus());
        r.setExpiryAt(e.getExpiryAt());
        r.setReleasedAmount(e.getReleasedAmount());
        r.setReleasedAt(e.getReleasedAt());
        r.setReleasedBy(e.getReleasedBy());
        r.setReleaseReason(e.getReleaseReason());
        r.setSourceTxnId(e.getSourceTxnId());
        return r;
    }
}
