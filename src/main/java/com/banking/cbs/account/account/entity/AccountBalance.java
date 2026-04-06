package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "account_balance")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountBalance {

    @Id
    @UuidGenerator
    @Column(name = "balance_id", length = 36, updatable = false, nullable = false)
    private String balanceId;

    @Column(name = "account_id", length = 36, nullable = false, unique = true)
    private String accountId;

    @Column(name = "ledger_balance", precision = 20, scale = 2, nullable = false)
    private BigDecimal ledgerBalance = BigDecimal.ZERO;

    @Column(name = "available_balance", precision = 20, scale = 2, nullable = false)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "earmarked_amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal earmarkedAmount = BigDecimal.ZERO;

    @Column(name = "uncollected_amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal uncollectedAmount = BigDecimal.ZERO;

    @Column(name = "overdraft_utilised", precision = 20, scale = 2, nullable = false)
    private BigDecimal overdraftUtilised = BigDecimal.ZERO;

    @Column(name = "overdraft_limit", precision = 20, scale = 2, nullable = false)
    private BigDecimal overdraftLimit = BigDecimal.ZERO;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate = LocalDate.now();

    @Column(name = "value_date", nullable = false)
    private Instant valueDate = Instant.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "balance_as_at")
    private Instant balanceAsAt;

    @Column(name = "balance_version", nullable = false)
    private Long balanceVersion = 1L;

    @Column(name = "last_txn_id", length = 36)
    private String lastTxnId;

    @Column(name = "last_txn_at")
    private Instant lastTxnAt;

    @Column(name = "txn_sequence", nullable = false)
    private Long txnSequence = 0L;

    @Column(name = "accrued_interest_cr", nullable = false, precision = 20, scale = 2)
    private BigDecimal accruedInterestCr = BigDecimal.ZERO;

    @Column(name = "accrued_interest_dr", nullable = false, precision = 20, scale = 2)
    private BigDecimal accruedInterestDr = BigDecimal.ZERO;

    @Column(name = "float_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal floatAmount = BigDecimal.ZERO;

    @Column(name = "shadow_balance", nullable = false, precision = 20, scale = 2)
    private BigDecimal shadowBalance = BigDecimal.ZERO;

    @Column(name = "earmark_count", nullable = false)
    private Short earmarkCount = 0;

    @Column(name = "uncollected_count", nullable = false)
    private Short uncollectedCount = 0;
}
