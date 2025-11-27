package banking.management.system;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created TwoFactorAuth.java to implement:
 * 1. OTP generation, delivery, and verification
 * 2. SMS and email delivery methods
 * 3. OTP expiration and validation
 */
public class TwoFactorAuth {
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_VERIFICATION_ATTEMPTS = 3;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Store active OTPs with expiration times and verification attempts
     * Format: {"accountId": {"otp": "123456", "expiry": timestamp, "attempts": 0}}
     */
    private static final ConcurrentHashMap<String, Map<String, Object>> activeOTPs = new ConcurrentHashMap<>();
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Generate a random numeric OTP
     */
    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Send OTP via specified contact method
     * Returns the generated OTP if successful, null otherwise
     */
    public String sendOTP(String accountNo, String contactMethod) {
        try {
            // Generate a new OTP
            String otp = generateOTP();
            
            // Parse contact method (format: "type:value")
            String[] parts = contactMethod.split(":", 2);
            if (parts.length != 2) {
                ErrorHandler.handleException(new IllegalArgumentException("Invalid contact method format"), 
                    "Invalid 2FA contact method");
                return null;
            }
            
            String deliveryMethod = parts[0].toLowerCase();
            String deliveryTarget = parts[1];
            
            // Send OTP via appropriate method
            boolean sent = false;
            if ("email".equals(deliveryMethod)) {
                sent = sendOTPViaEmail(deliveryTarget, otp);
            } else if ("phone".equals(deliveryMethod)) {
                sent = sendOTPViaSMS(deliveryTarget, otp);
            } else {
                ErrorHandler.handleException(new IllegalArgumentException("Unsupported delivery method: " + deliveryMethod), 
                    "Invalid 2FA delivery method");
                return null;
            }
            
            if (!sent) {
                return null;
            }
            
            // Store OTP with expiry time
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
            
            Map<String, Object> otpData = new HashMap<>();
            otpData.put("otp", otp);
            otpData.put("expiry", expiryTime);
            otpData.put("attempts", 0);
            
            activeOTPs.put(accountNo, otpData);
            
            // Log OTP generation (do not log the actual OTP)
            AuditLogger.logActivity(accountNo, "2FA OTP generated and sent via " + deliveryMethod, "Security");
            
            return otp;
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error sending OTP");
            return null;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Send OTP via email
     */
    private boolean sendOTPViaEmail(String email, String otp) {
        try {
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * In a production environment, this would use JavaMail API
             * For this implementation, we'll simulate successful delivery
             */
            
            // Log email sending (without OTP)
            AuditLogger.logActivity("SYSTEM", "2FA code sent to email: " + email, "Security");
            
            // Simulated email service - in production, replace with actual email sending logic
            System.out.println("SIMULATION - Email OTP '" + otp + "' sent to " + email);
            
            return true;
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error sending OTP via email");
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Send OTP via SMS
     */
    private boolean sendOTPViaSMS(String phoneNumber, String otp) {
        try {
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * In a production environment, this would use SMS gateway API
             * For this implementation, we'll simulate successful delivery
             */
            
            // Log SMS sending (without OTP)
            AuditLogger.logActivity("SYSTEM", "2FA code sent to phone: " + phoneNumber, "Security");
            
            // Simulated SMS service - in production, replace with actual SMS sending logic
            System.out.println("SIMULATION - SMS OTP '" + otp + "' sent to " + phoneNumber);
            
            return true;
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error sending OTP via SMS");
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Verify OTP entered by user
     */
    public boolean verifyOTP(String accountNo, String userEnteredOTP) {
        try {
            // Check if there's an active OTP for this account
            Map<String, Object> otpData = activeOTPs.get(accountNo);
            if (otpData == null) {
                AuditLogger.logActivity(accountNo, "2FA verification failed - no active OTP", "Security");
                return false;
            }
            
            // Get stored OTP data
            String storedOTP = (String) otpData.get("otp");
            LocalDateTime expiryTime = (LocalDateTime) otpData.get("expiry");
            int attempts = (int) otpData.get("attempts");
            
            // Increment attempt counter
            attempts++;
            otpData.put("attempts", attempts);
            
            // Check if OTP has expired
            if (LocalDateTime.now().isAfter(expiryTime)) {
                activeOTPs.remove(accountNo);
                AuditLogger.logActivity(accountNo, "2FA verification failed - OTP expired", "Security");
                return false;
            }
            
            // Check if max attempts exceeded
            if (attempts > MAX_VERIFICATION_ATTEMPTS) {
                activeOTPs.remove(accountNo);
                AuditLogger.logActivity(accountNo, 
                    "2FA verification failed - max attempts exceeded", "Security");
                return false;
            }
            
            // Check if OTP matches
            boolean isValid = storedOTP != null && storedOTP.equals(userEnteredOTP);
            
            // If valid, remove the OTP from active list
            if (isValid) {
                activeOTPs.remove(accountNo);
                AuditLogger.logActivity(accountNo, "2FA verification successful", "Security");
            } else {
                AuditLogger.logActivity(accountNo, 
                    "2FA verification failed - invalid OTP (attempt " + attempts + ")", "Security");
            }
            
            return isValid;
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error verifying OTP");
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get remaining time (in seconds) before OTP expires
     */
    public int getOTPRemainingTime(String accountNo) {
        Map<String, Object> otpData = activeOTPs.get(accountNo);
        if (otpData == null) {
            return 0;
        }
        
        LocalDateTime expiryTime = (LocalDateTime) otpData.get("expiry");
        if (expiryTime == null) {
            return 0;
        }
        
        long remainingSeconds = LocalDateTime.now().until(expiryTime, ChronoUnit.SECONDS);
        return remainingSeconds > 0 ? (int) remainingSeconds : 0;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Resend OTP using the same contact method
     */
    public String resendOTP(String accountNo) {
        try {
            // Get user's contact method from database
            String contactMethod = getUserContactMethod(accountNo);
            if (contactMethod == null) {
                return null;
            }
            
            // Clear any existing OTP
            activeOTPs.remove(accountNo);
            
            // Send new OTP
            return sendOTP(accountNo, contactMethod);
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Error resending OTP");
            return null;
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
     * Clean up expired OTPs (can be called periodically)
     */
    public static void cleanupExpiredOTPs() {
        LocalDateTime now = LocalDateTime.now();
        
        activeOTPs.forEach((accountNo, otpData) -> {
            LocalDateTime expiryTime = (LocalDateTime) otpData.get("expiry");
            if (expiryTime != null && now.isAfter(expiryTime)) {
                activeOTPs.remove(accountNo);
            }
        });
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-2FA-001, TC-2FA-002, TC-SEC-012
 * Requirement IDs: US-1 (Two-factor authentication)
 * Agent Run: AGENT-20251127-01
 */