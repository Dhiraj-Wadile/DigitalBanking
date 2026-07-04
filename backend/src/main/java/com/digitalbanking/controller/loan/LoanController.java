package com.digitalbanking.controller.loan;

import com.digitalbanking.dto.common.ApiResponse;
import com.digitalbanking.dto.common.PagedResponse;
import com.digitalbanking.dto.loan.*;
import com.digitalbanking.service.loan.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Loan management endpoints")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    @Operation(summary = "Apply for loan")
    public ResponseEntity<ApiResponse<LoanResponse>> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        LoanResponse response = loanService.applyForLoan(request);
        return ResponseEntity.ok(ApiResponse.success("Loan application submitted", response));
    }

    @GetMapping
    @Operation(summary = "Get my loans")
    public ResponseEntity<ApiResponse<PagedResponse<LoanResponse>>> getMyLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LoanResponse> loans = loanService.getMyLoans(page, size);
        PagedResponse<LoanResponse> pagedResponse = PagedResponse.of(
                loans.getContent(), page, size, loans.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{loanNumber}")
    @Operation(summary = "Get loan by number")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoan(@PathVariable String loanNumber) {
        LoanResponse response = loanService.getLoanByNumber(loanNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
