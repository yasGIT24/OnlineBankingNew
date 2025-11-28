package banking.management.system;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 5: Alerts & Notifications]
 * This class implements alert and notification functionality with:
 * 1. SMS/email alerts for large transactions
 * 2. Alerts for failed login attempts
 * 3. Low balance threshold notifications
 * 4. Customizable notification preferences
 * 
 * Linked to Value Stream Step: Alert Triggering, Notification Delivery
 * Linked to test cases: NOTIF-01, NOTIF-02, NOTIF-03
 */
public class NotificationService {
    private final ConnectionSql connectionSql;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(NotificationService.class.getName());
    
    // Notification types
    public static final String NOTIFICATION_TRANSACTION = "TRANSACTION";
    public static final String NOTIFICATION_LOGIN_ATTEMPT = "LOGIN_ATTEMPT";
    public static final String NOTIFICATION_LOW_BALANCE = "LOW_BALANCE";
    public static final String NOTIFICATION_SECURITY = "SECURITY";
    
    // Notification channels
    public static final String CHANNEL_SMS = "SMS";
    public static final String CHANNEL_EMAIL = "EMAIL";
    public static final String CHANNEL_BOTH = "BOTH";
    public static final String CHANNEL_NONE = "NONE";
    
    // Default thresholds
    private static final double DEFAULT_LARGE_TRANSACTION_THRESHOLD = 10000.0;
    private static final double DEFAULT_LOW_BALANCE_THRESHOLD = 1000.0;
    
    /**
     * Constructor
     */
    public NotificationService() {
        connectionSql = new ConnectionSql();
        connection = connectionSql.c;
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Database connection failed in NotificationService");
        }
    }
    
    /**
     * Send a transaction notification if it exceeds the user's threshold
     * 
     * @param accountNo The account number
     * @param amount Transaction amount
     * @param type Transaction type
     * @param description Transaction description
     * @return true if notification was sent
     */
    public boolean sendTransactionNotification(String accountNo, double amount, String type, String description) {
        // Get user's notification preferences
        NotificationPreference preference = getUserNotificationPreference(accountNo, NOTIFICATION_TRANSACTION);
        if (preference == null || CHANNEL_NONE.equals(preference.getChannel())) {
            return false; // No notification needed
        }
        
        // Check if transaction amount exceeds the threshold
        double threshold = preference.getThreshold() > 0 ? 
                          preference.getThreshold() : 
                          DEFAULT_LARGE_TRANSACTION_THRESHOLD;
        
        if (amount < threshold) {
            return false; // Below threshold, no notification needed
        }
        
        // Get user contact info
        UserContactInfo contactInfo = getUserContactInfo(accountNo);
        if (contactInfo == null) {
            LOGGER.log(Level.WARNING, "Contact information not found for account: {0}", accountNo);
            return false;
        }
        
        // Prepare notification message
        String message = String.format(
            "ALERT: A %s transaction of Rs. %.2f has been made on your account ending with %s. %s",
            type, amount, maskAccountNumber(accountNo), description
        );
        
        // Send notification based on preferred channel
        boolean sent = false;
        switch (preference.getChannel()) {
            case CHANNEL_SMS:
                sent = sendSMS(contactInfo.getPhoneNumber(), message);
                break;
            case CHANNEL_EMAIL:
                sent = sendEmail(contactInfo.getEmail(), "Transaction Alert", message);
                break;
            case CHANNEL_BOTH:
                boolean smsSent = sendSMS(contactInfo.getPhoneNumber(), message);
                boolean emailSent = sendEmail(contactInfo.getEmail(), "Transaction Alert", message);
                sent = smsSent || emailSent;
                break;
            default:
                LOGGER.log(Level.WARNING, "Invalid notification channel: {0}", preference.getChannel());
        }
        
        // Log notification
        if (sent) {
            logNotification(accountNo, NOTIFICATION_TRANSACTION, message, preference.getChannel());
        }
        
        return sent;
    }
    
    /**
     * Send a notification for failed login attempts
     * 
     * @param accountNo Account number
     * @param attemptDetails Details about the login attempt
     * @return true if notification was sent
     */
    public boolean sendLoginAttemptNotification(String accountNo, String attemptDetails) {
        // Get user's notification preferences
        NotificationPreference preference = getUserNotificationPreference(accountNo, NOTIFICATION_LOGIN_ATTEMPT);
        if (preference == null || CHANNEL_NONE.equals(preference.getChannel())) {
            return false; // No notification needed
        }
        
        // Get user contact info
        UserContactInfo contactInfo = getUserContactInfo(accountNo);
        if (contactInfo == null) {
            LOGGER.log(Level.WARNING, "Contact information not found for account: {0}", accountNo);
            return false;
        }
        
        // Prepare notification message
        String message = String.format(
            "SECURITY ALERT: Failed login attempt detected for your account ending with %s. %s",
            maskAccountNumber(accountNo), attemptDetails
        );
        
        // Send notification based on preferred channel
        boolean sent = false;
        switch (preference.getChannel()) {
            case CHANNEL_SMS:
                sent = sendSMS(contactInfo.getPhoneNumber(), message);
                break;
            case CHANNEL_EMAIL:
                sent = sendEmail(contactInfo.getEmail(), "Security Alert - Failed Login", message);
                break;
            case CHANNEL_BOTH:
                boolean smsSent = sendSMS(contactInfo.getPhoneNumber(), message);
                boolean emailSent = sendEmail(contactInfo.getEmail(), "Security Alert - Failed Login", message);
                sent = smsSent || emailSent;
                break;
            default:
                LOGGER.log(Level.WARNING, "Invalid notification channel: {0}", preference.getChannel());
        }
        
        // Log notification
        if (sent) {
            logNotification(accountNo, NOTIFICATION_LOGIN_ATTEMPT, message, preference.getChannel());
        }
        
        return sent;
    }
    
    /**
     * Send a low balance notification if account balance falls below threshold
     * 
     * @param accountNo Account number
     * @param balance Current account balance
     * @return true if notification was sent
     */
    public boolean sendLowBalanceNotification(String accountNo, double balance) {
        // Get user's notification preferences
        NotificationPreference preference = getUserNotificationPreference(accountNo, NOTIFICATION_LOW_BALANCE);
        if (preference == null || CHANNEL_NONE.equals(preference.getChannel())) {
            return false; // No notification needed
        }
        
        // Check if balance is below the threshold
        double threshold = preference.getThreshold() > 0 ? 
                          preference.getThreshold() : 
                          DEFAULT_LOW_BALANCE_THRESHOLD;
        
        if (balance >= threshold) {
            return false; // Above threshold, no notification needed
        }
        
        // Get user contact info
        UserContactInfo contactInfo = getUserContactInfo(accountNo);
        if (contactInfo == null) {
            LOGGER.log(Level.WARNING, "Contact information not found for account: {0}", accountNo);
            return false;
        }
        
        // Prepare notification message
        String message = String.format(
            "ALERT: Your account ending with %s balance is low. Current balance: Rs. %.2f",
            maskAccountNumber(accountNo), balance
        );
        
        // Send notification based on preferred channel
        boolean sent = false;
        switch (preference.getChannel()) {
            case CHANNEL_SMS:
                sent = sendSMS(contactInfo.getPhoneNumber(), message);
                break;
            case CHANNEL_EMAIL:
                sent = sendEmail(contactInfo.getEmail(), "Low Balance Alert", message);
                break;
            case CHANNEL_BOTH:
                boolean smsSent = sendSMS(contactInfo.getPhoneNumber(), message);
                boolean emailSent = sendEmail(contactInfo.getEmail(), "Low Balance Alert", message);
                sent = smsSent || emailSent;
                break;
            default:
                LOGGER.log(Level.WARNING, "Invalid notification channel: {0}", preference.getChannel());
        }
        
        // Log notification
        if (sent) {
            logNotification(accountNo, NOTIFICATION_LOW_BALANCE, message, preference.getChannel());
        }
        
        return sent;
    }
    
    /**
     * Update notification preferences for a user
     * 
     * @param accountNo Account number
     * @param notificationType Notification type
     * @param channel Notification channel
     * @param threshold Notification threshold (for transaction and low balance)
     * @return true if successful
     */
    public boolean updateNotificationPreference(String accountNo, String notificationType, 
                                              String channel, double threshold) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Check if preference already exists
            String checkQuery = "SELECT * FROM notification_preferences WHERE account_no = ? AND notification_type = ?";
            ps = connection.prepareStatement(checkQuery);
            ps.setString(1, accountNo);
            ps.setString(2, notificationType);
            rs = ps.executeQuery();
            
            boolean exists = rs.next();
            ps.close();
            rs.close();
            
            // Update or insert preference
            String query;
            if (exists) {
                query = "UPDATE notification_preferences SET channel = ?, threshold = ? WHERE account_no = ? AND notification_type = ?";
                ps = connection.prepareStatement(query);
                ps.setString(1, channel);
                ps.setDouble(2, threshold);
                ps.setString(3, accountNo);
                ps.setString(4, notificationType);
            } else {
                query = "INSERT INTO notification_preferences (account_no, notification_type, channel, threshold) VALUES (?, ?, ?, ?)";
                ps = connection.prepareStatement(query);
                ps.setString(1, accountNo);
                ps.setString(2, notificationType);
                ps.setString(3, channel);
                ps.setDouble(4, threshold);
            }
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating notification preference: {0}", e.getMessage());
            
            // If table doesn't exist, create it
            if (e.getMessage().contains("doesn't exist")) {
                createNotificationTables();
                // Retry the operation
                return updateNotificationPreference(accountNo, notificationType, channel, threshold);
            }
            
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Get notification preferences for a user
     * 
     * @param accountNo Account number
     * @return List of notification preferences
     */
    public List<NotificationPreference> getAllNotificationPreferences(String accountNo) {
        List<NotificationPreference> preferences = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String query = "SELECT * FROM notification_preferences WHERE account_no = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                NotificationPreference preference = new NotificationPreference();
                preference.setAccountNo(rs.getString("account_no"));
                preference.setNotificationType(rs.getString("notification_type"));
                preference.setChannel(rs.getString("channel"));
                preference.setThreshold(rs.getDouble("threshold"));
                preferences.add(preference);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching notification preferences: {0}", e.getMessage());
            
            // If table doesn't exist, create it
            if (e.getMessage().contains("doesn't exist")) {
                createNotificationTables();
            }
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
        
        return preferences;
    }
    
    /**
     * Get a specific notification preference for a user
     * 
     * @param accountNo Account number
     * @param notificationType Notification type
     * @return NotificationPreference or null if not found
     */
    public NotificationPreference getUserNotificationPreference(String accountNo, String notificationType) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String query = "SELECT * FROM notification_preferences WHERE account_no = ? AND notification_type = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, notificationType);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                NotificationPreference preference = new NotificationPreference();
                preference.setAccountNo(rs.getString("account_no"));
                preference.setNotificationType(rs.getString("notification_type"));
                preference.setChannel(rs.getString("channel"));
                preference.setThreshold(rs.getDouble("threshold"));
                return preference;
            } else {
                // If not found, return default preference
                return createDefaultPreference(accountNo, notificationType);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching notification preference: {0}", e.getMessage());
            
            // If table doesn't exist, create it
            if (e.getMessage().contains("doesn't exist")) {
                createNotificationTables();
                // Return default preference
                return createDefaultPreference(accountNo, notificationType);
            }
            
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Create default notification preference
     * 
     * @param accountNo Account number
     * @param notificationType Notification type
     * @return Default NotificationPreference
     */
    private NotificationPreference createDefaultPreference(String accountNo, String notificationType) {
        NotificationPreference preference = new NotificationPreference();
        preference.setAccountNo(accountNo);
        preference.setNotificationType(notificationType);
        
        // Set default values based on notification type
        switch (notificationType) {
            case NOTIFICATION_TRANSACTION:
                preference.setChannel(CHANNEL_SMS);
                preference.setThreshold(DEFAULT_LARGE_TRANSACTION_THRESHOLD);
                break;
            case NOTIFICATION_LOGIN_ATTEMPT:
                preference.setChannel(CHANNEL_BOTH);
                preference.setThreshold(0); // Not applicable
                break;
            case NOTIFICATION_LOW_BALANCE:
                preference.setChannel(CHANNEL_SMS);
                preference.setThreshold(DEFAULT_LOW_BALANCE_THRESHOLD);
                break;
            case NOTIFICATION_SECURITY:
                preference.setChannel(CHANNEL_BOTH);
                preference.setThreshold(0); // Not applicable
                break;
            default:
                preference.setChannel(CHANNEL_NONE);
                preference.setThreshold(0);
        }
        
        // Try to save the default preference
        updateNotificationPreference(accountNo, notificationType, 
                                    preference.getChannel(), preference.getThreshold());
        
        return preference;
    }
    
    /**
     * Get user contact information (phone and email)
     * 
     * @param accountNo Account number
     * @return UserContactInfo or null if not found
     */
    private UserContactInfo getUserContactInfo(String accountNo) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Get user details from signup tables
            String query = "SELECT s1.formno, s1.email FROM signup1 s1 JOIN login l ON s1.formno = l.formno WHERE l.Account_No = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                String email = rs.getString("email");
                String formno = rs.getString("formno");
                
                // Get phone number from signup2
                rs.close();
                ps.close();
                
                query = "SELECT mno FROM signup2 WHERE formno = ?";
                ps = connection.prepareStatement(query);
                ps.setString(1, formno);
                rs = ps.executeQuery();
                
                if (rs.next()) {
                    String phone = rs.getString("mno");
                    
                    UserContactInfo contactInfo = new UserContactInfo();
                    contactInfo.setAccountNo(accountNo);
                    contactInfo.setEmail(email);
                    contactInfo.setPhoneNumber(phone);
                    
                    return contactInfo;
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user contact info: {0}", e.getMessage());
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Log notification in the database
     * 
     * @param accountNo Account number
     * @param notificationType Notification type
     * @param message Notification message
     * @param channel Notification channel
     */
    private void logNotification(String accountNo, String notificationType, String message, String channel) {
        PreparedStatement ps = null;
        
        try {
            String query = "INSERT INTO notification_log (account_no, notification_type, message, channel, notification_time) VALUES (?, ?, ?, ?, NOW())";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, notificationType);
            ps.setString(3, message);
            ps.setString(4, channel);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error logging notification: {0}", e.getMessage());
            
            // If table doesn't exist, create it
            if (e.getMessage().contains("doesn't exist")) {
                createNotificationTables();
                // Retry logging
                logNotification(accountNo, notificationType, message, channel);
            }
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Create notification tables if they don't exist
     */
    private void createNotificationTables() {
        Statement stmt = null;
        
        try {
            stmt = connection.createStatement();
            
            // Create notification_preferences table
            String createPreferencesTable = "CREATE TABLE IF NOT EXISTS notification_preferences (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "account_no VARCHAR(20) NOT NULL, " +
                "notification_type VARCHAR(50) NOT NULL, " +
                "channel VARCHAR(20) NOT NULL, " +
                "threshold DOUBLE, " +
                "UNIQUE KEY unique_preference (account_no, notification_type)" +
                ")";
            stmt.execute(createPreferencesTable);
            
            // Create notification_log table
            String createLogTable = "CREATE TABLE IF NOT EXISTS notification_log (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "account_no VARCHAR(20) NOT NULL, " +
                "notification_type VARCHAR(50) NOT NULL, " +
                "message TEXT NOT NULL, " +
                "channel VARCHAR(20) NOT NULL, " +
                "notification_time DATETIME NOT NULL, " +
                "delivery_status VARCHAR(20) DEFAULT 'SENT'" +
                ")";
            stmt.execute(createLogTable);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating notification tables: {0}", e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing statement: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Send SMS notification
     * In a real implementation, this would connect to an SMS gateway
     * 
     * @param phoneNumber Phone number to send to
     * @param message Message content
     * @return true if sent successfully
     */
    private boolean sendSMS(String phoneNumber, String message) {
        // In a real implementation, this would connect to an SMS gateway
        LOGGER.log(Level.INFO, "Sending SMS to {0}: {1}", new Object[]{phoneNumber, message});
        return true; // Simulate successful sending
    }
    
    /**
     * Send email notification
     * In a real implementation, this would use JavaMail or similar
     * 
     * @param email Email address to send to
     * @param subject Email subject
     * @param message Message content
     * @return true if sent successfully
     */
    private boolean sendEmail(String email, String subject, String message) {
        // In a real implementation, this would use JavaMail or similar
        LOGGER.log(Level.INFO, "Sending email to {0} with subject '{1}': {2}", 
                 new Object[]{email, subject, message});
        return true; // Simulate successful sending
    }
    
    /**
     * Mask account number for security (show only last 4 digits)
     * 
     * @param accountNo Account number to mask
     * @return Masked account number
     */
    private String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() <= 4) {
            return accountNo;
        }
        int length = accountNo.length();
        String lastFour = accountNo.substring(length - 4);
        return "XXXX" + lastFour;
    }
    
    /**
     * Close database resources
     */
    public void closeConnection() {
        connectionSql.closeConnection();
    }
    
    /**
     * NotificationPreference class to store preference data
     */
    public static class NotificationPreference {
        private String accountNo;
        private String notificationType;
        private String channel;
        private double threshold;
        
        // Getters and setters
        public String getAccountNo() { return accountNo; }
        public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
        
        public String getNotificationType() { return notificationType; }
        public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
        
        public String getChannel() { return channel; }
        public void setChannel(String channel) { this.channel = channel; }
        
        public double getThreshold() { return threshold; }
        public void setThreshold(double threshold) { this.threshold = threshold; }
    }
    
    /**
     * UserContactInfo class to store user contact data
     */
    public static class UserContactInfo {
        private String accountNo;
        private String phoneNumber;
        private String email;
        
        // Getters and setters
        public String getAccountNo() { return accountNo; }
        public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}

/* 
 * Test cases:
 * NOTIF-01: Verify transaction notifications based on threshold
 * NOTIF-02: Verify login attempt notifications
 * NOTIF-03: Verify low balance notifications
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */