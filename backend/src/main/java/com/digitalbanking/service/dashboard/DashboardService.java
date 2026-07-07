package com.digitalbanking.service.dashboard;

import com.digitalbanking.dto.dashboard.DashboardResponse;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.auth.User;
import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.entity.payment.Payment;
import com.digitalbanking.entity.transaction.Transaction;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.repository.AccountRepository;
import com.digitalbanking.repository.CustomerRepository;
import com.digitalbanking.repository.PaymentRepository;
import com.digitalbanking.repository.TransactionRepository;
import com.digitalbanking.repository.UserRepository;
import com.digitalbanking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DashboardResponse getDashboard() {
        Long userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Account> accounts;
        if (user.getRole() == User.UserRole.ROLE_CUSTOMER) {
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            accounts = accountRepository.findByCustomer(customer);
        } else {
            accounts = accountRepository.findAll();
        }

        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardResponse response = new DashboardResponse();
        response.setTotalBalance(totalBalance);
        response.setTotalAccounts(accounts.size());

        List<DashboardResponse.AccountSummary> accountSummaries = new ArrayList<>();
        for (Account account : accounts) {
            DashboardResponse.AccountSummary summary = new DashboardResponse.AccountSummary();
            summary.setId(account.getId());
            summary.setAccountNumber(account.getAccountNumber());
            summary.setAccountType(account.getAccountType().name());
            summary.setBalance(account.getBalance());
            summary.setStatus(account.getStatus().name());
            accountSummaries.add(summary);
        }
        response.setAccounts(accountSummaries);

        List<DashboardResponse.RecentTransaction> allTransactions = new ArrayList<>();
        BigDecimal totalDeposits = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        int totalTransactionCount = 0;

        for (Account account : accounts) {
            Page<Transaction> txns = transactionRepository.findByAccountOrderByTransactionDateDesc(
                    account, PageRequest.of(0, 5));
            for (Transaction t : txns.getContent()) {
                DashboardResponse.RecentTransaction rt = new DashboardResponse.RecentTransaction();
                rt.setId(t.getId());
                rt.setReferenceNumber(t.getTransactionType().name());
                rt.setType(t.getTransactionType().name());
                rt.setAmount(t.getAmount());
                rt.setStatus(t.getStatus().name());
                rt.setDescription(t.getDescription() != null ? t.getDescription() : t.getTransactionType().name());
                rt.setDate(t.getTransactionDate() != null ? t.getTransactionDate().format(DATE_FMT) : "");
                allTransactions.add(rt);

                totalTransactionCount++;
                if (isDepositType(t.getTransactionType().name())) {
                    totalDeposits = totalDeposits.add(t.getAmount());
                } else if (isWithdrawalType(t.getTransactionType().name())) {
                    totalWithdrawals = totalWithdrawals.add(t.getAmount());
                }
            }
        }

        allTransactions.sort(Comparator.comparing(
                (DashboardResponse.RecentTransaction rt) -> rt.getDate()).reversed());
        if (allTransactions.size() > 10) {
            allTransactions = new ArrayList<>(allTransactions.subList(0, 10));
        }
        response.setRecentTransactions(allTransactions);
        response.setTotalTransactions(totalTransactionCount);
        response.setTotalDeposits(totalDeposits);
        response.setTotalWithdrawals(totalWithdrawals);

        List<DashboardResponse.PaymentSummary> pendingPayments = new ArrayList<>();
        List<Payment> allPending = paymentRepository.findByStatus(com.digitalbanking.entity.payment.Payment.PaymentStatus.PENDING);
        for (Payment p : allPending) {
            DashboardResponse.PaymentSummary ps = new DashboardResponse.PaymentSummary();
            ps.setId(p.getId());
            ps.setPaymentReference(p.getPaymentReference());
            ps.setType(p.getPaymentType().name());
            ps.setAmount(p.getAmount());
            ps.setStatus(p.getStatus().name());
            ps.setDate(p.getPaymentDate() != null ? p.getPaymentDate().format(DATE_FMT) : "");
            pendingPayments.add(ps);
        }
        response.setPendingPayments(pendingPayments);

        return response;
    }

    private boolean isDepositType(String type) {
        return "DEPOSIT".equals(type) || "TRANSFER_IN".equals(type)
                || "UPI_RECEIVE".equals(type) || "INTEREST_CREDIT".equals(type)
                || "REFUND".equals(type) || "REWARD_CREDIT".equals(type)
                || "CASHBACK_CREDIT".equals(type);
    }

    private boolean isWithdrawalType(String type) {
        return "WITHDRAWAL".equals(type) || "TRANSFER_OUT".equals(type)
                || "UPI_PAY".equals(type) || "FEE_DEBIT".equals(type)
                || "TAX_DEBIT".equals(type) || "CARD_PAYMENT".equals(type)
                || "LOAN_REPAYMENT".equals(type) || "EMI_DEBIT".equals(type);
    }
}
