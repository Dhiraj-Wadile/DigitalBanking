package com.digitalbanking.service.dashboard;

import com.digitalbanking.dto.dashboard.DashboardResponse;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.auth.User;
import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.repository.AccountRepository;
import com.digitalbanking.repository.CustomerRepository;
import com.digitalbanking.repository.TransactionRepository;
import com.digitalbanking.repository.UserRepository;
import com.digitalbanking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

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
        response.setRecentTransactions(new ArrayList<>());
        response.setPendingPayments(new ArrayList<>());

        return response;
    }
}
