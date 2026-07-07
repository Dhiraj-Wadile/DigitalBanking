package com.digitalbanking.controller.transaction;

import com.digitalbanking.dto.common.ApiResponse;
import com.digitalbanking.dto.common.PagedResponse;
import com.digitalbanking.dto.transaction.*;
import com.digitalbanking.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "Get my transactions")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionResponse> transactions = transactionService.getMyTransactions(page, size);
        PagedResponse<TransactionResponse> pagedResponse = PagedResponse.of(
                transactions.getContent(), page, size, transactions.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between accounts")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.ok(ApiResponse.success("Transfer successful", response));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit amount to account")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        TransactionResponse response = transactionService.deposit(accountNumber, amount, description);
        return ResponseEntity.ok(ApiResponse.success("Deposit successful", response));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw amount from account")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        TransactionResponse response = transactionService.withdraw(accountNumber, amount, description);
        return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", response));
    }

    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get account transactions")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponse>>> getAccountTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionResponse> transactions = transactionService.getAccountTransactions(accountNumber, page, size);
        PagedResponse<TransactionResponse> pagedResponse = PagedResponse.of(
                transactions.getContent(), page, size, transactions.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{referenceNumber}")
    @Operation(summary = "Get transaction by reference")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(@PathVariable String referenceNumber) {
        TransactionResponse response = transactionService.getTransactionByReference(referenceNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
