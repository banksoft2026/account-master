package com.banking.cbs.account.account.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Entity
@Table(name = "account_parameter_override")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountParameterOverride {

    @Id
    @UuidGenerator
    @Column(name = "override_id", length = 36, updatable = false, nullable = false)
    private String overrideId;

    @Column(name = "account_id", length = 36, nullable = false)
    private String accountId;

    @Column(name = "param_key", length = 50, nullable = false)
    private String paramKey;

    @Column(name = "original_product_value", length = 100, nullable = false)
    private String originalProductValue;

    @Column(name = "overridden_value", length = 100, nullable = false)
    private String overriddenValue;

    @Column(name = "override_reason", length = 500, nullable = false)
    private String overrideReason;

    @Column(name = "approved_by", length = 36, nullable = false)
    private String approvedBy;

    @Column(name = "approved_at", nullable = false)
    private Instant approvedAt = Instant.now();

    @Column(name = "approval_ref", length = 100)
    private String approvalRef;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom = Instant.now();

    @Column(name = "effective_to")
    private Instant effectiveTo;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
