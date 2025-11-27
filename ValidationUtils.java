package banking.management.system;

import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created ValidationUtils.java to implement:
 * 1. Input validation for security
 * 2. Data format validation
 * 3. Input sanitization to prevent injection attacks
 */
public class ValidationUtils {
    // Regular expressions for common validations
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s'-]+$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{10,16}$");
    
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(19|20)\\d\\d$");
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate name format (letters, spaces, hyphens, apostrophes)
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate phone number format (10 digits)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate account number format (10-16 digits)
     */
    public static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate amount format (positive number with up to 2 decimal places)
     */
    public static boolean isValidAmount(String amount) {
        if (amount == null || !AMOUNT_PATTERN.matcher(amount).matches()) {
            return false;
        }
        try {
            double value = Double.parseDouble(amount);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate date format (DD-MM-YYYY)
     */
    public static boolean isValidDateFormat(String date) {
        return date != null && DATE_PATTERN.matcher(date).matches();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate date is real and not in future
     */
    public static boolean isValidDate(String date) {
        if (!isValidDateFormat(date)) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            
            // Check if date is in the future
            return !parsedDate.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Sanitize input to prevent XSS and SQL injection
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove potentially dangerous characters
        String sanitized = input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;")
            .replace("\\", "&#x5C;")
            .replace(";", "&#59;");
        
        // Remove potential SQL injection patterns
        sanitized = sanitized
            .replaceAll("(?i)\\bDROP\\b", "")
            .replaceAll("(?i)\\bDELETE\\b", "")
            .replaceAll("(?i)\\bUPDATE\\b", "")
            .replaceAll("(?i)\\bINSERT\\b", "")
            .replaceAll("(?i)\\bALTER\\b", "")
            .replaceAll("(?i)\\bCREATE\\b", "")
            .replaceAll("(?i)\\bEXEC\\b", "")
            .replaceAll("--", "");
        
        return sanitized;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Check if input contains potential SQL injection attempts
     */
    public static boolean containsSqlInjection(String input) {
        if (input == null) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        return lowerInput.contains(" or ") ||
               lowerInput.contains("--") ||
               lowerInput.contains(";") ||
               lowerInput.contains("/*") ||
               lowerInput.contains("*/") ||
               lowerInput.contains("drop table") ||
               lowerInput.contains("drop database") ||
               lowerInput.contains("delete from") ||
               lowerInput.contains("information_schema") ||
               lowerInput.contains("union select") ||
               lowerInput.contains("exec(") ||
               lowerInput.matches(".*'\\s*or\\s*'.*");
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate password strength
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Check for digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        // Check for special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }
        
        return true;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get password strength score (0-5)
     */
    public static int getPasswordStrength(String password) {
        int score = 0;
        
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        // Length check
        if (password.length() >= 8) score++;
        if (password.length() >= 10) score++;
        
        // Character type checks
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
        
        // Cap at 5
        return Math.min(5, score);
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-VAL-001, TC-VAL-002, TC-SEC-009
 * Requirement IDs: US-1 (Input validation and security)
 * Agent Run: AGENT-20251127-01
 */