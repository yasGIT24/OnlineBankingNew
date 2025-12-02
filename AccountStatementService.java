package banking.management.system;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import java.text.SimpleDateFormat;

/* [AGENT GENERATED CODE - REQUIREMENT:Download Account Statement in PDF Format]
 * This service handles the business logic for account statement generation.
 * It coordinates between transaction data retrieval and PDF generation with:
 * 1. Account data validation
 * 2. Transaction history retrieval 
 * 3. PDF statement generation
 * 4. Audit logging
 * 
 * Linked to Value Stream Step: Account Statement Request, PDF Generation
 * Linked to test cases: STMT-01, STMT-02, AUDIT-01
 */
public class AccountStatementService {
    private static final Logger LOGGER = Logger.getLogger(AccountStatementService.class.getName());
    private final TransactionHistoryService transactionHistoryService;
    private final ConnectionSql connectionSql;
    private Connection connection;
    
    /**
     * Constructor
     */
    public AccountStatementService() {
        transactionHistoryService = new TransactionHistoryService();
        connectionSql = new ConnectionSql();
        connection = connectionSql.c;
        
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Database connection failed in AccountStatementService");
        }
        
        // Ensure audit tables exist
        createAuditTables();
    }
    
    /**
     * Generate account statement PDF for the specified account and date range
     * 
     * @param accountNo Account number
     * @param startDate Start date for statement period
     * @param endDate End date for statement period
     * @param outputPath Path where the PDF will be saved
     * @param pin PIN for authorization
     * @return true if successful, false otherwise
     */
    public boolean generateAccountStatement(String accountNo, Date startDate, 
                                          Date endDate, String outputPath, String pin) {
        try {
            // Verify account exists and pin is valid
            if (!verifyAccount(accountNo, pin)) {
                LOGGER.log(Level.WARNING, "Invalid account or PIN for statement generation: {0}", accountNo);
                return false;
            }
            
            // Use the TransactionHistoryService to generate the statement PDF
            boolean success = transactionHistoryService.generateAccountStatementPDF(
                accountNo, startDate, endDate, outputPath, pin);
            
            if (success) {
                LOGGER.log(Level.INFO, "Account statement generated successfully for account: {0}", accountNo);
            } else {
                LOGGER.log(Level.WARNING, "Failed to generate account statement for account: {0}", accountNo);
            }
            
            return success;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating account statement: {0}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Log statement generation event for audit and compliance
     * 
     * @param accountNo Account number
     * @param startDate Start date for statement period
     * @param endDate End date for statement period
     */
    public void logStatementGeneration(String accountNo, Date startDate, Date endDate) {
        PreparedStatement ps = null;
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            String query = "INSERT INTO statement_audit (account_no, action_type, start_date, end_date, " +
                         "request_time, request_status, ip_address) VALUES (?, ?, ?, ?, NOW(), ?, ?)";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, "GENERATE");
            ps.setString(3, dateFormat.format(startDate));
            ps.setString(4, dateFormat.format(endDate));
            ps.setString(5, "SUCCESS");
            ps.setString(6, "127.0.0.1"); // In a real app, this would be the actual IP
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error logging statement generation: {0}", e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Log statement download event for audit and compliance
     * 
     * @param accountNo Account number
     * @param filePath Path of the downloaded file
     */
    public void logStatementDownload(String accountNo, String filePath) {
        PreparedStatement ps = null;
        
        try {
            String query = "INSERT INTO statement_audit (account_no, action_type, file_path, " +
                         "request_time, request_status, ip_address) VALUES (?, ?, ?, NOW(), ?, ?)";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, "DOWNLOAD");
            ps.setString(3, filePath);
            ps.setString(4, "SUCCESS");
            ps.setString(5, "127.0.0.1"); // In a real app, this would be the actual IP
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error logging statement download: {0}", e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Verify account exists and PIN is valid
     * 
     * @param accountNo Account number
     * @param pin PIN for verification
     * @return true if account exists and PIN is valid, false otherwise
     */
    private boolean verifyAccount(String accountNo, String pin) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String query = "SELECT * FROM login WHERE Account_No = ? AND Login_Password = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            rs = ps.executeQuery();
            
            return rs.next(); // Returns true if account and PIN match
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verifying account: {0}", e.getMessage());
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
     * Create audit tables if they don't exist
     */
    private void createAuditTables() {
        Statement stmt = null;
        
        try {
            stmt = connection.createStatement();
            
            // Create statement_audit table
            String createAuditTable = "CREATE TABLE IF NOT EXISTS statement_audit (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "account_no VARCHAR(20) NOT NULL, " +
                "action_type VARCHAR(20) NOT NULL, " + // GENERATE, DOWNLOAD, FAILED
                "start_date VARCHAR(10), " +
                "end_date VARCHAR(10), " +
                "file_path VARCHAR(255), " +
                "request_time DATETIME NOT NULL, " +
                "request_status VARCHAR(20) NOT NULL, " +
                "ip_address VARCHAR(50)" +
                ")";
            stmt.execute(createAuditTable);
            
            LOGGER.log(Level.INFO, "Statement audit tables verified/created");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating audit tables: {0}", e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing statement: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Close database resources
     */
    public void closeConnection() {
        if (transactionHistoryService != null) {
            transactionHistoryService.closeConnection();
        }
        
        if (connectionSql != null) {
            connectionSql.closeConnection();
        }
    }
}

/* 
 * Test cases:
 * STMT-01: Verify account statement generation
 * STMT-02: Verify PDF content and formatting
 * AUDIT-01: Verify audit logging for statement generation and download
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */