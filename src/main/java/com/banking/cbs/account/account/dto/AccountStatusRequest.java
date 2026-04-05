package com.banking.cbs.account.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountStatusRequest {

    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "reason is required")
    private String reason;
}
