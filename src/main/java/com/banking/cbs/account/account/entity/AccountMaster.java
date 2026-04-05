package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "account_master")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountMaster {

    @Id
    @UuidGenerator
    @Column(name = "account_id", length = 36, updatable = false, nullable = false)
    private String accountId;

    @Column(name = "account_number", length = 34, nullable = false, unique = true)
    private String accountNumber;

    @Column(name = "account_type", length = 20, nullable = false)
    private String accountType;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "product_code", length = 30, nullable = false)
    private String productCode;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @Column(name = "account_name", length = 200, nullable = false)
    private String accountName;

    @Column(name = "short_name", length = 50)
    private String shortName;

    @Column(name = "customer_id", length = 36, nullable = false)
    private String customerId;

    @Column(name = "entity_id", length = 36, nullable = false)
    private String entityId;

    @Column(name = "branch_code", length = 20, nullable = false)
    private String branchCode;

    @Column(name = "purpose_code", length = 30)
    private String purposeCode;

    @Column(name = "risk_category", length = 10)
    private String riskCategory = "LOW";

    @Column(name = "ownership_type", length = 20)
    private String ownershipType = "SOLE";

    @Column(name = "relationship_manager_id", length = 36)
    private String relationshipManagerId;

    @Column(name = "account_status", length = 20, nullable = false)
    private String accountStatus = "PENDING_ACTIVATION";

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "close_reason", length = 200)
    private String closeReason;

    @Column(name = "closed_by", length = 36)
    private String closedBy;

    @Column(name = "dormancy_date")
    private LocalDate dormancyDate;

    @Column(name = "freeze_reason", length = 200)
    private String freezeReason;

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

    @Column(name = "overdraft_limit_cached", precision = 20, scale = 2, nullable = false)
    private BigDecimal overdraftLimitCached = BigDecimal.ZERO;

    @Column(name = "created_by", length = 36, nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @PrePersist
    void onPersist() {
        this.createdAt = Instant.now();
    }
}
