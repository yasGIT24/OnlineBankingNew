package banking.management.system;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.time.LocalDateTime;

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 1: Account Login & Authentication]
 * This class provides secure banking authentication with the following features:
 * 1. Strong password validation
 * 2. Password hashing using SHA-256
 * 3. Session timeout management
 * 4. Two-factor authentication preparation
 * 5. SQL Injection protection using PreparedStatements
 *
 * Linked to Value Stream Step: Authentication
 * Linked to test cases: AUTH-01, AUTH-02, SEC-01, SEC-02
 */
public class LoginModel {
    private final ConnectionSql connectionSql;
    private Connection connection;
    private static final int SESSION_TIMEOUT_MINUTES = 15;
    private static final Logger LOGGER = Logger.getLogger(LoginModel.class.getName());
    
    // Session tracking
    private LocalDateTime lastActivity;
    private boolean isAuthenticated = false;
    private String currentUserId = null;
    
    public LoginModel() {
        connectionSql = new ConnectionSql();
        connection = connectionSql.c;
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Database connection failed");
            System.exit(0);
        }
    }
    
    /**
     * Checks if database connection is active
     * @return true if connected, false otherwise
     */
    public boolean isDbConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Database connection error: {0}", ex.getMessage());
            return false;
        }
    }
    
    /**
     * Authenticate user with account number and password
     * 
     * @param accountNo The account number
     * @param password The user password
     * @return true if authentication successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean authenticateUser(String accountNo, String password) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM login WHERE Account_No = ? AND Login_Password = ?";
        
        try {
            // Use prepared statement to prevent SQL injection
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, accountNo);
            preparedStatement.setString(2, hashPassword(password)); // In real implementation, we'd need to retrieve the stored hash and compare
            
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Authentication successful
                initializeSession(accountNo);
                return true;
            } else {
                // Failed authentication attempt - log for security monitoring
                LOGGER.log(Level.INFO, "Failed login attempt for account: {0}", accountNo);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Authentication error: {0}", e.getMessage());
            return false;
        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
        }
    }
    
    /**
     * Initialize a new user session
     * @param userId The authenticated user ID
     */
    private void initializeSession(String userId) {
        currentUserId = userId;
        lastActivity = LocalDateTime.now();
        isAuthenticated = true;
        LOGGER.log(Level.INFO, "User session initialized: {0}", userId);
    }
    
    /**
     * Check if the current session is valid or timed out
     * @return true if session is valid, false if timed out
     */
    public boolean isSessionValid() {
        if (!isAuthenticated || currentUserId == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (lastActivity.plusMinutes(SESSION_TIMEOUT_MINUTES).isBefore(now)) {
            // Session timeout
            logout();
            return false;
        }
        
        // Update last activity time
        lastActivity = now;
        return true;
    }
    
    /**
     * End the user session
     */
    public void logout() {
        isAuthenticated = false;
        currentUserId = null;
        lastActivity = null;
        LOGGER.log(Level.INFO, "User logged out");
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets strength requirements, false otherwise
     */
    public boolean validatePasswordStrength(String password) {
        // Password must be at least 8 characters long
        if (password.length() < 8) {
            return false;
        }
        
        // Check for at least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Check for at least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Check for at least one digit
        if (!password.matches(".*[0-9].*")) {
            return false;
        }
        
        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Generate One-Time Password for two-factor authentication
     * @return Generated OTP
     */
    public String generateOTP() {
        // Generate a 6-digit OTP
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    /**
     * Store OTP for verification
     * @param userId User identifier
     * @param otp Generated OTP
     * @param expiryMinutes Minutes until OTP expires
     * @return true if stored successfully, false otherwise
     */
    public boolean storeOTP(String userId, String otp, int expiryMinutes) {
        PreparedStatement preparedStatement = null;
        String query = "INSERT INTO otp_table (user_id, otp, created_at, expires_at) VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL ? MINUTE))";
        
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, otp);
            preparedStatement.setInt(3, expiryMinutes);
            
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error storing OTP: {0}", e.getMessage());
            return false;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing statement: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Verify provided OTP against stored OTP
     * @param userId User identifier
     * @param providedOTP OTP provided by the user
     * @return true if OTP is valid, false otherwise
     */
    public boolean verifyOTP(String userId, String providedOTP) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM otp_table WHERE user_id = ? AND otp = ? AND expires_at > NOW() ORDER BY created_at DESC LIMIT 1";
        
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, providedOTP);
            
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // OTP verified successfully - delete the used OTP
                deleteOTP(userId, providedOTP);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verifying OTP: {0}", e.getMessage());
            return false;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Delete OTP after verification or expiration
     * @param userId User identifier
     * @param otp OTP to delete
     */
    private void deleteOTP(String userId, String otp) {
        PreparedStatement preparedStatement = null;
        String query = "DELETE FROM otp_table WHERE user_id = ? AND otp = ?";
        
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, otp);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting OTP: {0}", e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing statement: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Hash a password using SHA-256
     * @param password Password to hash
     * @return Hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Password hashing error: {0}", e.getMessage());
            return password; // Fallback to plain password in case of error
        }
    }
    
    /**
     * Get the current authenticated user ID
     * @return User ID or null if not authenticated
     */
    public String getCurrentUserId() {
        return isSessionValid() ? currentUserId : null;
    }
}

/* 
 * Test cases:
 * AUTH-01: Verify successful login with valid credentials
 * AUTH-02: Verify failed login with invalid credentials
 * SEC-01: Verify password strength validation
 * SEC-02: Verify session timeout after 15 minutes
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */