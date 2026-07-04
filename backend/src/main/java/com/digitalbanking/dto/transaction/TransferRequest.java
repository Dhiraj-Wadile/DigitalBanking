package com.digitalbanking.dto.transaction;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "Source account is required")
    private String fromAccountNumber;

    @NotBlank(message = "Destination account is required")
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1")
    private BigDecimal amount;

    @NotBlank(message = "Transfer type is required")
    private String transferType;

    private String description;

    private String ifscCode;

    private String idempotencyKey;
}
