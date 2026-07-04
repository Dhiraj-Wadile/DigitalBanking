package com.digitalbanking.dto.customer;

import lombok.Data;

@Data
public class ChangePinRequest {
    private String currentPin;
    private String newPin;
    private String confirmPin;
}
