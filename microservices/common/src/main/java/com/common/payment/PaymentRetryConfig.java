package com.common.payment;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Payment Retry & Timeout Architecture
 *
 * Handles:
 * 1. Payment timeout detection
 * 2. Automatic retry with exponential backoff
 * 3. Maximum retry limits
 * 4. Idempotency for retries
 * 5. Final failure handling
 *
 * Retry Strategy:
 * - Attempt 1: Immediate
 * - Attempt 2: After 5 seconds
 * - Attempt 3: After 30 seconds
 * - Attempt 4: After 5 minutes
 * - Final: Mark as failed, notify customer
 */
@Data
public class PaymentRetryConfig {
    private Integer maxRetries = 3;
    private Integer currentRetry = 0;
    private Long[] retryDelaysMs = {0L, 5000L, 30000L, 300000L}; // 0s, 5s, 30s, 5min
    private Integer timeoutSeconds = 30;
    private Boolean enableRetry = true;

    public Long getNextRetryDelay() {
        if (currentRetry < retryDelaysMs.length) {
            return retryDelaysMs[currentRetry];
        }
        return null; // No more retries
    }

    public boolean canRetry() {
        return enableRetry && currentRetry < maxRetries;
    }

    @Data
    public static class RetryAttempt {
        private Integer attemptNumber;
        private LocalDateTime attemptedAt;
        private String failureReason;
        private Long delayMs;
        private Boolean success;
    }
}
