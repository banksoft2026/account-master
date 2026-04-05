package com.banking.cbs.account.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_interest_tier")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductInterestTier {

    @Id
    @UuidGenerator
    @Column(name = "tier_id", length = 36, updatable = false, nullable = false)
    private String tierId;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "tier_name", length = 100, nullable = false)
    private String tierName;

    @Column(name = "tier_sequence", nullable = false)
    private Short tierSequence;

    @Column(name = "balance_from", precision = 20, scale = 2, nullable = false)
    private BigDecimal balanceFrom;

    @Column(name = "balance_to", precision = 20, scale = 2)
    private BigDecimal balanceTo;

    @Column(name = "credit_rate", precision = 8, scale = 4, nullable = false)
    private BigDecimal creditRate = BigDecimal.ZERO;

    @Column(name = "debit_rate", precision = 8, scale = 4, nullable = false)
    private BigDecimal debitRate = BigDecimal.ZERO;

    @Column(name = "rate_type", length = 10, nullable = false)
    private String rateType = "FIXED";

    @Column(name = "calculation_basis", length = 20, nullable = false)
    private String calculationBasis = "DAILY_BALANCE";

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
