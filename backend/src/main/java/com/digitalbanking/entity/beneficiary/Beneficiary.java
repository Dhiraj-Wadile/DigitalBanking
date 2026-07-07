package com.digitalbanking.entity.beneficiary;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beneficiaries", indexes = {
    @Index(name = "idx_beneficiary_customer", columnList = "customer_id"),
    @Index(name = "idx_beneficiary_account", columnList = "account_number")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Beneficiary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String accountNumber;

    @Column(nullable = false, length = 10)
    private String ifscCode;

    @Column(length = 200)
    private String bankName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private BeneficiaryType type = BeneficiaryType.BANK_TRANSFER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(length = 50)
    private String upiId;

    @Builder.Default
    private Integer transferCount = 0;

    @Builder.Default
    private Long totalTransferred = 0L;

    public enum BeneficiaryType {
        BANK_TRANSFER, UPI, WALLET, INTERNATIONAL
    }
}
