package com.banking.cbs.account.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParameterOverrideRequest {

    @NotBlank(message = "paramKey is required")
    private String paramKey;

    @NotBlank(message = "value is required")
    private String value;

    @NotBlank(message = "overrideReason is required")
    private String overrideReason;

    @NotBlank(message = "approvedBy is required")
    private String approvedBy;

    private String approvalRef;
}
