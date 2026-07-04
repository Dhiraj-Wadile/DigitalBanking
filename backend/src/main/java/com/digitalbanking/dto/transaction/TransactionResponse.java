package com.digitalbanking.dto.transaction;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private String referenceNumber;
    private String transactionType;
    private String status;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String channel;
    private String counterpartyName;
    private String counterpartyAccount;
    private LocalDateTime transactionDate;
    private String accountNumber;
}
