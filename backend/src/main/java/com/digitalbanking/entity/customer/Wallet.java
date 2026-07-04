package com.digitalbanking.entity.customer;

import com.digitalbanking.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallets", indexes = {
    @Index(name = "idx_wallet_customer", columnList = "customer_id"),
    @Index(name = "idx_wallet_number", columnList = "walletNumber", unique = true)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wallet extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String walletNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 100)
    private String walletName;

    @Column(nullable = false, precision = 18, scale = 2)
    private java.math.BigDecimal balance = java.math.BigDecimal.ZERO;

    @Column(nullable = false, precision = 18, scale = 2)
    private java.math.BigDecimal dailyLimit = new java.math.BigDecimal("100000");

    @Column(nullable = false, precision = 18, scale = 2)
    private java.math.BigDecimal monthlyLimit = new java.math.BigDecimal("500000");

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WalletStatus status = WalletStatus.ACTIVE;

    @Column(length = 50)
    private String upiId;

    public enum WalletStatus {
        ACTIVE, INACTIVE, SUSPENDED, CLOSED
    }
}
