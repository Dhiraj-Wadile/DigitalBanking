package com.common.payment;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * UPI Payment Flow - Complete Architecture
 *
 * Flow:
 * 1. Customer initiates UPI payment
 * 2. Payment Service validates request
 * 3. Fraud check via Fraud Service
 * 4. Debit via Account Service (with saga)
 * 5. UPI switch integration (NPCI)
 * 6. Credit beneficiary account
 * 7. Settlement with NPCI
 * 8. Notification to customer
 */
@Data
public class UpiPaymentFlow {
    private String upiTransactionId;
    private String payerVpa;
    private String payeeVpa;
    private BigDecimal amount;
    private String description;
    private UpiFlowStatus status;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private String npciReferenceId;
    private String bankReferenceId;
    private String rrn; // Retrieval Reference Number

    public enum UpiFlowStatus {
        INITIATED,
        VALIDATING,
        FRAUD_CHECK,
        DEBITING,
        PROCESSING_NPCI,
        CREDITING,
        SETTLING,
        COMPLETED,
        FAILED,
        TIMED_OUT,
        REVERSED
    }

    /**
     * UPI Payment States (FSM - Finite State Machine)
     *
     * INITIATED -> VALIDATING -> FRAUD_CHECK -> DEBITING -> PROCESSING_NPCI
     *     |           |              |            |              |
     *     v           v              v            v              v
     *   FAILED      FAILED         FAILED       FAILED        COMPLETED
     *                                             |              |
     *                                             v              v
     *                                          TIMED_OUT      SETTLING
     *                                                              |
     *                                                              v
     *                                                          COMPLETED
     */
    public boolean canTransitionTo(UpiFlowStatus newStatus) {
        return switch (this.status) {
            case INITIATED -> newStatus == UpiFlowStatus.VALIDATING || newStatus == UpiFlowStatus.FAILED;
            case VALIDATING -> newStatus == UpiFlowStatus.FRAUD_CHECK || newStatus == UpiFlowStatus.FAILED;
            case FRAUD_CHECK -> newStatus == UpiFlowStatus.DEBITING || newStatus == UpiFlowStatus.FAILED;
            case DEBITING -> newStatus == UpiFlowStatus.PROCESSING_NPCI || newStatus == UpiFlowStatus.TIMED_OUT;
            case PROCESSING_NPCI -> newStatus == UpiFlowStatus.CREDITING || newStatus == UpiFlowStatus.REVERSED;
            case CREDITING -> newStatus == UpiFlowStatus.SETTLING || newStatus == UpiFlowStatus.COMPLETED;
            case SETTLING -> newStatus == UpiFlowStatus.COMPLETED;
            default -> false;
        };
    }
}
