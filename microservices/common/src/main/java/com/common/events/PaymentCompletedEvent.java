package com.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedEvent extends BaseEvent {
    private String paymentReference;
    private Long accountId;
    private String accountNumber;
    private String paymentMethod;
    private java.math.BigDecimal amount;
    private String status;
    private String bankTransactionId;
}
