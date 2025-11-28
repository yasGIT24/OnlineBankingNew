package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 5: Alerts & Notifications]
 * This class implements the notification settings UI with:
 * 1. Configurable SMS/email for large transactions
 * 2. Settings for login attempt alerts
 * 3. Low balance threshold configuration
 * 4. Channel preference selection (SMS/Email/Both)
 *
 * Linked to Value Stream Step: Alert Configuration
 * Linked to test cases: NOTIF-UI-01, NOTIF-UI-02
 */
public class NotificationSettings extends JFrame implements ActionListener {
    // UI Components
    private JPanel mainPanel;
    private JButton saveButton, backButton, testButton;
    
    // Transaction notification components
    private JComboBox<String> transactionChannelCombo;
    private JTextField transactionThresholdField;
    private JCheckBox transactionEnabledCheck;
    
    // Login attempt notification components
    private JComboBox<String> loginAttemptChannelCombo;
    private JCheckBox loginAttemptEnabledCheck;
    
    // Low balance notification components
    private JComboBox<String> lowBalanceChannelCombo;
    private JTextField lowBalanceThresholdField;
    private JCheckBox lowBalanceEnabledCheck;
    
    // User data
    private String pin;
    private String accountNo;
    
    // Service
    private NotificationService notificationService;
    
    /**
     * Constructor
     * 
     * @param pin User's PIN
     * @param accountNo User's account number
     */
    public NotificationSettings(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        notificationService = new NotificationService();
        
        // Set up window
        setTitle("Notification Settings");
        setSize(1200, 800);
        setLayout(new BorderLayout());
        
        // Validate session before proceeding
        LoginModel loginModel = new LoginModel();
        if (!loginModel.isSessionValid()) {
            JOptionPane.showMessageDialog(this, 
                "Your session has expired. Please login again.", 
                "Session Timeout", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Create panels
        JPanel headerPanel = createHeaderPanel();
        mainPanel = createMainPanel();
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load user's notification preferences
        loadNotificationPreferences();
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    /**
     * Create the header panel with title and logo
     * 
     * @return Header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(204, 229, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Bank logo
        ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image h2 = h1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon h3 = new ImageIcon(h2);  
        JLabel logo = new JLabel(h3);
        panel.add(logo, BorderLayout.WEST);
        
        // Header
        JLabel header = new JLabel("Notification Settings");
        header.setFont(new Font("Osward", Font.BOLD, 32));
        header.setHorizontalAlignment(JLabel.CENTER);
        header.setForeground(Color.BLACK);
        panel.add(header, BorderLayout.CENTER);
        
        // Account info panel
        JPanel accountPanel = new JPanel(new GridLayout(1, 1));
        accountPanel.setBackground(new Color(204, 229, 255));
        
        JLabel accountLabel = new JLabel("Account: " + accountNo);
        accountLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        accountLabel.setHorizontalAlignment(JLabel.RIGHT);
        accountPanel.add(accountLabel);
        
        panel.add(accountPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Create the main panel with notification settings
     * 
     * @return Main panel
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        // Add notification settings sections
        panel.add(createTransactionNotificationPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createLoginAttemptNotificationPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(createLowBalanceNotificationPanel());
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Description panel
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BorderLayout());
        descriptionPanel.setBackground(new Color(240, 248, 255));
        descriptionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Important Information", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Raleway", Font.BOLD, 14)));
        
        JTextArea descriptionText = new JTextArea(
            "Notifications help keep your account secure and keep you informed about important account activities. " +
            "You can customize the channel (SMS, Email, or Both) and thresholds for different types of notifications.\n\n" +
            "• Transaction notifications alert you when large transactions occur on your account.\n" +
            "• Login attempt notifications alert you when there are failed login attempts.\n" +
            "• Low balance notifications alert you when your account balance falls below a specified threshold.\n\n" +
            "For your security, some notifications cannot be disabled completely."
        );
        descriptionText.setFont(new Font("Raleway", Font.PLAIN, 14));
        descriptionText.setEditable(false);
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setBackground(new Color(240, 248, 255));
        descriptionText.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        descriptionPanel.add(descriptionText, BorderLayout.CENTER);
        panel.add(descriptionPanel);
        
        return panel;
    }
    
    /**
     * Create the transaction notification panel
     * 
     * @return Transaction notification panel
     */
    private JPanel createTransactionNotificationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Large Transaction Alerts", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Raleway", Font.BOLD, 16)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Enabled checkbox
        transactionEnabledCheck = new JCheckBox("Enable large transaction notifications");
        transactionEnabledCheck.setFont(new Font("Raleway", Font.BOLD, 14));
        transactionEnabledCheck.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(transactionEnabledCheck, gbc);
        
        // Channel selection
        JLabel channelLabel = new JLabel("Notification Channel:");
        channelLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(channelLabel, gbc);
        
        String[] channelOptions = {
            NotificationService.CHANNEL_SMS,
            NotificationService.CHANNEL_EMAIL,
            NotificationService.CHANNEL_BOTH,
            NotificationService.CHANNEL_NONE
        };
        
        transactionChannelCombo = new JComboBox<>(channelOptions);
        transactionChannelCombo.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(transactionChannelCombo, gbc);
        
        // Threshold
        JLabel thresholdLabel = new JLabel("Threshold Amount (Rs.):");
        thresholdLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(thresholdLabel, gbc);
        
        transactionThresholdField = new JTextField(10);
        transactionThresholdField.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(transactionThresholdField, gbc);
        
        // Description
        JLabel descriptionLabel = new JLabel(
            "<html>You will be notified when a transaction exceeds this threshold amount.<br>" +
            "Recommended: Rs. 10,000 or higher.</html>");
        descriptionLabel.setFont(new Font("Raleway", Font.ITALIC, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(descriptionLabel, gbc);
        
        return panel;
    }
    
    /**
     * Create the login attempt notification panel
     * 
     * @return Login attempt notification panel
     */
    private JPanel createLoginAttemptNotificationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Failed Login Attempt Alerts", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Raleway", Font.BOLD, 16)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Enabled checkbox
        loginAttemptEnabledCheck = new JCheckBox("Enable failed login attempt notifications");
        loginAttemptEnabledCheck.setFont(new Font("Raleway", Font.BOLD, 14));
        loginAttemptEnabledCheck.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(loginAttemptEnabledCheck, gbc);
        
        // Channel selection
        JLabel channelLabel = new JLabel("Notification Channel:");
        channelLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(channelLabel, gbc);
        
        String[] channelOptions = {
            NotificationService.CHANNEL_SMS,
            NotificationService.CHANNEL_EMAIL,
            NotificationService.CHANNEL_BOTH,
            NotificationService.CHANNEL_NONE
        };
        
        loginAttemptChannelCombo = new JComboBox<>(channelOptions);
        loginAttemptChannelCombo.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(loginAttemptChannelCombo, gbc);
        
        // Description
        JLabel descriptionLabel = new JLabel(
            "<html>You will be notified when there are failed login attempts on your account.<br>" +
            "For security purposes, we strongly recommend keeping this enabled.</html>");
        descriptionLabel.setFont(new Font("Raleway", Font.ITALIC, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(descriptionLabel, gbc);
        
        return panel;
    }
    
    /**
     * Create the low balance notification panel
     * 
     * @return Low balance notification panel
     */
    private JPanel createLowBalanceNotificationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Low Balance Alerts", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Raleway", Font.BOLD, 16)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Enabled checkbox
        lowBalanceEnabledCheck = new JCheckBox("Enable low balance notifications");
        lowBalanceEnabledCheck.setFont(new Font("Raleway", Font.BOLD, 14));
        lowBalanceEnabledCheck.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lowBalanceEnabledCheck, gbc);
        
        // Channel selection
        JLabel channelLabel = new JLabel("Notification Channel:");
        channelLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(channelLabel, gbc);
        
        String[] channelOptions = {
            NotificationService.CHANNEL_SMS,
            NotificationService.CHANNEL_EMAIL,
            NotificationService.CHANNEL_BOTH,
            NotificationService.CHANNEL_NONE
        };
        
        lowBalanceChannelCombo = new JComboBox<>(channelOptions);
        lowBalanceChannelCombo.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(lowBalanceChannelCombo, gbc);
        
        // Threshold
        JLabel thresholdLabel = new JLabel("Threshold Amount (Rs.):");
        thresholdLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(thresholdLabel, gbc);
        
        lowBalanceThresholdField = new JTextField(10);
        lowBalanceThresholdField.setFont(new Font("Raleway", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(lowBalanceThresholdField, gbc);
        
        // Description
        JLabel descriptionLabel = new JLabel(
            "<html>You will be notified when your account balance falls below this threshold.<br>" +
            "Recommended: At least Rs. 1,000 or your typical minimum balance.</html>");
        descriptionLabel.setFont(new Font("Raleway", Font.ITALIC, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(descriptionLabel, gbc);
        
        return panel;
    }
    
    /**
     * Create the button panel with save and back buttons
     * 
     * @return Button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(204, 229, 255));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        testButton = new JButton("Test Notification");
        testButton.setBackground(new Color(0, 102, 204));
        testButton.setForeground(Color.WHITE);
        testButton.setFont(new Font("Raleway", Font.BOLD, 14));
        testButton.addActionListener(this);
        panel.add(testButton);
        
        saveButton = new JButton("Save Settings");
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Raleway", Font.BOLD, 14));
        saveButton.addActionListener(this);
        panel.add(saveButton);
        
        backButton = new JButton("Back");
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Raleway", Font.BOLD, 14));
        backButton.addActionListener(this);
        panel.add(backButton);
        
        return panel;
    }
    
    /**
     * Load user's notification preferences
     */
    private void loadNotificationPreferences() {
        List<NotificationService.NotificationPreference> preferences = 
            notificationService.getAllNotificationPreferences(accountNo);
        
        // Set default values first
        transactionEnabledCheck.setSelected(true);
        transactionChannelCombo.setSelectedItem(NotificationService.CHANNEL_SMS);
        transactionThresholdField.setText("10000");
        
        loginAttemptEnabledCheck.setSelected(true);
        loginAttemptChannelCombo.setSelectedItem(NotificationService.CHANNEL_BOTH);
        
        lowBalanceEnabledCheck.setSelected(true);
        lowBalanceChannelCombo.setSelectedItem(NotificationService.CHANNEL_SMS);
        lowBalanceThresholdField.setText("1000");
        
        // Update with user's preferences
        for (NotificationService.NotificationPreference pref : preferences) {
            switch (pref.getNotificationType()) {
                case NotificationService.NOTIFICATION_TRANSACTION:
                    transactionEnabledCheck.setSelected(!NotificationService.CHANNEL_NONE.equals(pref.getChannel()));
                    transactionChannelCombo.setSelectedItem(pref.getChannel());
                    transactionThresholdField.setText(String.valueOf((int)pref.getThreshold()));
                    break;
                case NotificationService.NOTIFICATION_LOGIN_ATTEMPT:
                    loginAttemptEnabledCheck.setSelected(!NotificationService.CHANNEL_NONE.equals(pref.getChannel()));
                    loginAttemptChannelCombo.setSelectedItem(pref.getChannel());
                    break;
                case NotificationService.NOTIFICATION_LOW_BALANCE:
                    lowBalanceEnabledCheck.setSelected(!NotificationService.CHANNEL_NONE.equals(pref.getChannel()));
                    lowBalanceChannelCombo.setSelectedItem(pref.getChannel());
                    lowBalanceThresholdField.setText(String.valueOf((int)pref.getThreshold()));
                    break;
            }
        }
    }
    
    /**
     * Save notification preferences
     */
    private boolean saveNotificationPreferences() {
        try {
            // Transaction notifications
            String transactionChannel = transactionEnabledCheck.isSelected() ?
                                      (String)transactionChannelCombo.getSelectedItem() :
                                      NotificationService.CHANNEL_NONE;
                                      
            double transactionThreshold = 10000.0; // Default value
            try {
                transactionThreshold = Double.parseDouble(transactionThresholdField.getText());
                if (transactionThreshold <= 0) {
                    transactionThreshold = 10000.0;
                    transactionThresholdField.setText("10000");
                }
            } catch (NumberFormatException e) {
                transactionThresholdField.setText("10000");
            }
            
            notificationService.updateNotificationPreference(
                accountNo, 
                NotificationService.NOTIFICATION_TRANSACTION, 
                transactionChannel, 
                transactionThreshold
            );
            
            // Login attempt notifications
            String loginAttemptChannel = loginAttemptEnabledCheck.isSelected() ?
                                       (String)loginAttemptChannelCombo.getSelectedItem() :
                                       NotificationService.CHANNEL_NONE;
                                       
            notificationService.updateNotificationPreference(
                accountNo, 
                NotificationService.NOTIFICATION_LOGIN_ATTEMPT, 
                loginAttemptChannel, 
                0 // No threshold needed
            );
            
            // Low balance notifications
            String lowBalanceChannel = lowBalanceEnabledCheck.isSelected() ?
                                     (String)lowBalanceChannelCombo.getSelectedItem() :
                                     NotificationService.CHANNEL_NONE;
                                     
            double lowBalanceThreshold = 1000.0; // Default value
            try {
                lowBalanceThreshold = Double.parseDouble(lowBalanceThresholdField.getText());
                if (lowBalanceThreshold <= 0) {
                    lowBalanceThreshold = 1000.0;
                    lowBalanceThresholdField.setText("1000");
                }
            } catch (NumberFormatException e) {
                lowBalanceThresholdField.setText("1000");
            }
            
            notificationService.updateNotificationPreference(
                accountNo, 
                NotificationService.NOTIFICATION_LOW_BALANCE, 
                lowBalanceChannel, 
                lowBalanceThreshold
            );
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Send test notification
     */
    private void sendTestNotification() {
        // Save preferences first to ensure we're testing the latest settings
        saveNotificationPreferences();
        
        // Create a test notification based on the type
        String[] options = {"Transaction Alert", "Login Attempt Alert", "Low Balance Alert"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Which type of notification would you like to test?", 
            "Test Notification", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
        
        boolean sent = false;
        if (choice == 0) {
            // Test transaction notification
            double amount = 10000.0; // Use threshold amount
            try {
                amount = Double.parseDouble(transactionThresholdField.getText());
            } catch (NumberFormatException e) {
                // Use default
            }
            sent = notificationService.sendTransactionNotification(
                accountNo, amount, "Test", "This is a test notification"
            );
        } else if (choice == 1) {
            // Test login attempt notification
            sent = notificationService.sendLoginAttemptNotification(
                accountNo, "This is a test notification. No actual login attempt occurred."
            );
        } else if (choice == 2) {
            // Test low balance notification
            double balance = 999.0; // Just below default threshold
            try {
                balance = Double.parseDouble(lowBalanceThresholdField.getText()) - 1;
            } catch (NumberFormatException e) {
                // Use default
            }
            sent = notificationService.sendLowBalanceNotification(
                accountNo, balance
            );
        }
        
        if (sent) {
            JOptionPane.showMessageDialog(this, 
                "Test notification sent successfully!\n\n" +
                "(In a production system, this would be delivered via the selected channel)", 
                "Notification Sent", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to send test notification. Please check your notification settings.", 
                "Notification Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handle button actions
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == saveButton) {
                boolean success = saveNotificationPreferences();
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Notification settings saved successfully!", 
                        "Settings Saved", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to save notification settings. Please try again.", 
                        "Save Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else if (ae.getSource() == testButton) {
                sendTestNotification();
            } else if (ae.getSource() == backButton) {
                setVisible(false);
                new Transactions(pin, accountNo).setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        new NotificationSettings("", "");
    }
}

/* 
 * Test cases:
 * NOTIF-UI-01: Verify notification settings are saved correctly
 * NOTIF-UI-02: Verify test notifications are sent correctly
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */