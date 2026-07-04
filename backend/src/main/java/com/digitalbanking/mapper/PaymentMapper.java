package com.digitalbanking.mapper;

import com.digitalbanking.entity.payment.Payment;
import com.digitalbanking.dto.payment.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse paymentToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentReference(payment.getPaymentReference());
        response.setPaymentType(payment.getPaymentType().name());
        response.setPaymentMethod(payment.getPaymentMethod().name());
        response.setStatus(payment.getStatus().name());
        response.setAmount(payment.getAmount());
        response.setFee(payment.getFee());
        response.setTax(payment.getTax());
        response.setDescription(payment.getDescription());
        response.setBeneficiaryName(payment.getBeneficiaryName());
        response.setBeneficiaryAccount(payment.getBeneficiaryAccount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setCompletionDate(payment.getCompletionDate());
        if (payment.getAccount() != null) {
            response.setAccountNumber(payment.getAccount().getAccountNumber());
        }
        return response;
    }
}
