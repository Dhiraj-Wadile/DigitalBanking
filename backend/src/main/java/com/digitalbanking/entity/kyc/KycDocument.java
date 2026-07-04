package com.digitalbanking.entity.kyc;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kyc_documents", indexes = {
    @Index(name = "idx_kyc_customer", columnList = "customer_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KycDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DocumentType documentType;

    @Column(nullable = false, length = 500)
    private String documentUrl;

    @Column(nullable = false, length = 20)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus status = VerificationStatus.PENDING;

    @Column(length = 500)
    private String verifiedBy;

    private java.time.LocalDateTime verifiedAt;

    @Column(length = 500)
    private String rejectionReason;

    public enum DocumentType {
        PAN_CARD, AADHAAR_CARD, PASSPORT, DRIVING_LICENSE,
        VOTER_ID, ADDRESS_PROOF, INCOME_PROOF, PHOTO
    }

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED, EXPIRED
    }
}
