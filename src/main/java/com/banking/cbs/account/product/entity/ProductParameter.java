package com.banking.cbs.account.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;

@Entity
@Table(name = "product_parameter")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductParameter {

    @Id
    @UuidGenerator
    @Column(name = "prod_param_id", length = 36, updatable = false, nullable = false)
    private String prodParamId;

    @Column(name = "product_id", length = 36, nullable = false)
    private String productId;

    @Column(name = "param_key", length = 50, nullable = false)
    private String paramKey;

    @Column(name = "param_label", length = 100, nullable = false)
    private String paramLabel;

    @Column(name = "value_type", length = 50, nullable = false)
    private String valueType;

    @Column(name = "default_value", length = 100, nullable = false)
    private String defaultValue;

    @Column(name = "min_value", length = 100)
    private String minValue;

    @Column(name = "max_value", length = 100)
    private String maxValue;

    @Column(name = "overridable_at_account", nullable = false)
    private boolean overridableAtAccount = true;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory = false;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
