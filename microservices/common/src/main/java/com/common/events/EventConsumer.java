package com.common.events;

import com.common.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    @RabbitListener(queues = RabbitMQConfig.TRANSACTION_COMPLETED_QUEUE)
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Received TransactionCompletedEvent: {} - Type: {} - Amount: {}",
                event.getReferenceNumber(), event.getTransactionType(), event.getAmount());
        // Trigger downstream processes:
        // 1. Send notification to customer
        // 2. Update fraud detection scores
        // 3. Log to audit trail
        // 4. Update analytics/reports
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_INITIATED_QUEUE)
    public void handlePaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Received PaymentInitiatedEvent: {} - Method: {} - Amount: {}",
                event.getPaymentReference(), event.getPaymentMethod(), event.getAmount());
        // Process payment based on method:
        // UPI -> UPI switch
        // NEFT -> NEFT batch
        // RTGS -> Real-time gross settlement
        // IMPS -> Immediate payment
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent: {} - Status: {}",
                event.getPaymentReference(), event.getStatus());
        // Settlement and reconciliation
    }

    @RabbitListener(queues = RabbitMQConfig.FRAUD_DETECTED_QUEUE)
    public void handleFraudDetected(FraudDetectedEvent event) {
        log.warn("FRAUD DETECTED: {} - Account: {} - Score: {} - Reason: {}",
                event.getEventId(), event.getAccountNumber(), event.getRiskScore(), event.getReason());
        // Actions:
        // 1. Block the transaction
        // 2. Send alert to fraud team
        // 3. Notify customer
        // 4. Create investigation case
        // 5. Update risk score
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationEvent event) {
        log.info("Processing notification: {} - Channel: {}", event.getTitle(), event.getChannel());
        // Send via appropriate channel:
        // EMAIL -> SendGrid/SES
        // SMS -> Twilio/AWS SNS
        // PUSH -> Firebase
        // IN_APP -> WebSocket
    }

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void handleAuditLog(AuditEvent event) {
        log.info("Audit Log: {} on {} {}", event.getAction(), event.getEntityType(), event.getEntityId());
        // Store in audit_logs table
        // Send to Elasticsearch for search
        // Forward to compliance system
    }

    @RabbitListener(queues = RabbitMQConfig.SETTLEMENT_QUEUE)
    public void handleSettlement(PaymentCompletedEvent event) {
        log.info("Processing settlement for payment: {}", event.getPaymentReference());
        // Settlement logic:
        // 1. Match with bank statements
        // 2. Reconcile with clearinghouse
        // 3. Generate settlement files
        // 4. Update settlement status
    }
}
