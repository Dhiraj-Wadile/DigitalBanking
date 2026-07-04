package com.digitalbanking.dto.loan;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {

    @NotBlank(message = "Loan type is required")
    private String loanType;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10000", message = "Minimum loan amount is 10000")
    private BigDecimal requestedAmount;

    @NotNull(message = "Tenure is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    private Integer tenureMonths;

    private BigDecimal interestRate;
    private String purpose;
    private String employmentType;
    private Long annualIncome;
}
