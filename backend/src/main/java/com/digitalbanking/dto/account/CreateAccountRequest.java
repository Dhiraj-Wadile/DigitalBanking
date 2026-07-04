package com.digitalbanking.dto.account;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {

    @NotBlank(message = "Account type is required")
    private String accountType;

    private Long branchId;

    private BigDecimal initialDeposit;

    @NotBlank(message = "Currency is required")
    private String currency;
}
