package com.digitalbanking.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionEvent {

    private Long transactionId;
    private Long accountId;
    private String transactionType;
    private String amount;
    private String status;
}
