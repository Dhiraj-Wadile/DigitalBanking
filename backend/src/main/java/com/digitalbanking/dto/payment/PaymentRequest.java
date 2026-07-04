package com.digitalbanking.dto.payment;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String description;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryIfsc;
    private String upiId;
    private Boolean scheduled;
    private Boolean recurring;
    private String idempotencyKey;
}
