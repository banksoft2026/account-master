package com.banking.cbs.account.product.dto;

import com.banking.cbs.account.product.entity.AccountProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductResponse {

    private String productId;
    private String productCode;
    private String productName;
    private String accountType;
    private String targetSegment;
    private String currencyCode;
    private String description;
    private boolean allowJoint;
    private boolean allowCorporate;
    private boolean allowIndividual;
    private BigDecimal minOpeningBalance;
    private BigDecimal maxBalance;
    private Short maxAccountPerCustomer;
    private String lifecycleStatus;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private Integer version;

    private List<ProductParameterResponse> parameters = new ArrayList<>();
    private List<ProductInterestTierResponse> interestTiers = new ArrayList<>();
    private List<ProductChargeResponse> charges = new ArrayList<>();

    public static ProductResponse from(AccountProduct p) {
        ProductResponse r = new ProductResponse();
        r.setProductId(p.getProductId());
        r.setProductCode(p.getProductCode());
        r.setProductName(p.getProductName());
        r.setAccountType(p.getAccountType());
        r.setTargetSegment(p.getTargetSegment());
        r.setCurrencyCode(p.getCurrencyCode());
        r.setDescription(p.getDescription());
        r.setAllowJoint(p.isAllowJoint());
        r.setAllowCorporate(p.isAllowCorporate());
        r.setAllowIndividual(p.isAllowIndividual());
        r.setMinOpeningBalance(p.getMinOpeningBalance());
        r.setMaxBalance(p.getMaxBalance());
        r.setMaxAccountPerCustomer(p.getMaxAccountPerCustomer());
        r.setLifecycleStatus(p.getLifecycleStatus());
        r.setEffectiveFrom(p.getEffectiveFrom());
        r.setEffectiveTo(p.getEffectiveTo());
        r.setCreatedBy(p.getCreatedBy());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedBy(p.getUpdatedBy());
        r.setUpdatedAt(p.getUpdatedAt());
        r.setVersion(p.getVersion());
        return r;
    }
}
