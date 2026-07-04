package com.common.payment;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reconciliation Architecture
 *
 * Types:
 * 1. Internal Reconciliation - Match our records with internal systems
 * 2. External Reconciliation - Match with bank/NPCI statements
 * 3. Inter-bank Reconciliation - Match with correspondent banks
 *
 * Flow:
 * 1. Download bank statement (CSV/MT940/COB)
 * 2. Parse and normalize data
 * 3. Match transactions using rules:
 *    - Exact match: amount + reference
 *    - Fuzzy match: amount + date + partial reference
 *    - Manual match: unmatched items
 * 4. Generate reconciliation report
 * 5. Flag discrepancies
 * 6. Initiate dispute resolution if needed
 */
@Data
public class ReconciliationRecord {
    private String reconciliationId;
    private ReconciliationType type;
    private LocalDateTime statementDate;
    private BigDecimal statementOpeningBalance;
    private BigDecimal statementClosingBalance;
    private BigDecimal systemOpeningBalance;
    private BigDecimal systemClosingBalance;
    private Integer matchedCount;
    private Integer unmatchedCount;
    private Integer discrepancyCount;
    private ReconciliationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public enum ReconciliationType {
        INTERNAL,          // Match within our systems
        BANK_STATEMENT,    // Match with bank statement
        NPCI_SETTLEMENT,   // Match with NPCI settlement
        INTER_BANK,        // Match with other banks
        CARD_NETWORK       // Match with Visa/Mastercard
    }

    public enum ReconciliationStatus {
        IN_PROGRESS,
        MATCHED,
        PARTIALLY_MATCHED,
        UNMATCHED,
        DISCREPANCY_FOUND,
        DISPUTED,
        RESOLVED
    }

    @Data
    public static class ReconciliationEntry {
        private String entryId;
        private String systemReference;
        private String bankReference;
        private BigDecimal amount;
        private LocalDateTime transactionDate;
        private String description;
        private MatchStatus matchStatus;
        private String discrepancyReason;

        public enum MatchStatus {
            EXACT_MATCH,      // Perfect match
            FUZZY_MATCH,      // Close match (needs review)
            PARTIAL_MATCH,    // Partial data match
            UNMATCHED,        // No match found
            DISPUTED          // Under dispute
        }
    }
}
