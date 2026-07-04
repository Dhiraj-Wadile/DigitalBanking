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
public class FraudDetectedEvent extends BaseEvent {
    private Long accountId;
    private String accountNumber;
    private Long customerId;
    private String reason;
    private Integer riskScore;
    private java.math.BigDecimal amount;
    private String transactionType;
}
