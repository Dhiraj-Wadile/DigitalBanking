-- ============================================================================
-- V4: TRIGGERS - Database Triggers for Audit, Validation & Automation
-- ============================================================================

-- ============================================================================
-- TRIGGER FUNCTION 1: Audit Balance Changes
-- Fires BEFORE balance update on accounts table
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_audit_balance_change()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- Only log if balance actually changed
    IF OLD.balance IS DISTINCT FROM NEW.balance THEN
        INSERT INTO audit_logs (
            action, entity_type, entity_id,
            old_values, new_values, action_date, status, created_at
        ) VALUES (
            'BALANCE_CHANGE',
            'Account',
            NEW.id,
            json_build_object(
                'balance', OLD.balance,
                'available_balance', OLD.available_balance
            )::text,
            json_build_object(
                'balance', NEW.balance,
                'available_balance', NEW.available_balance,
                'change', NEW.balance - OLD.balance
            )::text,
            NOW(),
            'SUCCESS',
            NOW()
        );
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_audit_balance_change
    AFTER UPDATE OF balance ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_balance_change();


-- ============================================================================
-- TRIGGER FUNCTION 2: Prevent Negative Balance (BEFORE UPDATE)
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_prevent_negative_balance()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.balance < 0 THEN
        RAISE EXCEPTION 'Insufficient balance. Current: %, Attempted: %',
            OLD.balance, NEW.balance;
    END IF;
    IF NEW.available_balance < 0 THEN
        RAISE EXCEPTION 'Available balance cannot be negative. Current: %, Attempted: %',
            OLD.available_balance, NEW.available_balance;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_prevent_negative_balance
    BEFORE UPDATE OF balance, available_balance ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION fn_prevent_negative_balance();


-- ============================================================================
-- TRIGGER FUNCTION 3: Auto-update account status to DORMANT
-- If no transaction in 90 days, mark as dormant
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_check_dormant_account()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    -- If last transaction was more than 90 days ago
    IF NEW.last_transaction_date IS NOT NULL
       AND NEW.last_transaction_date < (CURRENT_DATE - INTERVAL '90 days')
       AND NEW.status = 'ACTIVE' THEN
        NEW.status := 'DORMANT';
        NEW.dormant := true;

        INSERT INTO audit_logs (action, entity_type, entity_id, new_values, action_date, status, created_at)
        VALUES ('ACCOUNT_DORMANT', 'Account', NEW.id,
            'Account marked dormant due to inactivity', NOW(), 'SUCCESS', NOW());
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_check_dormant_account
    BEFORE UPDATE OF last_transaction_date ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_dormant_account();


-- ============================================================================
-- TRIGGER FUNCTION 4: Auto-update customer KYC status
-- When KYC document is verified, update customer status
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_update_kyc_status()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.status = 'VERIFIED' AND OLD.status != 'VERIFIED' THEN
        UPDATE customers
        SET kyc_status = 'VERIFIED', updated_at = NOW()
        WHERE id = NEW.customer_id;

        INSERT INTO audit_logs (action, entity_type, entity_id, new_values, action_date, status, created_at)
        VALUES ('KYC_VERIFIED', 'Customer', NEW.customer_id,
            'KYC document verified: ' || NEW.document_type, NOW(), 'SUCCESS', NOW());
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_update_kyc_status
    AFTER UPDATE OF status ON kyc_documents
    FOR EACH ROW
    EXECUTE FUNCTION fn_update_kyc_status();


-- ============================================================================
-- TRIGGER FUNCTION 5: Auto-lock account after failed login attempts
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_check_failed_login()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.failed_login_attempts >= 5 AND NEW.account_locked = false THEN
        NEW.account_locked := true;
        NEW.locked_until := NOW() + INTERVAL '30 minutes';

        INSERT INTO audit_logs (action, entity_type, entity_id, new_values, action_date, status, created_at)
        VALUES ('ACCOUNT_LOCKED', 'User', NEW.id,
            'Account locked after ' || NEW.failed_login_attempts || ' failed attempts', NOW(), 'SUCCESS', NOW());
    END IF;

    -- Auto-unlock if lock period has expired
    IF NEW.account_locked = true AND NEW.locked_until IS NOT NULL
       AND NEW.locked_until < NOW() THEN
        NEW.account_locked := false;
        NEW.failed_login_attempts := 0;
        NEW.locked_until := null;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_check_failed_login
    BEFORE UPDATE OF failed_login_attempts ON users
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_failed_login();


-- ============================================================================
-- TRIGGER FUNCTION 6: Log all transaction status changes
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_audit_transaction_status()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
        INSERT INTO audit_logs (
            action, entity_type, entity_id, old_values, new_values,
            action_date, status, created_at
        ) VALUES (
            'TXN_STATUS_CHANGE',
            'Transaction',
            NEW.id,
            json_build_object('status', OLD.status)::text,
            json_build_object('status', NEW.status, 'amount', NEW.amount)::text,
            NOW(),
            CASE WHEN NEW.status = 'COMPLETED' THEN 'SUCCESS'
                 WHEN NEW.status = 'FAILED' THEN 'FAILED'
                 ELSE 'SUCCESS' END,
            NOW()
        );
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_audit_transaction_status
    AFTER UPDATE OF status ON transactions
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_transaction_status();


-- ============================================================================
-- TRIGGER FUNCTION 7: Auto-generate customer number
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_auto_generate_customer_number()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.customer_number IS NULL OR NEW.customer_number = '' THEN
        NEW.customer_number := 'CUST' || LPAD(NEXTVAL('customer_number_seq')::TEXT, 6, '0');
    END IF;
    RETURN NEW;
END;
$$;

-- Create sequence if not exists
CREATE SEQUENCE IF NOT EXISTS customer_number_seq START 100001;

CREATE TRIGGER trg_auto_generate_customer_number
    BEFORE INSERT ON customers
    FOR EACH ROW
    EXECUTE FUNCTION fn_auto_generate_customer_number();


-- ============================================================================
-- TRIGGER FUNCTION 8: Auto-generate account number
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_auto_generate_account_number()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.account_number IS NULL OR NEW.account_number = '' THEN
        NEW.account_number := 'DB' || LPAD(NEXTVAL('account_number_seq')::TEXT, 10, '0');
    END IF;
    RETURN NEW;
END;
$$;

CREATE SEQUENCE IF NOT EXISTS account_number_seq START 100001;

CREATE TRIGGER trg_auto_generate_account_number
    BEFORE INSERT ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION fn_auto_generate_account_number();


-- ============================================================================
-- TRIGGER FUNCTION 9: Update beneficiary transfer stats
-- After each completed transaction, update beneficiary stats
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_update_beneficiary_stats()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status != 'COMPLETED' THEN
        UPDATE beneficiaries
        SET transfer_count = transfer_count + 1,
            total_transferred = total_transferred + NEW.amount::BIGINT,
            updated_at = NOW()
        WHERE account_number = NEW.counterparty_account;

        -- Also update customer's last transaction date
        UPDATE customers
        SET updated_at = NOW()
        WHERE id = (SELECT customer_id FROM accounts WHERE id = NEW.account_id);
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_update_beneficiary_stats
    AFTER UPDATE OF status ON transactions
    FOR EACH ROW
    EXECUTE FUNCTION fn_update_beneficiary_stats();


-- ============================================================================
-- TRIGGER FUNCTION 10: Card transaction logging
-- When card is used, log the attempt
-- ============================================================================
CREATE OR REPLACE FUNCTION fn_card_usage_log()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.status = 'BLOCKED' AND OLD.status != 'BLOCKED' THEN
        INSERT INTO audit_logs (action, entity_type, entity_id, new_values, action_date, status, created_at)
        VALUES ('CARD_BLOCKED', 'Card', NEW.id,
            json_build_object('card_number', NEW.card_number, 'reason', 'Manual block')::text,
            NOW(), 'SUCCESS', NOW());
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_card_usage_log
    AFTER UPDATE OF status ON cards
    FOR EACH ROW
    EXECUTE FUNCTION fn_card_usage_log();
