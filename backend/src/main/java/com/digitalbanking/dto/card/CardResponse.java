package com.digitalbanking.dto.card;

import lombok.Data;

@Data
public class CardResponse {
    private Long id;
    private String cardNumber;
    private String cardType;
    private String cardNetwork;
    private String status;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private Boolean internationalEnabled;
    private Boolean onlineEnabled;
    private Boolean tapToPayEnabled;
    private Boolean isVirtual;
    private String accountNumber;
}
