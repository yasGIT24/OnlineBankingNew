-- [AGENT GENERATED CODE - REQUIREMENT:US1-AC1,US1-AC2,US1-AC3,US1-AC4]
-- Database schema to support:
-- 1. Login functionality with immediate balance display
-- 2. Transaction status tracking (including pending)
-- 3. Multiple linked accounts
-- 4. Refresh functionality

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS bankmanagement;
USE bankmanagement;

-- Customer table - contains basic customer information
CREATE TABLE IF NOT EXISTS signup1 (
    form_no VARCHAR(20) PRIMARY KEY,
    name VARCHAR(50),
    father_name VARCHAR(50),
    mother_name VARCHAR(50),
    dob VARCHAR(20),
    gender VARCHAR(10),
    email VARCHAR(50),
    marital_status VARCHAR(15),
    address VARCHAR(100),
    city VARCHAR(30),
    state VARCHAR(30),
    nationality VARCHAR(30)
);

-- Additional customer details
CREATE TABLE IF NOT EXISTS signup2 (
    form_no VARCHAR(20) PRIMARY KEY,
    religion VARCHAR(20),
    category VARCHAR(20),
    income VARCHAR(20),
    education VARCHAR(30),
    occupation VARCHAR(20),
    pan VARCHAR(20),
    aadhar VARCHAR(20),
    senior_citizen VARCHAR(5),
    existing_account VARCHAR(5),
    FOREIGN KEY (form_no) REFERENCES signup1(form_no) ON DELETE CASCADE
);

-- Account information
CREATE TABLE IF NOT EXISTS signup3 (
    form_no VARCHAR(20) PRIMARY KEY,
    account_type VARCHAR(30),
    Account_No VARCHAR(20) UNIQUE,
    Login_Password VARCHAR(20),
    card_number VARCHAR(20),
    pin VARCHAR(10),
    service_required VARCHAR(100),
    FOREIGN KEY (form_no) REFERENCES signup2(form_no) ON DELETE CASCADE
);

-- Login information
CREATE TABLE IF NOT EXISTS login (
    form_no VARCHAR(20),
    Account_No VARCHAR(20) PRIMARY KEY,
    Login_Password VARCHAR(20),
    FOREIGN KEY (form_no) REFERENCES signup3(form_no) ON DELETE CASCADE
);

-- [AGENT GENERATED CODE - REQUIREMENT:US1-AC2]
-- Modified bank table to include transaction status
-- Added status column to track pending and completed transactions
CREATE TABLE IF NOT EXISTS bank (
    transaction_id VARCHAR(40) PRIMARY KEY,
    Account_No VARCHAR(20),
    date VARCHAR(50),
    type VARCHAR(20),
    amount VARCHAR(20),
    status VARCHAR(20) DEFAULT 'completed', -- Added status column (pending/completed)
    Login_Password VARCHAR(20),
    FOREIGN KEY (Account_No) REFERENCES login(Account_No) ON DELETE CASCADE
);

-- [AGENT GENERATED CODE - REQUIREMENT:US1-AC3]
-- New table to support multiple linked accounts for a customer
CREATE TABLE IF NOT EXISTS linked_accounts (
    link_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20),
    account_number VARCHAR(20),
    account_type VARCHAR(20),
    is_primary BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (account_number) REFERENCES login(Account_No) ON DELETE CASCADE
);

-- [AGENT GENERATED CODE - REQUIREMENT:US1-AC4]
-- Table to store refresh preferences
CREATE TABLE IF NOT EXISTS refresh_settings (
    Account_No VARCHAR(20) PRIMARY KEY,
    auto_refresh BOOLEAN DEFAULT TRUE,
    refresh_interval INT DEFAULT 60, -- seconds
    FOREIGN KEY (Account_No) REFERENCES login(Account_No) ON DELETE CASCADE
);

-- =========================================
-- Sample data for testing purposes
-- =========================================

-- Insert sample customer
INSERT INTO signup1 (form_no, name, father_name, mother_name, dob, gender, email, marital_status, address, city, state, nationality) 
VALUES ('100001', 'John Doe', 'Richard Doe', 'Mary Doe', '01-01-1990', 'Male', 'john@example.com', 'Single', '123 Main St', 'Mumbai', 'Maharashtra', 'Indian');

INSERT INTO signup2 (form_no, religion, category, income, education, occupation, pan, aadhar, senior_citizen, existing_account) 
VALUES ('100001', 'Hindu', 'General', '300000-500000', 'Graduate', 'Salaried', 'ABCDE1234F', '123456789012', 'No', 'No');

INSERT INTO signup3 (form_no, account_type, Account_No, Login_Password, card_number, pin, service_required) 
VALUES ('100001', 'Savings', 'ACC001', '1234', '5412751234567890', '5678', 'ATM Card, Internet Banking');

INSERT INTO login (form_no, Account_No, Login_Password) 
VALUES ('100001', 'ACC001', '1234');

-- Insert secondary account for the same customer
INSERT INTO signup1 (form_no, name, father_name, mother_name, dob, gender, email, marital_status, address, city, state, nationality) 
VALUES ('100002', 'John Doe', 'Richard Doe', 'Mary Doe', '01-01-1990', 'Male', 'john@example.com', 'Single', '123 Main St', 'Mumbai', 'Maharashtra', 'Indian');

INSERT INTO signup2 (form_no, religion, category, income, education, occupation, pan, aadhar, senior_citizen, existing_account) 
VALUES ('100002', 'Hindu', 'General', '300000-500000', 'Graduate', 'Salaried', 'ABCDE1234F', '123456789012', 'No', 'Yes');

INSERT INTO signup3 (form_no, account_type, Account_No, Login_Password, card_number, pin, service_required) 
VALUES ('100002', 'Current', 'ACC002', '1234', '5412751234567891', '5678', 'ATM Card, Internet Banking');

INSERT INTO login (form_no, Account_No, Login_Password) 
VALUES ('100002', 'ACC002', '1234');

-- [AGENT GENERATED CODE - REQUIREMENT:US1-AC3]
-- Link both accounts to the same customer
INSERT INTO linked_accounts (customer_id, account_number, account_type, is_primary) 
VALUES ('CUST001', 'ACC001', 'Savings', TRUE);

INSERT INTO linked_accounts (customer_id, account_number, account_type, is_primary) 
VALUES ('CUST001', 'ACC002', 'Current', FALSE);

-- [AGENT GENERATED CODE - REQUIREMENT:US1-AC2]
-- Sample transactions with status (completed/pending)
INSERT INTO bank (transaction_id, Account_No, date, type, amount, status, Login_Password) 
VALUES ('TXN001', 'ACC001', '01-01-2023', 'Deposit', '5000', 'completed', '1234');

INSERT INTO bank (transaction_id, Account_No, date, type, amount, status, Login_Password) 
VALUES ('TXN002', 'ACC001', '02-01-2023', 'Withdrawal', '1000', 'completed', '1234');

INSERT INTO bank (transaction_id, Account_No, date, type, amount, status, Login_Password) 
VALUES ('TXN003', 'ACC001', '03-01-2023', 'Deposit', '2500', 'pending', '1234');

INSERT INTO bank (transaction_id, Account_No, date, type, amount, status, Login_Password) 
VALUES ('TXN004', 'ACC002', '01-01-2023', 'Deposit', '10000', 'completed', '1234');

INSERT INTO bank (transaction_id, Account_No, date, type, amount, status, Login_Password) 
VALUES ('TXN005', 'ACC002', '02-01-2023', 'Withdrawal', '3000', 'completed', '1234');

INSERT INTO bank (transaction_id, Account_No, date, type, amount, status, Login_Password) 
VALUES ('TXN006', 'ACC002', '03-01-2023', 'Withdrawal', '1500', 'pending', '1234');

-- [AGENT GENERATED CODE - REQUIREMENT:US1-AC4]
-- Sample refresh settings
INSERT INTO refresh_settings (Account_No, auto_refresh, refresh_interval) 
VALUES ('ACC001', TRUE, 30);

INSERT INTO refresh_settings (Account_No, auto_refresh, refresh_interval) 
VALUES ('ACC002', TRUE, 60);

/* 
 * Test Cases: TC-US1-01, TC-US1-02, TC-US1-03, TC-US1-04, TC-US1-05, TC-US1-06
 * Agent Run ID: AR-2025-11-27-001
 */