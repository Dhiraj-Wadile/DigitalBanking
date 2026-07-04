package com.digitalbanking.dto.account;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String accountType;
    private String status;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private BigDecimal holdAmount;
    private BigDecimal interestRate;
    private BigDecimal dailyTransactionLimit;
    private BigDecimal singleTransactionLimit;
    private String ifscCode;
    private LocalDate openedDate;
    private String branchName;
    private String currency;
}
