package com.banking.cbs.account.product.controller;

import com.banking.cbs.account.common.response.ApiResponse;
import com.banking.cbs.account.product.dto.*;
import com.banking.cbs.account.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "1. Account Product", description = "Account product definition, parameters, interest tiers, and charges")
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    // ── Product Master ────────────────────────────────────────────────────────

    @Operation(summary = "Create account product", description = "Create a new account product with parameters, interest tiers, and charges.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductRequest req) {
        return ApiResponse.ok("Product created successfully", service.createProduct(req));
    }

    @Operation(summary = "List account products", description = "List products with optional filters.")
    @GetMapping
    public ApiResponse<List<ProductResponse>> list(
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String status) {
        return ApiResponse.ok(service.listProducts(accountType, segment, currency, status));
    }

    @Operation(summary = "Get account product", description = "Get full product details including parameters, tiers, and charges.")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> get(@PathVariable String productId) {
        return ApiResponse.ok(service.getProduct(productId));
    }

    @Operation(summary = "Update account product", description = "Partial update of mutable product fields.")
    @PatchMapping("/{productId}")
    public ApiResponse<ProductResponse> patch(@PathVariable String productId,
                                               @RequestBody ProductRequest req) {
        return ApiResponse.ok(service.patchProduct(productId, req));
    }

    @Operation(summary = "Update product lifecycle status", description = "Transition product status: DRAFT→ACTIVE, ACTIVE→SUSPENDED/RETIRED, SUSPENDED→ACTIVE/RETIRED.")
    @PutMapping("/{productId}/status")
    public ApiResponse<ProductResponse> updateStatus(@PathVariable String productId,
                                                      @Valid @RequestBody ProductStatusRequest req) {
        return ApiResponse.ok(service.updateStatus(productId, req.getStatus()));
    }

    // ── Product Parameters ────────────────────────────────────────────────────

    @Operation(summary = "Add product parameter", description = "Add a new configurable parameter to a product.")
    @PostMapping("/{productId}/parameters")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductParameterResponse> addParameter(@PathVariable String productId,
                                                               @Valid @RequestBody ProductParameterRequest req) {
        return ApiResponse.ok("Parameter added", service.addParameter(productId, req));
    }

    @Operation(summary = "List product parameters", description = "List all parameters for a product.")
    @GetMapping("/{productId}/parameters")
    public ApiResponse<List<ProductParameterResponse>> listParameters(@PathVariable String productId) {
        return ApiResponse.ok(service.listParameters(productId));
    }

    @Operation(summary = "Replace product parameter", description = "Replace all fields of a product parameter.")
    @PutMapping("/{productId}/parameters/{paramKey}")
    public ApiResponse<ProductParameterResponse> updateParameter(@PathVariable String productId,
                                                                   @PathVariable String paramKey,
                                                                   @RequestBody ProductParameterRequest req) {
        return ApiResponse.ok(service.updateParameter(productId, paramKey, req));
    }

    @Operation(summary = "Patch product parameter", description = "Partial update (min/max/overridable) of a product parameter.")
    @PatchMapping("/{productId}/parameters/{paramKey}")
    public ApiResponse<ProductParameterResponse> patchParameter(@PathVariable String productId,
                                                                  @PathVariable String paramKey,
                                                                  @RequestBody ProductParameterRequest req) {
        return ApiResponse.ok(service.patchParameter(productId, paramKey, req));
    }

    @Operation(summary = "Delete product parameter", description = "Delete a parameter from a product (only if no active accounts).")
    @DeleteMapping("/{productId}/parameters/{paramKey}")
    public ApiResponse<Void> deleteParameter(@PathVariable String productId,
                                              @PathVariable String paramKey) {
        service.deleteParameter(productId, paramKey);
        return ApiResponse.ok("Parameter deleted", null);
    }

    // ── Interest Tiers ────────────────────────────────────────────────────────

    @Operation(summary = "Add interest tier", description = "Add an interest rate tier to a product.")
    @PostMapping("/{productId}/interest-tiers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductInterestTierResponse> addTier(@PathVariable String productId,
                                                             @Valid @RequestBody ProductInterestTierRequest req) {
        return ApiResponse.ok("Interest tier added", service.addTier(productId, req));
    }

    @Operation(summary = "List interest tiers", description = "List interest tiers for a product, ordered by tier sequence.")
    @GetMapping("/{productId}/interest-tiers")
    public ApiResponse<List<ProductInterestTierResponse>> listTiers(@PathVariable String productId) {
        return ApiResponse.ok(service.listTiers(productId));
    }

    @Operation(summary = "Update interest tier", description = "Replace an interest tier's fields.")
    @PutMapping("/{productId}/interest-tiers/{tierId}")
    public ApiResponse<ProductInterestTierResponse> updateTier(@PathVariable String productId,
                                                                @PathVariable String tierId,
                                                                @RequestBody ProductInterestTierRequest req) {
        return ApiResponse.ok(service.updateTier(tierId, req));
    }

    @Operation(summary = "Delete interest tier", description = "Remove an interest tier from a product.")
    @DeleteMapping("/{productId}/interest-tiers/{tierId}")
    public ApiResponse<Void> deleteTier(@PathVariable String productId,
                                         @PathVariable String tierId) {
        service.deleteTier(tierId);
        return ApiResponse.ok("Interest tier deleted", null);
    }

    // ── Product Charges ───────────────────────────────────────────────────────

    @Operation(summary = "Add product charge", description = "Add a fee/charge definition to a product.")
    @PostMapping("/{productId}/charges")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductChargeResponse> addCharge(@PathVariable String productId,
                                                          @Valid @RequestBody ProductChargeRequest req) {
        return ApiResponse.ok("Charge added", service.addCharge(productId, req));
    }

    @Operation(summary = "List product charges", description = "List all charges for a product.")
    @GetMapping("/{productId}/charges")
    public ApiResponse<List<ProductChargeResponse>> listCharges(@PathVariable String productId) {
        return ApiResponse.ok(service.listCharges(productId));
    }

    @Operation(summary = "Patch product charge", description = "Partial update of a product charge.")
    @PatchMapping("/{productId}/charges/{chargeId}")
    public ApiResponse<ProductChargeResponse> patchCharge(@PathVariable String productId,
                                                            @PathVariable String chargeId,
                                                            @RequestBody ProductChargeRequest req) {
        return ApiResponse.ok(service.patchCharge(chargeId, req));
    }

    @Operation(summary = "Delete product charge", description = "Remove a charge definition from a product.")
    @DeleteMapping("/{productId}/charges/{chargeId}")
    public ApiResponse<Void> deleteCharge(@PathVariable String productId,
                                           @PathVariable String chargeId) {
        service.deleteCharge(chargeId);
        return ApiResponse.ok("Charge deleted", null);
    }
}
