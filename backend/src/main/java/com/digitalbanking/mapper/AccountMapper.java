package com.digitalbanking.mapper;

import com.digitalbanking.entity.account.Account;
import com.digitalbanking.dto.account.AccountResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse accountToResponse(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setStatus(account.getStatus().name());
        response.setBalance(account.getBalance());
        response.setAvailableBalance(account.getAvailableBalance());
        response.setHoldAmount(account.getHoldAmount());
        response.setInterestRate(account.getInterestRate());
        response.setDailyTransactionLimit(account.getDailyTransactionLimit());
        response.setSingleTransactionLimit(account.getSingleTransactionLimit());
        response.setIfscCode(account.getIfscCode());
        response.setOpenedDate(account.getOpenedDate());
        if (account.getBranch() != null) {
            response.setBranchName(account.getBranch().getBranchName());
        }
        return response;
    }
}
