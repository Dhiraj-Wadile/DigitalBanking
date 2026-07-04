-- ============================================================================
-- V6: COMPLEX QUERIES - Real-World Banking Analytics
-- ============================================================================

-- ============================================================================
-- QUERY 1: Customer Account Summary (CTE + JOINs + Subqueries)
-- Shows all accounts, total balance, transaction count for each customer
-- ============================================================================
WITH customer_summary AS (
    SELECT
        c.id AS customer_id,
        c.customer_number,
        u.first_name || ' ' || u.last_name AS full_name,
        u.email,
        c.kyc_status,
        c.status AS customer_status,
        COUNT(DISTINCT a.id) AS total_accounts,
        COALESCE(SUM(a.balance), 0) AS total_balance,
        COALESCE(SUM(a.available_balance), 0) AS total_available
    FROM customers c
    INNER JOIN users u ON c.user_id = u.id
    LEFT JOIN accounts a ON c.id = a.customer_id AND a.status = 'ACTIVE' AND a.deleted = false
    WHERE c.deleted = false
    GROUP BY c.id, c.customer_number, u.first_name, u.last_name, u.email, c.kyc_status, c.status
),
transaction_stats AS (
    SELECT
        a.customer_id,
        COUNT(t.id) AS total_transactions,
        COALESCE(SUM(CASE WHEN t.transaction_type = 'DEPOSIT' THEN t.amount ELSE 0 END), 0) AS total_deposits,
        COALESCE(SUM(CASE WHEN t.transaction_type = 'WITHDRAWAL' THEN t.amount ELSE 0 END), 0) AS total_withdrawals,
        COALESCE(SUM(CASE WHEN t.transaction_type IN ('TRANSFER_IN', 'TRANSFER_OUT') THEN t.amount ELSE 0 END), 0) AS total_transfers,
        MAX(t.transaction_date) AS last_transaction_date
    FROM accounts a
    LEFT JOIN transactions t ON a.id = t.account_id AND t.status = 'COMPLETED'
    WHERE a.deleted = false
    GROUP BY a.customer_id
)
SELECT
    cs.*,
    COALESCE(ts.total_transactions, 0) AS total_transactions,
    ts.total_deposits,
    ts.total_withdrawals,
    ts.total_transfers,
    ts.last_transaction_date,
    CASE
        WHEN cs.total_balance > 1000000 THEN 'PREMIUM'
        WHEN cs.total_balance > 100000 THEN 'GOLD'
        WHEN cs.total_balance > 10000 THEN 'SILVER'
        ELSE 'STANDARD'
    END AS customer_tier
FROM customer_summary cs
LEFT JOIN transaction_stats ts ON cs.customer_id = ts.customer_id
ORDER BY cs.total_balance DESC;


-- ============================================================================
-- QUERY 2: Daily Transaction Analytics (Window Functions + Grouping Sets)
-- Transaction volume, value, and trend analysis
-- ============================================================================
SELECT
    DATE(t.transaction_date) AS txn_date,
    t.transaction_type,
    COUNT(*) AS txn_count,
    SUM(t.amount) AS total_amount,
    AVG(t.amount) AS avg_amount,
    MIN(t.amount) AS min_amount,
    MAX(t.amount) AS max_amount,
    -- Running total using window function
    SUM(SUM(t.amount)) OVER (PARTITION BY t.transaction_type ORDER BY DATE(t.transaction_date)) AS running_total,
    -- Rank by volume
    RANK() OVER (PARTITION BY DATE(t.transaction_date) ORDER BY COUNT(*) DESC) AS volume_rank,
    -- Day-over-day change
    LAG(COUNT(*)) OVER (PARTITION BY t.transaction_type ORDER BY DATE(t.transaction_date)) AS prev_day_count,
    ROUND(
        ((COUNT(*) - LAG(COUNT(*)) OVER (PARTITION BY t.transaction_type ORDER BY DATE(t.transaction_date))) * 100.0 /
         NULLIF(LAG(COUNT(*)) OVER (PARTITION BY t.transaction_type ORDER BY DATE(t.transaction_date)), 0)),
    2) AS pct_change
FROM transactions t
WHERE t.status = 'COMPLETED'
AND t.transaction_date >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY DATE(t.transaction_date), t.transaction_type
ORDER BY txn_date DESC, total_amount DESC;


-- ============================================================================
-- QUERY 3: Fraud Detection Query (Correlated Subquery + CASE)
-- Identifies suspicious patterns
-- ============================================================================
SELECT
    a.account_number,
    u.first_name || ' ' || u.last_name AS account_holder,
    a.balance,
    -- Transaction velocity (txns in last hour)
    (SELECT COUNT(*)
     FROM transactions t2
     WHERE t2.account_id = a.id
     AND t2.transaction_date > NOW() - INTERVAL '1 hour'
     AND t2.status = 'COMPLETED') AS txns_last_hour,
    -- Transaction velocity (txns in last 24 hours)
    (SELECT COUNT(*)
     FROM transactions t3
     WHERE t3.account_id = a.id
     AND t3.transaction_date > NOW() - INTERVAL '24 hours'
     AND t3.status = 'COMPLETED') AS txns_last_24hr,
    -- Total amount in last 1 hour
    (SELECT COALESCE(SUM(t4.amount), 0)
     FROM transactions t4
     WHERE t4.account_id = a.id
     AND t4.transaction_date > NOW() - INTERVAL '1 hour'
     AND t4.status = 'COMPLETED') AS amount_last_hour,
    -- Average transaction amount
    (SELECT AVG(t5.amount)
     FROM transactions t5
     WHERE t5.account_id = a.id
     AND t5.transaction_date > NOW() - INTERVAL '30 days'
     AND t5.status = 'COMPLETED') AS avg_txn_amount_30d,
    -- Risk score calculation
    CASE
        WHEN (SELECT COUNT(*) FROM transactions t6 WHERE t6.account_id = a.id
              AND t6.transaction_date > NOW() - INTERVAL '1 hour' AND t6.status = 'COMPLETED') >= 10
            THEN 'CRITICAL'
        WHEN (SELECT COUNT(*) FROM transactions t7 WHERE t7.account_id = a.id
              AND t7.transaction_date > NOW() - INTERVAL '1 hour' AND t7.status = 'COMPLETED') >= 5
            THEN 'HIGH'
        WHEN (SELECT COALESCE(SUM(t8.amount), 0) FROM transactions t8 WHERE t8.account_id = a.id
              AND t8.transaction_date > NOW() - INTERVAL '24 hours' AND t8.status = 'COMPLETED')
             > a.daily_transaction_limit * 0.8
            THEN 'MEDIUM'
        ELSE 'LOW'
    END AS risk_level
FROM accounts a
INNER JOIN customers c ON a.customer_id = c.id
INNER JOIN users u ON c.user_id = u.id
WHERE a.status = 'ACTIVE' AND a.deleted = false
ORDER BY
    CASE
        WHEN (SELECT COUNT(*) FROM transactions t9 WHERE t9.account_id = a.id
              AND t9.transaction_date > NOW() - INTERVAL '1 hour' AND t9.status = 'COMPLETED') >= 10 THEN 1
        WHEN (SELECT COUNT(*) FROM transactions t10 WHERE t10.account_id = a.id
              AND t10.transaction_date > NOW() - INTERVAL '1 hour' AND t10.status = 'COMPLETED') >= 5 THEN 2
        ELSE 3
    END;


-- ============================================================================
-- QUERY 4: Account Statement (Complex JOINs + Window Functions)
-- Full statement with running balance
-- ============================================================================
WITH statement_data AS (
    SELECT
        t.id,
        t.reference_number,
        t.transaction_date,
        t.transaction_type,
        t.amount,
        t.description,
        t.channel,
        t.counterparty_name,
        t.counterparty_account,
        t.status,
        -- Running balance using window function
        SUM(CASE
            WHEN t.transaction_type IN ('DEPOSIT', 'TRANSFER_IN', 'INTEREST_CREDIT', 'REFUND')
            THEN t.amount
            WHEN t.transaction_type IN ('WITHDRAWAL', 'TRANSFER_OUT', 'CARD_PAYMENT', 'FEE_DEBIT')
            THEN -t.amount
            ELSE 0
        END) OVER (ORDER BY t.transaction_date, t.id) AS running_balance
    FROM transactions t
    WHERE t.account_id = (SELECT id FROM accounts WHERE account_number = 'DB0000000001')
    AND t.status = 'COMPLETED'
    AND t.deleted = false
)
SELECT
    sd.*,
    TO_CHAR(sd.transaction_date, 'DD-Mon-YYYY HH24:MI:SS') AS formatted_date,
    CASE
        WHEN sd.transaction_type IN ('DEPOSIT', 'TRANSFER_IN', 'INTEREST_CREDIT', 'REFUND')
        THEN '+' || TO_CHAR(sd.amount, 'FM999,999,999.00')
        ELSE '-' || TO_CHAR(sd.amount, 'FM999,999,999.00')
    END AS formatted_amount
FROM statement_data sd
ORDER BY sd.transaction_date DESC
LIMIT 50;


-- ============================================================================
-- QUERY 5: Payment Settlement Reconciliation (Subquery + JOIN + GROUP BY)
-- Match payments with transactions for reconciliation
-- ============================================================================
SELECT
    p.payment_reference,
    p.payment_type,
    p.payment_method,
    p.amount AS payment_amount,
    p.status AS payment_status,
    p.payment_date,
    p.beneficiary_name,
    p.beneficiary_account,
    a.account_number AS payer_account,
    u.first_name || ' ' || u.last_name AS payer_name,
    -- Check if corresponding debit transaction exists
    (SELECT t.reference_number
     FROM transactions t
     WHERE t.account_id = p.account_id
     AND t.amount = p.amount
     AND t.transaction_date BETWEEN p.payment_date - INTERVAL '5 minutes' AND p.payment_date + INTERVAL '5 minutes'
     AND t.status = 'COMPLETED'
     LIMIT 1) AS matching_txn_reference,
    -- Settlement status
    CASE
        WHEN p.status = 'COMPLETED' AND EXISTS (
            SELECT 1 FROM transactions t2
            WHERE t2.account_id = p.account_id AND t2.amount = p.amount
            AND t2.status = 'COMPLETED'
        ) THEN 'RECONCILED'
        WHEN p.status = 'COMPLETED' AND NOT EXISTS (
            SELECT 1 FROM transactions t3
            WHERE t3.account_id = p.account_id AND t3.amount = p.amount
            AND t3.status = 'COMPLETED'
        ) THEN 'UNMATCHED'
        WHEN p.status = 'PENDING' THEN 'AWAITING_SETTLEMENT'
        WHEN p.status = 'FAILED' THEN 'FAILED'
        ELSE 'UNKNOWN'
    END AS reconciliation_status
FROM payments p
INNER JOIN accounts a ON p.account_id = a.id
INNER JOIN customers c ON a.customer_id = c.id
INNER JOIN users u ON c.user_id = u.id
WHERE p.payment_date >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY p.payment_date DESC;


-- ============================================================================
-- QUERY 6: Loan EMI Schedule Generator (Recursive CTE)
-- Generates monthly EMI schedule for a loan
-- ============================================================================
WITH RECURSIVE emi_schedule AS (
    -- Base case: first EMI
    SELECT
        1 AS installment_number,
        (SELECT first_emi_date FROM loans WHERE loan_account_number = 'LN000000001') AS emi_date,
        (SELECT outstanding_amount FROM loans WHERE loan_account_number = 'LN000000001') AS opening_balance,
        (SELECT emi_amount FROM loans WHERE loan_account_number = 'LN000000001') AS emi_amount,
        (SELECT outstanding_amount FROM loans WHERE loan_account_number = 'LN000000001')
            * (SELECT interest_rate FROM loans WHERE loan_account_number = 'LN000000001') / 12.0 / 100.0
            AS interest_component,
        (SELECT emi_amount FROM loans WHERE loan_account_number = 'LN000000001')
            - ((SELECT outstanding_amount FROM loans WHERE loan_account_number = 'LN000000001')
                * (SELECT interest_rate FROM loans WHERE loan_account_number = 'LN000000001') / 12.0 / 100.0)
            AS principal_component,
        (SELECT outstanding_amount FROM loans WHERE loan_account_number = 'LN000000001')
            - ((SELECT emi_amount FROM loans WHERE loan_account_number = 'LN000000001')
                - ((SELECT outstanding_amount FROM loans WHERE loan_account_number = 'LN000000001')
                    * (SELECT interest_rate FROM loans WHERE loan_account_number = 'LN000000001') / 12.0 / 100.0))
            AS closing_balance

    UNION ALL

    -- Recursive case: subsequent EMIs
    SELECT
        installment_number + 1,
        emi_date + INTERVAL '1 month',
        closing_balance,
        emi_amount,
        closing_balance * (SELECT interest_rate FROM loans WHERE loan_account_number = 'LN000000001') / 12.0 / 100.0,
        emi_amount - (closing_balance * (SELECT interest_rate FROM loans WHERE loan_account_number = 'LN000000001') / 12.0 / 100.0),
        closing_balance - (emi_amount - (closing_balance * (SELECT interest_rate FROM loans WHERE loan_account_number = 'LN000000001') / 12.0 / 100.0))
    FROM emi_schedule
    WHERE installment_number < (SELECT tenure_months FROM loans WHERE loan_account_number = 'LN000000001')
    AND closing_balance > 0
)
SELECT
    installment_number,
    TO_CHAR(emi_date, 'DD-Mon-YYYY') AS emi_date,
    ROUND(opening_balance, 2) AS opening_balance,
    ROUND(emi_amount, 2) AS emi,
    ROUND(interest_component, 2) AS interest,
    ROUND(principal_component, 2) AS principal,
    ROUND(closing_balance, 2) AS closing_balance
FROM emi_schedule
ORDER BY installment_number;


-- ============================================================================
-- QUERY 7: Branch Performance Report (CTEs + Window Functions)
-- ============================================================================

WITH branch_stats AS (
    SELECT
        b.id AS branch_id,
        b.branch_code,
        b.branch_name,
        b.city,
        COUNT(DISTINCT a.id) AS total_accounts,
        COUNT(DISTINCT c.id) AS total_customers,
        COALESCE(SUM(a.balance), 0) AS total_deposits,
        COALESCE(AVG(a.balance), 0) AS avg_balance
    FROM branches b
    LEFT JOIN accounts a ON b.id = a.branch_id AND a.status = 'ACTIVE' AND a.deleted = false
    LEFT JOIN customers c ON a.customer_id = c.id AND c.deleted = false
    WHERE b.deleted = false
    GROUP BY b.id, b.branch_code, b.branch_name, b.city
),
branch_txns AS (
    SELECT
        a.branch_id,
        COUNT(t.id) AS total_transactions,
        COALESCE(SUM(t.amount), 0) AS total_transaction_volume,
        COUNT(DISTINCT DATE(t.transaction_date)) AS active_days
    FROM accounts a
    INNER JOIN transactions t ON a.id = t.account_id AND t.status = 'COMPLETED'
    WHERE a.branch_id IS NOT NULL
    GROUP BY a.branch_id
)
SELECT
    bs.*,
    COALESCE(bt.total_transactions, 0) AS total_transactions,
    COALESCE(bt.total_transaction_volume, 0) AS total_transaction_volume,
    CASE
        WHEN bs.total_accounts > 0
        THEN ROUND(COALESCE(bt.total_transaction_volume, 0) / bs.total_accounts, 2)
        ELSE 0
    END AS revenue_per_account,
    -- Rank branches by total deposits
    RANK() OVER (ORDER BY bs.total_deposits DESC) AS deposit_rank,
    -- Rank by customer count
    RANK() OVER (ORDER BY bs.total_customers DESC) AS customer_rank
FROM branch_stats bs
LEFT JOIN branch_txns bt ON bs.branch_id = bt.branch_id
ORDER BY bs.total_deposits DESC;


-- ============================================================================
-- QUERY 8: Monthly Revenue Report (GROUP BY CUBE - OLAP-style)
-- ============================================================================

SELECT
    COALESCE(TO_CHAR(t.transaction_date, 'Mon YYYY'), 'ALL MONTHS') AS month,
    COALESCE(t.transaction_type, 'ALL TYPES') AS transaction_type,
    COUNT(*) AS txn_count,
    SUM(t.amount) AS total_amount,
    AVG(t.amount) AS avg_amount,
    MIN(t.amount) AS min_amount,
    MAX(t.amount) AS max_amount
FROM transactions t
WHERE t.status = 'COMPLETED'
AND t.transaction_date >= CURRENT_DATE - INTERVAL '12 months'
GROUP BY CUBE(TO_CHAR(t.transaction_date, 'Mon YYYY'), t.transaction_type)
ORDER BY
    CASE WHEN TO_CHAR(t.transaction_date, 'Mon YYYY') IS NULL THEN 1 ELSE 0 END,
    month DESC,
    CASE WHEN t.transaction_type IS NULL THEN 1 ELSE 0 END,
    total_amount DESC;


-- ============================================================================
-- QUERY 9: Customer Segmentation (RFM Analysis)
-- Recency, Frequency, Monetary value
-- ============================================================================

WITH rfm_calc AS (
    SELECT
        c.id AS customer_id,
        c.customer_number,
        u.first_name || ' ' || u.last_name AS full_name,
        -- Recency: days since last transaction
        EXTRACT(DAY FROM NOW() - MAX(t.transaction_date)) AS recency_days,
        -- Frequency: total transaction count
        COUNT(t.id) AS frequency,
        -- Monetary: total transaction value
        COALESCE(SUM(t.amount), 0) AS monetary_value
    FROM customers c
    INNER JOIN users u ON c.user_id = u.id
    LEFT JOIN accounts a ON c.id = a.customer_id
    LEFT JOIN transactions t ON a.id = t.account_id AND t.status = 'COMPLETED'
    WHERE c.deleted = false
    GROUP BY c.id, c.customer_number, u.first_name, u.last_name
),
rfm_scored AS (
    SELECT
        *,
        NTILE(5) OVER (ORDER BY recency_days DESC) AS r_score,
        NTILE(5) OVER (ORDER BY frequency ASC) AS f_score,
        NTILE(5) OVER (ORDER BY monetary_value ASC) AS m_score
    FROM rfm_calc
)
SELECT
    *,
    (r_score + f_score + m_score) AS rfm_total,
    CASE
        WHEN r_score >= 4 AND f_score >= 4 AND m_score >= 4 THEN 'CHAMPION'
        WHEN r_score >= 3 AND f_score >= 3 AND m_score >= 3 THEN 'LOYAL'
        WHEN r_score >= 4 AND f_score <= 2 THEN 'NEW_CUSTOMER'
        WHEN r_score <= 2 AND f_score >= 3 THEN 'AT_RISK'
        WHEN r_score <= 2 AND f_score <= 2 THEN 'LOST'
        ELSE 'POTENTIAL'
    END AS customer_segment
FROM rfm_scored
ORDER BY rfm_total DESC;
