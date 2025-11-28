package banking.management.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

/**
 * Service class for audit logging operations.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
 * This class provides audit logging functionality for tracking user actions
 * within the banking system for compliance and security purposes.
 */
public class AuditLogService {
    
    private static final Logger logger = Logger.getLogger(AuditLogService.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs a user activity in the audit system.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param action Action performed
     * @param details Additional details about the action
     * @param actionStatus Success or failure status
     */
    public void logActivity(String accountNo, String action, String details, String actionStatus) {
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "INSERT INTO audit_log (account_no, action, details, status, timestamp, ip_address) " +
                           "VALUES (?, ?, ?, ?, NOW(), ?)";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.setString(4, actionStatus);
            ps.setString(5, getClientIPAddress());
            
            ps.executeUpdate();
            
            logger.info("Audit log recorded for account " + maskAccountNumber(accountNo) + 
                    ": " + action + " - " + actionStatus);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error recording audit log", e);
        } finally {
            connection.closeConnection();
        }
    }
    
    /**
     * Logs statement download activity for compliance.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param fromDate Start date of statement period
     * @param toDate End date of statement period
     * @param downloadToken Security token for download
     * @param status Download status
     */
    public void logStatementDownload(String accountNo, String fromDate, String toDate, 
                                     String downloadToken, String status) {
        String details = "Statement period: " + fromDate + " to " + toDate + 
                        ", Token: " + downloadToken.substring(0, 10) + "...";
        
        logActivity(accountNo, "STATEMENT_DOWNLOAD", details, status);
    }
    
    /**
     * Logs security-related events.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param accountNo Account number
     * @param securityEvent Type of security event
     * @param details Event details
     * @param status Event status
     */
    public void logSecurityEvent(String accountNo, String securityEvent, String details, String status) {
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "INSERT INTO security_log (account_no, event_type, details, status, timestamp, ip_address) " +
                           "VALUES (?, ?, ?, ?, NOW(), ?)";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, securityEvent);
            ps.setString(3, details);
            ps.setString(4, status);
            ps.setString(5, getClientIPAddress());
            
            ps.executeUpdate();
            
            // Also log to standard audit log
            logActivity(accountNo, "SECURITY_" + securityEvent, details, status);
            
            // High severity security events should be logged more prominently
            if (status.equals("FAILED") || securityEvent.contains("UNAUTHORIZED")) {
                logger.warning("Security event for account " + maskAccountNumber(accountNo) + 
                        ": " + securityEvent + " - " + status);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error recording security log", e);
        } finally {
            connection.closeConnection();
        }
    }
    
    /**
     * Retrieves audit logs for an account within a date range.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param fromDate Start date
     * @param toDate End date
     * @return List of audit log entries
     */
    public List<AuditLogEntry> getAuditLogs(String accountNo, String fromDate, String toDate) {
        List<AuditLogEntry> auditLogs = new ArrayList<>();
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "SELECT * FROM audit_log WHERE account_no = ? AND " +
                           "timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, fromDate + " 00:00:00");
            ps.setString(3, toDate + " 23:59:59");
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                AuditLogEntry entry = new AuditLogEntry(
                    rs.getInt("log_id"),
                    rs.getString("account_no"),
                    rs.getString("action"),
                    rs.getString("details"),
                    rs.getString("status"),
                    rs.getTimestamp("timestamp").toString(),
                    rs.getString("ip_address")
                );
                auditLogs.add(entry);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving audit logs", e);
        } finally {
            connection.closeConnection();
        }
        
        return auditLogs;
    }
    
    /**
     * Gets client IP address for logging.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @return Client IP address or "unknown" if not available
     */
    private String getClientIPAddress() {
        // In a desktop application, this would typically return localhost or the actual machine IP
        // In a web application, this would extract the client IP from the request
        return "127.0.0.1"; // Placeholder for desktop application
    }
    
    /**
     * Masks account number for logging (shows only last 4 digits).
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param accountNo Full account number
     * @return Masked account number
     */
    private String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() <= 4) {
            return "****";
        }
        
        int length = accountNo.length();
        return "****" + accountNo.substring(length - 4, length);
    }
    
    /**
     * Audit log entry data model class.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     */
    public static class AuditLogEntry {
        private int logId;
        private String accountNo;
        private String action;
        private String details;
        private String status;
        private String timestamp;
        private String ipAddress;
        
        public AuditLogEntry(int logId, String accountNo, String action, String details, 
                           String status, String timestamp, String ipAddress) {
            this.logId = logId;
            this.accountNo = accountNo;
            this.action = action;
            this.details = details;
            this.status = status;
            this.timestamp = timestamp;
            this.ipAddress = ipAddress;
        }
        
        public int getLogId() { return logId; }
        public String getAccountNo() { return accountNo; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
        public String getStatus() { return status; }
        public String getTimestamp() { return timestamp; }
        public String getIpAddress() { return ipAddress; }
    }
}