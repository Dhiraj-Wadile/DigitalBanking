package com.digitalbanking.service.transaction;

import com.digitalbanking.dto.transaction.*;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.transaction.Transaction;
import com.digitalbanking.exception.BadRequestException;
import com.digitalbanking.exception.InsufficientBalanceException;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.mapper.TransactionMapper;
import com.digitalbanking.repository.AccountRepository;
import com.digitalbanking.repository.TransactionRepository;
import com.digitalbanking.security.SecurityUtils;
import com.digitalbanking.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BadRequestException("Source account is not active");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal newFromBalance = fromAccount.getBalance().subtract(request.getAmount());
        BigDecimal newToBalance = toAccount.getBalance().add(request.getAmount());

        fromAccount.setBalance(newFromBalance);
        fromAccount.setAvailableBalance(newFromBalance);
        toAccount.setBalance(newToBalance);
        toAccount.setAvailableBalance(newToBalance);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        String referenceNumber = request.getIdempotencyKey() != null ?
                request.getIdempotencyKey() : ReferenceGenerator.generateTransactionReference();

        Transaction debitTxn = Transaction.builder()
                .referenceNumber(referenceNumber)
                .account(fromAccount)
                .transactionType(Transaction.TransactionType.TRANSFER_OUT)
                .status(Transaction.TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .balanceAfter(newFromBalance)
                .description(request.getDescription())
                .counterpartyName(toAccount.getCustomer().getUser().getFullName())
                .counterpartyAccount(request.getToAccountNumber())
                .transactionDate(LocalDateTime.now())
                .channel("INTERNAL_TRANSFER")
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        transactionRepository.save(debitTxn);

        String creditRef = ReferenceGenerator.generateTransactionReference();
        Transaction creditTxn = Transaction.builder()
                .referenceNumber(creditRef)
                .account(toAccount)
                .transactionType(Transaction.TransactionType.TRANSFER_IN)
                .status(Transaction.TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .balanceAfter(newToBalance)
                .description(request.getDescription())
                .counterpartyName(fromAccount.getCustomer().getUser().getFullName())
                .counterpartyAccount(request.getFromAccountNumber())
                .transactionDate(LocalDateTime.now())
                .channel("INTERNAL_TRANSFER")
                .build();

        transactionRepository.save(creditTxn);

        log.info("Transfer completed: {} from {} to {}", request.getAmount(), request.getFromAccountNumber(), request.getToAccountNumber());
        return transactionMapper.transactionToResponse(debitTxn);
    }

    @Transactional
    public TransactionResponse deposit(String accountNumber, BigDecimal amount, String description) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        account.setAvailableBalance(newBalance);
        accountRepository.save(account);

        String referenceNumber = ReferenceGenerator.generateTransactionReference();
        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .account(account)
                .transactionType(Transaction.TransactionType.DEPOSIT)
                .status(Transaction.TransactionStatus.COMPLETED)
                .amount(amount)
                .balanceAfter(newBalance)
                .description(description != null ? description : "Deposit")
                .transactionDate(LocalDateTime.now())
                .channel("CASH_DEPOSIT")
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Deposit completed: {} to account: {}", amount, accountNumber);
        return transactionMapper.transactionToResponse(transaction);
    }

    @Transactional
    public TransactionResponse withdraw(String accountNumber, BigDecimal amount, String description) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        account.setAvailableBalance(newBalance);
        accountRepository.save(account);

        String referenceNumber = ReferenceGenerator.generateTransactionReference();
        Transaction transaction = Transaction.builder()
                .referenceNumber(referenceNumber)
                .account(account)
                .transactionType(Transaction.TransactionType.WITHDRAWAL)
                .status(Transaction.TransactionStatus.COMPLETED)
                .amount(amount)
                .balanceAfter(newBalance)
                .description(description != null ? description : "Withdrawal")
                .transactionDate(LocalDateTime.now())
                .channel("ATM_WITHDRAWAL")
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Withdrawal completed: {} from account: {}", amount, accountNumber);
        return transactionMapper.transactionToResponse(transaction);
    }

    public Page<TransactionResponse> getAccountTransactions(String accountNumber, int page, int size) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        return transactionRepository.findByAccountOrderByTransactionDateDesc(account, PageRequest.of(page, size))
                .map(transactionMapper::transactionToResponse);
    }

    public TransactionResponse getTransactionByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return transactionMapper.transactionToResponse(transaction);
    }
}
