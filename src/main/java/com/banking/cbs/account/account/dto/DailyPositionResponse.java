package com.banking.cbs.account.account.dto;

import com.banking.cbs.account.account.entity.AccountDailyPosition;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class DailyPositionResponse {

    private String positionId;
    private String accountId;
    private String currencyCode;
    private LocalDate positionDate;
    private BigDecimal openingBalance;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;
    private Integer creditCount;
    private Integer debitCount;
    private BigDecimal peakBalance;
    private BigDecimal troughBalance;
    private BigDecimal averageBalance;
    private Boolean eodConfirmed;
    private Instant eodConfirmedAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer version;
    private BigDecimal netMovement;
    private BigDecimal closingBalance;

    public static DailyPositionResponse from(AccountDailyPosition p) {
        DailyPositionResponse r = new DailyPositionResponse();
        r.setPositionId(p.getPositionId());
        r.setAccountId(p.getAccountId());
        r.setCurrencyCode(p.getCurrencyCode());
        r.setPositionDate(p.getPositionDate());
        r.setOpeningBalance(p.getOpeningBalance());
        r.setTotalCredits(p.getTotalCredits());
        r.setTotalDebits(p.getTotalDebits());
        r.setCreditCount(p.getCreditCount());
        r.setDebitCount(p.getDebitCount());
        r.setPeakBalance(p.getPeakBalance());
        r.setTroughBalance(p.getTroughBalance());
        r.setAverageBalance(p.getAverageBalance());
        r.setEodConfirmed(p.getEodConfirmed());
        r.setEodConfirmedAt(p.getEodConfirmedAt());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        r.setVersion(p.getVersion());
        r.setNetMovement(p.getNetMovement());
        r.setClosingBalance(p.getClosingBalance());
        return r;
    }
}
