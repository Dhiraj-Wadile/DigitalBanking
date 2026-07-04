-- ============================================================================
-- V5: ADVANCED INDEXING - Performance Optimization
-- ============================================================================

-- ============================================================================
-- COMPOSITE INDEXES - For frequently joined/filtered queries
-- ============================================================================

-- Account lookups by customer + status
CREATE INDEX idx_accounts_customer_status ON accounts(customer_id, status);

-- Transaction lookups by account + type + date (most common dashboard query)
CREATE INDEX idx_transactions_account_type_date ON transactions(account_id, transaction_type, transaction_date DESC);

-- Transaction date range queries (statements)
CREATE INDEX idx_transactions_date_range ON transactions(transaction_date DESC, account_id, status);

-- Payment processing queue
CREATE INDEX idx_payments_status_scheduled ON payments(status, scheduled_date) WHERE status IN ('PENDING', 'PROCESSING', 'SCHEDULED');

-- Beneficiary verification lookups
CREATE INDEX idx_beneficiary_customer_active ON beneficiaries(customer_id, active) WHERE active = true;

-- Card status lookups
CREATE INDEX idx_cards_account_status ON cards(account_id, status);

-- Loan status workflow
CREATE INDEX idx_loans_status_date ON loans(status, application_date DESC);

-- Notification delivery queue
CREATE INDEX idx_notifications_unread ON notifications(user_id, is_read, created_at DESC) WHERE is_read = false;

-- Audit trail queries
CREATE INDEX idx_audit_entity_date ON audit_logs(entity_type, entity_id, action_date DESC);
CREATE INDEX idx_audit_user_date ON audit_logs(user_id, action_date DESC) WHERE user_id IS NOT NULL;

-- ============================================================================
-- PARTIAL INDEXES - For filtered queries (PostgreSQL specific)
-- ============================================================================

-- Only active accounts
CREATE INDEX idx_active_accounts ON accounts(account_number) WHERE status = 'ACTIVE';

-- Only pending transactions
CREATE INDEX idx_pending_transactions ON transactions(reference_number, account_id) WHERE status = 'PENDING';

-- Only unverified beneficiaries
CREATE INDEX idx_unverified_beneficiaries ON beneficiaries(customer_id) WHERE verified = false;

-- Only locked users
CREATE INDEX idx_locked_users ON users(locked_until) WHERE account_locked = true;

-- Only unread notifications
CREATE INDEX idx_pending_notifications ON notifications(user_id, type) WHERE is_read = false AND sent = true;

-- Scheduled payments to process
CREATE INDEX idx_pending_scheduled_payments ON payments(payment_reference, account_id) WHERE status = 'SCHEDULED' AND scheduled_date <= NOW();

-- ============================================================================
-- COVERING INDEXES - Include all needed columns (index-only scans)
-- ============================================================================

-- Dashboard query: customer's account balances
CREATE INDEX idx_accounts_dashboard ON accounts(customer_id, status)
    INCLUDE (account_number, balance, available_balance, account_type);

-- Transaction history: all data needed for display
CREATE INDEX idx_transactions_history ON transactions(account_id, transaction_date DESC)
    INCLUDE (reference_number, transaction_type, status, amount, balance_after, description);

-- Payment history display
CREATE INDEX idx_payments_history ON payments(account_id, payment_date DESC)
    INCLUDE (payment_reference, payment_type, payment_method, status, amount, beneficiary_name);

-- ============================================================================
-- EXPRESSION INDEXES - For computed lookups
-- ============================================================================

-- Case-insensitive email search
CREATE INDEX idx_users_email_lower ON users(LOWER(email));

-- Case-insensitive username search
CREATE INDEX idx_users_username_lower ON users(LOWER(username));

-- ============================================================================
-- BRIN INDEXES - For time-series data (very efficient for large tables)
-- ============================================================================

-- Transaction date series
CREATE INDEX idx_txn_date_brin ON transactions USING BRIN (transaction_date);

-- Audit log date series
CREATE INDEX idx_audit_date_brin ON audit_logs USING BRIN (action_date);

-- Payment date series
CREATE INDEX idx_payment_date_brin ON payments USING BRIN (payment_date);
