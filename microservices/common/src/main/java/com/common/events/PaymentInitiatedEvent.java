package com.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PaymentInitiatedEvent extends BaseEvent {
    private String paymentReference;
    private Long accountId;
    private String accountNumber;
    private String paymentType;
    private String paymentMethod;
    private BigDecimal amount;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryIfsc;
    private String upiId;
    private Long customerId;
}
