package com.digitalbanking.entity.card;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards", indexes = {
    @Index(name = "idx_card_number", columnList = "cardNumber", unique = true),
    @Index(name = "idx_card_account", columnList = "account_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Card extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardNetwork cardNetwork;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status = CardStatus.ACTIVE;

    @Column(nullable = false, length = 100)
    private String cardHolderName;

    @Column(nullable = false, length = 4)
    private String expiryMonth;

    @Column(nullable = false, length = 4)
    private String expiryYear;

    @Column(nullable = false)
    private String cvvHash;

    @Column(nullable = false)
    private String pinHash;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal dailyLimit = new BigDecimal("100000");

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal monthlyLimit = new BigDecimal("1000000");

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal singleTransactionLimit = new BigDecimal("50000");

    @Column(nullable = false)
    private Boolean internationalEnabled = false;

    @Column(nullable = false)
    private Boolean onlineEnabled = true;

    @Column(nullable = false)
    private Boolean tapToPayEnabled = true;

    @Column(nullable = false)
    private Boolean atmEnabled = true;

    @Column(nullable = false)
    private Boolean posEnabled = true;

    @Column(nullable = false)
    private Boolean contactlessEnabled = true;

    @Column(nullable = false)
    private Boolean isVirtual = false;

    private LocalDate issuedDate;

    private LocalDate blockedDate;

    private Integer failedPinAttempts = 0;

    @Column(nullable = false)
    private Boolean pinSet = false;

    public enum CardType {
        DEBIT, CREDIT, PREPAID, VIRTUAL
    }

    public enum CardNetwork {
        VISA, MASTERCARD, RUPAY, AMEX
    }

    public enum CardStatus {
        ACTIVE, BLOCKED, EXPIRED, DAMAGED, REISSUED, PENDING_ACTIVATION
    }
}
