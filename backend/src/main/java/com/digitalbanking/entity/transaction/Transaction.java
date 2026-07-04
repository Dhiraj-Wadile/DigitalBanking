package com.digitalbanking.entity.transaction;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_txn_reference", columnList = "referenceNumber", unique = true),
    @Index(name = "idx_txn_account", columnList = "account_id"),
    @Index(name = "idx_txn_date", columnList = "transactionDate"),
    @Index(name = "idx_txn_type", columnList = "transactionType")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String channel;

    @Column(length = 200)
    private String counterpartyName;

    @Column(length = 20)
    private String counterpartyAccount;

    @Column(length = 20)
    private String counterpartyIfsc;

    @Column(length = 50)
    private String upiId;

    @Column(length = 500)
    private String metadata;

    private LocalDateTime transactionDate;

    private LocalDateTime valueDate;

    @Column(length = 500)
    private String failureReason;

    @Column(length = 50)
    private String idempotencyKey;

    private Integer retryCount = 0;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT,
        UPI_PAY, UPI_RECEIVE, NEFT, RTGS, IMPS,
        WALLET_LOAD, WALLET_UNLOAD,
        INTEREST_CREDIT, FEE_DEBIT, TAX_DEBIT,
        REFUND, REVERSAL, CHARGEBACK,
        LOAN_DISBURSEMENT, LOAN_REPAYMENT,
        CARD_PAYMENT, CARD_REFUND,
        EMI_DEBIT, FIXED_DEPOSIT_CREATION, FIXED_DEPOSIT_CLOSURE,
        RECURRING_DEPOSIT_CREATION, RECURRING_DEPOSIT_CLOSURE,
        REWARD_CREDIT, CASHBACK_CREDIT
    }

    public enum TransactionStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REVERSED, ON_HOLD
    }
}
