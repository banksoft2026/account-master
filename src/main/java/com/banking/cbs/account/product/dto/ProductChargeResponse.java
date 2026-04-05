package com.banking.cbs.account.product.dto;

import com.banking.cbs.account.product.entity.ProductCharge;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductChargeResponse {

    private String chargeId;
    private String productId;
    private String chargeCode;
    private String chargeName;
    private String chargeType;
    private BigDecimal chargeAmount;
    private String chargeCurrency;
    private BigDecimal percentageRate;
    private String frequency;
    private String triggerEvent;
    private boolean waivable;
    private BigDecimal minBalanceForWaiver;
    private boolean isActive;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Integer version;

    public static ProductChargeResponse from(ProductCharge c) {
        ProductChargeResponse r = new ProductChargeResponse();
        r.setChargeId(c.getChargeId());
        r.setProductId(c.getProductId());
        r.setChargeCode(c.getChargeCode());
        r.setChargeName(c.getChargeName());
        r.setChargeType(c.getChargeType());
        r.setChargeAmount(c.getChargeAmount());
        r.setChargeCurrency(c.getChargeCurrency());
        r.setPercentageRate(c.getPercentageRate());
        r.setFrequency(c.getFrequency());
        r.setTriggerEvent(c.getTriggerEvent());
        r.setWaivable(c.isWaivable());
        r.setMinBalanceForWaiver(c.getMinBalanceForWaiver());
        r.setActive(c.isActive());
        r.setEffectiveFrom(c.getEffectiveFrom());
        r.setEffectiveTo(c.getEffectiveTo());
        r.setVersion(c.getVersion());
        return r;
    }
}
