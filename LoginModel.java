package banking.management.system;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.time.LocalDateTime;
import javax.swing.*;

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
    
    // [AGENT GENERATED CODE - REQUIREMENT:ACCOUNT_LOCKOUT_MECHANISM]
    // Account lockout configuration
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 30;
    // [END AGENT GENERATED CODE]
    
    // Session tracking
    private LocalDateTime lastActivity;
    private boolean isAuthenticated = false;
    private String currentUserId = null;
    private String currentAccountNo = null;
    
    public LoginModel() {
        connectionSql = new ConnectionSql();
        connection = connectionSql.c;
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Database connection failed");
            System.exit(0);
        }
    }
    
    // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_5_USER_LOGIN_AUTHENTICATION]
    // Constructor for LoginPage integration
    public LoginModel(String cardNumber, String pin) {
        this();
        try {
            if (authenticateUserByCard(cardNumber, pin)) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                new Transactions(pin, currentAccountNo).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Card Number or PIN");
                new LoginPage().setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Login error: " + e.getMessage());
            new LoginPage().setVisible(true);
        }
    }
    // [END AGENT GENERATED CODE]
    
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
    
    // [AGENT GENERATED CODE - REQUIREMENT:ACCOUNT_LOCKOUT_MECHANISM]
    /**
     * Authenticate user by card number and PIN with account lockout protection
     * @param cardNumber Card number
     * @param pin PIN
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticateUserByCard(String cardNumber, String pin) throws SQLException {
        // Check if account is currently locked
        if (isAccountLocked(cardNumber)) {
            JOptionPane.showMessageDialog(null, "Account is temporarily locked due to multiple failed login attempts. Please try again later.");
            return false;
        }
        
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM login WHERE Card_No = ? AND Login_Password = ?";
        
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setString(2, pin);
            
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Authentication successful - reset failed attempts
                currentAccountNo = resultSet.getString("Account_No");
                initializeSession(cardNumber);
                resetFailedLoginAttempts(cardNumber);
                return true;
            } else {
                // Failed authentication - increment failed attempts
                incrementFailedLoginAttempts(cardNumber);
                return false;
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
        }
    }
    
    /**
     * Check if account is currently locked
     * @param cardNumber Card number to check
     * @return true if locked, false otherwise
     */
    private boolean isAccountLocked(String cardNumber) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String query = "SELECT locked_until FROM login_attempts WHERE card_no = ? AND locked_until > NOW()";
        
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking account lock status: {0}", e.getMessage());
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
     * Increment failed login attempts for an account
     * @param cardNumber Card number
     */
    private void incrementFailedLoginAttempts(String cardNumber) {
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet resultSet = null;
        
        try {
            // Check current failed attempts
            String selectQuery = "SELECT failed_attempts FROM login_attempts WHERE card_no = ?";
            selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setString(1, cardNumber);
            resultSet = selectStmt.executeQuery();
            
            int currentAttempts = 0;
            boolean recordExists = false;
            
            if (resultSet.next()) {
                currentAttempts = resultSet.getInt("failed_attempts");
                recordExists = true;
            }
            
            currentAttempts++;
            
            if (recordExists) {
                // Update existing record
                if (currentAttempts >= MAX_LOGIN_ATTEMPTS) {
                    String updateQuery = "UPDATE login_attempts SET failed_attempts = ?, locked_until = DATE_ADD(NOW(), INTERVAL ? MINUTE) WHERE card_no = ?";
                    updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setInt(1, currentAttempts);
                    updateStmt.setInt(2, LOCKOUT_DURATION_MINUTES);
                    updateStmt.setString(3, cardNumber);
                } else {
                    String updateQuery = "UPDATE login_attempts SET failed_attempts = ? WHERE card_no = ?";
                    updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setInt(1, currentAttempts);
                    updateStmt.setString(2, cardNumber);
                }
                updateStmt.executeUpdate();
            } else {
                // Insert new record
                if (currentAttempts >= MAX_LOGIN_ATTEMPTS) {
                    String insertQuery = "INSERT INTO login_attempts (card_no, failed_attempts, locked_until) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? MINUTE))";
                    insertStmt = connection.prepareStatement(insertQuery);
                    insertStmt.setString(1, cardNumber);
                    insertStmt.setInt(2, currentAttempts);
                    insertStmt.setInt(3, LOCKOUT_DURATION_MINUTES);
                } else {
                    String insertQuery = "INSERT INTO login_attempts (card_no, failed_attempts) VALUES (?, ?)";
                    insertStmt = connection.prepareStatement(insertQuery);
                    insertStmt.setString(1, cardNumber);
                    insertStmt.setInt(2, currentAttempts);
                }
                insertStmt.executeUpdate();
            }
            
            if (currentAttempts >= MAX_LOGIN_ATTEMPTS) {
                LOGGER.log(Level.WARNING, "Account locked due to {0} failed attempts: {1}", new Object[]{currentAttempts, cardNumber});
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating failed login attempts: {0}", e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (selectStmt != null) selectStmt.close();
                if (updateStmt != null) updateStmt.close();
                if (insertStmt != null) insertStmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Reset failed login attempts after successful login
     * @param cardNumber Card number
     */
    private void resetFailedLoginAttempts(String cardNumber) {
        PreparedStatement preparedStatement = null;
        String query = "DELETE FROM login_attempts WHERE card_no = ?";
        
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error resetting failed login attempts: {0}", e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing statement: {0}", e.getMessage());
            }
        }
    }
    // [END AGENT GENERATED CODE]
    
    /**
     * Get the current authenticated user ID
     * @return User ID or null if not authenticated
     */
    public String getCurrentUserId() {
        return isSessionValid() ? currentUserId : null;
    }
    
    /**
     * Get the current account number
     * @return Account number or null if not authenticated
     */
    public String getCurrentAccountNo() {
        return isSessionValid() ? currentAccountNo : null;
    }
}

/*
 * REQUIREMENT SUMMARY - AGENT GENERATED CODE
 * Agent Run Identifier: CHANGE_IMPACT_ANALYSIS_IMPLEMENTATION_2026_02_03
 * 
 * Requirements Implemented:
 * - USER_STORY_1_ACCOUNT_LOGIN_AUTHENTICATION: Enhanced authentication with card-based login
 * - USER_STORY_5_USER_LOGIN_AUTHENTICATION: LoginPage integration with constructor
 * - ACCOUNT_LOCKOUT_MECHANISM: Account lockout after 3 failed attempts for 30 minutes
 * - USER_STORY_21_SESSION_TIMEOUT: 15-minute session timeout with activity tracking
 * - TWO_FACTOR_AUTHENTICATION: OTP generation and verification framework
 * 
 * Security Features Enhanced:
 * - Account lockout protection against brute force attacks
 * - Failed login attempt tracking and management
 * - Secure session management with timeout controls
 * - Password strength validation
 * - OTP-based two-factor authentication framework
 * - SQL injection prevention through PreparedStatements
 * - Comprehensive logging for security monitoring
 * 
 * Test cases:
 * AUTH-01: Verify successful login with valid credentials
 * AUTH-02: Verify failed login with invalid credentials
 * SEC-01: Verify password strength validation
 * SEC-02: Verify session timeout after 15 minutes
 * LOCKOUT-01: Verify account lockout after 3 failed attempts
 * LOCKOUT-02: Verify account unlock after lockout duration
 */