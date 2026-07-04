package com.digitalbanking.dto.dashboard;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardResponse {
    private BigDecimal totalBalance;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private Integer totalAccounts;
    private Integer totalTransactions;
    private List<AccountSummary> accounts;
    private List<RecentTransaction> recentTransactions;
    private List<PaymentSummary> pendingPayments;

    @Data
    public static class AccountSummary {
        private Long id;
        private String accountNumber;
        private String accountType;
        private BigDecimal balance;
        private String status;
    }

    @Data
    public static class RecentTransaction {
        private Long id;
        private String referenceNumber;
        private String type;
        private BigDecimal amount;
        private String status;
        private String description;
        private String date;
    }

    @Data
    public static class PaymentSummary {
        private Long id;
        private String paymentReference;
        private String type;
        private BigDecimal amount;
        private String status;
        private String date;
    }
}
