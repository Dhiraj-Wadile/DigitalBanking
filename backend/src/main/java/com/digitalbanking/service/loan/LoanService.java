package com.digitalbanking.service.loan;

import com.digitalbanking.dto.loan.*;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.loan.Loan;
import com.digitalbanking.exception.BadRequestException;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.mapper.LoanMapper;
import com.digitalbanking.repository.AccountRepository;
import com.digitalbanking.repository.LoanRepository;
import com.digitalbanking.service.audit.AuditService;
import com.digitalbanking.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanMapper loanMapper;
    private final AuditService auditService;

    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        BigDecimal interestRate = request.getInterestRate() != null ?
                request.getInterestRate() : getDefaultInterestRate(request.getLoanType());

        BigDecimal emi = calculateEMI(request.getRequestedAmount(), interestRate, request.getTenureMonths());

        Loan loan = Loan.builder()
                .loanAccountNumber(ReferenceGenerator.generateLoanAccountNumber())
                .account(account)
                .loanType(Loan.LoanType.valueOf(request.getLoanType()))
                .status(Loan.LoanStatus.PENDING_APPROVAL)
                .sanctionedAmount(request.getRequestedAmount())
                .outstandingAmount(request.getRequestedAmount())
                .interestRate(interestRate)
                .tenureMonths(request.getTenureMonths())
                .emiAmount(emi)
                .applicationDate(LocalDate.now())
                .emisRemaining(request.getTenureMonths())
                .build();

        loan = loanRepository.save(loan);
        auditService.logAction("LOAN_APPLICATION", "Loan", loan.getId(),
                "Loan application for " + request.getRequestedAmount());
        log.info("Loan application submitted: {}", loan.getLoanAccountNumber());
        return loanMapper.loanToResponse(loan);
    }

    public Page<LoanResponse> getMyLoans(int page, int size) {
        return loanRepository.findAll(PageRequest.of(page, size))
                .map(loanMapper::loanToResponse);
    }

    public LoanResponse getLoanByNumber(String loanNumber) {
        Loan loan = loanRepository.findByLoanAccountNumber(loanNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        return loanMapper.loanToResponse(loan);
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal annualRate, int months) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        BigDecimal ratePlusOne = monthlyRate.add(BigDecimal.ONE);
        BigDecimal power = ratePlusOne.pow(months);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getDefaultInterestRate(String loanType) {
        return switch (loanType) {
            case "PERSONAL" -> new BigDecimal("12.00");
            case "HOME" -> new BigDecimal("8.50");
            case "CAR" -> new BigDecimal("9.00");
            case "EDUCATION" -> new BigDecimal("7.50");
            default -> new BigDecimal("10.00");
        };
    }
}
