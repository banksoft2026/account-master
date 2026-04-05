package com.banking.cbs.account.product.dto;

import com.banking.cbs.account.product.entity.ProductInterestTier;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductInterestTierResponse {

    private String tierId;
    private String productId;
    private String tierName;
    private Short tierSequence;
    private BigDecimal balanceFrom;
    private BigDecimal balanceTo;
    private BigDecimal creditRate;
    private BigDecimal debitRate;
    private String rateType;
    private String calculationBasis;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Integer version;

    public static ProductInterestTierResponse from(ProductInterestTier t) {
        ProductInterestTierResponse r = new ProductInterestTierResponse();
        r.setTierId(t.getTierId());
        r.setProductId(t.getProductId());
        r.setTierName(t.getTierName());
        r.setTierSequence(t.getTierSequence());
        r.setBalanceFrom(t.getBalanceFrom());
        r.setBalanceTo(t.getBalanceTo());
        r.setCreditRate(t.getCreditRate());
        r.setDebitRate(t.getDebitRate());
        r.setRateType(t.getRateType());
        r.setCalculationBasis(t.getCalculationBasis());
        r.setEffectiveFrom(t.getEffectiveFrom());
        r.setEffectiveTo(t.getEffectiveTo());
        r.setVersion(t.getVersion());
        return r;
    }
}
