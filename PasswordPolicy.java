package banking.management.system;

/**
 * Password policy enforcement for Online Banking system security
 *
 * [AGENT GENERATED CODE - REQUIREMENT:US1-AC2]
 * This class implements strong password rules as per security requirements
 * and provides password validation functionality.
 */
public class PasswordPolicy {
    // Password strength requirements
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final boolean REQUIRE_UPPERCASE = true;
    private static final boolean REQUIRE_LOWERCASE = true;
    private static final boolean REQUIRE_DIGITS = true;
    private static final boolean REQUIRE_SPECIAL_CHARS = true;
    
    /**
     * Validates if a password meets all required strength criteria
     * 
     * @param password Password to validate
     * @return boolean True if password meets all criteria, false otherwise
     */
    public static boolean validatePassword(String password) {
        // Check password length
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        
        // Check for required character types
        boolean hasUppercase = !REQUIRE_UPPERCASE || password.matches(".*[A-Z].*");
        boolean hasLowercase = !REQUIRE_LOWERCASE || password.matches(".*[a-z].*");
        boolean hasDigit = !REQUIRE_DIGITS || password.matches(".*\\d.*");
        boolean hasSpecial = !REQUIRE_SPECIAL_CHARS || password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
    
    /**
     * Returns a formatted string containing all password requirements
     * 
     * @return String Description of password requirements
     */
    public static String getPasswordRules() {
        StringBuilder rules = new StringBuilder("Password must:");
        rules.append("\n- Be at least ").append(MIN_PASSWORD_LENGTH).append(" characters long");
        if (REQUIRE_UPPERCASE) rules.append("\n- Include at least one uppercase letter");
        if (REQUIRE_LOWERCASE) rules.append("\n- Include at least one lowercase letter");
        if (REQUIRE_DIGITS) rules.append("\n- Include at least one digit");
        if (REQUIRE_SPECIAL_CHARS) rules.append("\n- Include at least one special character (!@#$%^&*()_+-=[]{};':\"\\|,.<>/?)");
        
        return rules.toString();
    }
    
    /**
     * Provides detailed feedback on why a password fails to meet requirements
     * 
     * @param password Password to check
     * @return String Feedback on what requirements are not met, or null if all are met
     */
    public static String getPasswordFeedback(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        StringBuilder feedback = new StringBuilder();
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            feedback.append("- Password is too short (minimum ").append(MIN_PASSWORD_LENGTH).append(" characters)\n");
        }
        
        if (REQUIRE_UPPERCASE && !password.matches(".*[A-Z].*")) {
            feedback.append("- Missing uppercase letter\n");
        }
        
        if (REQUIRE_LOWERCASE && !password.matches(".*[a-z].*")) {
            feedback.append("- Missing lowercase letter\n");
        }
        
        if (REQUIRE_DIGITS && !password.matches(".*\\d.*")) {
            feedback.append("- Missing digit\n");
        }
        
        if (REQUIRE_SPECIAL_CHARS && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            feedback.append("- Missing special character\n");
        }
        
        return feedback.length() > 0 ? feedback.toString() : null;
    }
}

/*
 * File generated/modified to fulfill User Story 1: Account Login & Authentication
 * Test cases: Password policy enforcement
 * Agent run: VIBE-1001
 */