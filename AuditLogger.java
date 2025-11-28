package banking.management.system;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * AuditLogger provides comprehensive audit logging for regulatory compliance
 * and security tracking in the Online Banking System
 */
public class AuditLogger {
    
    /* [AGENT GENERATED CODE - REQUIREMENT:INFRA-001]
     * Audit logging framework implementation with:
     * - Different log levels
     * - Separate logs for security, user actions, and system events
     * - Timestamping
     * - User activity tracking
     * - Compliance with banking audit requirements
     */
    
    // Log levels
    public static final String SECURITY = "SECURITY";
    public static final String USER = "USER";
    public static final String SYSTEM = "SYSTEM";
    public static final String COMPLIANCE = "COMPLIANCE";
    
    // Status levels
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    public static final String WARNING = "WARNING";
    public static final String INFO = "INFO";
    public static final String ERROR = "ERROR";
    
    // Logger instances for different types of logs
    private static final Logger securityLogger = Logger.getLogger("banking.security");
    private static final Logger userLogger = Logger.getLogger("banking.user");
    private static final Logger systemLogger = Logger.getLogger("banking.system");
    private static final Logger complianceLogger = Logger.getLogger("banking.compliance");
    
    // Compliance log file (for banking regulations)
    private static final String COMPLIANCE_LOG_FILE = "compliance_audit.log";
    
    static {
        try {
            // Configure Java loggers
            configureLogger(securityLogger, "security.log");
            configureLogger(userLogger, "user.log");
            configureLogger(systemLogger, "system.log");
            configureLogger(complianceLogger, "compliance.log");
            
            // Create compliance audit directory if it doesn't exist
            File complianceDir = new File("audit");
            if (!complianceDir.exists()) {
                complianceDir.mkdir();
            }
            
            // Log system startup
            systemLogger.info("AuditLogger initialized");
        } catch (IOException e) {
            System.err.println("Failed to initialize loggers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Configure a logger with file handler
     * 
     * @param logger The logger to configure
     * @param logFile The log file name
     * @throws IOException If log file cannot be created
     */
    private static void configureLogger(Logger logger, String logFile) throws IOException {
        // Create logs directory if it doesn't exist
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }
        
        FileHandler fileHandler = new FileHandler("logs/" + logFile, true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
    }
    
    /**
     * Log an event with the specified parameters
     * 
     * @param logType Type of log (SECURITY, USER, SYSTEM, COMPLIANCE)
     * @param action The action being performed
     * @param details Additional details about the action
     * @param status Status of the action (SUCCESS, FAILURE, WARNING, INFO, ERROR)
     */
    public static void log(String logType, String action, String details, String status) {
        String message = String.format("[%s] %s: %s", status, action, details);
        
        // Log to appropriate logger based on type
        switch (logType) {
            case SECURITY:
                securityLogger.log(Level.INFO, message);
                // Also log security events to compliance log
                logCompliance(logType, action, details, status, null);
                break;
            case USER:
                userLogger.log(Level.INFO, message);
                break;
            case SYSTEM:
                systemLogger.log(Level.INFO, message);
                break;
            case COMPLIANCE:
                complianceLogger.log(Level.INFO, message);
                logCompliance(logType, action, details, status, null);
                break;
            default:
                systemLogger.log(Level.WARNING, "Unknown log type: " + logType + " - " + message);
        }
    }
    
    /**
     * Log a user activity with user identifier
     * 
     * @param userId User identifier (account number, username)
     * @param action The action being performed
     * @param details Additional details about the action
     * @param status Status of the action
     */
    public static void logUserActivity(String userId, String action, String details, String status) {
        String message = String.format("[%s] User %s - %s: %s", status, userId, action, details);
        userLogger.log(Level.INFO, message);
        
        // Also log to compliance log for audit trail
        logCompliance(USER, action, details, status, userId);
    }
    
    /**
     * Log a security event with user identifier
     * 
     * @param userId User identifier (account number, username) or IP address
     * @param action The security action
     * @param details Additional details
     * @param status Status of the security event
     */
    public static void logSecurity(String userId, String action, String details, String status) {
        String message = String.format("[%s] User %s - %s: %s", status, userId, action, details);
        securityLogger.log(Level.INFO, message);
        
        // Always log security events to compliance log
        logCompliance(SECURITY, action, details, status, userId);
    }
    
    /**
     * Write to the special compliance audit log that meets banking regulations
     * 
     * @param logType Type of log
     * @param action Action performed
     * @param details Details of the action
     * @param status Status of the action
     * @param userId User ID if applicable, null otherwise
     */
    private static synchronized void logCompliance(String logType, String action, String details, String status, String userId) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String timestamp = dateFormat.format(new Date());
            
            // Format: timestamp|log_type|user_id|action|status|details
            String logEntry = String.format("%s|%s|%s|%s|%s|%s%n",
                    timestamp,
                    logType,
                    (userId != null ? userId : "SYSTEM"),
                    action,
                    status,
                    details);
            
            // Write to compliance log file
            Files.write(Paths.get("audit", COMPLIANCE_LOG_FILE), 
                    logEntry.getBytes(), 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write to compliance log: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Log a transaction for audit purposes
     * 
     * @param accountNo Account number
     * @param transactionType Type of transaction
     * @param amount Transaction amount
     * @param status Transaction status
     * @param details Additional details
     */
    public static void logTransaction(String accountNo, String transactionType, 
            double amount, String status, String details) {
        
        String message = String.format("Account: %s, Type: %s, Amount: %.2f, Details: %s",
                accountNo, transactionType, amount, details);
        
        // Log to user activity log
        logUserActivity(accountNo, transactionType, message, status);
        
        // Log to compliance log
        logCompliance(COMPLIANCE, transactionType, message, status, accountNo);
    }
    
    /**
     * Log a PDF statement download
     * 
     * @param accountNo Account number
     * @param startDate Start date of the statement period
     * @param endDate End date of the statement period
     * @param status Download status
     */
    public static void logStatementDownload(String accountNo, String startDate, String endDate, String status) {
        String details = String.format("Statement period: %s to %s", startDate, endDate);
        logUserActivity(accountNo, "PDF_STATEMENT_DOWNLOAD", details, status);
        
        // Special log for PDF statement downloads (regulatory requirement)
        logCompliance(COMPLIANCE, "PDF_STATEMENT_DOWNLOAD", details, status, accountNo);
    }
    
    /**
     * Log a wallet integration event
     * 
     * @param accountNo Account number
     * @param walletType Type of wallet (Google Pay, Apple Pay, etc.)
     * @param action Action performed (link, unlink, transaction)
     * @param status Status of the action
     * @param details Additional details
     */
    public static void logWalletActivity(String accountNo, String walletType, String action, 
            String status, String details) {
        
        String message = String.format("Wallet: %s, Action: %s, Details: %s",
                walletType, action, details);
        
        logUserActivity(accountNo, "WALLET_" + action.toUpperCase(), message, status);
        
        // Also log as security event for wallet integrations
        logSecurity(accountNo, "WALLET_" + action.toUpperCase(), message, status);
    }
    
    /**
     * Log a currency conversion event
     * 
     * @param accountNo Account number
     * @param fromCurrency Source currency
     * @param toCurrency Target currency
     * @param amount Amount to convert
     * @param convertedAmount Resulting amount after conversion
     * @param status Conversion status
     */
    public static void logCurrencyConversion(String accountNo, String fromCurrency, String toCurrency,
            double amount, double convertedAmount, String status) {
        
        String details = String.format("Converted %.2f %s to %.2f %s",
                amount, fromCurrency, convertedAmount, toCurrency);
        
        logUserActivity(accountNo, "CURRENCY_CONVERSION", details, status);
    }
}

/* 
 * AGENT GENERATED SUMMARY:
 * Implementation requirements addressed:
 * - INFRA-001: Created comprehensive AuditLogger for compliance logging
 * - PDF-004: Added logStatementDownload method for PDF downloads
 * - WALLET-001: Added logWalletActivity method for wallet integration events
 * - CURR-004: Added logCurrencyConversion method for tracking conversions
 * 
 * Human review required:
 * - Review compliance with specific banking regulations for audit logs
 * - Consider log rotation and archival policies for production
 * - Review log file locations and permissions for security
 * 
 * Agent run identifier: Claude-3.7-Sonnet-20250219
 */