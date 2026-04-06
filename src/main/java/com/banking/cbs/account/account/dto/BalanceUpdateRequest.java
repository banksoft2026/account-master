package com.banking.cbs.account.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceUpdateRequest {

    @NotBlank(message = "accountId is required")
    private String accountId;

    @NotBlank(message = "drCrIndicator is required")
    private String drCrIndicator;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "currencyCode is required")
    private String currencyCode;

    @NotBlank(message = "txnId is required")
    private String txnId;
}
