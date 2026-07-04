package com.digitalbanking.dto.payment;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1")
    private BigDecimal amount;

    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryIfsc;
    private String upiId;
    private String description;

    private Boolean scheduled;
    private String scheduledDate;
    private Boolean recurring;
    private String recurringFrequency;

    private String idempotencyKey;
}
