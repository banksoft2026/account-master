package com.banking.cbs.account.account.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceUpdateResult {

    private String accountId;
    private BigDecimal newLedgerBalance;
    private BigDecimal newAvailableBalance;
    private BigDecimal overdraftUtilised;
    private Long balanceVersion;
    private Long txnSequence;
}
