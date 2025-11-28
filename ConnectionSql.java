package banking.management.system;

import java.sql.*;

/**
 * @author Claude AI
 * Database connection utility class for the Online Banking System
 */
public class ConnectionSql {
    
    Connection c;
    Statement s;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:REQ001-REQ003]
     * Creating database connection for all features
     * This connection will be used by PDF statement downloads, digital wallets, and currency conversion
     */
    public ConnectionSql() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection to database
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/onlinebanking", "root", "");
            
            // Create statement for executing SQL queries
            s = c.createStatement();
            
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:REQ001]
     * Method to get transaction data for statement downloads
     */
    public ResultSet getTransactionsByDateRange(String accountNo, String fromDate, String toDate) throws SQLException {
        String query = "SELECT * FROM bank WHERE Account_No = '" + accountNo + "' AND date BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY date DESC";
        return s.executeQuery(query);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:REQ002]
     * Methods for digital wallet integration
     */
    public void saveWalletInfo(String accountNo, String walletType, String walletId, String status) throws SQLException {
        String query = "INSERT INTO wallet_links (Account_No, wallet_type, wallet_id, status, link_date) VALUES ('" 
                     + accountNo + "', '" + walletType + "', '" + walletId + "', '" + status + "', CURRENT_TIMESTAMP)";
        s.executeUpdate(query);
    }
    
    public ResultSet getLinkedWallets(String accountNo) throws SQLException {
        String query = "SELECT * FROM wallet_links WHERE Account_No = '" + accountNo + "'";
        return s.executeQuery(query);
    }
    
    public void recordWalletTransaction(String accountNo, String walletType, double amount, String transactionType) throws SQLException {
        String query = "INSERT INTO wallet_transactions (Account_No, wallet_type, amount, transaction_type, transaction_date) VALUES ('" 
                     + accountNo + "', '" + walletType + "', " + amount + ", '" + transactionType + "', CURRENT_TIMESTAMP)";
        s.executeUpdate(query);
        
        // Also update the main bank table to reflect this transaction
        String bankUpdate = "INSERT INTO bank (Account_No, date, type, amount) VALUES ('" 
                          + accountNo + "', CURRENT_TIMESTAMP, '" + transactionType + "', '" + amount + "')";
        s.executeUpdate(bankUpdate);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:REQ003]
     * Methods for currency conversion
     */
    public void saveCurrencyConversion(String accountNo, String fromCurrency, String toCurrency, 
                                       double amount, double convertedAmount, double rate) throws SQLException {
        String query = "INSERT INTO currency_conversions (Account_No, from_currency, to_currency, amount, " +
                       "converted_amount, rate, conversion_date) VALUES ('" + accountNo + "', '" + 
                       fromCurrency + "', '" + toCurrency + "', " + amount + ", " + convertedAmount + 
                       ", " + rate + ", CURRENT_TIMESTAMP)";
        s.executeUpdate(query);
    }
    
    public ResultSet getConversionHistory(String accountNo) throws SQLException {
        String query = "SELECT * FROM currency_conversions WHERE Account_No = '" + accountNo + "' ORDER BY conversion_date DESC";
        return s.executeQuery(query);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:REQ001]
     * Audit logging for regulatory compliance
     */
    public void logActivity(String accountNo, String activity, String details) throws SQLException {
        String query = "INSERT INTO audit_log (Account_No, activity, details, log_timestamp) VALUES ('" +
                       accountNo + "', '" + activity + "', '" + details + "', CURRENT_TIMESTAMP)";
        s.executeUpdate(query);
    }
    /* [END AGENT GENERATED CODE] */
    
    // Close connection resources
    public void close() {
        try {
            if (s != null) s.close();
            if (c != null) c.close();
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        new ConnectionSql();
    }
}

/* 
 * Requirements implemented:
 * REQ001: PDF Statement Downloads
 * REQ002: Digital Wallet Integration
 * REQ003: Currency Conversion Enhancement
 * Agent Run Identifier: CLAUDE-3-SONNET-20250219
 */