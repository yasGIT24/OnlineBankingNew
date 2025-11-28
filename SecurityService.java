package banking.management.system;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling security operations in the banking system.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
 * This class provides centralized security services including authentication,
 * authorization, and secure token generation for protected resources.
 */
public class SecurityService {
    
    private static final Logger logger = Logger.getLogger(SecurityService.class.getName());
    private final EncryptionUtils encryptionUtils;
    
    /**
     * Constructor initializes encryption utilities.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    public SecurityService() {
        this.encryptionUtils = new EncryptionUtils();
    }
    
    /**
     * Authenticates a user with account number and password.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param accountNo Account number
     * @param password Login password
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticateUser(String accountNo, String password) {
        boolean isAuthenticated = false;
        ConnectionSql connection = new ConnectionSql();
        
        try {
            // Use prepared statement to prevent SQL injection
            String query = "SELECT * FROM login WHERE Account_No = ? AND Login_Password = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            isAuthenticated = rs.next();
            
            // Log authentication attempt (without sensitive data)
            if (isAuthenticated) {
                logger.info("Successful authentication for account: " + maskAccountNumber(accountNo));
            } else {
                logger.warning("Failed authentication attempt for account: " + maskAccountNumber(accountNo));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Authentication error", e);
        } finally {
            connection.closeConnection();
        }
        
        return isAuthenticated;
    }
    
    /**
     * Generates a secure token for resource access.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param resourceType Type of resource being accessed
     * @return Encrypted access token
     */
    public String generateSecureToken(String accountNo, String resourceType) {
        String rawToken = accountNo + ":" + resourceType + ":" + System.currentTimeMillis();
        return encryptionUtils.encrypt(rawToken);
    }
    
    /**
     * Validates a secure token for resource access.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param token Encrypted access token
     * @param resourceType Expected resource type
     * @return true if token is valid, false otherwise
     */
    public boolean validateSecureToken(String token, String resourceType) {
        try {
            String decryptedToken = encryptionUtils.decrypt(token);
            String[] parts = decryptedToken.split(":");
            
            if (parts.length != 3) {
                return false;
            }
            
            // Verify resource type matches
            if (!parts[1].equals(resourceType)) {
                return false;
            }
            
            // Check if token is expired (1 hour validity)
            long tokenTime = Long.parseLong(parts[2]);
            long currentTime = System.currentTimeMillis();
            long validityPeriod = 3600000; // 1 hour in milliseconds
            
            return (currentTime - tokenTime <= validityPeriod);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Token validation failed", e);
            return false;
        }
    }
    
    /**
     * Records security audit log.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param action Security action performed
     * @param status Status of the action
     */
    public void logSecurityAction(String accountNo, String action, String status) {
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "INSERT INTO security_log (account_no, action, status, timestamp) VALUES (?, ?, ?, NOW())";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, action);
            ps.setString(3, status);
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging security action", e);
        } finally {
            connection.closeConnection();
        }
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
}