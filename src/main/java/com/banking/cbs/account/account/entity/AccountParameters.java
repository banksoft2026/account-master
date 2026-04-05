package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "account_parameters")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountParameters {

    @Id
    @UuidGenerator
    @Column(name = "param_id", length = 36, updatable = false, nullable = false)
    private String paramId;

    @Column(name = "account_id", length = 36, nullable = false, unique = true)
    private String accountId;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "credit_interest_rate", precision = 8, scale = 4, nullable = false)
    private BigDecimal creditInterestRate = BigDecimal.ZERO;

    @Column(name = "debit_interest_rate", precision = 8, scale = 4, nullable = false)
    private BigDecimal debitInterestRate = BigDecimal.ZERO;

    @Column(name = "penalty_rate", precision = 8, scale = 4, nullable = false)
    private BigDecimal penaltyRate = BigDecimal.ZERO;

    @Column(name = "interest_accrual_freq", length = 20, nullable = false)
    private String interestAccrualFreq = "DAILY";

    @Column(name = "interest_payment_freq", length = 20, nullable = false)
    private String interestPaymentFreq = "MONTHLY";

    @Column(name = "overdraft_limit", precision = 20, scale = 2, nullable = false)
    private BigDecimal overdraftLimit = BigDecimal.ZERO;

    @Column(name = "daily_debit_limit", precision = 20, scale = 2, nullable = false)
    private BigDecimal dailyDebitLimit = new BigDecimal("1000000");

    @Column(name = "daily_credit_limit", precision = 20, scale = 2, nullable = false)
    private BigDecimal dailyCreditLimit = new BigDecimal("5000000");

    @Column(name = "single_txn_limit", precision = 20, scale = 2, nullable = false)
    private BigDecimal singleTxnLimit = new BigDecimal("500000");

    @Column(name = "overdraft_allowed", nullable = false)
    private boolean overdraftAllowed = false;

    @Column(name = "sweep_enabled", nullable = false)
    private boolean sweepEnabled = false;

    @Column(name = "auto_renewal", nullable = false)
    private boolean autoRenewal = false;

    @Column(name = "statements_enabled", nullable = false)
    private boolean statementsEnabled = true;

    @Column(name = "statement_day", nullable = false)
    private Short statementDay = 1;

    @Column(name = "statement_frequency", length = 10, nullable = false)
    private String statementFrequency = "MONTHLY";

    @Column(name = "special_instruction", length = 500)
    private String specialInstruction;

    @Column(name = "charge_scheme_ref", length = 36)
    private String chargeSchemeRef;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom = Instant.now();

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
