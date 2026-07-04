package com.digitalbanking.service.account;

import com.digitalbanking.dto.account.*;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.account.Branch;
import com.digitalbanking.entity.customer.Customer;
import com.digitalbanking.exception.BadRequestException;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.mapper.AccountMapper;
import com.digitalbanking.repository.AccountRepository;
import com.digitalbanking.repository.BranchRepository;
import com.digitalbanking.repository.CustomerRepository;
import com.digitalbanking.security.SecurityUtils;
import com.digitalbanking.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final AccountMapper accountMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUser(
                userRepository().findById(userId).orElseThrow())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Branch branch = null;
        if (request.getBranchId() != null) {
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        }

        Account account = Account.builder()
                .accountNumber(ReferenceGenerator.generateAccountNumber())
                .customer(customer)
                .branch(branch)
                .accountType(Account.AccountType.valueOf(request.getAccountType()))
                .status(Account.AccountStatus.ACTIVE)
                .balance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO)
                .availableBalance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO)
                .ifscCode("DBIN0001")
                .openedDate(LocalDate.now())
                .build();

        account = accountRepository.save(account);
        log.info("Account created: {} for customer: {}", account.getAccountNumber(), customer.getCustomerNumber());
        return accountMapper.accountToResponse(account);
    }

    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return accountMapper.accountToResponse(account);
    }

    public List<AccountResponse> getMyAccounts() {
        Long userId = securityUtils.getCurrentUserId();
        Customer customer = customerRepository.findByUser(
                userRepository().findById(userId).orElseThrow())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return accountRepository.findByCustomer(customer).stream()
                .map(accountMapper::accountToResponse)
                .toList();
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return account.getBalance();
    }

    @Transactional
    public void freezeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setStatus(Account.AccountStatus.FROZEN);
        accountRepository.save(account);
        log.info("Account frozen: {}", accountNumber);
    }

    @Transactional
    public void unfreezeAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(account);
        log.info("Account unfrozen: {}", accountNumber);
    }

    public Account getAccountEntity(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }

    private com.digitalbanking.repository.UserRepository userRepository() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
