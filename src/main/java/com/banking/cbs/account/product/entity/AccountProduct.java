package com.banking.cbs.account.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "account_product")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountProduct {

    @Id
    @UuidGenerator
    @Column(name = "product_id", length = 36, updatable = false, nullable = false)
    private String productId;

    @Column(name = "product_code", length = 30, nullable = false, unique = true)
    private String productCode;

    @Column(name = "product_name", length = 200, nullable = false)
    private String productName;

    @Column(name = "account_type", length = 20, nullable = false)
    private String accountType;

    @Column(name = "target_segment", length = 30, nullable = false)
    private String targetSegment;

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "allow_joint", nullable = false)
    private boolean allowJoint = false;

    @Column(name = "allow_corporate", nullable = false)
    private boolean allowCorporate = true;

    @Column(name = "allow_individual", nullable = false)
    private boolean allowIndividual = false;

    @Column(name = "min_opening_balance", precision = 20, scale = 2, nullable = false)
    private BigDecimal minOpeningBalance = BigDecimal.ZERO;

    @Column(name = "max_balance", precision = 20, scale = 2)
    private BigDecimal maxBalance;

    @Column(name = "max_account_per_customer")
    private Short maxAccountPerCustomer;

    @Column(name = "lifecycle_status", length = 20, nullable = false)
    private String lifecycleStatus = "DRAFT";

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

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

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
