-- V7: Fix invalid BCrypt password hashes from V2 + add missing user and profile

-- Fix password hashes (V2 had truncated/invalid hashes)
UPDATE users SET password = '$2b$10$GXMPVqW5u6/o7M5uAqtJKO54LlEJHtu6AWEYJh/bwTKeNW6pPk0Dm' WHERE username = 'admin';
UPDATE users SET password = '$2b$10$4PdiUdtWKY8t51kIvA7TLuU4ZT8dd1aF16aTB2huR/8.MxHjWYbIG' WHERE username = 'employee';
UPDATE users SET password = '$2b$10$ZahQjotUQv6xuiMfCABHjO0peRHEqM5GZ/kUkookeZ6pMZIcJp4mC' WHERE username = 'customer';

-- Add second customer user (priya)
INSERT INTO users (email, username, password, first_name, last_name, phone, role, enabled, email_verified)
VALUES ('priya@example.com', 'priya', '$2b$10$ZahQjotUQv6xuiMfCABHjO0peRHEqM5GZ/kUkookeZ6pMZIcJp4mC', 'Priya', 'Sharma', '6666666666', 'ROLE_CUSTOMER', true, true);

-- Add second customer profile
INSERT INTO customers (user_id, customer_number, pan_number, date_of_birth, gender, address_line1, city, state, pincode, country, kyc_status, status, occupation, annual_income, nominee_added)
VALUES (4, 'CUST002', 'XYZPR5678B', '1988-03-22', 'FEMALE', '456 Park Avenue', 'Delhi', 'Delhi', '110001', 'India', 'VERIFIED', 'ACTIVE', 'Business Analyst', 1200000, false);

-- Add priya's account
INSERT INTO accounts (account_number, customer_id, branch_id, account_type, status, balance, available_balance, ifsc_code, opened_date)
VALUES ('DB0000000003', 2, 2, 'SAVINGS', 'ACTIVE', 75000.00, 75000.00, 'DBIN0002', '2024-02-01');
