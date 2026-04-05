package com.banking.cbs.account.product.dto;

import com.banking.cbs.account.product.entity.ProductParameter;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductParameterResponse {

    private String prodParamId;
    private String productId;
    private String paramKey;
    private String paramLabel;
    private String valueType;
    private String defaultValue;
    private String minValue;
    private String maxValue;
    private boolean overridableAtAccount;
    private boolean mandatory;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private Integer version;

    public static ProductParameterResponse from(ProductParameter p) {
        ProductParameterResponse r = new ProductParameterResponse();
        r.setProdParamId(p.getProdParamId());
        r.setProductId(p.getProductId());
        r.setParamKey(p.getParamKey());
        r.setParamLabel(p.getParamLabel());
        r.setValueType(p.getValueType());
        r.setDefaultValue(p.getDefaultValue());
        r.setMinValue(p.getMinValue());
        r.setMaxValue(p.getMaxValue());
        r.setOverridableAtAccount(p.isOverridableAtAccount());
        r.setMandatory(p.isMandatory());
        r.setEffectiveFrom(p.getEffectiveFrom());
        r.setEffectiveTo(p.getEffectiveTo());
        r.setVersion(p.getVersion());
        return r;
    }
}
