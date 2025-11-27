package banking.management.system;

import banking.management.system.security.*;
import banking.management.system.security.LoginController.AuthResult;
import banking.management.system.security.LoginController.AuthStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * [AGENT GENERATED CODE - REQUIREMENT:US1-AC1,US1-AC3,US1-AC4,US1-AC5]
 * Enhanced login screen with two-factor authentication support and secure authentication flow.
 */
public class Login extends JFrame implements ActionListener {
    
    // UI Components for Login
    private JLabel titleLabel, accountLabel, pinLabel, otpLabel, messageLabel, timerLabel;
    private JTextField accountField;
    private JPasswordField pinField, otpField;
    private JButton loginButton, clearButton, signupButton, cancelButton;
    private JPanel otpPanel;
    
    // Authentication state
    private String accountNo;
    private String sessionId;
    private Timer otpTimer;
    private int otpTimeRemaining = 0;
    
    /**
     * Constructor for Login screen
     */
    public Login() {
        setTitle("Online Banking System - Secure Login");
        setLayout(null);
        
        // Logo and title
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel label = new JLabel(i3);
        label.setBounds(70, 10, 100, 100);
        add(label);
        
        titleLabel = new JLabel("WELCOME TO ATM");
        titleLabel.setFont(new Font("Osward", Font.BOLD, 38));
        titleLabel.setBounds(200, 40, 450, 40);
        add(titleLabel);
        
        // Account Number
        accountLabel = new JLabel("Account No:");
        accountLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        accountLabel.setBounds(125, 150, 375, 30);
        add(accountLabel);
        
        accountField = new JTextField();
        accountField.setBounds(300, 150, 230, 30);
        accountField.setFont(new Font("Arial", Font.BOLD, 14));
        add(accountField);
        
        // PIN
        pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        pinLabel.setBounds(125, 220, 375, 30);
        add(pinLabel);
        
        pinField = new JPasswordField();
        pinField.setBounds(300, 220, 230, 30);
        pinField.setFont(new Font("Arial", Font.BOLD, 14));
        add(pinField);
        
        // Message/Error display
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(125, 300, 450, 30);
        add(messageLabel);
        
        // Buttons for initial login screen
        loginButton = new JButton("SIGN IN");
        loginButton.setBounds(300, 300, 100, 30);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(this);
        add(loginButton);
        
        clearButton = new JButton("CLEAR");
        clearButton.setBounds(430, 300, 100, 30);
        clearButton.setBackground(Color.BLACK);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(this);
        add(clearButton);
        
        signupButton = new JButton("SIGN UP");
        signupButton.setBounds(300, 350, 100, 30);
        signupButton.setBackground(Color.BLACK);
        signupButton.setForeground(Color.WHITE);
        signupButton.addActionListener(this);
        add(signupButton);
        
        // OTP Panel (initially hidden)
        otpPanel = new JPanel();
        otpPanel.setLayout(null);
        otpPanel.setBounds(100, 400, 450, 150);
        otpPanel.setVisible(false);
        add(otpPanel);
        
        otpLabel = new JLabel("Enter Verification Code:");
        otpLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        otpLabel.setBounds(20, 20, 200, 30);
        otpPanel.add(otpLabel);
        
        otpField = new JPasswordField();
        otpField.setBounds(220, 20, 100, 30);
        otpField.setFont(new Font("Arial", Font.BOLD, 14));
        otpPanel.add(otpField);
        
        timerLabel = new JLabel("");
        timerLabel.setFont(new Font("Raleway", Font.ITALIC, 14));
        timerLabel.setBounds(220, 55, 200, 20);
        otpPanel.add(timerLabel);
        
        JButton submitOtpButton = new JButton("SUBMIT");
        submitOtpButton.setBounds(120, 90, 100, 30);
        submitOtpButton.setBackground(Color.BLACK);
        submitOtpButton.setForeground(Color.WHITE);
        submitOtpButton.setActionCommand("SUBMIT_OTP");
        submitOtpButton.addActionListener(this);
        otpPanel.add(submitOtpButton);
        
        cancelButton = new JButton("CANCEL");
        cancelButton.setBounds(230, 90, 100, 30);
        cancelButton.setBackground(Color.BLACK);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setActionCommand("CANCEL_OTP");
        cancelButton.addActionListener(this);
        otpPanel.add(cancelButton);
        
        // Frame settings
        getContentPane().setBackground(new Color(255, 255, 255));
        setSize(800, 650);
        setLocation(350, 10);
        setVisible(true);
    }
    
    /**
     * Handle button actions
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("SIGN IN")) {
            handleLogin();
        } else if (ae.getActionCommand().equals("CLEAR")) {
            accountField.setText("");
            pinField.setText("");
            messageLabel.setText("");
        } else if (ae.getActionCommand().equals("SIGN UP")) {
            setVisible(false);
            new Signup1().setVisible(true);
        } else if (ae.getActionCommand().equals("SUBMIT_OTP")) {
            handleOTPSubmission();
        } else if (ae.getActionCommand().equals("CANCEL_OTP")) {
            resetOTPView();
        }
    }
    
    /**
     * Handle the initial login step
     */
    private void handleLogin() {
        String accountNo = accountField.getText();
        String pin = new String(pinField.getPassword());
        
        if (accountNo.isEmpty() || pin.isEmpty()) {
            messageLabel.setText("Account Number and PIN are required");
            return;
        }
        
        // Validate credentials with LoginController
        AuthResult result = LoginController.validateCredentials(accountNo, pin);
        
        if (result.getStatus() == AuthStatus.REQUIRES_2FA) {
            // Show OTP entry field
            this.accountNo = accountNo;
            messageLabel.setText(result.getMessage());
            showOTPView();
        } else if (result.getStatus() == AuthStatus.SUCCESS) {
            // Should not happen in 2FA flow, but handle just in case
            proceedToMainMenu(accountNo, result.getSessionId());
        } else {
            // Display error message
            messageLabel.setText(result.getMessage());
        }
    }
    
    /**
     * Handle OTP submission for second factor
     */
    private void handleOTPSubmission() {
        String otpCode = new String(otpField.getPassword());
        
        if (otpCode.isEmpty()) {
            messageLabel.setText("Please enter the verification code");
            return;
        }
        
        // Validate OTP with LoginController
        AuthResult result = LoginController.validateOTP(accountNo, otpCode);
        
        if (result.isSuccess()) {
            // Proceed to main menu
            proceedToMainMenu(accountNo, result.getSessionId());
        } else {
            // Display error message
            messageLabel.setText(result.getMessage());
        }
    }
    
    /**
     * Show the OTP input panel and start the timer
     */
    private void showOTPView() {
        otpPanel.setVisible(true);
        otpField.setText("");
        
        // Start timer for OTP expiration countdown
        otpTimeRemaining = 300; // 5 minutes in seconds
        startOTPTimer();
        
        // Adjust frame size if needed
        setSize(800, 650);
    }
    
    /**
     * Reset the view after OTP attempt
     */
    private void resetOTPView() {
        otpPanel.setVisible(false);
        messageLabel.setText("");
        if (otpTimer != null) {
            otpTimer.cancel();
        }
    }
    
    /**
     * Start the OTP expiration countdown timer
     */
    private void startOTPTimer() {
        if (otpTimer != null) {
            otpTimer.cancel();
        }
        
        otpTimer = new Timer();
        otpTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                otpTimeRemaining--;
                updateTimerLabel();
                
                if (otpTimeRemaining <= 0) {
                    SwingUtilities.invokeLater(() -> {
                        messageLabel.setText("Verification code has expired. Please try again.");
                        resetOTPView();
                    });
                    this.cancel();
                }
            }
        }, 0, 1000);
    }
    
    /**
     * Update the timer display
     */
    private void updateTimerLabel() {
        int minutes = otpTimeRemaining / 60;
        int seconds = otpTimeRemaining % 60;
        
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText(String.format("Expires in: %02d:%02d", minutes, seconds));
        });
    }
    
    /**
     * Proceed to main menu after successful authentication
     * @param accountNo User's account number
     * @param sessionId Active session ID
     */
    private void proceedToMainMenu(String accountNo, String sessionId) {
        this.sessionId = sessionId;
        setVisible(false);
        new Transactions(accountNo, sessionId).setVisible(true);
    }
    
    public static void main(String[] args) {
        new Login();
    }
}

/*
 * [AGENT GENERATED CODE - REQUIREMENT:US1-AC1,US1-AC3,US1-AC4,US1-AC5]
 * Test Cases: UI-TC1 (Login UI Flow), AUTH-TC3 (Login Process), AUTH-TC4 (2FA Flow)
 * Agent Run ID: AGR-20251127-001
 */