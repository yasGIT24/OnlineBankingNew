package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created Login.java to implement:
 * 1. Secure login functionality with username/password
 * 2. Two-factor authentication integration
 * 3. Failed login attempt handling
 * 4. Session timeout initialization
 */
public class Login extends JFrame implements ActionListener {
    private JLabel titleLabel, accountLabel, pinLabel, securityLabel;
    private JTextField accountField;
    private JPasswordField pinField;
    private JButton loginButton, clearButton, signupButton;
    private JPanel loginPanel;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Maximum allowed login attempts before account lockout
     */
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION = 15; // minutes
    
    public Login() {
        setTitle("Online Banking System - Login");
        
        // Set window properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 480);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel with background
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(new Color(204, 229, 255));
        
        // Add bank logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image logoImage = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledLogoIcon = new ImageIcon(logoImage);
        JLabel logoLabel = new JLabel(scaledLogoIcon);
        logoLabel.setBounds(70, 30, 100, 100);
        loginPanel.add(logoLabel);
        
        // Title
        titleLabel = new JLabel("ONLINE BANKING SYSTEM");
        titleLabel.setFont(new Font("Osward", Font.BOLD, 32));
        titleLabel.setBounds(200, 50, 450, 40);
        titleLabel.setForeground(Color.BLACK);
        loginPanel.add(titleLabel);
        
        // Account number field
        accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        accountLabel.setBounds(150, 150, 200, 30);
        loginPanel.add(accountLabel);
        
        accountField = new JTextField();
        accountField.setFont(new Font("Raleway", Font.PLAIN, 18));
        accountField.setBounds(350, 150, 250, 30);
        loginPanel.add(accountField);
        
        // PIN field
        pinLabel = new JLabel("Password/PIN:");
        pinLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        pinLabel.setBounds(150, 200, 200, 30);
        loginPanel.add(pinLabel);
        
        pinField = new JPasswordField();
        pinField.setFont(new Font("Raleway", Font.PLAIN, 18));
        pinField.setBounds(350, 200, 250, 30);
        loginPanel.add(pinField);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Security message for 2FA
         */
        securityLabel = new JLabel("Two-factor authentication will be required after login");
        securityLabel.setFont(new Font("Raleway", Font.ITALIC, 14));
        securityLabel.setForeground(new Color(0, 102, 204));
        securityLabel.setBounds(200, 240, 400, 30);
        loginPanel.add(securityLabel);
        
        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBounds(350, 300, 120, 40);
        loginButton.addActionListener(this);
        loginPanel.add(loginButton);
        
        // Clear button
        clearButton = new JButton("CLEAR");
        clearButton.setBackground(Color.GRAY);
        clearButton.setForeground(Color.WHITE);
        clearButton.setFont(new Font("Arial", Font.BOLD, 16));
        clearButton.setBounds(200, 300, 120, 40);
        clearButton.addActionListener(this);
        loginPanel.add(clearButton);
        
        // Signup button
        signupButton = new JButton("SIGN UP");
        signupButton.setBackground(new Color(0, 102, 204));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setBounds(500, 300, 120, 40);
        signupButton.addActionListener(this);
        loginPanel.add(signupButton);
        
        // Add panel to frame
        add(loginPanel);
        setVisible(true);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Add focus listener to clear fields when window gains focus
         * (security feature to prevent reading passwords from screen)
         */
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // Only clear password field for security
                pinField.setText("");
            }
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Action handler for button clicks
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == clearButton) {
            accountField.setText("");
            pinField.setText("");
        } else if (ae.getSource() == loginButton) {
            authenticateUser();
        } else if (ae.getSource() == signupButton) {
            setVisible(false);
            new Signup1().setVisible(true);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Authenticate user with secure practices
     */
    private void authenticateUser() {
        String accountNo = accountField.getText();
        char[] pinChars = pinField.getPassword();
        String pin = new String(pinChars);
        
        // Clear sensitive data from memory
        java.util.Arrays.fill(pinChars, '0');
        
        // Validate inputs
        if (accountNo.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Account Number and PIN are required", 
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check for SQL injection attempts
        if (ValidationUtils.containsSqlInjection(accountNo) || ValidationUtils.containsSqlInjection(pin)) {
            // Log potential attack
            try {
                AuditLogger.logActivity(accountNo, "Possible SQL injection attempt at login", "Security");
            } catch (Exception e) {
                ErrorHandler.handleException(e, "Error logging security event");
            }
            
            JOptionPane.showMessageDialog(this, 
                "Invalid input detected", 
                "Security Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if account is locked
        if (isAccountLocked(accountNo)) {
            JOptionPane.showMessageDialog(this, 
                "This account is temporarily locked due to multiple failed login attempts.\n" +
                "Please try again later or contact customer support.", 
                "Account Locked", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ConnectionSql c = new ConnectionSql();
            
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * Use prepared statement to prevent SQL injection
             */
            PreparedStatement ps = c.getConnection().prepareStatement(
                "SELECT * FROM login WHERE Account_No = ? AND Login_Password = ?");
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Reset failed login attempts on successful login
                 */
                resetFailedLoginAttempts(accountNo);
                
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Log successful login
                 */
                AuditLogger.logActivity(accountNo, "User logged in successfully", "Authentication");
                
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Initiate 2FA verification
                 */
                boolean twoFAVerified = initiateAndVerify2FA(accountNo);
                
                if (twoFAVerified) {
                    // Close login window
                    setVisible(false);
                    
                    // Start transactions screen with session management
                    new Transactions(pin, accountNo).setVisible(true);
                } else {
                    // 2FA failed
                    JOptionPane.showMessageDialog(this, 
                        "Two-factor authentication failed", 
                        "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    
                    AuditLogger.logActivity(accountNo, "Two-factor authentication failed", "Security");
                }
                
            } else {
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Handle failed login attempt
                 */
                handleFailedLogin(accountNo);
                
                JOptionPane.showMessageDialog(this, 
                    "Invalid Account Number or PIN", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Login error");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Handle failed login attempt with attempt tracking
     */
    private void handleFailedLogin(String accountNo) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            // Check if account exists
            PreparedStatement checkStmt = c.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM login WHERE Account_No = ?");
            checkStmt.setString(1, accountNo);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Account exists, increment failed attempts
                PreparedStatement updateStmt = c.getConnection().prepareStatement(
                    "UPDATE login SET failed_attempts = IFNULL(failed_attempts, 0) + 1, " +
                    "last_failed_attempt = CURRENT_TIMESTAMP WHERE Account_No = ?");
                updateStmt.setString(1, accountNo);
                updateStmt.executeUpdate();
                
                // Check if account should be locked
                PreparedStatement lockCheckStmt = c.getConnection().prepareStatement(
                    "SELECT failed_attempts FROM login WHERE Account_No = ?");
                lockCheckStmt.setString(1, accountNo);
                ResultSet lockRs = lockCheckStmt.executeQuery();
                
                if (lockRs.next() && lockRs.getInt("failed_attempts") >= MAX_LOGIN_ATTEMPTS) {
                    // Lock account
                    PreparedStatement lockStmt = c.getConnection().prepareStatement(
                        "UPDATE login SET account_locked = TRUE, " +
                        "lock_time = CURRENT_TIMESTAMP WHERE Account_No = ?");
                    lockStmt.setString(1, accountNo);
                    lockStmt.executeUpdate();
                    
                    // Log security event
                    AuditLogger.logActivity(accountNo, 
                        "Account locked due to multiple failed login attempts", "Security");
                }
            }
            
            // Log failed login attempt
            AuditLogger.logActivity(accountNo, "Failed login attempt", "Security");
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error handling failed login");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Check if account is locked
     */
    private boolean isAccountLocked(String accountNo) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "SELECT account_locked, lock_time FROM login " +
                "WHERE Account_No = ? AND account_locked = TRUE");
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Check if lockout duration has expired
                Timestamp lockTime = rs.getTimestamp("lock_time");
                if (lockTime != null) {
                    long lockoutMillis = LOCKOUT_DURATION * 60 * 1000; // Convert to milliseconds
                    long currentTime = System.currentTimeMillis();
                    long lockTimeMillis = lockTime.getTime();
                    
                    if ((currentTime - lockTimeMillis) > lockoutMillis) {
                        // Lockout period expired, unlock the account
                        PreparedStatement unlockStmt = c.getConnection().prepareStatement(
                            "UPDATE login SET account_locked = FALSE, failed_attempts = 0 " +
                            "WHERE Account_No = ?");
                        unlockStmt.setString(1, accountNo);
                        unlockStmt.executeUpdate();
                        
                        // Log security event
                        AuditLogger.logActivity(accountNo, "Account automatically unlocked", "Security");
                        
                        return false;
                    }
                    
                    // Account is still locked
                    return true;
                }
            }
            
            // Account is not locked
            return false;
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error checking account lock status");
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Reset failed login attempts on successful login
     */
    private void resetFailedLoginAttempts(String accountNo) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "UPDATE login SET failed_attempts = 0, account_locked = FALSE WHERE Account_No = ?");
            ps.setString(1, accountNo);
            ps.executeUpdate();
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error resetting failed login attempts");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Implement two-factor authentication
     */
    private boolean initiateAndVerify2FA(String accountNo) {
        try {
            // Initialize TwoFactorAuth
            TwoFactorAuth twoFA = new TwoFactorAuth();
            
            // Get user's contact information for OTP delivery
            String contactMethod = getUserContactMethod(accountNo);
            
            if (contactMethod == null || contactMethod.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No contact method available for two-factor authentication", 
                    "2FA Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Send OTP
            String otpSent = twoFA.sendOTP(accountNo, contactMethod);
            
            if (otpSent == null) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to send verification code", 
                    "2FA Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Display OTP verification dialog
            String userOtp = JOptionPane.showInputDialog(this,
                "A verification code has been sent to " + maskContactInfo(contactMethod) + ".\n" +
                "Please enter the code below:",
                "Two-Factor Authentication",
                JOptionPane.QUESTION_MESSAGE);
            
            // Verify OTP
            return twoFA.verifyOTP(accountNo, userOtp);
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Two-factor authentication error");
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get user's preferred contact method for OTP
     */
    private String getUserContactMethod(String accountNo) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "SELECT email, phone FROM signup3 WHERE Account_No = ?");
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                
                // Prefer email if available
                if (email != null && !email.isEmpty()) {
                    return "email:" + email;
                }
                
                // Fall back to phone
                if (phone != null && !phone.isEmpty()) {
                    return "phone:" + phone;
                }
            }
            
            return null;
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error fetching user contact information");
            return null;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Mask contact information for display
     */
    private String maskContactInfo(String contactInfo) {
        if (contactInfo == null || contactInfo.isEmpty()) {
            return "***";
        }
        
        String[] parts = contactInfo.split(":");
        if (parts.length != 2) {
            return "***";
        }
        
        String type = parts[0];
        String value = parts[1];
        
        if (type.equals("email")) {
            // Mask email: j***@example.com
            int atIndex = value.indexOf('@');
            if (atIndex > 1) {
                return value.substring(0, 1) + "***" + value.substring(atIndex);
            }
        } else if (type.equals("phone")) {
            // Mask phone: ***-***-1234
            if (value.length() > 4) {
                return "***-***-" + value.substring(value.length() - 4);
            }
        }
        
        return "***";
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Main method to start application
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error setting look and feel");
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Login();
            }
        });
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-LOGIN-001, TC-LOGIN-002, TC-SEC-011
 * Requirement IDs: US-1 (Account Login & Authentication)
 * Agent Run: AGENT-20251127-01
 */