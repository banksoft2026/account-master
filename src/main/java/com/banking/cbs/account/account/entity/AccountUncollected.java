package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "account_uncollected")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountUncollected {

    @Id @UuidGenerator
    @Column(name = "uncollected_id", length = 36, updatable = false, nullable = false)
    private String uncollectedId;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    @Column(name = "instrument_type", length = 20, nullable = false)
    private String instrumentType;  // CHEQUE|DEMAND_DRAFT|PAY_ORDER|ELECTRONIC_CLEARING|RETURN_ITEM

    @Column(name = "instrument_ref", length = 100, nullable = false)
    private String instrumentRef;

    @Column(name = "presenting_bank", length = 100)
    private String presentingBank;

    @Column(name = "presenting_bank_bic", length = 11)
    private String presentingBankBic;

    @Column(name = "instrument_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal instrumentAmount;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @Builder.Default
    @Column(name = "collection_status", length = 20, nullable = false)
    private String collectionStatus = "PRESENTED";
    // PRESENTED|IN_CLEARING|CLEARED|RETURNED|CANCELLED

    @Column(name = "presented_date", nullable = false)
    private LocalDate presentedDate;

    @Column(name = "expected_clearance_date", nullable = false)
    private LocalDate expectedClearanceDate;

    @Column(name = "actual_clearance_date")
    private LocalDate actualClearanceDate;

    @Builder.Default
    @Column(name = "cleared_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal clearedAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "returned_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal returnedAmount = BigDecimal.ZERO;

    @Column(name = "return_reason", length = 200)
    private String returnReason;

    @Column(name = "clearing_ref", length = 100)
    private String clearingRef;

    @Column(name = "source_txn_id", length = 36)
    private String sourceTxnId;

    @Column(name = "cleared_txn_id", length = 36)
    private String clearedTxnId;

    @Column(name = "created_by", length = 36, nullable = false)
    private String createdBy;

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
}
