-- ============================================================================
-- V3: STORED PROCEDURES - Complex Banking Operations
-- ============================================================================

-- ============================================================================
-- SP1: FUND TRANSFER (Atomic, ACID Compliant)
-- Handles: Balance check, debit, credit, transaction logging, audit trail
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_transfer_funds(
    p_from_account VARCHAR(20),
    p_to_account VARCHAR(20),
    p_amount DECIMAL(18,2),
    p_description VARCHAR(500),
    p_channel VARCHAR(50),
    p_idempotency_key VARCHAR(50),
    OUT p_status VARCHAR(20),
    OUT p_message VARCHAR(500),
    OUT p_reference VARCHAR(50)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_from_balance DECIMAL(18,2);
    v_from_available DECIMAL(18,2);
    v_from_status VARCHAR(20);
    v_to_status VARCHAR(20);
    v_to_balance DECIMAL(18,2);
    v_from_id BIGINT;
    v_to_id BIGINT;
    v_txn_ref VARCHAR(50);
    v_idempotent_exists BOOLEAN;
BEGIN
    -- Check idempotency to prevent duplicate transactions
    SELECT EXISTS(SELECT 1 FROM transactions WHERE idempotency_key = p_idempotency_key)
    INTO v_idempotent_exists;

    IF v_idempotent_exists THEN
        SELECT reference_number INTO v_txn_ref
        FROM transactions WHERE idempotency_key = p_idempotency_key LIMIT 1;
        p_status := 'DUPLICATE';
        p_message := 'Transaction already processed';
        p_reference := v_txn_ref;
        RETURN;
    END IF;

    -- Generate reference number
    v_txn_ref := 'TXN' || EXTRACT(EPOCH FROM NOW())::BIGINT || FLOOR(RANDOM() * 10000)::INT;

    -- Lock source account row to prevent race conditions (SELECT FOR UPDATE)
    SELECT a.id, a.balance, a.available_balance, a.status
    INTO v_from_id, v_from_balance, v_from_available, v_from_status
    FROM accounts a WHERE a.account_number = p_from_account
    FOR UPDATE;

    -- Lock destination account row
    SELECT a.id, a.balance, a.status
    INTO v_to_id, v_to_balance, v_to_status
    FROM accounts a WHERE a.account_number = p_to_account
    FOR UPDATE;

    -- Validation
    IF v_from_id IS NULL THEN
        p_status := 'FAILED';
        p_message := 'Source account not found';
        p_reference := NULL;
        RETURN;
    END IF;

    IF v_to_id IS NULL THEN
        p_status := 'FAILED';
        p_message := 'Destination account not found';
        p_reference := NULL;
        RETURN;
    END IF;

    IF v_from_status != 'ACTIVE' THEN
        p_status := 'FAILED';
        p_message := 'Source account is not active (status: ' || v_from_status || ')';
        p_reference := NULL;
        RETURN;
    END IF;

    IF v_to_status != 'ACTIVE' THEN
        p_status := 'FAILED';
        p_message := 'Destination account is not active';
        p_reference := NULL;
        RETURN;
    END IF;

    IF p_amount <= 0 THEN
        p_status := 'FAILED';
        p_message := 'Amount must be greater than zero';
        p_reference := NULL;
        RETURN;
    END IF;

    IF v_from_available < p_amount THEN
        p_status := 'FAILED';
        p_message := 'Insufficient available balance. Available: ' || v_from_available || ', Requested: ' || p_amount;
        p_reference := NULL;
        RETURN;
    END IF;

    -- Debit source account
    UPDATE accounts
    SET balance = balance - p_amount,
        available_balance = available_balance - p_amount,
        last_transaction_date = CURRENT_DATE,
        updated_at = NOW()
    WHERE id = v_from_id;

    -- Credit destination account
    UPDATE accounts
    SET balance = balance + p_amount,
        available_balance = available_balance + p_amount,
        last_transaction_date = CURRENT_DATE,
        updated_at = NOW()
    WHERE id = v_to_id;

    -- Create debit transaction record
    INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount,
        balance_after, description, channel, counterparty_account, transaction_date,
        idempotency_key, created_at, updated_at, deleted)
    VALUES (v_txn_ref, v_from_id, 'TRANSFER_OUT', 'COMPLETED', p_amount,
        (v_from_balance - p_amount), p_description, p_channel, p_to_account,
        NOW(), p_idempotency_key, NOW(), NOW(), false);

    -- Create credit transaction record
    INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount,
        balance_after, description, channel, counterparty_account, transaction_date,
        created_at, updated_at, deleted)
    VALUES (v_txn_ref || '-C', v_to_id, 'TRANSFER_IN', 'COMPLETED', p_amount,
        (v_to_balance + p_amount), p_description, p_channel, p_from_account,
        NOW(), NOW(), NOW(), false);

    -- Create audit log
    INSERT INTO audit_logs (action, entity_type, entity_id, new_values, action_date, status, created_at)
    VALUES ('TRANSFER', 'Transaction', v_from_id,
        json_build_object('from', p_from_account, 'to', p_to_account, 'amount', p_amount, 'reference', v_txn_ref)::text,
        NOW(), 'SUCCESS', NOW());

    p_status := 'SUCCESS';
    p_message := 'Transfer completed successfully';
    p_reference := v_txn_ref;

EXCEPTION
    WHEN OTHERS THEN
        p_status := 'ERROR';
        p_message := 'Transfer failed: ' || SQLERRM;
        p_reference := NULL;

        -- Log error in audit
        INSERT INTO audit_logs (action, entity_type, new_values, action_date, status, failure_reason, created_at)
        VALUES ('TRANSFER_FAILED', 'Transaction',
            json_build_object('from', p_from_account, 'to', p_to_account, 'amount', p_amount, 'error', SQLERRM)::text,
            NOW(), 'FAILED', SQLERRM, NOW());
END;
$$;

-- ============================================================================
-- SP2: DEPOSIT FUNDS
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_deposit_funds(
    p_account_number VARCHAR(20),
    p_amount DECIMAL(18,2),
    p_description VARCHAR(500),
    p_channel VARCHAR(50),
    OUT p_status VARCHAR(20),
    OUT p_message VARCHAR(500),
    OUT p_reference VARCHAR(50)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_account_id BIGINT;
    v_current_balance DECIMAL(18,2);
    v_txn_ref VARCHAR(50);
BEGIN
    v_txn_ref := 'DEP' || EXTRACT(EPOCH FROM NOW())::BIGINT || FLOOR(RANDOM() * 10000)::INT;

    SELECT a.id, a.balance INTO v_account_id, v_current_balance
    FROM accounts a WHERE a.account_number = p_account_number AND a.status = 'ACTIVE'
    FOR UPDATE;

    IF v_account_id IS NULL THEN
        p_status := 'FAILED';
        p_message := 'Account not found or inactive';
        RETURN;
    END IF;

    IF p_amount <= 0 THEN
        p_status := 'FAILED';
        p_message := 'Amount must be greater than zero';
        RETURN;
    END IF;

    UPDATE accounts
    SET balance = balance + p_amount,
        available_balance = available_balance + p_amount,
        last_transaction_date = CURRENT_DATE,
        updated_at = NOW()
    WHERE id = v_account_id;

    INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount,
        balance_after, description, channel, transaction_date, created_at, updated_at, deleted)
    VALUES (v_txn_ref, v_account_id, 'DEPOSIT', 'COMPLETED', p_amount,
        (v_current_balance + p_amount), p_description, p_channel, NOW(), NOW(), NOW(), false);

    p_status := 'SUCCESS';
    p_message := 'Deposit completed';
    p_reference := v_txn_ref;
END;
$$;

-- ============================================================================
-- SP3: WITHDRAW FUNDS
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_withdraw_funds(
    p_account_number VARCHAR(20),
    p_amount DECIMAL(18,2),
    p_description VARCHAR(500),
    p_channel VARCHAR(50),
    OUT p_status VARCHAR(20),
    OUT p_message VARCHAR(500),
    OUT p_reference VARCHAR(50)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_account_id BIGINT;
    v_balance DECIMAL(18,2);
    v_available DECIMAL(18,2);
    v_txn_ref VARCHAR(50);
BEGIN
    v_txn_ref := 'WTH' || EXTRACT(EPOCH FROM NOW())::BIGINT || FLOOR(RANDOM() * 10000)::INT;

    SELECT a.id, a.balance, a.available_balance
    INTO v_account_id, v_balance, v_available
    FROM accounts a WHERE a.account_number = p_account_number AND a.status = 'ACTIVE'
    FOR UPDATE;

    IF v_account_id IS NULL THEN
        p_status := 'FAILED';
        p_message := 'Account not found or inactive';
        RETURN;
    END IF;

    IF p_amount <= 0 THEN
        p_status := 'FAILED';
        p_message := 'Amount must be greater than zero';
        RETURN;
    END IF;

    IF v_available < p_amount THEN
        p_status := 'FAILED';
        p_message := 'Insufficient balance';
        RETURN;
    END IF;

    UPDATE accounts
    SET balance = balance - p_amount,
        available_balance = available_balance - p_amount,
        last_transaction_date = CURRENT_DATE,
        updated_at = NOW()
    WHERE id = v_account_id;

    INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount,
        balance_after, description, channel, transaction_date, created_at, updated_at, deleted)
    VALUES (v_txn_ref, v_account_id, 'WITHDRAWAL', 'COMPLETED', p_amount,
        (v_balance - p_amount), p_description, p_channel, NOW(), NOW(), NOW(), false);

    p_status := 'SUCCESS';
    p_message := 'Withdrawal completed';
    p_reference := v_txn_ref;
END;
$$;

-- ============================================================================
-- SP4: INTEREST CALCULATION (Fixed Deposit / Savings)
-- Uses compound interest formula: A = P(1 + r/n)^(nt)
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_calculate_interest(
    p_account_number VARCHAR(20),
    OUT p_status VARCHAR(20),
    OUT p_interest_amount DECIMAL(18,2)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_account_id BIGINT;
    v_balance DECIMAL(18,2);
    v_rate DECIMAL(5,2);
    v_account_type VARCHAR(30);
    v_daily_interest DECIMAL(18,8);
BEGIN
    SELECT a.id, a.balance, a.interest_rate, a.account_type
    INTO v_account_id, v_balance, v_rate, v_account_type
    FROM accounts a WHERE a.account_number = p_account_number AND a.status = 'ACTIVE';

    IF v_account_id IS NULL THEN
        p_status := 'FAILED';
        p_interest_amount := 0;
        RETURN;
    END IF;

    IF v_rate <= 0 THEN
        p_status := 'NO_INTEREST';
        p_interest_amount := 0;
        RETURN;
    END IF;

    -- Daily interest = Balance * (Annual Rate / 365) / 100
    v_daily_interest := v_balance * (v_rate / 365.0) / 100.0;
    p_interest_amount := ROUND(v_daily_interest, 2);

    -- Credit interest to account
    UPDATE accounts
    SET balance = balance + p_interest_amount,
        available_balance = available_balance + p_interest_amount,
        updated_at = NOW()
    WHERE id = v_account_id;

    -- Log transaction
    INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount,
        balance_after, description, channel, transaction_date, created_at, updated_at, deleted)
    VALUES ('INT' || EXTRACT(EPOCH FROM NOW())::BIGINT, v_account_id, 'INTEREST_CREDIT', 'COMPLETED',
        p_interest_amount, (v_balance + p_interest_amount),
        'Interest calculated at ' || v_rate || '% p.a.', 'SYSTEM',
        NOW(), NOW(), NOW(), false);

    p_status := 'SUCCESS';
END;
$$;

-- ============================================================================
-- SP5: EMI CALCULATION (Loan)
-- EMI = P * r * (1+r)^n / ((1+r)^n - 1)
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_calculate_emi(
    p_principal DECIMAL(18,2),
    p_annual_rate DECIMAL(5,2),
    p_tenure_months INT,
    OUT p_emi DECIMAL(18,2),
    OUT p_total_interest DECIMAL(18,2),
    OUT p_total_payment DECIMAL(18,2)
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_monthly_rate DECIMAL(10,8);
    v_rate_plus_one DECIMAL(10,8);
    v_power DECIMAL(20,8);
    v_numerator DECIMAL(20,8);
    v_denominator DECIMAL(20,8);
BEGIN
    v_monthly_rate := p_annual_rate / 12.0 / 100.0;
    v_rate_plus_one := 1.0 + v_monthly_rate;
    v_power := v_rate_plus_one ^ p_tenure_months;

    v_numerator := p_principal * v_monthly_rate * v_power;
    v_denominator := v_power - 1.0;

    p_emi := ROUND(v_numerator / v_denominator, 2);
    p_total_payment := p_emi * p_tenure_months;
    p_total_interest := p_total_payment - p_principal;
END;
$$;

-- ============================================================================
-- SP6: FRAUD DETECTION - Velocity Check
-- Checks: Multiple transactions in short time, unusual amount patterns
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_fraud_check(
    p_account_number VARCHAR(20),
    p_amount DECIMAL(18,2),
    OUT p_is_suspicious BOOLEAN,
    OUT p_risk_score INT,
    OUT p_reasons TEXT[]
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_txn_count_1hr INT;
    v_txn_count_24hr INT;
    v_total_amount_1hr DECIMAL(18,2);
    v_avg_amount DECIMAL(18,2);
    v_max_amount DECIMAL(18,2);
    v_account_id BIGINT;
BEGIN
    p_is_suspicious := false;
    p_risk_score := 0;
    p_reasons := ARRAY[]::TEXT[];

    SELECT id INTO v_account_id FROM accounts WHERE account_number = p_account_number;

    IF v_account_id IS NULL THEN
        p_is_suspicious := true;
        p_risk_score := 100;
        p_reasons := array_append(p_reasons, 'Account not found');
        RETURN;
    END IF;

    -- Check 1: Transaction count in last 1 hour
    SELECT COUNT(*), COALESCE(SUM(amount), 0)
    INTO v_txn_count_1hr, v_total_amount_1hr
    FROM transactions
    WHERE account_id = v_account_id
    AND transaction_date > NOW() - INTERVAL '1 hour'
    AND status = 'COMPLETED';

    IF v_txn_count_1hr >= 5 THEN
        p_risk_score := p_risk_score + 30;
        p_reasons := array_append(p_reasons, 'High frequency: ' || v_txn_count_1hr || ' txns in 1hr');
    END IF;

    -- Check 2: Transaction count in last 24 hours
    SELECT COUNT(*) INTO v_txn_count_24hr
    FROM transactions
    WHERE account_id = v_account_id
    AND transaction_date > NOW() - INTERVAL '24 hours'
    AND status = 'COMPLETED';

    IF v_txn_count_24hr >= 20 THEN
        p_risk_score := p_risk_score + 25;
        p_reasons := array_append(p_reasons, 'Very high frequency: ' || v_txn_count_24hr || ' txns in 24hr');
    END IF;

    -- Check 3: Amount is 5x average transaction
    SELECT AVG(amount), MAX(amount)
    INTO v_avg_amount, v_max_amount
    FROM transactions
    WHERE account_id = v_account_id
    AND transaction_date > NOW() - INTERVAL '30 days'
    AND status = 'COMPLETED';

    IF v_avg_amount > 0 AND p_amount > (v_avg_amount * 5) THEN
        p_risk_score := p_risk_score + 35;
        p_reasons := array_append(p_reasons, 'Amount ' || p_amount || ' is 5x avg (' || ROUND(v_avg_amount, 2) || ')');
    END IF;

    -- Check 4: Amount exceeds single transaction limit
    IF p_amount > (SELECT single_transaction_limit FROM accounts WHERE id = v_account_id) THEN
        p_risk_score := p_risk_score + 40;
        p_reasons := array_append(p_reasons, 'Exceeds single transaction limit');
    END IF;

    -- Check 5: Total in 1hr exceeds daily limit
    IF (v_total_amount_1hr + p_amount) > (SELECT daily_transaction_limit FROM accounts WHERE id = v_account_id) THEN
        p_risk_score := p_risk_score + 40;
        p_reasons := array_append(p_reasons, 'Would exceed daily transaction limit');
    END IF;

    -- Check 6: Large amount without prior activity
    IF v_avg_amount IS NULL OR v_avg_amount = 0 THEN
        IF p_amount > 10000 THEN
            p_risk_score := p_risk_score + 20;
            p_reasons := array_append(p_reasons, 'Large amount on new/inactive account');
        END IF;
    END IF;

    -- Determine if suspicious (threshold: 50)
    IF p_risk_score >= 50 THEN
        p_is_suspicious := true;
    END IF;
END;
$$;

-- ============================================================================
-- SP7: SETTLEMENT BATCH PROCESSOR
-- Processes all pending settlements in batch
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_process_settlement_batch(
    OUT p_processed INT,
    OUT p_failed INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    r RECORD;
    v_settlement_status VARCHAR(20);
BEGIN
    p_processed := 0;
    p_failed := 0;

    FOR r IN
        SELECT p.id, p.payment_reference, p.account_id, p.amount, p.payment_method,
               p.beneficiary_account, p.beneficiary_ifsc
        FROM payments p
        WHERE p.status = 'PROCESSING'
        AND p.scheduled_date <= NOW()
        ORDER BY p.payment_date ASC
        LIMIT 100
    LOOP
        BEGIN
            -- Simulate settlement based on method
            CASE r.payment_method
                WHEN 'NEFT' THEN
                    v_settlement_status := 'COMPLETED';
                WHEN 'RTGS' THEN
                    v_settlement_status := 'COMPLETED';
                WHEN 'IMPS' THEN
                    v_settlement_status := 'COMPLETED';
                WHEN 'UPI' THEN
                    v_settlement_status := 'COMPLETED';
                ELSE
                    v_settlement_status := 'COMPLETED';
            END CASE;

            UPDATE payments
            SET status = v_settlement_status,
                completion_date = NOW(),
                updated_at = NOW()
            WHERE id = r.id;

            p_processed := p_processed + 1;

        EXCEPTION
            WHEN OTHERS THEN
                UPDATE payments
                SET status = 'FAILED',
                    failure_reason = SQLERRM,
                    updated_at = NOW()
                WHERE id = r.id;
                p_failed := p_failed + 1;
        END;
    END LOOP;
END;
$$;
