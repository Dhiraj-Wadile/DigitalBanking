package com.common.payment;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fraud Detection Engine Architecture
 *
 * Detection Methods:
 * 1. Velocity Checks - Transaction frequency
 * 2. Amount Analysis - Unusual amounts
 * 3. Location Analysis - Impossible travel
 * 4. Device Fingerprinting - New device detection
 * 5. Behavioral Analysis - Pattern deviation
 * 6. Blacklist Checks - Known fraud indicators
 * 7. ML Model Scoring - Risk score prediction
 *
 * Risk Score: 0-100
 * - 0-20: LOW (Auto-approve)
 * - 21-50: MEDIUM (Approve with monitoring)
 * - 51-75: HIGH (Hold for review)
 * - 76-100: CRITICAL (Block + alert)
 */
@Data
public class FraudDetectionResult {
    private String detectionId;
    private Long transactionId;
    private Long accountId;
    private Long customerId;
    private Integer riskScore; // 0-100
    private RiskLevel riskLevel;
    private Boolean isBlocked;
    private Boolean requiresReview;
    private LocalDateTime detectedAt;
    private String[] triggeredRules;
    private BigDecimal transactionAmount;
    private String transactionType;

    public enum RiskLevel {
        LOW(0, 20),
        MEDIUM(21, 50),
        HIGH(51, 75),
        CRITICAL(76, 100);

        private final int minScore;
        private final int maxScore;

        RiskLevel(int minScore, int maxScore) {
            this.minScore = minScore;
            this.maxScore = maxScore;
        }

        public static RiskLevel fromScore(int score) {
            for (RiskLevel level : values()) {
                if (score >= level.minScore && score <= level.maxScore) {
                    return level;
                }
            }
            return CRITICAL;
        }
    }

    @Data
    public static class FraudRule {
        private String ruleId;
        private String ruleName;
        private String ruleType; // VELOCITY, AMOUNT, LOCATION, DEVICE, BEHAVIORAL, BLACKLIST
        private Integer riskWeight; // How much this rule adds to risk score
        private Boolean triggered;
        private String triggerReason;
    }
}
