-- V9: Add missing BaseEntity columns that ddl-auto used to create

-- Users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Refresh tokens
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Customers
ALTER TABLE customers ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE customers ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Branches
ALTER TABLE branches ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE branches ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Accounts
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Transactions
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE transactions ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Beneficiaries
ALTER TABLE beneficiaries ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE beneficiaries ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Payments
ALTER TABLE payments ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE payments ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Cards
ALTER TABLE cards ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE cards ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Loans
ALTER TABLE loans ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE loans ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);

-- Audit logs (has its own id, not BaseEntity)
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS created_by VARCHAR(50);
ALTER TABLE audit_logs ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50);
