-- V2: Seed Data

-- Insert Default Branch
INSERT INTO branches (branch_code, branch_name, address, city, state, pincode, phone, email, active, ifsc_code)
VALUES ('BR001', 'Main Branch', '123 Banking Street', 'Mumbai', 'Maharashtra', '400001', '02212345678', 'main@digitalbanking.com', true, 'DBIN0001');

INSERT INTO branches (branch_code, branch_name, address, city, state, pincode, phone, email, active, ifsc_code)
VALUES ('BR002', 'Delhi Branch', '456 Finance Road', 'New Delhi', 'Delhi', '110001', '01112345678', 'delhi@digitalbanking.com', true, 'DBIN0002');

INSERT INTO branches (branch_code, branch_name, address, city, state, pincode, phone, email, active, ifsc_code)
VALUES ('BR003', 'Bangalore Branch', '789 Tech Park', 'Bangalore', 'Karnataka', '560001', '08012345678', 'bangalore@digitalbanking.com', true, 'DBIN0003');

-- Insert Default Admin User (Password: admin123)
INSERT INTO users (email, username, password, first_name, last_name, phone, role, enabled, email_verified)
VALUES ('admin@digitalbanking.com', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'System', 'Administrator', '9999999999', 'ROLE_SUPER_ADMIN', true, true);

-- Insert Default Employee User (Password: employee123)
INSERT INTO users (email, username, password, first_name, last_name, phone, role, enabled, email_verified)
VALUES ('employee@digitalbanking.com', 'employee', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Test', 'Employee', '8888888888', 'ROLE_EMPLOYEE', true, true);

-- Insert Demo Customer User (Password: customer123)
INSERT INTO users (email, username, password, first_name, last_name, phone, role, enabled, email_verified)
VALUES ('customer@digitalbanking.com', 'customer', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Rajesh', 'Kumar', '7777777777', 'ROLE_CUSTOMER', true, true);

-- Insert Demo Customer Profile
INSERT INTO customers (user_id, customer_number, pan_number, date_of_birth, gender, address_line1, city, state, pincode, country, kyc_status, status)
VALUES (3, 'CUST001', 'ABCPK1234A', '1990-05-15', 'MALE', '123 Customer Street', 'Mumbai', 'Maharashtra', '400001', 'India', 'VERIFIED', 'ACTIVE');

-- Insert Demo Account
INSERT INTO accounts (account_number, customer_id, branch_id, account_type, status, balance, available_balance, ifsc_code, opened_date)
VALUES ('DB0000000001', 1, 1, 'SAVINGS', 'ACTIVE', 50000.00, 50000.00, 'DBIN0001', '2024-01-01');

INSERT INTO accounts (account_number, customer_id, branch_id, account_type, status, balance, available_balance, ifsc_code, opened_date)
VALUES ('DB0000000002', 1, 1, 'CURRENT', 'ACTIVE', 150000.00, 150000.00, 'DBIN0001', '2024-01-15');
