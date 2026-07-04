package com.digitalbanking.entity.loan;

import com.digitalbanking.entity.base.BaseEntity;
import com.digitalbanking.entity.account.Account;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans", indexes = {
    @Index(name = "idx_loan_account", columnList = "account_id"),
    @Index(name = "idx_loan_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Loan extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String loanAccountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING_APPROVAL;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal sanctionedAmount;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal disbursedAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal emiAmount;

    @Column(nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal processingFee = BigDecimal.ZERO;

    private LocalDate applicationDate;

    private LocalDate approvalDate;

    private LocalDate disbursementDate;

    private LocalDate firstEmiDate;

    private LocalDate closureDate;

    @Builder.Default
    private Integer emisPaid = 0;

    private Integer emisRemaining;

    @Column(precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal totalInterestPaid = BigDecimal.ZERO;

    @Column(length = 500)
    private String rejectionReason;

    public enum LoanType {
        PERSONAL, HOME, CAR, EDUCATION, BUSINESS, GOLD, LAP
    }

    public enum LoanStatus {
        PENDING_APPROVAL, APPROVED, DISBURSED, ACTIVE,
        OVERDUE, CLOSED, WRITTEN_OFF, RESTRUCTURED
    }
}
