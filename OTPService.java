package banking.management.system;

import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.sql.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Service for OTP generation, delivery, and validation
 *
 * [AGENT GENERATED CODE - REQUIREMENT:US1-AC3]
 * This service implements two-factor authentication using OTP via SMS/email
 * as specified in the authentication requirements.
 */
public class OTPService {
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static Map<String, OTPData> otpStorage = new HashMap<>();
    
    // OTP data storage class
    private static class OTPData {
        String otp;
        LocalDateTime expiryTime;
        
        OTPData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
    
    /**
     * Generates a new OTP for the specified account
     * 
     * @param accountNo Account number to generate OTP for
     * @return String Generated OTP
     */
    public static String generateOTP(String accountNo) {
        // Generate random numeric OTP of specified length
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        String otpValue = otp.toString();
        
        // Store OTP with expiry time
        LocalDateTime expiryTime = LocalDateTime.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES);
        otpStorage.put(accountNo, new OTPData(otpValue, expiryTime));
        
        return otpValue;
    }
    
    /**
     * Validates an OTP for a given account
     * 
     * @param accountNo Account number for validation
     * @param otpToValidate OTP entered by user
     * @return boolean True if OTP is valid and not expired
     */
    public static boolean validateOTP(String accountNo, String otpToValidate) {
        // Check if OTP exists for account
        if (!otpStorage.containsKey(accountNo)) {
            return false;
        }
        
        OTPData otpData = otpStorage.get(accountNo);
        
        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(otpData.expiryTime)) {
            otpStorage.remove(accountNo); // Remove expired OTP
            return false;
        }
        
        // Validate OTP
        boolean isValid = otpData.otp.equals(otpToValidate);
        
        // Remove OTP after successful validation
        if (isValid) {
            otpStorage.remove(accountNo);
        }
        
        return isValid;
    }
    
    /**
     * Sends OTP via SMS (simulation)
     * 
     * @param phoneNumber Phone number to send OTP to
     * @param otp One-time password to send
     * @return boolean True if sending was successful
     */
    public static boolean sendSMS(String phoneNumber, String otp) {
        // In a production environment, this would integrate with an SMS gateway
        // This is a simulation for development purposes
        System.out.println("SMS OTP sent to " + phoneNumber + ": " + otp);
        return true;
    }
    
    /**
     * Sends OTP via email
     * 
     * @param email Email address to send OTP to
     * @param otp One-time password to send
     * @return boolean True if sending was successful
     */
    public static boolean sendEmail(String email, String otp) {
        try {
            // Email configuration - would be externalized in production
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.example.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            // In production, use proper authentication from secure configuration
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("username", "password");
                }
            });
            
            // Create and send email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("banking@example.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your Online Banking OTP");
            message.setText("Your One-Time Password is: " + otp + "\n\nThis OTP will expire in " + 
                           OTP_EXPIRY_MINUTES + " minutes. Do not share this OTP with anyone.");
            
            // Transport.send(message); // Uncomment in production
            
            // Simulation for development
            System.out.println("Email OTP sent to " + email + ": " + otp);
            
            return true;
        } catch (Exception e) {
            System.out.println("Error sending email: " + e);
            return false;
        }
    }
    
    /**
     * Gets contact information for OTP delivery from database
     * 
     * @param accountNo Account number to lookup
     * @param contactType Type of contact ("email" or "phone")
     * @return String Contact information or null if not found
     */
    public static String getUserContact(String accountNo, String contactType) {
        ConnectionSql c = new ConnectionSql();
        String contact = null;
        
        try {
            // Use prepared statements for security
            String query = "SELECT email, phone_number FROM user_contacts WHERE Account_No = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, accountNo);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                contact = "email".equals(contactType) ? rs.getString("email") : rs.getString("phone_number");
            }
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error retrieving user contact: " + e);
        } finally {
            c.closeConnection();
        }
        
        return contact;
    }
    
    /**
     * Delivers OTP based on selected method
     * 
     * @param accountNo Account number
     * @param method Delivery method ("sms" or "email")
     * @return String Generated OTP or null if delivery failed
     */
    public static String deliverOTP(String accountNo, String method) {
        String otp = generateOTP(accountNo);
        boolean delivered = false;
        
        if ("sms".equalsIgnoreCase(method)) {
            String phoneNumber = getUserContact(accountNo, "phone");
            if (phoneNumber != null) {
                delivered = sendSMS(phoneNumber, otp);
            }
        } else if ("email".equalsIgnoreCase(method)) {
            String email = getUserContact(accountNo, "email");
            if (email != null) {
                delivered = sendEmail(email, otp);
            }
        }
        
        return delivered ? otp : null;
    }
}

/*
 * File generated/modified to fulfill User Story 1: Account Login & Authentication
 * Test cases: Two-factor authentication via OTP
 * Agent run: VIBE-1001
 */