package com.common.events;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Saga Pattern - Manages distributed transactions across microservices
 *
 * Flow for a complex payment:
 * 1. PaymentService: Validate payment request
 * 2. AccountService: Debit source account
 * 3. TransactionService: Record transaction
 * 4. FraudService: Check for fraud
 * 5. PaymentService: Process with bank/UPI
 * 6. NotificationService: Send confirmation
 *
 * If any step fails, compensating transactions are executed in reverse order.
 */
@Data
public class PaymentSaga {
    private String sagaId;
    private String paymentReference;
    private SagaStatus status;
    private Integer currentStep;
    private Integer totalSteps;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String failureReason;
    private SagaStep[] steps;

    public enum SagaStatus {
        STARTED, PROCESSING, COMPLETED, FAILED, COMPENSATING, COMPENSATED
    }

    public enum SagaStepType {
        VALIDATE_PAYMENT,
        DEBIT_ACCOUNT,
        RECORD_TRANSACTION,
        FRAUD_CHECK,
        PROCESS_WITH_BANK,
        CREDIT_BENEFICIARY,
        SEND_NOTIFICATION,
        // Compensating steps
        REVERSE_DEBIT,
        REVERSE_TRANSACTION,
        REVERSE_CREDIT,
        CANCEL_PAYMENT
    }

    @Data
    public static class SagaStep {
        private SagaStepType stepType;
        private SagaStepStatus status;
        private String serviceName;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String failureReason;
        private String compensationData;

        public enum SagaStepStatus {
            PENDING, IN_PROGRESS, COMPLETED, FAILED, COMPENSATED
        }
    }

    /**
     * Execute the saga step by step
     * If a step fails, compensate all previous steps
     */
    public void executeStep(SagaStepType stepType) {
        for (SagaStep step : steps) {
            if (step.getStepType() == stepType) {
                step.setStatus(SagaStep.SagaStepStatus.IN_PROGRESS);
                step.setStartedAt(LocalDateTime.now());
                // Step execution happens in the service layer
                break;
            }
        }
    }

    public void compensate() {
        this.status = SagaStatus.COMPENSATING;
        // Execute compensating transactions in reverse order
        for (int i = steps.length - 1; i >= 0; i--) {
            SagaStep step = steps[i];
            if (step.getStatus() == SagaStep.SagaStepStatus.COMPLETED) {
                // Execute compensation for this step
                step.setStatus(SagaStep.SagaStepStatus.COMPENSATED);
            }
        }
        this.status = SagaStatus.COMPENSATED;
        this.completedAt = LocalDateTime.now();
    }
}
