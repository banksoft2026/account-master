package com.banking.cbs.account.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductInterestTierRequest {

    @NotBlank(message = "tierName is required")
    private String tierName;

    @NotNull(message = "tierSequence is required")
    private Short tierSequence;

    @NotNull(message = "balanceFrom is required")
    private BigDecimal balanceFrom;

    private BigDecimal balanceTo;

    private BigDecimal creditRate = BigDecimal.ZERO;
    private BigDecimal debitRate = BigDecimal.ZERO;
    private String rateType = "FIXED";
    private String calculationBasis = "DAILY_BALANCE";

    @NotNull(message = "effectiveFrom is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}
