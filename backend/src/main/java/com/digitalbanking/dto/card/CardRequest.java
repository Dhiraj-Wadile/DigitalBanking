package com.digitalbanking.dto.card;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CardRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Card type is required")
    private String cardType;

    @NotBlank(message = "Card network is required")
    private String cardNetwork;

    private Boolean isVirtual;
}
