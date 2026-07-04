package com.digitalbanking.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "OTP is required")
    private String otpCode;

    @NotBlank(message = "Purpose is required")
    private String purpose;
}
