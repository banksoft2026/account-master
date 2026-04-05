package com.banking.cbs.account.account.dto;

import lombok.Data;

@Data
public class AccountMasterUpdateRequest {
    private String accountName;
    private String shortName;
    private String purposeCode;
    private String riskCategory;
    private String relationshipManagerId;
    private String updatedBy;
}
