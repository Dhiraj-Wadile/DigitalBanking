-- V1: Initial Schema

-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(500) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    enabled BOOLEAN NOT NULL DEFAULT false,
    account_locked BOOLEAN NOT NULL DEFAULT false,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    last_login_at TIMESTAMP,
    profile_image_url VARCHAR(500),
    mfa_enabled BOOLEAN NOT NULL DEFAULT false,
    mfa_secret VARCHAR(100),
    reset_token VARCHAR(500),
    reset_token_expiry TIMESTAMP,
    email_verification_token VARCHAR(500),
    email_verified BOOLEAN DEFAULT false,
    otp_code VARCHAR(10),
    otp_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0
);

-- Refresh Tokens Table
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    device_info VARCHAR(200),
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Device Sessions Table
CREATE TABLE device_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    device_fingerprint VARCHAR(500) NOT NULL,
    device_name VARCHAR(200),
    browser VARCHAR(100),
    operating_system VARCHAR(100),
    ip_address VARCHAR(50),
    location VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    last_active_at TIMESTAMP,
    trusted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    version BIGINT DEFAULT 0
);

-- Customers Table
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id),
    customer_number VARCHAR(20) UNIQUE NOT NULL,
    pan_number VARCHAR(20),
    aadhaar_number VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    country VARCHAR(100),
    kyc_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    occupation VARCHAR(200),
    annual_income BIGINT,
    document_url VARCHAR(500),
    profile_image_url VARCHAR(500),
    nominee_added BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Branches Table
CREATE TABLE branches (
    id BIGSERIAL PRIMARY KEY,
    branch_code VARCHAR(10) UNIQUE NOT NULL,
    branch_name VARCHAR(200) NOT NULL,
    address VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    ifsc_code VARCHAR(10),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    version BIGINT DEFAULT 0
);

-- Accounts Table
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    branch_id BIGINT REFERENCES branches(id),
    account_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    available_balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    hold_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    interest_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    daily_transaction_limit DECIMAL(18,2) NOT NULL DEFAULT 500000,
    single_transaction_limit DECIMAL(18,2) NOT NULL DEFAULT 100000,
    dormant BOOLEAN NOT NULL DEFAULT false,
    last_transaction_date DATE,
    ifsc_code VARCHAR(10),
    swift_code VARCHAR(20),
    opened_date DATE,
    closed_date DATE,
    overdraft_enabled BOOLEAN NOT NULL DEFAULT false,
    overdraft_limit DECIMAL(18,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Transactions Table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    reference_number VARCHAR(50) UNIQUE NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    transaction_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(18,2) NOT NULL,
    balance_after DECIMAL(18,2) NOT NULL,
    description VARCHAR(500),
    channel VARCHAR(50),
    counterparty_name VARCHAR(200),
    counterparty_account VARCHAR(20),
    counterparty_ifsc VARCHAR(20),
    upi_id VARCHAR(50),
    metadata VARCHAR(500),
    transaction_date TIMESTAMP,
    value_date TIMESTAMP,
    failure_reason VARCHAR(500),
    idempotency_key VARCHAR(50),
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Beneficiaries Table
CREATE TABLE beneficiaries (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    name VARCHAR(100) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    ifsc_code VARCHAR(10) NOT NULL,
    bank_name VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    type VARCHAR(20) DEFAULT 'BANK_TRANSFER',
    verified BOOLEAN NOT NULL DEFAULT false,
    active BOOLEAN NOT NULL DEFAULT true,
    upi_id VARCHAR(50),
    transfer_count INTEGER DEFAULT 0,
    total_transferred BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Payments Table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    payment_reference VARCHAR(50) UNIQUE NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    payment_type VARCHAR(30) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    amount DECIMAL(18,2) NOT NULL,
    description VARCHAR(500),
    beneficiary_name VARCHAR(100),
    beneficiary_account VARCHAR(20),
    beneficiary_ifsc VARCHAR(10),
    upi_id VARCHAR(50),
    upi_transaction_id VARCHAR(500),
    bank_transaction_id VARCHAR(500),
    scheduled BOOLEAN NOT NULL DEFAULT false,
    scheduled_date TIMESTAMP,
    recurring BOOLEAN NOT NULL DEFAULT false,
    recurring_frequency VARCHAR(30),
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    failure_reason VARCHAR(200),
    idempotency_key VARCHAR(50),
    payment_date TIMESTAMP,
    completion_date TIMESTAMP,
    fee DECIMAL(18,2) DEFAULT 0,
    tax DECIMAL(18,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Cards Table
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(20) UNIQUE NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    card_type VARCHAR(20) NOT NULL,
    card_network VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    card_holder_name VARCHAR(100) NOT NULL,
    expiry_month VARCHAR(4) NOT NULL,
    expiry_year VARCHAR(4) NOT NULL,
    cvv_hash VARCHAR(100) NOT NULL,
    pin_hash VARCHAR(100) NOT NULL,
    daily_limit DECIMAL(18,2) NOT NULL DEFAULT 100000,
    monthly_limit DECIMAL(18,2) NOT NULL DEFAULT 1000000,
    single_transaction_limit DECIMAL(18,2) NOT NULL DEFAULT 50000,
    international_enabled BOOLEAN NOT NULL DEFAULT false,
    online_enabled BOOLEAN NOT NULL DEFAULT true,
    tap_to_pay_enabled BOOLEAN NOT NULL DEFAULT true,
    atm_enabled BOOLEAN NOT NULL DEFAULT true,
    pos_enabled BOOLEAN NOT NULL DEFAULT true,
    contactless_enabled BOOLEAN NOT NULL DEFAULT true,
    is_virtual BOOLEAN NOT NULL DEFAULT false,
    issued_date DATE,
    blocked_date DATE,
    failed_pin_attempts INTEGER DEFAULT 0,
    pin_set BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Loans Table
CREATE TABLE loans (
    id BIGSERIAL PRIMARY KEY,
    loan_account_number VARCHAR(30) UNIQUE NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    loan_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_APPROVAL',
    sanctioned_amount DECIMAL(18,2) NOT NULL,
    disbursed_amount DECIMAL(18,2) DEFAULT 0,
    outstanding_amount DECIMAL(18,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INTEGER NOT NULL,
    emi_amount DECIMAL(18,2) NOT NULL,
    processing_fee DECIMAL(18,2) DEFAULT 0,
    application_date DATE,
    approval_date DATE,
    disbursement_date DATE,
    first_emi_date DATE,
    closure_date DATE,
    emis_paid INTEGER DEFAULT 0,
    emis_remaining INTEGER,
    total_interest_paid DECIMAL(18,2) DEFAULT 0,
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Notifications Table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    type VARCHAR(30) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP,
    action_url VARCHAR(500),
    metadata VARCHAR(500),
    sent BOOLEAN NOT NULL DEFAULT false,
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- KYC Documents Table
CREATE TABLE kyc_documents (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    document_type VARCHAR(30) NOT NULL,
    document_url VARCHAR(500) NOT NULL,
    document_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verified_by VARCHAR(500),
    verified_at TIMESTAMP,
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Audit Logs Table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    old_values VARCHAR(5000),
    new_values VARCHAR(5000),
    ip_address VARCHAR(200),
    device_info VARCHAR(500),
    session_id VARCHAR(100),
    action_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'SUCCESS',
    failure_reason VARCHAR(2000)
);

-- Wallets Table
CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    wallet_number VARCHAR(30) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    wallet_name VARCHAR(100) NOT NULL,
    balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    daily_limit DECIMAL(18,2) NOT NULL DEFAULT 100000,
    monthly_limit DECIMAL(18,2) NOT NULL DEFAULT 500000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    upi_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Nominees Table
CREATE TABLE nominees (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    name VARCHAR(100) NOT NULL,
    relation VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(100),
    percentage DOUBLE PRECISION NOT NULL,
    address VARCHAR(200),
    pan_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Indexes
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_refresh_token ON refresh_tokens(token);
CREATE INDEX idx_device_session ON device_sessions(user_id, device_fingerprint);
CREATE INDEX idx_customer_user ON customers(user_id);
CREATE INDEX idx_customer_pan ON customers(pan_number);
CREATE INDEX idx_customer_aadhaar ON customers(aadhaar_number);
CREATE INDEX idx_account_number ON accounts(account_number);
CREATE INDEX idx_account_customer ON accounts(customer_id);
CREATE INDEX idx_account_branch ON accounts(branch_id);
CREATE INDEX idx_txn_reference ON transactions(reference_number);
CREATE INDEX idx_txn_account ON transactions(account_id);
CREATE INDEX idx_txn_date ON transactions(transaction_date);
CREATE INDEX idx_txn_type ON transactions(transaction_type);
CREATE INDEX idx_beneficiary_customer ON beneficiaries(customer_id);
CREATE INDEX idx_beneficiary_account ON beneficiaries(account_number);
CREATE INDEX idx_payment_reference ON payments(payment_reference);
CREATE INDEX idx_payment_account ON payments(account_id);
CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_date ON payments(payment_date);
CREATE INDEX idx_card_number ON cards(card_number);
CREATE INDEX idx_card_account ON cards(account_id);
CREATE INDEX idx_loan_account ON loans(account_id);
CREATE INDEX idx_loan_status ON loans(status);
CREATE INDEX idx_notif_user ON notifications(user_id);
CREATE INDEX idx_notif_read ON notifications(is_read);
CREATE INDEX idx_kyc_customer ON kyc_documents(customer_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_date ON audit_logs(action_date);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type);
CREATE INDEX idx_wallet_customer ON wallets(customer_id);
CREATE INDEX idx_wallet_number ON wallets(wallet_number);
CREATE INDEX idx_nominee_customer ON nominees(customer_id);
