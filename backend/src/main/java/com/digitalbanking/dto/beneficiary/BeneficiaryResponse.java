package com.digitalbanking.dto.beneficiary;

import lombok.Data;

@Data
public class BeneficiaryResponse {
    private Long id;
    private String name;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String phone;
    private String type;
    private Boolean verified;
    private Boolean active;
    private String upiId;
}
