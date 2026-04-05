package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "account_earmark")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountEarmark {

    @Id
    @UuidGenerator
    @Column(name = "earmark_id", length = 36, updatable = false, nullable = false)
    private String earmarkId;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    @Column(name = "earmark_amount", precision = 20, scale = 2, nullable = false)
    private BigDecimal earmarkAmount;

    @Column(name = "earmark_type", length = 30, nullable = false)
    private String earmarkType;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom = Instant.now();

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @Column(name = "created_by", length = 36, nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_by", length = 36)
    private String updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
