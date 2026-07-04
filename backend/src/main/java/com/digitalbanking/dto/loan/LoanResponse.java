package com.digitalbanking.dto.loan;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanResponse {
    private Long id;
    private String loanAccountNumber;
    private String loanType;
    private String status;
    private BigDecimal sanctionedAmount;
    private BigDecimal outstandingAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal processingFee;
    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate disbursementDate;
    private Integer emisPaid;
    private Integer emisRemaining;
    private String accountNumber;
}
