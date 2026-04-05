package com.banking.cbs.account.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "productCode is required")
    private String productCode;

    @NotBlank(message = "productName is required")
    private String productName;

    @NotBlank(message = "accountType is required")
    private String accountType;

    @NotBlank(message = "targetSegment is required")
    private String targetSegment;

    @NotBlank(message = "currencyCode is required")
    private String currencyCode;

    private String description;
    private boolean allowJoint = false;
    private boolean allowCorporate = true;
    private boolean allowIndividual = false;
    private BigDecimal minOpeningBalance = BigDecimal.ZERO;
    private BigDecimal maxBalance;
    private Short maxAccountPerCustomer;

    @NotNull(message = "effectiveFrom is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @NotBlank(message = "createdBy is required")
    private String createdBy;

    @Valid
    private List<ProductParameterRequest> parameters = new ArrayList<>();

    @Valid
    private List<ProductInterestTierRequest> interestTiers = new ArrayList<>();

    @Valid
    private List<ProductChargeRequest> charges = new ArrayList<>();
}
