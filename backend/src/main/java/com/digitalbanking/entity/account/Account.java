package com.digitalbanking.entity.account;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts", indexes = {
    @Index(name = "idx_account_number", columnList = "accountNumber", unique = true),
    @Index(name = "idx_account_customer", columnList = "customer_id"),
    @Index(name = "idx_account_branch", columnList = "branch_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal holdAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal interestRate = BigDecimal.ZERO;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal dailyTransactionLimit = new BigDecimal("500000");

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal singleTransactionLimit = new BigDecimal("100000");

    @Column(nullable = false)
    @Builder.Default
    private Boolean dormant = false;

    private LocalDate lastTransactionDate;

    @Column(length = 10)
    private String ifscCode;

    @Column(length = 20)
    private String swiftCode;

    private LocalDate openedDate;

    private LocalDate closedDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean overdraftEnabled = false;

    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal overdraftLimit = BigDecimal.ZERO;

    public enum AccountType {
        SAVINGS, CURRENT, FIXED_DEPOSIT, RECURRING_DEPOSIT, WALLET, VIRTUAL
    }

    public enum AccountStatus {
        ACTIVE, INACTIVE, FROZEN, DORMANT, CLOSED, PENDING_APPROVAL
    }
}
