package com.digitalbanking.scheduler;

import com.digitalbanking.entity.payment.Payment;
import com.digitalbanking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {

    private final PaymentRepository paymentRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    public void processScheduledPayments() {
        List<Payment> scheduledPayments = paymentRepository.findScheduledPayments();
        log.info("Processing {} scheduled payments", scheduledPayments.size());
        // Process each scheduled payment
        for (Payment payment : scheduledPayments) {
            try {
                // Process payment logic here
                log.info("Processing payment: {}", payment.getPaymentReference());
            } catch (Exception e) {
                log.error("Failed to process payment: {}", payment.getPaymentReference(), e);
            }
        }
    }
}
