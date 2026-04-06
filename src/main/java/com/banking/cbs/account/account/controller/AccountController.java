package com.banking.cbs.account.account.controller;

import com.banking.cbs.account.account.dto.*;
import com.banking.cbs.account.account.entity.AccountLedger;
import com.banking.cbs.account.account.dto.DailyPositionResponse;
import com.banking.cbs.account.account.dto.UncollectedRequest;
import com.banking.cbs.account.account.dto.UncollectedResponse;
import com.banking.cbs.account.account.dto.BalanceUpdateRequest;
import com.banking.cbs.account.account.dto.BalanceUpdateResult;
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

    @Operation(summary = "Release earmark (v2)", description = "Release an active earmark with reason and releasedBy tracking.")
    @PutMapping("/{accountId}/earmarks/{earmarkId}/release")
    public ApiResponse<EarmarkResponse> releaseEarmarkV2(@PathVariable String accountId,
                                                          @PathVariable String earmarkId,
                                                          @RequestParam String reason,
                                                          @RequestParam String releasedBy) {
        return ApiResponse.ok("Earmark released", service.releaseEarmark(accountId, earmarkId, reason, releasedBy));
    }

    @Operation(summary = "Cancel earmark", description = "Cancel an active earmark.")
    @PutMapping("/{accountId}/earmarks/{earmarkId}/cancel")
    public ApiResponse<EarmarkResponse> cancelEarmark(@PathVariable String accountId,
                                                       @PathVariable String earmarkId,
                                                       @RequestParam String reason) {
        return ApiResponse.ok("Earmark cancelled", service.cancelEarmark(accountId, earmarkId, reason));
    }

    // ── Balance Position ──────────────────────────────────────────────────────

    @Operation(summary = "Get daily position", description = "Get daily balance position for a specific date.")
    @GetMapping("/{accountId}/balance/position")
    public ApiResponse<DailyPositionResponse> getDailyPosition(
            @PathVariable String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ApiResponse.ok(service.getDailyPosition(accountId, date));
    }

    @Operation(summary = "Get today's position", description = "Get today's balance position.")
    @GetMapping("/{accountId}/balance/position/today")
    public ApiResponse<DailyPositionResponse> getTodayPosition(@PathVariable String accountId) {
        return ApiResponse.ok(service.getTodayPosition(accountId));
    }

    @Operation(summary = "Get position history", description = "Get balance position history for a date range.")
    @GetMapping("/{accountId}/balance/history")
    public ApiResponse<List<DailyPositionResponse>> getPositionHistory(
            @PathVariable String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.ok(service.getPositionHistory(accountId, from, to));
    }

    // ── Uncollected Instruments ───────────────────────────────────────────────

    @Operation(summary = "Register uncollected instrument", description = "Register a cheque, draft or other instrument as uncollected/pending clearing.")
    @PostMapping("/{accountId}/uncollected")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UncollectedResponse> registerUncollected(
            @PathVariable String accountId,
            @Valid @RequestBody UncollectedRequest req) {
        req.setAccountId(accountId);
        return ApiResponse.ok("Uncollected instrument registered", service.registerUncollected(req));
    }

    @Operation(summary = "List uncollected instruments", description = "List uncollected instruments for an account, optionally filtered by status.")
    @GetMapping("/{accountId}/uncollected")
    public ApiResponse<List<UncollectedResponse>> listUncollected(
            @PathVariable String accountId,
            @RequestParam(required = false) String status) {
        return ApiResponse.ok(service.listUncollected(accountId, status));
    }

    @Operation(summary = "Clear uncollected instrument", description = "Mark an uncollected instrument as cleared and credit available balance.")
    @PutMapping("/{accountId}/uncollected/{id}/clear")
    public ApiResponse<UncollectedResponse> clearUncollected(
            @PathVariable String accountId,
            @PathVariable String id,
            @RequestParam String clearingRef) {
        return ApiResponse.ok("Instrument cleared", service.clearUncollected(accountId, id, clearingRef));
    }

    @Operation(summary = "Return uncollected instrument", description = "Mark an uncollected instrument as returned.")
    @PutMapping("/{accountId}/uncollected/{id}/return")
    public ApiResponse<UncollectedResponse> returnUncollected(
            @PathVariable String accountId,
            @PathVariable String id,
            @RequestParam String returnReason) {
        return ApiResponse.ok("Instrument returned", service.returnUncollected(accountId, id, returnReason));
    }

    // ── Internal: Posting Engine ──────────────────────────────────────────────

    @Operation(summary = "Apply posting engine balance update", description = "Internal endpoint for posting engine to apply DR/CR balance movements.")
    @PostMapping("/internal/balance-update")
    public ApiResponse<BalanceUpdateResult> applyBalanceUpdate(@Valid @RequestBody BalanceUpdateRequest req) {
        return ApiResponse.ok("Balance updated",
                service.applyPostingEngineBalanceUpdate(
                        req.getAccountId(),
                        req.getDrCrIndicator(),
                        req.getAmount(),
                        req.getCurrencyCode(),
                        req.getTxnId()));
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
