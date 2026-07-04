package com.digitalbanking.dto.beneficiary;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BeneficiaryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Account number is required")
    @Size(min = 10, max = 20)
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    @Size(min = 11, max = 11)
    private String ifscCode;

    private String bankName;
    private String phone;
    private String email;
    private String upiId;
    private String type;
}
