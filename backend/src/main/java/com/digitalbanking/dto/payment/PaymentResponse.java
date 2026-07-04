package com.digitalbanking.dto.payment;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {

    private Long id;
    private String paymentReference;
    private String accountNumber;
    private String paymentType;
    private String paymentMethod;
    private String status;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal tax;
    private String description;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryIfsc;
    private String upiId;
    private LocalDateTime paymentDate;
    private LocalDateTime completionDate;
}
