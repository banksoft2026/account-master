package com.banking.cbs.account.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductParameterRequest {

    @NotBlank(message = "paramKey is required")
    private String paramKey;

    @NotBlank(message = "paramLabel is required")
    private String paramLabel;

    @NotBlank(message = "valueType is required")
    private String valueType;

    @NotBlank(message = "defaultValue is required")
    private String defaultValue;

    private String minValue;
    private String maxValue;
    private boolean overridableAtAccount = true;
    private boolean mandatory = false;

    @NotNull(message = "effectiveFrom is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}
