package com.digitalbanking.controller.payment;

import com.digitalbanking.dto.common.ApiResponse;
import com.digitalbanking.dto.common.PagedResponse;
import com.digitalbanking.dto.payment.*;
import com.digitalbanking.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process payment")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed", response));
    }

    @GetMapping
    @Operation(summary = "Get my payments")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponse>>> getMyPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PaymentResponse> payments = paymentService.getMyPayments(page, size);
        PagedResponse<PaymentResponse> pagedResponse = PagedResponse.of(
                payments.getContent(), page, size, payments.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{reference}")
    @Operation(summary = "Get payment by reference")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String reference) {
        PaymentResponse response = paymentService.getPaymentByReference(reference);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
