package banking.management.system;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US1]
 * This class logs bank activity for compliance and audit purposes.
 * It stores logs in a database table for retrieval and reporting.
 */
public class AuditLogger {
    
    /**
     * Logs a banking activity for compliance and audit purposes
     * 
     * @param accountNo The account number associated with the activity
     * @param activityType Type of activity (e.g., PDF_STATEMENT_GENERATED)
     * @param description Additional details about the activity
     * @return boolean indicating success or failure
     */
    public boolean logActivity(String accountNo, String activityType, String description) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            // Generate timestamp
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = formatter.format(new Date());
            
            // Generate a unique log ID
            String logId = generateLogId();
            
            // Prepare SQL statement
            String query = "INSERT INTO audit_log (log_id, account_no, activity_type, description, timestamp, ip_address) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = c.c.prepareStatement(query);
            
            // Set parameters
            pstmt.setString(1, logId);
            pstmt.setString(2, accountNo);
            pstmt.setString(3, activityType);
            pstmt.setString(4, description);
            pstmt.setString(5, timestamp);
            pstmt.setString(6, getClientIpAddress());
            
            // Execute the statement
            pstmt.executeUpdate();
            
            return true;
        } catch (Exception e) {
            System.out.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generates a unique log ID for the audit entry
     * 
     * @return String containing the unique log ID
     */
    private String generateLogId() {
        // Generate a timestamp-based log ID
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return "LOG" + formatter.format(new Date());
    }
    
    /**
     * Retrieves the client's IP address
     * In a Java Swing desktop app context, this would be the local machine IP
     * 
     * @return String containing the IP address
     */
    private String getClientIpAddress() {
        // For a desktop application, we'll return localhost
        return "127.0.0.1";
    }
    
    /**
     * Creates the audit_log table if it doesn't exist
     * This should be called during application initialization
     */
    public void ensureAuditTableExists() {
        try {
            ConnectionSql c = new ConnectionSql();
            
            // Check if the table exists
            DatabaseMetaData meta = c.c.getMetaData();
            ResultSet rs = meta.getTables(null, null, "audit_log", null);
            
            if (!rs.next()) {
                // Table doesn't exist, create it
                String createTableSQL = 
                    "CREATE TABLE audit_log (" +
                    "log_id VARCHAR(50) PRIMARY KEY, " +
                    "account_no VARCHAR(20) NOT NULL, " +
                    "activity_type VARCHAR(50) NOT NULL, " +
                    "description VARCHAR(255), " +
                    "timestamp DATETIME NOT NULL, " +
                    "ip_address VARCHAR(50)" +
                    ")";
                
                c.s.executeUpdate(createTableSQL);
            }
        } catch (Exception e) {
            System.out.println("Error creating audit table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/* 
 * Agent Run Identifier: BANK-AUDIT-20251126
 * Related Test Cases: AUDIT-LOG-001
 */