package com.digitalbanking.entity.payment;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_reference", columnList = "payment_reference", unique = true),
    @Index(name = "idx_payment_account", columnList = "account_id"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_date", columnList = "payment_date")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String paymentReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String beneficiaryName;

    @Column(length = 20)
    private String beneficiaryAccount;

    @Column(length = 10)
    private String beneficiaryIfsc;

    @Column(length = 50)
    private String upiId;

    @Column(length = 500)
    private String upiTransactionId;

    @Column(length = 500)
    private String bankTransactionId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean scheduled = false;

    private LocalDateTime scheduledDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean recurring = false;

    @Column(length = 30)
    private String recurringFrequency;

    @Builder.Default
    private Integer retryCount = 0;

    @Builder.Default
    private Integer maxRetries = 3;

    @Column(length = 200)
    private String failureReason;

    @Column(length = 50)
    private String idempotencyKey;

    private LocalDateTime paymentDate;

    private LocalDateTime completionDate;

    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    public enum PaymentType {
        P2P, P2M, BILL_PAYMENT, RECHARGE, LOAN_EMI,
        SUBSCRIPTION, MERCHANT, REQUEST_MONEY, SPLIT_BILL
    }

    public enum PaymentMethod {
        UPI, NEFT, RTGS, IMPS, WALLET, CARD, NET_BANKING, QR_CODE
    }

    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED,
        REFUNDED, PARTIALLY_REFUNDED, ON_HOLD, SCHEDULED
    }
}
