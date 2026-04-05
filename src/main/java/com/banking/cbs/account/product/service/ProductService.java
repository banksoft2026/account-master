package com.banking.cbs.account.product.service;

import com.banking.cbs.account.account.repository.AccountMasterRepository;
import com.banking.cbs.account.common.exception.CbsException;
import com.banking.cbs.account.product.dto.*;
import com.banking.cbs.account.product.entity.*;
import com.banking.cbs.account.product.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private static final Set<String> VALID_ACCOUNT_TYPES =
            Set.of("CURRENT", "SAVINGS", "TERM_DEPOSIT", "LOAN", "OVERDRAFT");
    private static final Set<String> VALID_SEGMENTS =
            Set.of("CORPORATE", "RETAIL", "SME", "HNW", "GOVERNMENT");
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = new HashMap<>();

    static {
        ALLOWED_TRANSITIONS.put("DRAFT",     Set.of("ACTIVE"));
        ALLOWED_TRANSITIONS.put("ACTIVE",    Set.of("SUSPENDED", "RETIRED"));
        ALLOWED_TRANSITIONS.put("SUSPENDED", Set.of("ACTIVE", "RETIRED"));
        ALLOWED_TRANSITIONS.put("RETIRED",   Set.of());
    }

    private final AccountProductRepository productRepo;
    private final ProductParameterRepository paramRepo;
    private final ProductInterestTierRepository tierRepo;
    private final ProductChargeRepository chargeRepo;
    private final AccountMasterRepository accountMasterRepo;

    // ── Product CRUD ──────────────────────────────────────────────────────────

    @Transactional
    public ProductResponse createProduct(ProductRequest req) {
        if (productRepo.existsByProductCode(req.getProductCode())) {
            throw CbsException.conflict("DUPLICATE_PRODUCT_CODE",
                    "Product code already exists: " + req.getProductCode());
        }
        if (!VALID_ACCOUNT_TYPES.contains(req.getAccountType())) {
            throw CbsException.badRequest("INVALID_ACCOUNT_TYPE",
                    "accountType must be one of: " + VALID_ACCOUNT_TYPES);
        }
        if (!VALID_SEGMENTS.contains(req.getTargetSegment())) {
            throw CbsException.badRequest("INVALID_TARGET_SEGMENT",
                    "targetSegment must be one of: " + VALID_SEGMENTS);
        }
        if (req.getCurrencyCode() == null || req.getCurrencyCode().length() != 3) {
            throw CbsException.badRequest("INVALID_CURRENCY_CODE",
                    "currencyCode must be exactly 3 characters");
        }

        AccountProduct product = AccountProduct.builder()
                .productCode(req.getProductCode())
                .productName(req.getProductName())
                .accountType(req.getAccountType())
                .targetSegment(req.getTargetSegment())
                .currencyCode(req.getCurrencyCode().toUpperCase())
                .description(req.getDescription())
                .allowJoint(req.isAllowJoint())
                .allowCorporate(req.isAllowCorporate())
                .allowIndividual(req.isAllowIndividual())
                .minOpeningBalance(req.getMinOpeningBalance() != null ? req.getMinOpeningBalance() : java.math.BigDecimal.ZERO)
                .maxBalance(req.getMaxBalance())
                .maxAccountPerCustomer(req.getMaxAccountPerCustomer())
                .lifecycleStatus("DRAFT")
                .effectiveFrom(req.getEffectiveFrom())
                .effectiveTo(req.getEffectiveTo())
                .createdBy(req.getCreatedBy())
                .build();

        product = productRepo.save(product);

        final String productId = product.getProductId();

        List<ProductParameter> params = req.getParameters().stream()
                .map(p -> ProductParameter.builder()
                        .productId(productId)
                        .paramKey(p.getParamKey())
                        .paramLabel(p.getParamLabel())
                        .valueType(p.getValueType())
                        .defaultValue(p.getDefaultValue())
                        .minValue(p.getMinValue())
                        .maxValue(p.getMaxValue())
                        .overridableAtAccount(p.isOverridableAtAccount())
                        .mandatory(p.isMandatory())
                        .effectiveFrom(p.getEffectiveFrom())
                        .effectiveTo(p.getEffectiveTo())
                        .build())
                .collect(Collectors.toList());
        paramRepo.saveAll(params);

        List<ProductInterestTier> tiers = req.getInterestTiers().stream()
                .map(t -> ProductInterestTier.builder()
                        .productId(productId)
                        .tierName(t.getTierName())
                        .tierSequence(t.getTierSequence())
                        .balanceFrom(t.getBalanceFrom())
                        .balanceTo(t.getBalanceTo())
                        .creditRate(t.getCreditRate() != null ? t.getCreditRate() : java.math.BigDecimal.ZERO)
                        .debitRate(t.getDebitRate() != null ? t.getDebitRate() : java.math.BigDecimal.ZERO)
                        .rateType(t.getRateType() != null ? t.getRateType() : "FIXED")
                        .calculationBasis(t.getCalculationBasis() != null ? t.getCalculationBasis() : "DAILY_BALANCE")
                        .effectiveFrom(t.getEffectiveFrom())
                        .effectiveTo(t.getEffectiveTo())
                        .build())
                .collect(Collectors.toList());
        tierRepo.saveAll(tiers);

        List<ProductCharge> charges = req.getCharges().stream()
                .map(c -> ProductCharge.builder()
                        .productId(productId)
                        .chargeCode(c.getChargeCode())
                        .chargeName(c.getChargeName())
                        .chargeType(c.getChargeType())
                        .chargeAmount(c.getChargeAmount())
                        .chargeCurrency(c.getChargeCurrency())
                        .percentageRate(c.getPercentageRate())
                        .frequency(c.getFrequency())
                        .triggerEvent(c.getTriggerEvent())
                        .waivable(c.isWaivable())
                        .minBalanceForWaiver(c.getMinBalanceForWaiver())
                        .isActive(c.isActive())
                        .effectiveFrom(c.getEffectiveFrom())
                        .effectiveTo(c.getEffectiveTo())
                        .build())
                .collect(Collectors.toList());
        chargeRepo.saveAll(charges);

        return buildFullResponse(product, params, tiers, charges);
    }

    public ProductResponse getProduct(String productId) {
        AccountProduct product = productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        return buildFullResponse(product,
                paramRepo.findByProductId(productId),
                tierRepo.findByProductIdOrderByTierSequenceAsc(productId),
                chargeRepo.findByProductId(productId));
    }

    public List<ProductResponse> listProducts(String accountType, String segment,
                                               String currency, String status) {
        List<AccountProduct> products = productRepo.findAll();
        return products.stream()
                .filter(p -> accountType == null || p.getAccountType().equals(accountType))
                .filter(p -> segment == null || p.getTargetSegment().equals(segment))
                .filter(p -> currency == null || p.getCurrencyCode().equals(currency))
                .filter(p -> status == null || p.getLifecycleStatus().equals(status))
                .map(p -> {
                    ProductResponse r = ProductResponse.from(p);
                    r.setParameters(paramRepo.findByProductId(p.getProductId()).stream()
                            .map(ProductParameterResponse::from).collect(Collectors.toList()));
                    r.setInterestTiers(tierRepo.findByProductIdOrderByTierSequenceAsc(p.getProductId()).stream()
                            .map(ProductInterestTierResponse::from).collect(Collectors.toList()));
                    r.setCharges(chargeRepo.findByProductId(p.getProductId()).stream()
                            .map(ProductChargeResponse::from).collect(Collectors.toList()));
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse patchProduct(String productId, ProductRequest req) {
        AccountProduct product = productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));

        // Mutable fields only; immutable: productId, productCode, accountType, currencyCode
        if (req.getProductName() != null) product.setProductName(req.getProductName());
        if (req.getTargetSegment() != null) {
            if (!VALID_SEGMENTS.contains(req.getTargetSegment())) {
                throw CbsException.badRequest("INVALID_TARGET_SEGMENT",
                        "targetSegment must be one of: " + VALID_SEGMENTS);
            }
            product.setTargetSegment(req.getTargetSegment());
        }
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        product.setAllowJoint(req.isAllowJoint());
        product.setAllowCorporate(req.isAllowCorporate());
        product.setAllowIndividual(req.isAllowIndividual());
        if (req.getMinOpeningBalance() != null) product.setMinOpeningBalance(req.getMinOpeningBalance());
        if (req.getMaxBalance() != null) product.setMaxBalance(req.getMaxBalance());
        if (req.getMaxAccountPerCustomer() != null) product.setMaxAccountPerCustomer(req.getMaxAccountPerCustomer());
        if (req.getEffectiveTo() != null) product.setEffectiveTo(req.getEffectiveTo());

        product = productRepo.save(product);
        return buildFullResponse(product,
                paramRepo.findByProductId(productId),
                tierRepo.findByProductIdOrderByTierSequenceAsc(productId),
                chargeRepo.findByProductId(productId));
    }

    @Transactional
    public ProductResponse updateStatus(String productId, String newStatus) {
        AccountProduct product = productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));

        String currentStatus = product.getLifecycleStatus();

        if ("RETIRED".equals(currentStatus)) {
            throw CbsException.unprocessable("TERMINAL_STATUS",
                    "Product is in terminal RETIRED status and cannot be transitioned");
        }

        Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw CbsException.unprocessable("INVALID_STATUS_TRANSITION",
                    "Cannot transition from " + currentStatus + " to " + newStatus);
        }

        if ("RETIRED".equals(newStatus)) {
            long activeAccounts = accountMasterRepo.countByProductIdAndAccountStatusNot(productId, "CLOSED");
            if (activeAccounts > 0) {
                throw CbsException.unprocessable("ACTIVE_ACCOUNTS_EXIST",
                        "Cannot retire product: " + activeAccounts + " active account(s) reference this product");
            }
        }

        product.setLifecycleStatus(newStatus);
        product = productRepo.save(product);
        return buildFullResponse(product,
                paramRepo.findByProductId(productId),
                tierRepo.findByProductIdOrderByTierSequenceAsc(productId),
                chargeRepo.findByProductId(productId));
    }

    // ── Product Parameters ────────────────────────────────────────────────────

    @Transactional
    public ProductParameterResponse addParameter(String productId, ProductParameterRequest req) {
        productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        if (paramRepo.existsByProductIdAndParamKey(productId, req.getParamKey())) {
            throw CbsException.conflict("DUPLICATE_PARAM_KEY",
                    "Parameter key already exists for this product: " + req.getParamKey());
        }
        ProductParameter param = ProductParameter.builder()
                .productId(productId)
                .paramKey(req.getParamKey())
                .paramLabel(req.getParamLabel())
                .valueType(req.getValueType())
                .defaultValue(req.getDefaultValue())
                .minValue(req.getMinValue())
                .maxValue(req.getMaxValue())
                .overridableAtAccount(req.isOverridableAtAccount())
                .mandatory(req.isMandatory())
                .effectiveFrom(req.getEffectiveFrom())
                .effectiveTo(req.getEffectiveTo())
                .build();
        return ProductParameterResponse.from(paramRepo.save(param));
    }

    public List<ProductParameterResponse> listParameters(String productId) {
        productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        return paramRepo.findByProductId(productId).stream()
                .map(ProductParameterResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductParameterResponse updateParameter(String productId, String paramKey, ProductParameterRequest req) {
        ProductParameter param = paramRepo.findByProductIdAndParamKey(productId, paramKey)
                .orElseThrow(() -> CbsException.notFound("PARAM_NOT_FOUND",
                        "Parameter not found: " + paramKey));

        // Check if bounds tightened
        boolean boundsTightened = false;
        if (param.getMaxValue() != null && req.getMaxValue() != null) {
            try {
                double oldMax = Double.parseDouble(param.getMaxValue());
                double newMax = Double.parseDouble(req.getMaxValue());
                if (newMax < oldMax) boundsTightened = true;
            } catch (NumberFormatException ignored) {}
        }
        if (param.getMinValue() != null && req.getMinValue() != null) {
            try {
                double oldMin = Double.parseDouble(param.getMinValue());
                double newMin = Double.parseDouble(req.getMinValue());
                if (newMin > oldMin) boundsTightened = true;
            } catch (NumberFormatException ignored) {}
        }
        if (boundsTightened) {
            log.warn("Parameter bounds tightened for product {} param {}: " +
                    "in a full implementation this would propagate to accounts", productId, paramKey);
        }

        param.setParamLabel(req.getParamLabel());
        param.setValueType(req.getValueType());
        param.setDefaultValue(req.getDefaultValue());
        param.setMinValue(req.getMinValue());
        param.setMaxValue(req.getMaxValue());
        param.setOverridableAtAccount(req.isOverridableAtAccount());
        param.setMandatory(req.isMandatory());
        param.setEffectiveFrom(req.getEffectiveFrom());
        param.setEffectiveTo(req.getEffectiveTo());
        return ProductParameterResponse.from(paramRepo.save(param));
    }

    @Transactional
    public ProductParameterResponse patchParameter(String productId, String paramKey, ProductParameterRequest req) {
        ProductParameter param = paramRepo.findByProductIdAndParamKey(productId, paramKey)
                .orElseThrow(() -> CbsException.notFound("PARAM_NOT_FOUND",
                        "Parameter not found: " + paramKey));
        if (req.getMinValue() != null) param.setMinValue(req.getMinValue());
        if (req.getMaxValue() != null) param.setMaxValue(req.getMaxValue());
        param.setOverridableAtAccount(req.isOverridableAtAccount());
        return ProductParameterResponse.from(paramRepo.save(param));
    }

    @Transactional
    public void deleteParameter(String productId, String paramKey) {
        productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        long activeAccounts = accountMasterRepo.countByProductIdAndAccountStatusNot(productId, "CLOSED");
        if (activeAccounts > 0) {
            throw CbsException.unprocessable("ACTIVE_ACCOUNTS_EXIST",
                    "Cannot delete parameter: " + activeAccounts + " active account(s) use this product");
        }
        paramRepo.findByProductIdAndParamKey(productId, paramKey)
                .orElseThrow(() -> CbsException.notFound("PARAM_NOT_FOUND",
                        "Parameter not found: " + paramKey));
        paramRepo.deleteByProductIdAndParamKey(productId, paramKey);
    }

    // ── Interest Tiers ────────────────────────────────────────────────────────

    @Transactional
    public ProductInterestTierResponse addTier(String productId, ProductInterestTierRequest req) {
        productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        if (tierRepo.existsByProductIdAndTierSequence(productId, req.getTierSequence())) {
            throw CbsException.conflict("DUPLICATE_TIER_SEQUENCE",
                    "Tier sequence already exists: " + req.getTierSequence());
        }
        ProductInterestTier tier = ProductInterestTier.builder()
                .productId(productId)
                .tierName(req.getTierName())
                .tierSequence(req.getTierSequence())
                .balanceFrom(req.getBalanceFrom())
                .balanceTo(req.getBalanceTo())
                .creditRate(req.getCreditRate() != null ? req.getCreditRate() : java.math.BigDecimal.ZERO)
                .debitRate(req.getDebitRate() != null ? req.getDebitRate() : java.math.BigDecimal.ZERO)
                .rateType(req.getRateType() != null ? req.getRateType() : "FIXED")
                .calculationBasis(req.getCalculationBasis() != null ? req.getCalculationBasis() : "DAILY_BALANCE")
                .effectiveFrom(req.getEffectiveFrom())
                .effectiveTo(req.getEffectiveTo())
                .build();
        return ProductInterestTierResponse.from(tierRepo.save(tier));
    }

    public List<ProductInterestTierResponse> listTiers(String productId) {
        productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        return tierRepo.findByProductIdOrderByTierSequenceAsc(productId).stream()
                .map(ProductInterestTierResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductInterestTierResponse updateTier(String tierId, ProductInterestTierRequest req) {
        ProductInterestTier tier = tierRepo.findById(tierId)
                .orElseThrow(() -> CbsException.notFound("TIER_NOT_FOUND",
                        "Interest tier not found: " + tierId));
        tier.setTierName(req.getTierName());
        tier.setTierSequence(req.getTierSequence());
        tier.setBalanceFrom(req.getBalanceFrom());
        tier.setBalanceTo(req.getBalanceTo());
        if (req.getCreditRate() != null) tier.setCreditRate(req.getCreditRate());
        if (req.getDebitRate() != null) tier.setDebitRate(req.getDebitRate());
        if (req.getRateType() != null) tier.setRateType(req.getRateType());
        if (req.getCalculationBasis() != null) tier.setCalculationBasis(req.getCalculationBasis());
        tier.setEffectiveFrom(req.getEffectiveFrom());
        if (req.getEffectiveTo() != null) tier.setEffectiveTo(req.getEffectiveTo());
        return ProductInterestTierResponse.from(tierRepo.save(tier));
    }

    @Transactional
    public void deleteTier(String tierId) {
        tierRepo.findById(tierId)
                .orElseThrow(() -> CbsException.notFound("TIER_NOT_FOUND",
                        "Interest tier not found: " + tierId));
        tierRepo.deleteById(tierId);
    }

    // ── Product Charges ───────────────────────────────────────────────────────

    @Transactional
    public ProductChargeResponse addCharge(String productId, ProductChargeRequest req) {
        productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        ProductCharge charge = ProductCharge.builder()
                .productId(productId)
                .chargeCode(req.getChargeCode())
                .chargeName(req.getChargeName())
                .chargeType(req.getChargeType())
                .chargeAmount(req.getChargeAmount())
                .chargeCurrency(req.getChargeCurrency())
                .percentageRate(req.getPercentageRate())
                .frequency(req.getFrequency())
                .triggerEvent(req.getTriggerEvent())
                .waivable(req.isWaivable())
                .minBalanceForWaiver(req.getMinBalanceForWaiver())
                .isActive(req.isActive())
                .effectiveFrom(req.getEffectiveFrom())
                .effectiveTo(req.getEffectiveTo())
                .build();
        return ProductChargeResponse.from(chargeRepo.save(charge));
    }

    public List<ProductChargeResponse> listCharges(String productId) {
        productRepo.findById(productId)
                .orElseThrow(() -> CbsException.notFound("PRODUCT_NOT_FOUND",
                        "Product not found: " + productId));
        return chargeRepo.findByProductId(productId).stream()
                .map(ProductChargeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductChargeResponse patchCharge(String chargeId, ProductChargeRequest req) {
        ProductCharge charge = chargeRepo.findById(chargeId)
                .orElseThrow(() -> CbsException.notFound("CHARGE_NOT_FOUND",
                        "Charge not found: " + chargeId));
        if (req.getChargeName() != null) charge.setChargeName(req.getChargeName());
        if (req.getChargeAmount() != null) charge.setChargeAmount(req.getChargeAmount());
        if (req.getPercentageRate() != null) charge.setPercentageRate(req.getPercentageRate());
        if (req.getFrequency() != null) charge.setFrequency(req.getFrequency());
        if (req.getTriggerEvent() != null) charge.setTriggerEvent(req.getTriggerEvent());
        charge.setWaivable(req.isWaivable());
        if (req.getMinBalanceForWaiver() != null) charge.setMinBalanceForWaiver(req.getMinBalanceForWaiver());
        charge.setActive(req.isActive());
        return ProductChargeResponse.from(chargeRepo.save(charge));
    }

    @Transactional
    public void deleteCharge(String chargeId) {
        chargeRepo.findById(chargeId)
                .orElseThrow(() -> CbsException.notFound("CHARGE_NOT_FOUND",
                        "Charge not found: " + chargeId));
        chargeRepo.deleteById(chargeId);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private ProductResponse buildFullResponse(AccountProduct product,
                                               List<ProductParameter> params,
                                               List<ProductInterestTier> tiers,
                                               List<ProductCharge> charges) {
        ProductResponse r = ProductResponse.from(product);
        r.setParameters(params.stream().map(ProductParameterResponse::from).collect(Collectors.toList()));
        r.setInterestTiers(tiers.stream().map(ProductInterestTierResponse::from).collect(Collectors.toList()));
        r.setCharges(charges.stream().map(ProductChargeResponse::from).collect(Collectors.toList()));
        return r;
    }
}
