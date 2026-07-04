package com.digitalbanking.mapper;

import com.digitalbanking.entity.transaction.Transaction;
import com.digitalbanking.dto.transaction.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse transactionToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setTransactionType(transaction.getTransactionType().name());
        response.setStatus(transaction.getStatus().name());
        response.setAmount(transaction.getAmount());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setDescription(transaction.getDescription());
        response.setChannel(transaction.getChannel());
        response.setCounterpartyName(transaction.getCounterpartyName());
        response.setCounterpartyAccount(transaction.getCounterpartyAccount());
        response.setTransactionDate(transaction.getTransactionDate());
        if (transaction.getAccount() != null) {
            response.setAccountNumber(transaction.getAccount().getAccountNumber());
        }
        return response;
    }
}
