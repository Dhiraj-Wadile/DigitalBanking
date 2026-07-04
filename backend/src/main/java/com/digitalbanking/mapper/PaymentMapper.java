package com.digitalbanking.mapper;

import com.digitalbanking.dto.payment.PaymentResponse;
import com.digitalbanking.entity.payment.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse paymentToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .accountNumber(payment.getAccount().getAccountNumber())
                .paymentType(payment.getPaymentType().name())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .amount(payment.getAmount())
                .fee(payment.getFee())
                .tax(payment.getTax())
                .description(payment.getDescription())
                .beneficiaryName(payment.getBeneficiaryName())
                .beneficiaryAccount(payment.getBeneficiaryAccount())
                .beneficiaryIfsc(payment.getBeneficiaryIfsc())
                .upiId(payment.getUpiId())
                .paymentDate(payment.getPaymentDate())
                .completionDate(payment.getCompletionDate())
                .build();
    }
}
