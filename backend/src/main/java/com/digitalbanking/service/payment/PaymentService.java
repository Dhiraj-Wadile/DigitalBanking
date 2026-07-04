package com.digitalbanking.service.payment;

import com.digitalbanking.dto.payment.*;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.entity.payment.Payment;
import com.digitalbanking.exception.BadRequestException;
import com.digitalbanking.exception.InsufficientBalanceException;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.mapper.PaymentMapper;
import com.digitalbanking.repository.AccountRepository;
import com.digitalbanking.repository.PaymentRepository;
import com.digitalbanking.service.audit.AuditService;
import com.digitalbanking.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final PaymentMapper paymentMapper;
    private final AuditService auditService;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for payment");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        account.setAvailableBalance(newBalance);
        accountRepository.save(account);

        String paymentReference = request.getIdempotencyKey() != null ?
                request.getIdempotencyKey() : ReferenceGenerator.generatePaymentReference();

        Payment payment = Payment.builder()
                .paymentReference(paymentReference)
                .account(account)
                .paymentType(Payment.PaymentType.valueOf(request.getPaymentType()))
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .status(Payment.PaymentStatus.COMPLETED)
                .amount(request.getAmount())
                .description(request.getDescription())
                .beneficiaryName(request.getBeneficiaryName())
                .beneficiaryAccount(request.getBeneficiaryAccount())
                .beneficiaryIfsc(request.getBeneficiaryIfsc())
                .upiId(request.getUpiId())
                .paymentDate(LocalDateTime.now())
                .completionDate(LocalDateTime.now())
                .scheduled(request.getScheduled() != null && request.getScheduled())
                .recurring(request.getRecurring() != null && request.getRecurring())
                .idempotencyKey(request.getIdempotencyKey())
                .fee(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .build();

        payment = paymentRepository.save(payment);

        auditService.logAction("PAYMENT", "Payment", payment.getId(),
                String.format("Payment of %s via %s", request.getAmount(), request.getPaymentMethod()));

        log.info("Payment processed: {} via {}", paymentReference, request.getPaymentMethod());
        return paymentMapper.paymentToResponse(payment);
    }

    public Page<PaymentResponse> getMyPayments(int page, int size) {
        return paymentRepository.findAll(PageRequest.of(page, size))
                .map(paymentMapper::paymentToResponse);
    }

    public PaymentResponse getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return paymentMapper.paymentToResponse(payment);
    }
}
