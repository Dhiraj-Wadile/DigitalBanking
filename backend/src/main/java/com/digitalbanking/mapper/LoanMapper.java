package com.digitalbanking.mapper;

import com.digitalbanking.entity.loan.Loan;
import com.digitalbanking.dto.loan.LoanResponse;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

    public LoanResponse loanToResponse(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setLoanAccountNumber(loan.getLoanAccountNumber());
        response.setLoanType(loan.getLoanType().name());
        response.setStatus(loan.getStatus().name());
        response.setSanctionedAmount(loan.getSanctionedAmount());
        response.setOutstandingAmount(loan.getOutstandingAmount());
        response.setInterestRate(loan.getInterestRate());
        response.setTenureMonths(loan.getTenureMonths());
        response.setEmiAmount(loan.getEmiAmount());
        response.setProcessingFee(loan.getProcessingFee());
        response.setApplicationDate(loan.getApplicationDate());
        response.setApprovalDate(loan.getApprovalDate());
        response.setDisbursementDate(loan.getDisbursementDate());
        response.setEmisPaid(loan.getEmisPaid());
        response.setEmisRemaining(loan.getEmisRemaining());
        if (loan.getAccount() != null) {
            response.setAccountNumber(loan.getAccount().getAccountNumber());
        }
        return response;
    }
}
