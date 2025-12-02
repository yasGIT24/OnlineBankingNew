-- [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-04, REQ-WALLET-05, REQ-CURRENCY-04]
-- SQL schema updates for the new features

-- Table for storing statement download logs
CREATE TABLE IF NOT EXISTS statement_downloads (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_no VARCHAR(20) NOT NULL,
    download_date DATETIME NOT NULL,
    statement_period_start DATE NOT NULL,
    statement_period_end DATE NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    download_ip VARCHAR(50),
    download_status VARCHAR(20) NOT NULL,
    encrypted_link_id VARCHAR(255) NOT NULL,
    link_expiry DATETIME NOT NULL,
    INDEX idx_account_no (account_no),
    INDEX idx_download_date (download_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for storing digital wallet connections
CREATE TABLE IF NOT EXISTS wallet_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_no VARCHAR(20) NOT NULL,
    wallet_provider VARCHAR(50) NOT NULL,
    wallet_id VARCHAR(255) NOT NULL,
    wallet_name VARCHAR(100) NOT NULL,
    link_date DATETIME NOT NULL,
    auth_token VARCHAR(255),
    token_expiry DATETIME,
    refresh_token VARCHAR(255),
    wallet_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_sync_date DATETIME,
    INDEX idx_account_no (account_no),
    INDEX idx_wallet_provider (wallet_provider),
    UNIQUE KEY unique_account_wallet (account_no, wallet_provider),
    INDEX idx_wallet_status (wallet_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for recurring wallet transactions
CREATE TABLE IF NOT EXISTS recurring_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_no VARCHAR(20) NOT NULL,
    wallet_id INT,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    frequency VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    last_execution_date DATETIME,
    next_execution_date DATETIME NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_date DATETIME NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallet_accounts(id) ON DELETE SET NULL,
    INDEX idx_account_no (account_no),
    INDEX idx_next_execution (next_execution_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for storing wallet transactions
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    wallet_id INT NOT NULL,
    account_no VARCHAR(20) NOT NULL,
    transaction_date DATETIME NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    wallet_reference_id VARCHAR(255),
    bank_reference_id VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (wallet_id) REFERENCES wallet_accounts(id),
    INDEX idx_account_no (account_no),
    INDEX idx_transaction_date (transaction_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for storing currency metadata
CREATE TABLE IF NOT EXISTS currency_metadata (
    currency_code VARCHAR(3) PRIMARY KEY,
    currency_name VARCHAR(100) NOT NULL,
    decimal_places INT NOT NULL DEFAULT 2,
    symbol VARCHAR(10),
    country VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_updated DATETIME NOT NULL,
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for storing exchange rates
CREATE TABLE IF NOT EXISTS exchange_rates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(20,10) NOT NULL,
    rate_date DATETIME NOT NULL,
    provider VARCHAR(50) NOT NULL,
    FOREIGN KEY (source_currency) REFERENCES currency_metadata(currency_code),
    FOREIGN KEY (target_currency) REFERENCES currency_metadata(currency_code),
    UNIQUE KEY unique_rate (source_currency, target_currency, rate_date),
    INDEX idx_rate_date (rate_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table for storing currency conversion history
CREATE TABLE IF NOT EXISTS currency_conversions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_no VARCHAR(20) NOT NULL,
    conversion_date DATETIME NOT NULL,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    source_amount DECIMAL(15,2) NOT NULL,
    target_amount DECIMAL(15,2) NOT NULL,
    exchange_rate DECIMAL(20,10) NOT NULL,
    fee_amount DECIMAL(15,2) DEFAULT 0.00,
    reference_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (source_currency) REFERENCES currency_metadata(currency_code),
    FOREIGN KEY (target_currency) REFERENCES currency_metadata(currency_code),
    INDEX idx_account_no (account_no),
    INDEX idx_conversion_date (conversion_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert some initial currency data
INSERT IGNORE INTO currency_metadata (currency_code, currency_name, decimal_places, symbol, country, is_active, last_updated)
VALUES 
('USD', 'US Dollar', 2, '$', 'United States', TRUE, NOW()),
('EUR', 'Euro', 2, '€', 'Eurozone', TRUE, NOW()),
('GBP', 'British Pound', 2, '£', 'United Kingdom', TRUE, NOW()),
('JPY', 'Japanese Yen', 0, '¥', 'Japan', TRUE, NOW()),
('CAD', 'Canadian Dollar', 2, '$', 'Canada', TRUE, NOW()),
('AUD', 'Australian Dollar', 2, '$', 'Australia', TRUE, NOW()),
('CHF', 'Swiss Franc', 2, 'Fr', 'Switzerland', TRUE, NOW()),
('CNY', 'Chinese Yuan', 2, '¥', 'China', TRUE, NOW()),
('INR', 'Indian Rupee', 2, '₹', 'India', TRUE, NOW()),
('SGD', 'Singapore Dollar', 2, '$', 'Singapore', TRUE, NOW());

-- Agent run identifier: AGENT-DB-SCHEMA-2025-12-02