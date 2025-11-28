package banking.management.system;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.text.SimpleDateFormat;

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 3: Fund Transfer Between Accounts]
 * This class implements the fund transfer service to:
 * 1. Transfer funds between accounts
 * 2. Validate sufficient funds
 * 3. Record transfer transactions
 * 4. Generate transaction receipts
 *
 * Linked to Value Stream Steps: Fund Transfer Initiation, Transfer Confirmation, Transfer Processing
 * Linked to test cases: TRAN-01, TRAN-02, TRAN-03, SEC-05
 */
public class TransferService {
    private final ConnectionSql connectionSql;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(TransferService.class.getName());
    
    public TransferService() {
        connectionSql = new ConnectionSql();
        connection = connectionSql.c;
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Database connection failed in TransferService");
        }
    }
    
    /**
     * Transfer funds from source account to destination account
     * 
     * @param sourceAccountNo Source account number
     * @param destinationAccountNo Destination account number
     * @param amount Amount to transfer
     * @param description Transaction description
     * @param pin PIN for authorization
     * @return Transfer receipt or error message
     */
    public String transferFunds(String sourceAccountNo, String destinationAccountNo, 
                              double amount, String description, String pin) {
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Validate accounts exist
            if (!accountExists(sourceAccountNo)) {
                return "Source account does not exist";
            }
            
            if (!accountExists(destinationAccountNo)) {
                return "Destination account does not exist";
            }
            
            // Validate PIN for source account
            if (!validatePin(sourceAccountNo, pin)) {
                LOGGER.log(Level.WARNING, "Invalid PIN attempted for account: {0}", sourceAccountNo);
                return "Invalid PIN";
            }
            
            // Check for sufficient funds
            double sourceBalance = getAccountBalance(sourceAccountNo);
            if (sourceBalance < amount) {
                LOGGER.log(Level.INFO, "Insufficient funds in account: {0}", sourceAccountNo);
                return "Insufficient funds. Available balance: Rs. " + sourceBalance;
            }
            
            // Begin transaction
            connection.setAutoCommit(false);
            
            // Get current timestamp
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = formatter.format(date);
            
            // Insert withdrawal from source account
            String withdrawQuery = "INSERT INTO bank VALUES (?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(withdrawQuery);
            ps.setString(1, pin);
            ps.setString(2, sourceAccountNo);
            ps.setString(3, formattedDate);
            ps.setString(4, "Transfer to " + destinationAccountNo);
            ps.setDouble(5, amount);
            ps.executeUpdate();
            
            // Insert deposit to destination account
            String depositQuery = "INSERT INTO bank VALUES (?, ?, ?, ?, ?)";
            ps = connection.prepareStatement(depositQuery);
            ps.setString(1, "N/A"); // PIN not relevant for recipient
            ps.setString(2, destinationAccountNo);
            ps.setString(3, formattedDate);
            ps.setString(4, "Transfer from " + sourceAccountNo);
            ps.setDouble(5, amount);
            ps.executeUpdate();
            
            // Record the transfer in transactions table if it exists
            try {
                String transactionQuery = "INSERT INTO transactions (source_account, destination_account, amount, description, transaction_date, status) " +
                                        "VALUES (?, ?, ?, ?, ?, ?)";
                ps = connection.prepareStatement(transactionQuery);
                ps.setString(1, sourceAccountNo);
                ps.setString(2, destinationAccountNo);
                ps.setDouble(3, amount);
                ps.setString(4, description);
                ps.setString(5, formattedDate);
                ps.setString(6, "Completed");
                ps.executeUpdate();
            } catch (SQLException e) {
                // Transactions table might not exist yet
                LOGGER.log(Level.INFO, "Transactions table not found: {0}", e.getMessage());
            }
            
            // Commit the transaction
            connection.commit();
            
            // Generate receipt
            String receipt = "TRANSACTION RECEIPT\n" +
                           "--------------------\n" +
                           "Date: " + formattedDate + "\n" +
                           "From Account: " + maskAccountNumber(sourceAccountNo) + "\n" +
                           "To Account: " + maskAccountNumber(destinationAccountNo) + "\n" +
                           "Amount: Rs. " + amount + "\n" +
                           "Description: " + description + "\n" +
                           "Status: Successful\n" +
                           "Reference: " + generateReferenceNumber() + "\n";
            
            return receipt;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during transfer: {0}", e.getMessage());
            
            // Rollback transaction on error
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", ex.getMessage());
            }
            
            return "Transfer failed: Database error";
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Check if an account exists in the database
     * 
     * @param accountNo Account number to check
     * @return true if account exists, false otherwise
     */
    private boolean accountExists(String accountNo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String query = "SELECT DISTINCT Account_No FROM bank WHERE Account_No = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking account existence: {0}", e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Validate PIN for a given account
     * 
     * @param accountNo Account number
     * @param pin PIN to validate
     * @return true if PIN is valid, false otherwise
     */
    private boolean validatePin(String accountNo, String pin) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String query = "SELECT * FROM login WHERE Account_No = ? AND Login_Password = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating PIN: {0}", e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Get current balance of an account
     * 
     * @param accountNo Account number
     * @return Account balance
     */
    public double getAccountBalance(String accountNo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        double balance = 0;
        
        try {
            String query = "SELECT * FROM bank WHERE Account_No = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                if (rs.getString("type").equals("Deposit") || rs.getString("type").startsWith("Transfer from")) {
                    balance += Double.parseDouble(rs.getString("amount"));
                } else {
                    balance -= Double.parseDouble(rs.getString("amount"));
                }
            }
            
            return balance;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating account balance: {0}", e.getMessage());
            return 0;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Generate a unique reference number for transactions
     * 
     * @return Reference number
     */
    private String generateReferenceNumber() {
        // Generate a reference number: TXN-YYYYMMDDhhmmss-XXXX
        SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMddHHmmss");
        String timestamp = formatter.format(new Date());
        int random = (int) (Math.random() * 10000);
        return String.format("TXN-%s-%04d", timestamp, random);
    }
    
    /**
     * Mask account number for security (show only last 4 digits)
     * 
     * @param accountNo Account number to mask
     * @return Masked account number
     */
    private String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() <= 4) {
            return accountNo;
        }
        int length = accountNo.length();
        String lastFour = accountNo.substring(length - 4);
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < length - 4; i++) {
            masked.append("X");
        }
        masked.append(lastFour);
        return masked.toString();
    }
    
    /**
     * Close database resources
     */
    public void closeConnection() {
        connectionSql.closeConnection();
    }
}

/* 
 * Test cases:
 * TRAN-01: Verify successful transfer between two accounts
 * TRAN-02: Verify transfer fails with insufficient funds
 * TRAN-03: Verify transaction receipt generation
 * SEC-05: Verify PIN validation for transfer authorization
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */