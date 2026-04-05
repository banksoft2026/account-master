package com.banking.cbs.account.account.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountOpenRequest {

    @NotBlank(message = "productId is required")
    private String productId;

    @NotNull(message = "master details are required")
    @Valid
    private MasterDetails master;

    @Valid
    private List<ParameterOverrideRequest> parameterOverrides = new ArrayList<>();

    @Data
    public static class MasterDetails {
        @NotBlank(message = "accountName is required")
        private String accountName;
        private String shortName;
        @NotBlank(message = "customerId is required")
        private String customerId;
        @NotBlank(message = "entityId is required")
        private String entityId;
        @NotBlank(message = "branchCode is required")
        private String branchCode;
        private String purposeCode;
        private String riskCategory = "LOW";
        private String ownershipType = "SOLE";
        private String relationshipManagerId;
        @NotBlank(message = "createdBy is required")
        private String createdBy;
    }
}
