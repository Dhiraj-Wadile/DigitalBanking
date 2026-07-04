package com.digitalbanking.entity.customer;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customer_user", columnList = "user_id"),
    @Index(name = "idx_customer_pan", columnList = "pan_number"),
    @Index(name = "idx_customer_aadhaar", columnList = "aadhaar_number")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 20)
    private String customerNumber;

    @Column(length = 20)
    private String panNumber;

    @Column(length = 20)
    private String aadhaarNumber;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(length = 200)
    private String addressLine1;

    @Column(length = 200)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private KycStatus kycStatus = KycStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @Column(length = 200)
    private String occupation;

    private Long annualIncome;

    @Column(length = 500)
    private String documentUrl;

    @Column(length = 500)
    private String profileImageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean nomineeAdded = false;

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public enum KycStatus {
        PENDING, VERIFIED, REJECTED, EXPIRED
    }

    public enum CustomerStatus {
        ACTIVE, INACTIVE, SUSPENDED, DORMANT
    }
}
