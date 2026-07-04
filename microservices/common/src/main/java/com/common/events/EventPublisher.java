package com.common.events;

import com.common.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Publishing TransactionCompletedEvent: {}", event.getReferenceNumber());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRANSACTION_EXCHANGE,
                RabbitMQConfig.TXN_COMPLETED_KEY,
                event
        );
    }

    public void publishPaymentInitiated(PaymentInitiatedEvent event) {
        log.info("Publishing PaymentInitiatedEvent: {}", event.getPaymentReference());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_INITIATED_KEY,
                event
        );
    }

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Publishing PaymentCompletedEvent: {}", event.getPaymentReference());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_COMPLETED_KEY,
                event
        );
    }

    public void publishFraudDetected(FraudDetectedEvent event) {
        log.warn("Publishing FraudDetectedEvent: {} for account {}", event.getReason(), event.getAccountNumber());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRANSACTION_EXCHANGE,
                RabbitMQConfig.FRAUD_DETECTED_KEY,
                event
        );
    }

    public void publishNotification(NotificationEvent event) {
        log.info("Publishing NotificationEvent: {} to user {}", event.getTitle(), event.getUserId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_KEY,
                event
        );
    }

    public void publishAuditLog(String entityType, String entityId, String action, String details) {
        log.info("Publishing AuditLog: {} on {} {}", action, entityType, entityId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BANKING_EXCHANGE,
                RabbitMQConfig.AUDIT_KEY,
                new AuditEvent(entityType, entityId, action, details)
        );
    }
}
