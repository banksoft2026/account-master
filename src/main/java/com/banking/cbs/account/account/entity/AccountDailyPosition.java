package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "account_daily_position")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountDailyPosition {

    @Id @UuidGenerator
    @Column(name = "position_id", length = 36, updatable = false, nullable = false)
    private String positionId;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @Column(name = "position_date", nullable = false)
    private LocalDate positionDate;

    @Builder.Default
    @Column(name = "opening_balance", nullable = false, precision = 20, scale = 2)
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_credits", nullable = false, precision = 20, scale = 2)
    private BigDecimal totalCredits = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_debits", nullable = false, precision = 20, scale = 2)
    private BigDecimal totalDebits = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "credit_count", nullable = false)
    private Integer creditCount = 0;

    @Builder.Default
    @Column(name = "debit_count", nullable = false)
    private Integer debitCount = 0;

    @Column(name = "peak_balance", precision = 20, scale = 2)
    private BigDecimal peakBalance;

    @Column(name = "trough_balance", precision = 20, scale = 2)
    private BigDecimal troughBalance;

    @Column(name = "average_balance", precision = 20, scale = 2)
    private BigDecimal averageBalance;

    @Builder.Default
    @Column(name = "eod_confirmed", nullable = false)
    private Boolean eodConfirmed = false;

    @Column(name = "eod_confirmed_at")
    private Instant eodConfirmedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }

    @PreUpdate
    void onUpdate() { this.updatedAt = Instant.now(); }

    @Transient
    public BigDecimal getNetMovement() {
        return totalCredits.subtract(totalDebits);
    }

    @Transient
    public BigDecimal getClosingBalance() {
        return openingBalance.add(totalCredits).subtract(totalDebits);
    }
}
