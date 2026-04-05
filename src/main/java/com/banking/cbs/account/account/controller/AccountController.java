package com.banking.cbs.account.account.controller;

import com.banking.cbs.account.account.dto.*;
import com.banking.cbs.account.account.entity.AccountLedger;
import com.banking.cbs.account.account.service.AccountService;
import com.banking.cbs.account.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "2. Account Master", description = "Account opening, lifecycle management, parameters, balances, earmarks, and ledger")
@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    // ── Account Master ────────────────────────────────────────────────────────

    @Operation(summary = "Open account", description = "Open a new account. Validates entity eligibility via customer-entity service.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AccountResponse> openAccount(@Valid @RequestBody AccountOpenRequest req) {
        return ApiResponse.ok("Account opened successfully", service.openAccount(req));
    }

    @Operation(summary = "Get account", description = "Get full account details including parameters, overrides, and balance.")
    @GetMapping("/{accountId}")
    public ApiResponse<AccountResponse> get(@PathVariable String accountId) {
        return ApiResponse.ok(service.getAccount(accountId));
    }

    @Operation(summary = "Get account summary", description = "Get lightweight account summary with balance cache.")
    @GetMapping("/{accountId}/summary")
    public ApiResponse<AccountSummaryResponse> getSummary(@PathVariable String accountId) {
        return ApiResponse.ok(service.getAccountSummary(accountId));
    }

    @Operation(summary = "Update account master", description = "Partial update of mutable account fields.")
    @PatchMapping("/{accountId}/master")
    public ApiResponse<AccountResponse> updateMaster(@PathVariable String accountId,
                                                      @RequestBody AccountMasterUpdateRequest req) {
        return ApiResponse.ok(service.updateAccountMaster(accountId, req));
    }

    @Operation(summary = "Update account status", description = "Transition account status.")
    @PutMapping("/{accountId}/status")
    public ApiResponse<AccountResponse> updateStatus(@PathVariable String accountId,
                                                      @Valid @RequestBody AccountStatusRequest req) {
        return ApiResponse.ok(service.updateStatus(accountId, req.getStatus(), req.getReason()));
    }

    @Operation(summary = "Close account (soft)", description = "Soft close an account.")
    @DeleteMapping("/{accountId}")
    public ApiResponse<AccountResponse> softClose(@PathVariable String accountId,
                                                   @RequestParam(defaultValue = "Account closed via API") String reason) {
        return ApiResponse.ok("Account closed", service.softClose(accountId, reason));
    }

    @Operation(summary = "List accounts", description = "List accounts with optional filters.")
    @GetMapping
    public ApiResponse<List<AccountResponse>> list(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String branchCode) {
        return ApiResponse.ok(service.listAccounts(productId, customerId, entityId, status, branchCode));
    }

    // ── Account Parameters ────────────────────────────────────────────────────

    @Operation(summary = "Get account parameters", description = "Get resolved parameter view with source (product default or account override).")
    @GetMapping("/{accountId}/parameters")
    public ApiResponse<AccountParametersResponse> getParameters(@PathVariable String accountId) {
        return ApiResponse.ok(service.getParameters(accountId));
    }

    @Operation(summary = "List parameter overrides", description = "List all active parameter overrides for an account.")
    @GetMapping("/{accountId}/parameters/overrides")
    public ApiResponse<List<OverrideResponse>> listOverrides(@PathVariable String accountId) {
        return ApiResponse.ok(service.listOverrides(accountId));
    }

    @Operation(summary = "Apply parameter override", description = "Override a product parameter at account level. Requires approval.")
    @PostMapping("/{accountId}/parameters/overrides")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OverrideResponse> applyOverride(@PathVariable String accountId,
                                                         @Valid @RequestBody ParameterOverrideRequest req) {
        return ApiResponse.ok("Override applied", service.applyOverride(accountId, req));
    }

    @Operation(summary = "Revert parameter override", description = "Revert an account-level override back to the product default.")
    @DeleteMapping("/{accountId}/parameters/overrides/{paramKey}")
    public ApiResponse<Void> revertOverride(@PathVariable String accountId,
                                             @PathVariable String paramKey) {
        service.revertOverride(accountId, paramKey);
        return ApiResponse.ok("Override reverted to product default", null);
    }

    // ── Account Balance ───────────────────────────────────────────────────────

    @Operation(summary = "Get account balance", description = "Get full balance record with all balance components.")
    @GetMapping("/{accountId}/balance")
    public ApiResponse<AccountBalanceResponse> getBalance(@PathVariable String accountId) {
        return ApiResponse.ok(service.getBalance(accountId));
    }

    @Operation(summary = "Get balance summary", description = "Get balance cache from account master (fast read path).")
    @GetMapping("/{accountId}/balance/summary")
    public ApiResponse<AccountSummaryResponse> getBalanceSummary(@PathVariable String accountId) {
        return ApiResponse.ok(service.getBalanceSummary(accountId));
    }

    @Operation(summary = "Place earmark", description = "Place a hold/earmark on the account balance.")
    @PostMapping("/{accountId}/balance/earmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EarmarkResponse> placeEarmark(@PathVariable String accountId,
                                                      @Valid @RequestBody EarmarkRequest req) {
        return ApiResponse.ok("Earmark placed", service.placeEarmark(accountId, req));
    }

    @Operation(summary = "Release earmark", description = "Release an active earmark and restore available balance.")
    @DeleteMapping("/{accountId}/balance/earmarks/{earmarkId}")
    public ApiResponse<Void> releaseEarmark(@PathVariable String accountId,
                                             @PathVariable String earmarkId) {
        service.releaseEarmark(accountId, earmarkId);
        return ApiResponse.ok("Earmark released", null);
    }

    // ── Account Ledger ────────────────────────────────────────────────────────

    @Operation(summary = "Get account ledger", description = "Get paginated ledger entries for an account, optionally filtered by date range.")
    @GetMapping("/{accountId}/ledger")
    public ApiResponse<Page<AccountLedger>> getLedger(
            @PathVariable String accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return ApiResponse.ok(service.getLedger(accountId, from, to, pageable));
    }
}
