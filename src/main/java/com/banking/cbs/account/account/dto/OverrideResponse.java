package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountParameterOverride;
import lombok.Data;

import java.time.Instant;

@Data
public class OverrideResponse {

    private String overrideId;
    private String accountId;
    private String paramKey;
    private String originalProductValue;
    private String overriddenValue;
    private String overrideReason;
    private String approvedBy;
    private Instant approvedAt;
    private String approvalRef;
    private Instant effectiveFrom;
    private Instant effectiveTo;
    private boolean isActive;
    private Integer version;

    public static OverrideResponse from(AccountParameterOverride o) {
        OverrideResponse r = new OverrideResponse();
        r.setOverrideId(o.getOverrideId());
        r.setAccountId(o.getAccountId());
        r.setParamKey(o.getParamKey());
        r.setOriginalProductValue(o.getOriginalProductValue());
        r.setOverriddenValue(o.getOverriddenValue());
        r.setOverrideReason(o.getOverrideReason());
        r.setApprovedBy(o.getApprovedBy());
        r.setApprovedAt(o.getApprovedAt());
        r.setApprovalRef(o.getApprovalRef());
        r.setEffectiveFrom(o.getEffectiveFrom());
        r.setEffectiveTo(o.getEffectiveTo());
        r.setActive(o.isActive());
        r.setVersion(o.getVersion());
        return r;
    }
}
