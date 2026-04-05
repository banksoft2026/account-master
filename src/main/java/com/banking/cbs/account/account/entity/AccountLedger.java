package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "account_ledger")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountLedger {

    @Id
    @UuidGenerator
    @Column(name = "ledger_id", length = 36, updatable = false, nullable = false)
    private String ledgerId;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    @Column(name = "transaction_ref", length = 100, nullable = false)
    private String transactionRef;

    @Column(name = "value_date", nullable = false)
    private LocalDate valueDate;

    @Column(name = "posting_date", nullable = false)
    private Instant postingDate = Instant.now();

    @Column(name = "debit_amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(name = "credit_amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Column(name = "running_balance", precision = 20, scale = 2, nullable = false)
    private BigDecimal runningBalance;

    @Column(name = "narrative", length = 500)
    private String narrative;

    @Column(name = "channel_code", length = 20)
    private String channelCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
