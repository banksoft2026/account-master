package com.banking.cbs.account.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EarmarkRequest {

    @Positive(message = "earmarkAmount must be positive")
    private BigDecimal earmarkAmount;

    @NotBlank(message = "earmarkType is required")
    private String earmarkType;

    private String referenceId;
    private String description;

    @NotBlank(message = "createdBy is required")
    private String createdBy;
}
