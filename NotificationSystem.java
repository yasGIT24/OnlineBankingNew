package banking.management.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/* [AGENT GENERATED CODE - REQUIREMENT:US-5]
 * Created NotificationSystem.java to implement:
 * 1. Security alert notifications
 * 2. Transaction notifications
 * 3. Balance threshold alerts
 * 4. User notification preferences
 */
public class NotificationSystem {
    // Singleton instance
    private static NotificationSystem instance;
    
    private ExecutorService notificationExecutor;
    private boolean initialized = false;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Private constructor for singleton pattern
     */
    private NotificationSystem() {
        notificationExecutor = Executors.newFixedThreadPool(3);
        try {
            AuditLogger.logActivity("SYSTEM", "Notification system initialized", "System");
            initialized = true;
        } catch (Exception e) {
            System.err.println("Error initializing notification system: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Get singleton instance
     */
    public static synchronized NotificationSystem getInstance() {
        if (instance == null) {
            instance = new NotificationSystem();
        }
        return instance;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Send security alert notification
     */
    public void sendSecurityAlert(String accountNo, String message, String alertType) {
        notificationExecutor.submit(() -> {
            try {
                if (!initialized) {
                    return;
                }
                
                // Get user notification preferences
                UserPreferences preferences = getUserPreferences(accountNo);
                
                // Check if this alert type is enabled
                if (!preferences.isSecurityAlertsEnabled()) {
                    return;
                }
                
                // Send alert through preferred channels
                if (preferences.isEmailAlertsEnabled()) {
                    sendEmailAlert(preferences.getEmail(), "Security Alert: " + alertType, message);
                }
                
                if (preferences.isSmsAlertsEnabled()) {
                    sendSmsAlert(preferences.getPhone(), "Security Alert: " + message);
                }
                
                if (preferences.isInAppAlertsEnabled()) {
                    sendInAppAlert(accountNo, "Security Alert", message, "security");
                }
                
                // Log the alert
                AuditLogger.logActivity(accountNo, 
                    "Security alert sent: " + alertType + " - " + message, "Notification");
                
            } catch (Exception e) {
                ErrorHandler.handleException(e, "Error sending security alert");
            }
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Send transaction notification
     */
    public void sendTransactionAlert(String accountNo, String transactionType, double amount, String description) {
        notificationExecutor.submit(() -> {
            try {
                if (!initialized) {
                    return;
                }
                
                // Get user notification preferences
                UserPreferences preferences = getUserPreferences(accountNo);
                
                // Check if transaction alerts are enabled and if amount exceeds threshold
                if (!preferences.isTransactionAlertsEnabled() || 
                    amount < preferences.getTransactionAlertThreshold()) {
                    return;
                }
                
                String message = String.format("%s transaction of Rs. %.2f - %s", 
                    transactionType, amount, description);
                
                // Send through preferred channels
                if (preferences.isEmailAlertsEnabled()) {
                    sendEmailAlert(preferences.getEmail(), 
                        "Transaction Alert: " + transactionType, message);
                }
                
                if (preferences.isSmsAlertsEnabled()) {
                    sendSmsAlert(preferences.getPhone(), "Transaction Alert: " + message);
                }
                
                if (preferences.isInAppAlertsEnabled()) {
                    sendInAppAlert(accountNo, "Transaction Alert", message, "transaction");
                }
                
                // Log the alert
                AuditLogger.logActivity(accountNo, 
                    "Transaction alert sent: " + transactionType + " of Rs. " + amount, "Notification");
                
            } catch (Exception e) {
                ErrorHandler.handleException(e, "Error sending transaction alert");
            }
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Send balance threshold alert
     */
    public void sendBalanceAlert(String accountNo, double balance) {
        notificationExecutor.submit(() -> {
            try {
                if (!initialized) {
                    return;
                }
                
                // Get user notification preferences
                UserPreferences preferences = getUserPreferences(accountNo);
                
                // Check if balance alerts are enabled and if balance is below threshold
                if (!preferences.isBalanceAlertsEnabled() || 
                    balance > preferences.getBalanceAlertThreshold()) {
                    return;
                }
                
                String message = String.format("Your account balance (Rs. %.2f) is below your threshold (Rs. %.2f)", 
                    balance, preferences.getBalanceAlertThreshold());
                
                // Send through preferred channels
                if (preferences.isEmailAlertsEnabled()) {
                    sendEmailAlert(preferences.getEmail(), "Low Balance Alert", message);
                }
                
                if (preferences.isSmsAlertsEnabled()) {
                    sendSmsAlert(preferences.getPhone(), "Low Balance Alert: " + message);
                }
                
                if (preferences.isInAppAlertsEnabled()) {
                    sendInAppAlert(accountNo, "Low Balance Alert", message, "balance");
                }
                
                // Log the alert
                AuditLogger.logActivity(accountNo, 
                    "Low balance alert sent: Rs. " + balance, "Notification");
                
            } catch (Exception e) {
                ErrorHandler.handleException(e, "Error sending balance alert");
            }
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Send custom notification
     */
    public void sendCustomNotification(String accountNo, String title, String message) {
        notificationExecutor.submit(() -> {
            try {
                if (!initialized) {
                    return;
                }
                
                // Get user notification preferences
                UserPreferences preferences = getUserPreferences(accountNo);
                
                // Send through preferred channels
                if (preferences.isEmailAlertsEnabled()) {
                    sendEmailAlert(preferences.getEmail(), title, message);
                }
                
                if (preferences.isSmsAlertsEnabled()) {
                    sendSmsAlert(preferences.getPhone(), title + ": " + message);
                }
                
                if (preferences.isInAppAlertsEnabled()) {
                    sendInAppAlert(accountNo, title, message, "custom");
                }
                
                // Log the notification
                AuditLogger.logActivity(accountNo, 
                    "Custom notification sent: " + title, "Notification");
                
            } catch (Exception e) {
                ErrorHandler.handleException(e, "Error sending custom notification");
            }
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Get user notification preferences
     */
    private UserPreferences getUserPreferences(String accountNo) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "SELECT * FROM notification_preferences WHERE Account_No = ?");
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                UserPreferences preferences = new UserPreferences();
                preferences.setAccountNo(accountNo);
                preferences.setEmail(rs.getString("email"));
                preferences.setPhone(rs.getString("phone"));
                preferences.setEmailAlertsEnabled(rs.getBoolean("email_alerts_enabled"));
                preferences.setSmsAlertsEnabled(rs.getBoolean("sms_alerts_enabled"));
                preferences.setInAppAlertsEnabled(rs.getBoolean("in_app_alerts_enabled"));
                preferences.setSecurityAlertsEnabled(rs.getBoolean("security_alerts_enabled"));
                preferences.setTransactionAlertsEnabled(rs.getBoolean("transaction_alerts_enabled"));
                preferences.setBalanceAlertsEnabled(rs.getBoolean("balance_alerts_enabled"));
                preferences.setTransactionAlertThreshold(rs.getDouble("transaction_alert_threshold"));
                preferences.setBalanceAlertThreshold(rs.getDouble("balance_alert_threshold"));
                return preferences;
            } else {
                // Return default preferences
                return getDefaultPreferences(accountNo);
            }
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error fetching user preferences");
            // Return default preferences
            return getDefaultPreferences(accountNo);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Get default notification preferences
     */
    private UserPreferences getDefaultPreferences(String accountNo) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            // Try to get user contact information
            PreparedStatement ps = c.getConnection().prepareStatement(
                "SELECT email, phone FROM signup3 WHERE Account_No = ?");
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            UserPreferences preferences = new UserPreferences();
            preferences.setAccountNo(accountNo);
            
            if (rs.next()) {
                preferences.setEmail(rs.getString("email"));
                preferences.setPhone(rs.getString("phone"));
            }
            
            // Set default values
            preferences.setEmailAlertsEnabled(true);
            preferences.setSmsAlertsEnabled(true);
            preferences.setInAppAlertsEnabled(true);
            preferences.setSecurityAlertsEnabled(true);
            preferences.setTransactionAlertsEnabled(true);
            preferences.setBalanceAlertsEnabled(true);
            preferences.setTransactionAlertThreshold(10000); // Rs. 10,000
            preferences.setBalanceAlertThreshold(1000);      // Rs. 1,000
            
            return preferences;
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error fetching default preferences");
            
            // Create basic default preferences
            UserPreferences preferences = new UserPreferences();
            preferences.setAccountNo(accountNo);
            preferences.setInAppAlertsEnabled(true);
            preferences.setSecurityAlertsEnabled(true);
            return preferences;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Send email alert
     */
    private void sendEmailAlert(String email, String subject, String message) {
        try {
            if (email == null || email.isEmpty()) {
                return;
            }
            
            /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
             * In a production environment, this would use JavaMail API
             * For this implementation, we'll simulate successful delivery
             */
            
            // Log email sending (without sensitive content)
            AuditLogger.logActivity("SYSTEM", 
                "Alert email sent to: " + email + " - Subject: " + subject, "Notification");
            
            // Simulated email service - in production, replace with actual email sending logic
            System.out.println("SIMULATION - Email sent to " + email + 
                "\nSubject: " + subject + "\nMessage: " + message);
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error sending email alert");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Send SMS alert
     */
    private void sendSmsAlert(String phone, String message) {
        try {
            if (phone == null || phone.isEmpty()) {
                return;
            }
            
            /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
             * In a production environment, this would use SMS gateway API
             * For this implementation, we'll simulate successful delivery
             */
            
            // Log SMS sending (without sensitive content)
            AuditLogger.logActivity("SYSTEM", 
                "Alert SMS sent to: " + phone, "Notification");
            
            // Simulated SMS service - in production, replace with actual SMS sending logic
            System.out.println("SIMULATION - SMS sent to " + phone + "\nMessage: " + message);
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error sending SMS alert");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Send in-app alert
     */
    private void sendInAppAlert(String accountNo, String title, String message, String type) {
        try {
            // Save alert to database for persistence
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "INSERT INTO notifications (Account_No, title, message, type, timestamp, is_read) " +
                "VALUES (?, ?, ?, ?, NOW(), FALSE)");
            ps.setString(1, accountNo);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.setString(4, type);
            ps.executeUpdate();
            
            // Display alert if user is currently logged in
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    message,
                    title,
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error sending in-app alert");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Get unread notifications for user
     */
    public List<Map<String, Object>> getUnreadNotifications(String accountNo) {
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        try {
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "SELECT id, title, message, type, timestamp FROM notifications " +
                "WHERE Account_No = ? AND is_read = FALSE ORDER BY timestamp DESC");
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("id", rs.getInt("id"));
                notification.put("title", rs.getString("title"));
                notification.put("message", rs.getString("message"));
                notification.put("type", rs.getString("type"));
                notification.put("timestamp", rs.getTimestamp("timestamp").toLocalDateTime());
                notifications.add(notification);
            }
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error fetching notifications");
        }
        
        return notifications;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Mark notification as read
     */
    public void markNotificationAsRead(int notificationId) {
        try {
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "UPDATE notifications SET is_read = TRUE WHERE id = ?");
            ps.setInt(1, notificationId);
            ps.executeUpdate();
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error marking notification as read");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Clean up old notifications
     */
    public void cleanupOldNotifications() {
        try {
            ConnectionSql c = new ConnectionSql();
            
            PreparedStatement ps = c.getConnection().prepareStatement(
                "DELETE FROM notifications WHERE timestamp < ?");
            ps.setObject(1, LocalDateTime.now().minusMonths(3)); // Delete older than 3 months
            int deleted = ps.executeUpdate();
            
            AuditLogger.logActivity("SYSTEM", 
                "Cleaned up " + deleted + " old notifications", "Maintenance");
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error cleaning up old notifications");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Shutdown notification system
     */
    public void shutdown() {
        try {
            notificationExecutor.shutdown();
            AuditLogger.logActivity("SYSTEM", "Notification system shutdown", "System");
        } catch (Exception e) {
            System.err.println("Error shutting down notification system: " + e.getMessage());
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-5]
     * Inner class for user notification preferences
     */
    public static class UserPreferences {
        private String accountNo;
        private String email;
        private String phone;
        private boolean emailAlertsEnabled;
        private boolean smsAlertsEnabled;
        private boolean inAppAlertsEnabled;
        private boolean securityAlertsEnabled;
        private boolean transactionAlertsEnabled;
        private boolean balanceAlertsEnabled;
        private double transactionAlertThreshold;
        private double balanceAlertThreshold;
        
        // Getters and setters
        public String getAccountNo() { return accountNo; }
        public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public boolean isEmailAlertsEnabled() { return emailAlertsEnabled; }
        public void setEmailAlertsEnabled(boolean enabled) { this.emailAlertsEnabled = enabled; }
        
        public boolean isSmsAlertsEnabled() { return smsAlertsEnabled; }
        public void setSmsAlertsEnabled(boolean enabled) { this.smsAlertsEnabled = enabled; }
        
        public boolean isInAppAlertsEnabled() { return inAppAlertsEnabled; }
        public void setInAppAlertsEnabled(boolean enabled) { this.inAppAlertsEnabled = enabled; }
        
        public boolean isSecurityAlertsEnabled() { return securityAlertsEnabled; }
        public void setSecurityAlertsEnabled(boolean enabled) { this.securityAlertsEnabled = enabled; }
        
        public boolean isTransactionAlertsEnabled() { return transactionAlertsEnabled; }
        public void setTransactionAlertsEnabled(boolean enabled) { this.transactionAlertsEnabled = enabled; }
        
        public boolean isBalanceAlertsEnabled() { return balanceAlertsEnabled; }
        public void setBalanceAlertsEnabled(boolean enabled) { this.balanceAlertsEnabled = enabled; }
        
        public double getTransactionAlertThreshold() { return transactionAlertThreshold; }
        public void setTransactionAlertThreshold(double threshold) { this.transactionAlertThreshold = threshold; }
        
        public double getBalanceAlertThreshold() { return balanceAlertThreshold; }
        public void setBalanceAlertThreshold(double threshold) { this.balanceAlertThreshold = threshold; }
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-NOTIF-001, TC-NOTIF-002, TC-SEC-013
 * Requirement IDs: US-5 (Alerts & Notifications)
 * Agent Run: AGENT-20251127-01
 */