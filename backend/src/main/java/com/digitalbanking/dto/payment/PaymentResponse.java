package com.digitalbanking.dto.payment;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private String paymentReference;
    private String paymentType;
    private String paymentMethod;
    private String status;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal tax;
    private String description;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private LocalDateTime paymentDate;
    private LocalDateTime completionDate;
    private String accountNumber;
}
