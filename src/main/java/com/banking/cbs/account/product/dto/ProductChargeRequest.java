package com.banking.cbs.account.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductChargeRequest {

    @NotBlank(message = "chargeCode is required")
    private String chargeCode;

    @NotBlank(message = "chargeName is required")
    private String chargeName;

    @NotBlank(message = "chargeType is required")
    private String chargeType;

    private BigDecimal chargeAmount;

    @NotBlank(message = "chargeCurrency is required")
    private String chargeCurrency;

    private BigDecimal percentageRate;

    @NotBlank(message = "frequency is required")
    private String frequency;

    private String triggerEvent;
    private boolean waivable = false;
    private BigDecimal minBalanceForWaiver;
    private boolean isActive = true;

    @NotNull(message = "effectiveFrom is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}
