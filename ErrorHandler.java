package banking.management.system;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created ErrorHandler.java to implement:
 * 1. Centralized error handling and logging
 * 2. User-friendly error messages
 * 3. Error tracking for security events
 */
public class ErrorHandler {
    private static final Logger logger = Logger.getLogger(ErrorHandler.class.getName());
    private static final String LOG_FILE = "bank_app_errors.log";
    private static final boolean SHOW_STACK_TRACE = false; // Set to false in production
    
    static {
        try {
            // Configure logger
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Failed to initialize error logging: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Handle exception with custom message
     */
    public static void handleException(Exception e, String message) {
        // Log the error
        logError(e, message);
        
        // Display user-friendly message (only basic info for security)
        showErrorMessage(message);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Handle exception with default message
     */
    public static void handleException(Exception e) {
        handleException(e, "An error occurred in the application");
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Log error details to file
     */
    private static void logError(Exception e, String message) {
        try {
            // Log basic info
            logger.severe(message + ": " + e.getMessage());
            
            // Log stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            logger.severe(sw.toString());
            
            // For security-related exceptions, log additional info
            if (e instanceof SecurityException || message.toLowerCase().contains("security")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logger.warning("SECURITY EVENT at " + sdf.format(new Date()));
                
                // Log to AuditLogger if available
                try {
                    AuditLogger.logActivity("SYSTEM", "Security Exception: " + message, "Error");
                } catch (Exception ignored) {
                    // AuditLogger might not be initialized yet
                }
            }
        } catch (Exception loggingEx) {
            // Fallback logging if the logger fails
            System.err.println("Error logging failed: " + loggingEx.getMessage());
            System.err.println("Original error: " + message + ": " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Show user-friendly error message
     */
    private static void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                message + "\n\nThis error has been logged for review.",
                "Error", JOptionPane.ERROR_MESSAGE);
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get custom user-friendly message for common exceptions
     */
    public static String getUserFriendlyMessage(Exception e) {
        if (e instanceof SQLException) {
            return "A database error has occurred. Please try again later.";
        } else if (e instanceof IOException) {
            return "A system I/O error has occurred. Please try again later.";
        } else if (e instanceof SecurityException) {
            return "A security validation failed. Please verify your input.";
        } else if (e instanceof NullPointerException) {
            return "The application encountered a data error. Please contact support.";
        } else {
            return "An unexpected error has occurred. Please try again later.";
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Handle SQL errors specifically
     */
    public static void handleSqlException(SQLException e, String operation) {
        // Log detailed SQL error
        logError(e, "SQL Error during " + operation);
        
        // Display generic message to user
        showErrorMessage("Database operation failed: " + operation);
        
        // For security, check if this might be an SQL injection attempt
        String sqlMessage = e.getMessage().toLowerCase();
        if (sqlMessage.contains("syntax") || sqlMessage.contains("injection") || 
            sqlMessage.contains("malformed")) {
            try {
                AuditLogger.logActivity("SYSTEM", 
                    "Possible SQL injection attempt: " + operation, "Security");
            } catch (Exception ignored) {
                // AuditLogger might not be initialized
            }
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Clear error logs (for maintenance)
     */
    public static void clearErrorLogs() {
        try {
            File logFile = new File(LOG_FILE);
            if (logFile.exists()) {
                FileWriter fw = new FileWriter(logFile, false);
                fw.write(""); // Clear file
                fw.close();
            }
        } catch (IOException e) {
            System.err.println("Failed to clear error logs: " + e.getMessage());
        }
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-ERR-001, TC-ERR-002
 * Requirement IDs: US-1 (Error handling), US-5 (Security alerting)
 * Agent Run: AGENT-20251127-01
 */