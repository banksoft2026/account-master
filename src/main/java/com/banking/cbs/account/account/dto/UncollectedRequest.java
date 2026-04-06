package com.banking.cbs.account.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UncollectedRequest {

    @NotBlank(message = "accountId is required")
    private String accountId;

    @NotBlank(message = "instrumentType is required")
    private String instrumentType;

    @NotBlank(message = "instrumentRef is required")
    private String instrumentRef;

    private String presentingBank;

    private String presentingBankBic;

    @NotNull(message = "instrumentAmount is required")
    @Positive(message = "instrumentAmount must be positive")
    private BigDecimal instrumentAmount;

    @NotBlank(message = "currencyCode is required")
    private String currencyCode;

    @NotNull(message = "presentedDate is required")
    private LocalDate presentedDate;

    @NotNull(message = "expectedClearanceDate is required")
    private LocalDate expectedClearanceDate;

    @NotBlank(message = "createdBy is required")
    private String createdBy;
}
