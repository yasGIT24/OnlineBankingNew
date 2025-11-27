package banking.management.system;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* [AGENT GENERATED CODE - REQUIREMENT:US-5]
 * Created AuditLogger.java to implement:
 * 1. Security event logging for compliance
 * 2. User activity tracking
 * 3. Asynchronous logging for performance
 */
public class AuditLogger {
    private static final String LOG_FILE = "audit_trail.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final boolean LOG_TO_FILE = true;
    private static final boolean LOG_TO_DATABASE = true;
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Log activity with identifier, activity description, and category
     */
    public static void logActivity(String identifier, String activity, String category) {
        // Use executor service to handle logging asynchronously
        logExecutor.submit(() -> {
            try {
                final String timestamp = DATE_FORMAT.format(new Date());
                final String logEntry = String.format("[%s] %s | %s | %s", timestamp, identifier, category, activity);
                
                // Log to file if enabled
                if (LOG_TO_FILE) {
                    logToFile(logEntry);
                }
                
                // Log to database if enabled
                if (LOG_TO_DATABASE) {
                    logToDatabase(identifier, activity, category, timestamp);
                }
                
                // Check for security alerts
                if (category.equalsIgnoreCase("Security")) {
                    checkSecurityAlert(identifier, activity, category);
                }
                
            } catch (Exception e) {
                System.err.println("Audit logging failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Log to file system
     */
    private static synchronized void logToFile(String logEntry) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to audit log file: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Log to database
     */
    private static void logToDatabase(String identifier, String activity, String category, String timestamp) {
        try {
            ConnectionSql connection = new ConnectionSql();
            PreparedStatement stmt = connection.getConnection().prepareStatement(
                "INSERT INTO audit_log (timestamp, identifier, activity, category) VALUES (?, ?, ?, ?)"
            );
            
            stmt.setTimestamp(1, Timestamp.valueOf(timestamp));
            stmt.setString(2, identifier);
            stmt.setString(3, activity);
            stmt.setString(4, category);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            // Don't use ErrorHandler here to avoid circular dependency
            System.err.println("Failed to write to audit log database: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Check if security alert needs to be triggered
     */
    private static void checkSecurityAlert(String identifier, String activity, String category) {
        try {
            // Check for security events that require immediate alerts
            boolean needsAlert = false;
            
            // Failed login attempts
            if (activity.contains("failed login") || activity.contains("authentication failed")) {
                needsAlert = true;
                triggerSecurityAlert(identifier, "Failed authentication attempt", "Authentication");
            }
            
            // Multiple failed password attempts
            if (activity.contains("multiple password failures")) {
                needsAlert = true;
                triggerSecurityAlert(identifier, "Multiple password failures detected", "Authentication");
            }
            
            // Possible SQL injection
            if (activity.contains("sql injection") || activity.contains("injection attempt")) {
                needsAlert = true;
                triggerSecurityAlert(identifier, "Possible SQL injection attempt", "Database");
            }
            
            // Large transactions
            if (activity.contains("large transaction") || (activity.contains("transfer") && 
                activity.matches(".*\\$\\d{4,}.*|.*Rs\\.\\s*\\d{4,}.*"))) {
                needsAlert = true;
                triggerSecurityAlert(identifier, "Large transaction detected", "Transaction");
            }
            
            // Password changes
            if (activity.contains("password changed")) {
                logToFile("[NOTICE] Password change for account " + identifier);
            }
        } catch (Exception e) {
            System.err.println("Security alert check failed: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Trigger security alert notification
     */
    private static void triggerSecurityAlert(String identifier, String alertMessage, String alertType) {
        try {
            // Log the alert
            logToFile("[ALERT] " + alertType + " | " + identifier + " | " + alertMessage);
            
            // Send the alert via the notification system
            NotificationSystem notificationSystem = NotificationSystem.getInstance();
            notificationSystem.sendSecurityAlert(identifier, alertMessage, alertType);
            
        } catch (Exception e) {
            System.err.println("Failed to trigger security alert: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Clean up resources on application shutdown
     */
    public static void shutdown() {
        try {
            logExecutor.shutdown();
        } catch (Exception e) {
            System.err.println("Error shutting down audit logger: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Utility method to clear audit logs (for testing/maintenance only)
     */
    public static void clearAuditLogs() {
        try {
            // Clear file logs
            new FileWriter(LOG_FILE, false).close();
            
            // Clear database logs
            if (LOG_TO_DATABASE) {
                ConnectionSql connection = new ConnectionSql();
                connection.executeUpdate("DELETE FROM audit_log WHERE timestamp < CURRENT_TIMESTAMP");
            }
        } catch (Exception e) {
            System.err.println("Failed to clear audit logs: " + e.getMessage());
        }
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-LOG-001, TC-SEC-008
 * Requirement IDs: US-5 (Alerts & Notifications)
 * Agent Run: AGENT-20251127-01
 */