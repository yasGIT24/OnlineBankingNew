package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Login implementation for Online Banking system
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:US1-AC1,AC2,AC3,AC4,AC5]
 * This class implements the login screen with authentication, validation,
 * error handling and two-factor authentication integration.
 */
public class Login extends JFrame implements ActionListener {
    
    // UI Components
    private JTextField accountTextField;
    private JPasswordField passwordField;
    private JButton loginButton, clearButton, signupButton;
    private JLabel errorMessageLabel;
    
    // OTP related components
    private JPanel otpPanel;
    private JTextField otpTextField;
    private JButton verifyOtpButton, resendOtpButton;
    private JLabel otpMessageLabel;
    
    // Authentication state
    private String currentAccountNo;
    private String currentSessionId;
    private enum AuthState { INITIAL, OTP_VERIFICATION }
    private AuthState currentState = AuthState.INITIAL;
    
    // OTP delivery method (would be user preference in production)
    private String otpMethod = "email"; // or "sms"
    
    /**
     * Constructor - sets up the login UI
     */
    public Login() {
        setTitle("Online Banking Login");
        
        // Layout and styling
        setLayout(null);
        getContentPane().setBackground(new Color(204, 229, 255));
        
        // Logo and title
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);  
        JLabel label = new JLabel(i3);
        label.setBounds(70, 30, 100, 100);
        add(label);
        
        JLabel title = new JLabel("WELCOME TO ONLINE BANKING");
        title.setFont(new Font("Osward", Font.BOLD, 32));
        title.setBounds(200, 40, 600, 40);
        add(title);
        
        JLabel subtitle = new JLabel("Please Login To Your Account");
        subtitle.setFont(new Font("Raleway", Font.BOLD, 24));
        subtitle.setBounds(200, 90, 400, 40);
        add(subtitle);
        
        // Account Number
        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        accountLabel.setBounds(120, 160, 150, 30);
        add(accountLabel);
        
        accountTextField = new JTextField();
        accountTextField.setBounds(320, 160, 250, 30);
        accountTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        add(accountTextField);
        
        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        passwordLabel.setBounds(120, 210, 150, 30);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(320, 210, 250, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        add(passwordField);
        
        // Error message label
        errorMessageLabel = new JLabel("");
        errorMessageLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        errorMessageLabel.setForeground(Color.RED);
        errorMessageLabel.setBounds(320, 240, 250, 30);
        add(errorMessageLabel);
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.setBounds(320, 280, 100, 30);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(this);
        add(loginButton);
        
        // Clear button
        clearButton = new JButton("Clear");
        clearButton.setBounds(440, 280, 100, 30);
        clearButton.setBackground(Color.BLACK);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(this);
        add(clearButton);
        
        // Sign up button
        signupButton = new JButton("Sign Up");
        signupButton.setBounds(380, 330, 100, 30);
        signupButton.setBackground(Color.BLACK);
        signupButton.setForeground(Color.WHITE);
        signupButton.addActionListener(this);
        add(signupButton);
        
        // Password policy help
        JButton passwordHelpButton = new JButton("?");
        passwordHelpButton.setBounds(580, 210, 30, 30);
        passwordHelpButton.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                                            PasswordPolicy.getPasswordRules(),
                                            "Password Requirements", 
                                            JOptionPane.INFORMATION_MESSAGE));
        add(passwordHelpButton);
        
        // Create OTP panel (initially invisible)
        createOtpPanel();
        
        // Frame settings
        setSize(700, 500);
        setVisible(true);
        setLocation(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * Creates the OTP verification panel components
     */
    private void createOtpPanel() {
        otpPanel = new JPanel();
        otpPanel.setLayout(null);
        otpPanel.setBounds(100, 350, 500, 100);
        otpPanel.setBackground(new Color(204, 229, 255));
        otpPanel.setVisible(false);
        
        JLabel otpLabel = new JLabel("Enter OTP:");
        otpLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        otpLabel.setBounds(20, 10, 100, 30);
        otpPanel.add(otpLabel);
        
        otpTextField = new JTextField();
        otpTextField.setBounds(120, 10, 150, 30);
        otpTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        otpPanel.add(otpTextField);
        
        verifyOtpButton = new JButton("Verify");
        verifyOtpButton.setBounds(120, 50, 100, 30);
        verifyOtpButton.setBackground(Color.BLACK);
        verifyOtpButton.setForeground(Color.WHITE);
        verifyOtpButton.addActionListener(this);
        otpPanel.add(verifyOtpButton);
        
        resendOtpButton = new JButton("Resend");
        resendOtpButton.setBounds(230, 50, 100, 30);
        resendOtpButton.setBackground(Color.BLACK);
        resendOtpButton.setForeground(Color.WHITE);
        resendOtpButton.addActionListener(this);
        otpPanel.add(resendOtpButton);
        
        otpMessageLabel = new JLabel("");
        otpMessageLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        otpMessageLabel.setBounds(280, 10, 200, 30);
        otpPanel.add(otpMessageLabel);
        
        add(otpPanel);
    }
    
    /**
     * Handles all button click events
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == clearButton) {
            accountTextField.setText("");
            passwordField.setText("");
            errorMessageLabel.setText("");
        } else if (ae.getSource() == loginButton) {
            handleLoginAttempt();
        } else if (ae.getSource() == signupButton) {
            setVisible(false);
            new Signup1().setVisible(true);
        } else if (ae.getSource() == verifyOtpButton) {
            verifyOtp();
        } else if (ae.getSource() == resendOtpButton) {
            sendOtp();
        }
    }
    
    /**
     * Handles initial login credential validation
     */
    private void handleLoginAttempt() {
        String accountNo = accountTextField.getText();
        String password = new String(passwordField.getPassword());
        
        // Validate inputs
        if (accountNo.equals("")) {
            errorMessageLabel.setText("Account number required");
            return;
        }
        
        if (password.equals("")) {
            errorMessageLabel.setText("Password required");
            return;
        }
        
        // Validate credentials against database
        ConnectionSql conn = new ConnectionSql();
        boolean validCredentials = conn.validateCredentials(accountNo, password);
        
        if (!validCredentials) {
            errorMessageLabel.setText("Invalid credentials");
            return;
        }
        
        // Store account for OTP verification
        currentAccountNo = accountNo;
        
        // Proceed to OTP verification
        currentState = AuthState.OTP_VERIFICATION;
        otpPanel.setVisible(true);
        otpMessageLabel.setForeground(Color.BLACK);
        otpMessageLabel.setText("OTP sent");
        
        // Adjust frame size to fit OTP panel
        setSize(700, 600);
        
        // Send OTP for verification
        sendOtp();
    }
    
    /**
     * Sends OTP to user via configured method
     */
    private void sendOtp() {
        // Send OTP via configured method
        OTPService.deliverOTP(currentAccountNo, otpMethod);
        
        // Update UI
        otpMessageLabel.setForeground(Color.BLACK);
        otpMessageLabel.setText("OTP sent");
        otpTextField.setText("");
    }
    
    /**
     * Verifies entered OTP and completes login if valid
     */
    private void verifyOtp() {
        String enteredOtp = otpTextField.getText();
        
        if (enteredOtp.equals("")) {
            otpMessageLabel.setForeground(Color.RED);
            otpMessageLabel.setText("OTP required");
            return;
        }
        
        // Validate OTP
        boolean validOtp = OTPService.validateOTP(currentAccountNo, enteredOtp);
        
        if (!validOtp) {
            otpMessageLabel.setForeground(Color.RED);
            otpMessageLabel.setText("Invalid OTP");
            return;
        }
        
        // Create session for the user
        currentSessionId = SessionManager.createSession(currentAccountNo, this::handleSessionTimeout);
        
        // Login successful - redirect to transactions
        setVisible(false);
        new Transactions(new String(passwordField.getPassword()), currentAccountNo).setVisible(true);
    }
    
    /**
     * Handles session timeout by returning to login screen
     */
    private void handleSessionTimeout() {
        JOptionPane.showMessageDialog(null, 
                                    "Your session has timed out due to inactivity.", 
                                    "Session Timeout", 
                                    JOptionPane.WARNING_MESSAGE);
        
        // Close any open windows and show login screen again
        setVisible(false);
        new Login().setVisible(true);
    }
    
    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        new Login();
    }
}

/*
 * File generated/modified to fulfill User Story 1: Account Login & Authentication
 * Test cases: Authentication flow, 2FA, error handling, session management
 * Agent run: VIBE-1001
 */