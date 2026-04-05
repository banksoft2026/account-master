package com.banking.cbs.account.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_charge")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductCharge {

    @Id
    @UuidGenerator
    @Column(name = "charge_id", length = 36, updatable = false, nullable = false)
    private String chargeId;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "charge_code", length = 30, nullable = false)
    private String chargeCode;

    @Column(name = "charge_name", length = 150, nullable = false)
    private String chargeName;

    @Column(name = "charge_type", length = 30, nullable = false)
    private String chargeType;

    @Column(name = "charge_amount", precision = 20, scale = 2)
    private BigDecimal chargeAmount;

    @Column(name = "charge_currency", length = 3, nullable = false)
    private String chargeCurrency;

    @Column(name = "percentage_rate", precision = 8, scale = 4)
    private BigDecimal percentageRate;

    @Column(name = "frequency", length = 20, nullable = false)
    private String frequency;

    @Column(name = "trigger_event", length = 50)
    private String triggerEvent;

    @Column(name = "waivable", nullable = false)
    private boolean waivable = false;

    @Column(name = "min_balance_for_waiver", precision = 20, scale = 2)
    private BigDecimal minBalanceForWaiver;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
