-- V8: Seed demo transactions, payments, cards, loans, beneficiaries

-- Fix IFSC columns to accommodate 11-char Indian IFSC codes
ALTER TABLE branches ALTER COLUMN ifsc_code TYPE VARCHAR(20);
ALTER TABLE beneficiaries ALTER COLUMN ifsc_code TYPE VARCHAR(20);
ALTER TABLE payments ALTER COLUMN beneficiary_ifsc TYPE VARCHAR(20);

-- ============================================================
-- TRANSACTIONS (account_id references: 1=DB0000000001, 2=DB0000000002, 3=DB0000000003)
-- ============================================================

-- Customer 1 (Rajesh) - Account 1 (SAVINGS)
INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount, balance_after, description, channel, counterparty_name, counterparty_account, transaction_date, created_at, updated_at, deleted)
VALUES
('TXN-20260601-001', 1, 'DEPOSIT', 'COMPLETED', 25000.00, 74900.00, 'Salary credit from TCS', 'NEFT', 'TCS Ltd', 'ICIC0001234', '2026-06-01 09:30:00', '2026-06-01 09:30:00', '2026-06-01 09:30:00', false),
('TXN-20260605-002', 1, 'TRANSFER_OUT', 'COMPLETED', 5000.00, 69900.00, 'Transfer to Priya Sharma', 'IMPS', 'Priya Sharma', 'DB0000000003', '2026-06-05 14:15:00', '2026-06-05 14:15:00', '2026-06-05 14:15:00', false),
('TXN-20260610-003', 1, 'UPI_PAY', 'COMPLETED', 1200.00, 68700.00, 'Zomato order payment', 'UPI', 'Zomato', NULL, '2026-06-10 20:00:00', '2026-06-10 20:00:00', '2026-06-10 20:00:00', false),
('TXN-20260615-004', 1, 'INTEREST_CREDIT', 'COMPLETED', 350.00, 69050.00, 'Monthly interest credit', 'SYSTEM', NULL, NULL, '2026-06-15 00:00:00', '2026-06-15 00:00:00', '2026-06-15 00:00:00', false),
('TXN-20260620-005', 1, 'WITHDRAWAL', 'COMPLETED', 3000.00, 66050.00, 'ATM withdrawal at Andheri', 'ATM', NULL, NULL, '2026-06-20 11:45:00', '2026-06-20 11:45:00', '2026-06-20 11:45:00', false),
('TXN-20260625-006', 1, 'DEPOSIT', 'COMPLETED', 15000.00, 81050.00, 'Freelance project payment', 'NEFT', 'Acme Corp', 'HDFC0005678', '2026-06-25 16:00:00', '2026-06-25 16:00:00', '2026-06-25 16:00:00', false),
('TXN-20260628-007', 1, 'FEE_DEBIT', 'COMPLETED', 150.00, 80900.00, 'Account maintenance fee', 'SYSTEM', NULL, NULL, '2026-06-28 00:00:00', '2026-06-28 00:00:00', '2026-06-28 00:00:00', false),
('TXN-20260701-008', 1, 'DEPOSIT', 'COMPLETED', 25000.00, 105900.00, 'Salary credit from TCS', 'NEFT', 'TCS Ltd', 'ICIC0001234', '2026-07-01 09:30:00', '2026-07-01 09:30:00', '2026-07-01 09:30:00', false),
('TXN-20260703-009', 1, 'TRANSFER_OUT', 'COMPLETED', 10000.00, 95900.00, 'Rent payment to landlord', 'NEFT', 'Suresh Patil', 'SBIN0001111', '2026-07-03 10:00:00', '2026-07-03 10:00:00', '2026-07-03 10:00:00', false),
('TXN-20260705-010', 1, 'CARD_PAYMENT', 'COMPLETED', 2500.00, 93400.00, 'Amazon shopping', 'CARD', 'Amazon India', NULL, '2026-07-05 18:30:00', '2026-07-05 18:30:00', '2026-07-05 18:30:00', false);

-- Customer 1 (Rajesh) - Account 2 (CURRENT)
INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount, balance_after, description, channel, counterparty_name, counterparty_account, transaction_date, created_at, updated_at, deleted)
VALUES
('TXN-20260601-011', 2, 'DEPOSIT', 'COMPLETED', 50000.00, 200000.00, 'Business income - June', 'RTGS', 'Infotech Solutions', 'KKBK0002222', '2026-06-01 11:00:00', '2026-06-01 11:00:00', '2026-06-01 11:00:00', false),
('TXN-20260608-012', 2, 'TRANSFER_OUT', 'COMPLETED', 25000.00, 175000.00, 'Vendor payment - June', 'RTGS', 'Rajan Enterprises', 'PUNB0003333', '2026-06-08 13:00:00', '2026-06-08 13:00:00', '2026-06-08 13:00:00', false),
('TXN-20260612-013', 2, 'UPI_PAY', 'COMPLETED', 3500.00, 171500.00, 'Office supplies - Amazon', 'UPI', 'Amazon Business', NULL, '2026-06-12 15:30:00', '2026-06-12 15:30:00', '2026-06-12 15:30:00', false),
('TXN-20260618-014', 2, 'DEPOSIT', 'COMPLETED', 30000.00, 201500.00, 'Client payment - Project Alpha', 'NEFT', 'GlobalTech', 'UBIN0004444', '2026-06-18 10:00:00', '2026-06-18 10:00:00', '2026-06-18 10:00:00', false),
('TXN-20260622-015', 2, 'WITHDRAWAL', 'COMPLETED', 10000.00, 191500.00, 'Cash withdrawal for office', 'ATM', NULL, NULL, '2026-06-22 09:00:00', '2026-06-22 09:00:00', '2026-06-22 09:00:00', false),
('TXN-20260630-016', 2, 'TAX_DEBIT', 'COMPLETED', 5000.00, 186500.00, 'GST payment - June', 'SYSTEM', NULL, NULL, '2026-06-30 00:00:00', '2026-06-30 00:00:00', '2026-06-30 00:00:00', false),
('TXN-20260702-017', 2, 'DEPOSIT', 'COMPLETED', 45000.00, 231500.00, 'Business income - July', 'RTGS', 'Infotech Solutions', 'KKBK0002222', '2026-07-02 11:00:00', '2026-07-02 11:00:00', '2026-07-02 11:00:00', false),
('TXN-20260705-018', 2, 'TRANSFER_OUT', 'COMPLETED', 20000.00, 211500.00, 'Staff salary advance', 'NEFT', 'Vikram Joshi', 'BARB0005555', '2026-07-05 14:00:00', '2026-07-05 14:00:00', '2026-07-05 14:00:00', false);

-- Customer 2 (Priya) - Account 3 (SAVINGS)
INSERT INTO transactions (reference_number, account_id, transaction_type, status, amount, balance_after, description, channel, counterparty_name, counterparty_account, transaction_date, created_at, updated_at, deleted)
VALUES
('TXN-20260602-019', 3, 'DEPOSIT', 'COMPLETED', 30000.00, 105000.00, 'Salary credit - Infosys', 'NEFT', 'Infosys Ltd', 'SBIN0006666', '2026-06-02 09:00:00', '2026-06-02 09:00:00', '2026-06-02 09:00:00', false),
('TXN-20260607-020', 3, 'TRANSFER_OUT', 'COMPLETED', 8000.00, 97000.00, 'Transfer to Rajesh Kumar', 'IMPS', 'Rajesh Kumar', 'DB0000000001', '2026-06-07 18:00:00', '2026-06-07 18:00:00', '2026-06-07 18:00:00', false),
('TXN-20260614-021', 3, 'UPI_PAY', 'COMPLETED', 2000.00, 95000.00, 'Swiggy food order', 'UPI', 'Swiggy', NULL, '2026-06-14 20:15:00', '2026-06-14 20:15:00', '2026-06-14 20:15:00', false),
('TXN-20260620-022', 3, 'DEPOSIT', 'COMPLETED', 5000.00, 100000.00, 'Freelance design project', 'IMPS', 'DesignHub', 'ICIC0007777', '2026-06-20 14:00:00', '2026-06-20 14:00:00', '2026-06-20 14:00:00', false),
('TXN-20260626-023', 3, 'CARD_PAYMENT', 'COMPLETED', 4500.00, 95500.00, 'Myntra shopping', 'CARD', 'Myntra', NULL, '2026-06-26 12:00:00', '2026-06-26 12:00:00', '2026-06-26 12:00:00', false),
('TXN-20260701-024', 3, 'DEPOSIT', 'COMPLETED', 30000.00, 125500.00, 'Salary credit - Infosys', 'NEFT', 'Infosys Ltd', 'SBIN0006666', '2026-07-01 09:00:00', '2026-07-01 09:00:00', '2026-07-01 09:00:00', false),
('TXN-20260704-025', 3, 'WITHDRAWAL', 'COMPLETED', 5000.00, 120500.00, 'ATM withdrawal - Bandra', 'ATM', NULL, NULL, '2026-07-04 16:00:00', '2026-07-04 16:00:00', '2026-07-04 16:00:00', false),
('TXN-20260706-026', 3, 'TRANSFER_OUT', 'COMPLETED', 10000.00, 110500.00, 'Rent payment', 'NEFT', 'Landlord - Meera', 'HDFC0008888', '2026-07-06 10:00:00', '2026-07-06 10:00:00', '2026-07-06 10:00:00', false);

-- ============================================================
-- PAYMENTS
-- ============================================================

-- Customer 1 payments
INSERT INTO payments (payment_reference, account_id, payment_type, payment_method, status, amount, description, beneficiary_name, beneficiary_account, beneficiary_ifsc, payment_date, fee, tax, created_at, updated_at, deleted)
VALUES
('PAY-20260601-001', 1, 'BILL_PAYMENT', 'UPI', 'COMPLETED', 2500.00, 'Electricity bill - Adani Power', 'Adani Power', NULL, 'ADAN0001', '2026-06-01 10:00:00', 0, 0, '2026-06-01 10:00:00', '2026-06-01 10:00:00', false),
('PAY-20260605-002', 1, 'RECHARGE', 'UPI', 'COMPLETED', 599.00, 'Jio prepaid recharge', 'Reliance Jio', NULL, NULL, '2026-06-05 12:00:00', 0, 0, '2026-06-05 12:00:00', '2026-06-05 12:00:00', false),
('PAY-20260615-003', 1, 'P2P', 'IMPS', 'COMPLETED', 3000.00, 'Dinner split with friends', 'Amit Deshmukh', 'SBIN0009999', 'SBIN0009999', '2026-06-15 21:00:00', 5.00, 0.90, '2026-06-15 21:00:00', '2026-06-15 21:00:00', false),
('PAY-20260620-004', 1, 'BILL_PAYMENT', 'NET_BANKING', 'COMPLETED', 1800.00, 'Broadband bill - Airtel', 'Airtel Broadband', NULL, NULL, '2026-06-20 09:00:00', 0, 0, '2026-06-20 09:00:00', '2026-06-20 09:00:00', false),
('PAY-20260701-005', 1, 'LOAN_EMI', 'NEFT', 'COMPLETED', 8500.00, 'Home loan EMI - July', 'HDFC Home Loans', 'HDFC0010010', 'HDFC0010010', '2026-07-01 06:00:00', 0, 0, '2026-07-01 06:00:00', '2026-07-01 06:00:00', false),
('PAY-20260703-006', 1, 'SUBSCRIPTION', 'CARD', 'COMPLETED', 1499.00, 'Netflix annual plan', 'Netflix', NULL, NULL, '2026-07-03 00:00:00', 0, 0, '2026-07-03 00:00:00', '2026-07-03 00:00:00', false);

-- Customer 2 payments
INSERT INTO payments (payment_reference, account_id, payment_type, payment_method, status, amount, description, beneficiary_name, beneficiary_account, beneficiary_ifsc, payment_date, fee, tax, created_at, updated_at, deleted)
VALUES
('PAY-20260603-007', 3, 'BILL_PAYMENT', 'UPI', 'COMPLETED', 1800.00, 'Electricity bill - BEST', 'BEST Mumbai', NULL, NULL, '2026-06-03 10:00:00', 0, 0, '2026-06-03 10:00:00', '2026-06-03 10:00:00', false),
('PAY-20260610-008', 3, 'P2P', 'UPI', 'COMPLETED', 2000.00, 'Gift for mom birthday', 'Sunita Sharma', 'SBIN0011111', NULL, '2026-06-10 18:00:00', 0, 0, '2026-06-10 18:00:00', '2026-06-10 18:00:00', false),
('PAY-20260625-009', 3, 'RECHARGE', 'UPI', 'PENDING', 399.00, 'Airtel prepaid recharge', 'Airtel', NULL, NULL, '2026-06-25 14:00:00', 0, 0, '2026-06-25 14:00:00', '2026-06-25 14:00:00', false),
('PAY-20260702-010', 3, 'BILL_PAYMENT', 'NET_BANKING', 'COMPLETED', 1200.00, 'Water bill - BMC', 'BMC Water Dept', NULL, NULL, '2026-07-02 11:00:00', 0, 0, '2026-07-02 11:00:00', '2026-07-02 11:00:00', false);

-- ============================================================
-- CARDS
-- ============================================================

-- Customer 1 cards
INSERT INTO cards (card_number, account_id, card_type, card_network, status, card_holder_name, expiry_month, expiry_year, cvv_hash, pin_hash, daily_limit, monthly_limit, single_transaction_limit, international_enabled, online_enabled, tap_to_pay_enabled, is_virtual, issued_date, created_at, updated_at, deleted)
VALUES
('4532015112830366', 1, 'DEBIT', 'VISA', 'ACTIVE', 'Rajesh Kumar', '12', '2030', '$2a$10$placeholder_cvv1', '$2a$10$placeholder_pin1', 200000.00, 2000000.00, 100000.00, false, true, true, false, '2024-01-15', '2024-01-15 00:00:00', '2024-01-15 00:00:00', false),
('5425233430109903', 2, 'CREDIT', 'MASTERCARD', 'ACTIVE', 'Rajesh Kumar', '06', '2029', '$2a$10$placeholder_cvv2', '$2a$10$placeholder_pin2', 300000.00, 3000000.00, 150000.00, true, true, true, false, '2024-03-20', '2024-03-20 00:00:00', '2024-03-20 00:00:00', false),
('6069850000000001', 1, 'VIRTUAL', 'VISA', 'ACTIVE', 'Rajesh Kumar', '01', '2028', '$2a$10$placeholder_cvv3', '$2a$10$placeholder_pin3', 50000.00, 200000.00, 25000.00, false, true, false, true, '2026-01-01', '2026-01-01 00:00:00', '2026-01-01 00:00:00', false);

-- Customer 2 cards
INSERT INTO cards (card_number, account_id, card_type, card_network, status, card_holder_name, expiry_month, expiry_year, cvv_hash, pin_hash, daily_limit, monthly_limit, single_transaction_limit, international_enabled, online_enabled, tap_to_pay_enabled, is_virtual, issued_date, created_at, updated_at, deleted)
VALUES
('4916338506082832', 3, 'DEBIT', 'VISA', 'ACTIVE', 'Priya Sharma', '08', '2031', '$2a$10$placeholder_cvv4', '$2a$10$placeholder_pin4', 200000.00, 2000000.00, 100000.00, true, true, true, false, '2024-06-10', '2024-06-10 00:00:00', '2024-06-10 00:00:00', false),
('5191338833331009', 3, 'CREDIT', 'MASTERCARD', 'BLOCKED', 'Priya Sharma', '11', '2028', '$2a$10$placeholder_cvv5', '$2a$10$placeholder_pin5', 250000.00, 2500000.00, 125000.00, false, true, true, false, '2025-01-15', '2025-01-15 00:00:00', '2026-06-20 00:00:00', false);

-- ============================================================
-- LOANS
-- ============================================================

-- Customer 1 loans
INSERT INTO loans (loan_account_number, account_id, loan_type, status, sanctioned_amount, disbursed_amount, outstanding_amount, interest_rate, tenure_months, emi_amount, processing_fee, application_date, approval_date, disbursement_date, first_emi_date, emis_paid, emis_remaining, total_interest_paid, created_at, updated_at, deleted)
VALUES
('LN0000000001', 1, 'HOME', 'ACTIVE', 2500000.00, 2500000.00, 2100000.00, 8.50, 240, 21250.00, 25000.00, '2024-01-10', '2024-01-25', '2024-02-01', '2024-03-01', 17, 223, 145000.00, '2024-01-10 00:00:00', '2026-07-01 00:00:00', false),
('LN0000000002', 2, 'PERSONAL', 'ACTIVE', 500000.00, 500000.00, 320000.00, 12.00, 36, 16667.00, 5000.00, '2025-06-01', '2025-06-10', '2025-06-15', '2025-07-01', 12, 24, 60000.00, '2025-06-01 00:00:00', '2026-07-01 00:00:00', false);

-- Customer 2 loans
INSERT INTO loans (loan_account_number, account_id, loan_type, status, sanctioned_amount, disbursed_amount, outstanding_amount, interest_rate, tenure_months, emi_amount, processing_fee, application_date, approval_date, disbursement_date, first_emi_date, emis_paid, emis_remaining, total_interest_paid, created_at, updated_at, deleted)
VALUES
('LN0000000003', 3, 'CAR', 'ACTIVE', 800000.00, 800000.00, 560000.00, 9.25, 60, 16900.00, 8000.00, '2025-09-15', '2025-09-25', '2025-10-01', '2025-11-01', 9, 51, 45000.00, '2025-09-15 00:00:00', '2026-07-01 00:00:00', false);

-- ============================================================
-- BENEFICIARIES
-- ============================================================

-- Customer 1 (Rajesh) beneficiaries
INSERT INTO beneficiaries (customer_id, name, account_number, ifsc_code, bank_name, phone, type, verified, active, created_at, updated_at, deleted)
VALUES
(1, 'Priya Sharma', 'DB0000000003', 'DBIN0000003', 'Digital Bank', '9876543211', 'BANK_TRANSFER', true, true, '2026-01-15 00:00:00', '2026-01-15 00:00:00', false),
(1, 'Suresh Patil', 'SBIN0001111', 'SBIN0001111', 'State Bank of India', '9876543212', 'BANK_TRANSFER', true, true, '2026-02-01 00:00:00', '2026-02-01 00:00:00', false),
(1, 'Amit Deshmukh', 'HDFC0005678', 'HDFC0005678', 'HDFC Bank', '9876543213', 'BANK_TRANSFER', true, true, '2026-03-10 00:00:00', '2026-03-10 00:00:00', false),
(1, 'Rajan Enterprises', 'PUNB0003333', 'PUNB0003333', 'Punjab National Bank', '9876543214', 'BANK_TRANSFER', false, true, '2026-05-20 00:00:00', '2026-05-20 00:00:00', false);

-- Customer 2 (Priya) beneficiaries
INSERT INTO beneficiaries (customer_id, name, account_number, ifsc_code, bank_name, phone, type, verified, active, created_at, updated_at, deleted)
VALUES
(2, 'Rajesh Kumar', 'DB0000000001', 'DBIN0000001', 'Digital Bank', '9876543210', 'BANK_TRANSFER', true, true, '2026-01-20 00:00:00', '2026-01-20 00:00:00', false),
(2, 'Sunita Sharma', 'SBIN0011111', 'SBIN0011111', 'State Bank of India', '9876543215', 'BANK_TRANSFER', true, true, '2026-02-10 00:00:00', '2026-02-10 00:00:00', false),
(2, 'Infosys Salary', 'ICIC0007777', 'ICIC0007777', 'ICICI Bank', NULL, 'BANK_TRANSFER', true, true, '2026-03-01 00:00:00', '2026-03-01 00:00:00', false);
