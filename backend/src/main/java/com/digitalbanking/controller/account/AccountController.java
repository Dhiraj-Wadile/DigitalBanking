package com.digitalbanking.controller.account;

import com.digitalbanking.dto.account.*;
import com.digitalbanking.dto.common.ApiResponse;
import com.digitalbanking.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create new account")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(ApiResponse.success("Account created", response));
    }

    @GetMapping
    @Operation(summary = "Get my accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getMyAccounts() {
        List<AccountResponse> accounts = accountService.getMyAccounts();
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by number")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(@PathVariable String accountNumber) {
        BigDecimal balance = accountService.getBalance(accountNumber);
        return ResponseEntity.ok(ApiResponse.success(balance));
    }

    @PostMapping("/{accountNumber}/freeze")
    @Operation(summary = "Freeze account")
    public ResponseEntity<ApiResponse<Void>> freezeAccount(@PathVariable String accountNumber) {
        accountService.freezeAccount(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account frozen", null));
    }

    @PostMapping("/{accountNumber}/unfreeze")
    @Operation(summary = "Unfreeze account")
    public ResponseEntity<ApiResponse<Void>> unfreezeAccount(@PathVariable String accountNumber) {
        accountService.unfreezeAccount(accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account unfrozen", null));
    }
}
