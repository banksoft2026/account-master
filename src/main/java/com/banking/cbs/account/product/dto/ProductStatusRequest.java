package com.banking.cbs.account.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductStatusRequest {

    @NotBlank(message = "status is required")
    private String status;

    private String reason;
}
