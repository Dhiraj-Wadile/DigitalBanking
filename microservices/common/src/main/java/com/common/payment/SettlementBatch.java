package com.common.payment;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Settlement Architecture
 *
 * Settlement Types:
 * 1. T+0 - Same day settlement (RTGS, IMPS)
 * 2. T+1 - Next day settlement (NEFT)
 * 3. T+2 - Two day settlement (UPI)
 * 4. Batch settlement - End of day processing
 *
 * Settlement Flow:
 * 1. Payment Service processes payment
 * 2. Transaction Service records transaction
 * 3. Settlement Service batches transactions
 * 4. Generate settlement file for NPCI/bank
 * 5. Submit to clearing house
 * 6. Reconcile with bank statements
 * 7. Update settlement status
 */
@Data
public class SettlementBatch {
    private String batchId;
    private String bankCode;
    private String settlementType; // T+0, T+1, T+2
    private BigDecimal totalAmount;
    private Integer transactionCount;
    private SettlementStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime settledAt;
    private List<SettlementEntry> entries;
    private String settlementFileReference;
    private String clearingHouseReference;

    public enum SettlementStatus {
        CREATED,       // Batch created, collecting transactions
        SUBMITTED,     // Submitted to clearing house
        PROCESSING,    // Being processed by clearing house
        SETTLED,       // Settlement confirmed
        FAILED,        // Settlement failed
        RECONCILED     // Reconciled with bank statement
    }

    @Data
    public static class SettlementEntry {
        private String paymentReference;
        private String transactionReference;
        private String payerBankCode;
        private String payeeBankCode;
        private BigDecimal amount;
        private String paymentMethod; // NEFT, RTGS, IMPS, UPI
        private LocalDateTime transactionDate;
        private SettlementEntryStatus status;

        public enum SettlementEntryStatus {
            PENDING, SETTLED, FAILED, RECONCILED
        }
    }
}
