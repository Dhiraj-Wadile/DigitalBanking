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
public class AccountCreatedEvent extends BaseEvent {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private Long customerId;
    private String customerNumber;
    private BigDecimal initialBalance;
}
