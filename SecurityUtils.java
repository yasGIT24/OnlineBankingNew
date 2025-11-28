package banking.management.system;

import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Security utilities for encryption, hashing, and secure token generation
 * for the Online Banking System
 */
public class SecurityUtils {

    /* [AGENT GENERATED CODE - REQUIREMENT:SEC-006]
     * Encryption utilities implementation with:
     * - Strong password hashing with salt
     * - AES encryption for sensitive data
     * - Secure random token generation
     * - Download link encryption and validation
     */
    
    // Constants for security parameters
    private static final int SALT_LENGTH = 16;
    private static final int PBKDF2_ITERATIONS = 10000;
    private static final int SECURE_TOKEN_BYTES = 32;
    
    // Secret key for application-level encryption (would be loaded from secure storage in production)
    private static final String MASTER_KEY_BASE64 = "dGhpc2lzYXNlY3JldGtleWZvcmRlbW9wdXJwb3Nlc29ubHk=";
    private static final int LINK_EXPIRATION_MINUTES = 15;
    
    /**
     * Hash a password with PBKDF2 and a random salt
     * 
     * @param password The password to hash
     * @return Base64-encoded string containing salt and hash
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password with PBKDF2
            KeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    PBKDF2_ITERATIONS,
                    256); // 256-bit hash
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            // Combine salt and hash and encode with Base64
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.log(AuditLogger.SECURITY, "PasswordHash", 
                    "Failed to hash password: " + e.getMessage(), AuditLogger.ERROR);
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against a stored hash
     * 
     * @param password The password to verify
     * @param storedHash The stored hash to verify against
     * @return True if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Decode the stored hash
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Extract salt and hash
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combined, SALT_LENGTH, hash, 0, hash.length);
            
            // Hash the input password with the same salt
            KeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    PBKDF2_ITERATIONS,
                    256); // 256-bit hash
            
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = factory.generateSecret(spec).getEncoded();
            
            // Compare the hashes
            return MessageDigest.isEqual(hash, testHash);
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.log(AuditLogger.SECURITY, "PasswordVerify", 
                    "Failed to verify password: " + e.getMessage(), AuditLogger.ERROR);
            return false;
        }
    }
    
    /**
     * Encrypt data using AES
     * 
     * @param data The data to encrypt
     * @return Base64-encoded encrypted data with IV
     */
    public static String encrypt(String data) {
        try {
            // Get the master key
            byte[] keyBytes = Base64.getDecoder().decode(MASTER_KEY_BASE64);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            
            // Initialize cipher with random IV
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            
            // Encrypt the data
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.log(AuditLogger.SECURITY, "Encryption", 
                    "Failed to encrypt data: " + e.getMessage(), AuditLogger.ERROR);
            throw new RuntimeException("Error encrypting data", e);
        }
    }
    
    /**
     * Decrypt AES-encrypted data
     * 
     * @param encryptedData Base64-encoded encrypted data with IV
     * @return Decrypted data
     */
    public static String decrypt(String encryptedData) {
        try {
            // Get the master key
            byte[] keyBytes = Base64.getDecoder().decode(MASTER_KEY_BASE64);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            
            // Decode the combined data
            byte[] combined = Base64.getDecoder().decode(encryptedData);
            
            // Extract IV and encrypted data
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, encrypted, 0, encrypted.length);
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            // Decrypt the data
            byte[] decrypted = cipher.doFinal(encrypted);
            
            return new String(decrypted, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.log(AuditLogger.SECURITY, "Decryption", 
                    "Failed to decrypt data: " + e.getMessage(), AuditLogger.ERROR);
            throw new RuntimeException("Error decrypting data", e);
        }
    }
    
    /**
     * Generate a secure random token
     * 
     * @return Base64-encoded secure random token
     */
    public static String generateSecureToken() {
        try {
            byte[] tokenBytes = new byte[SECURE_TOKEN_BYTES];
            new SecureRandom().nextBytes(tokenBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        } catch (Exception e) {
            // Log security error
            AuditLogger.log(AuditLogger.SECURITY, "TokenGeneration", 
                    "Failed to generate secure token: " + e.getMessage(), AuditLogger.ERROR);
            throw new RuntimeException("Error generating secure token", e);
        }
    }
    
    /**
     * Generate a secure download link for a PDF statement
     * 
     * @param accountNo Account number
     * @param fileName Name of the file to download
     * @param filePath Path to the file
     * @return Encrypted download link
     */
    public static String generateSecureDownloadLink(String accountNo, String fileName, String filePath) {
        try {
            // Create a download token with expiration time
            String token = UUID.randomUUID().toString();
            Instant expiration = Instant.now().plus(LINK_EXPIRATION_MINUTES, ChronoUnit.MINUTES);
            
            // Create download information
            String downloadInfo = String.format("%s|%s|%s|%s|%d",
                    token,
                    accountNo,
                    fileName,
                    filePath,
                    expiration.getEpochSecond());
            
            // Encrypt the download information
            String encryptedInfo = encrypt(downloadInfo);
            
            // Log the secure link generation
            AuditLogger.logSecurity(accountNo, "GenerateSecureLink", 
                    "Generated secure download link for " + fileName, AuditLogger.SUCCESS);
            
            return encryptedInfo;
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.logSecurity(accountNo, "GenerateSecureLink", 
                    "Failed to generate secure download link: " + e.getMessage(), AuditLogger.ERROR);
            throw new RuntimeException("Error generating secure download link", e);
        }
    }
    
    /**
     * Validate and retrieve information from a secure download link
     * 
     * @param encryptedLink The encrypted download link
     * @return String array with [token, accountNo, fileName, filePath] or null if invalid
     */
    public static String[] validateSecureDownloadLink(String encryptedLink) {
        try {
            // Decrypt the link
            String decrypted = decrypt(encryptedLink);
            String[] parts = decrypted.split("\\|");
            
            if (parts.length != 5) {
                AuditLogger.log(AuditLogger.SECURITY, "ValidateSecureLink", 
                        "Invalid link format", AuditLogger.FAILURE);
                return null;
            }
            
            // Extract information
            String token = parts[0];
            String accountNo = parts[1];
            String fileName = parts[2];
            String filePath = parts[3];
            long expirationTime = Long.parseLong(parts[4]);
            
            // Check if link has expired
            if (Instant.now().getEpochSecond() > expirationTime) {
                AuditLogger.logSecurity(accountNo, "ValidateSecureLink", 
                        "Download link expired", AuditLogger.FAILURE);
                return null;
            }
            
            // Log successful validation
            AuditLogger.logSecurity(accountNo, "ValidateSecureLink", 
                    "Validated secure download link for " + fileName, AuditLogger.SUCCESS);
            
            return new String[]{token, accountNo, fileName, filePath};
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.log(AuditLogger.SECURITY, "ValidateSecureLink", 
                    "Failed to validate secure download link: " + e.getMessage(), AuditLogger.ERROR);
            return null;
        }
    }
    
    /**
     * Generate a secure session token with expiration
     * 
     * @param accountNo Account number
     * @param expirationMinutes Expiration time in minutes
     * @return Encrypted session token
     */
    public static String generateSessionToken(String accountNo, int expirationMinutes) {
        try {
            // Create session information with expiration
            String sessionId = UUID.randomUUID().toString();
            Instant expiration = Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);
            
            String sessionInfo = String.format("%s|%s|%d",
                    sessionId,
                    accountNo,
                    expiration.getEpochSecond());
            
            // Encrypt the session information
            return encrypt(sessionInfo);
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.logSecurity(accountNo, "GenerateSessionToken", 
                    "Failed to generate session token: " + e.getMessage(), AuditLogger.ERROR);
            throw new RuntimeException("Error generating session token", e);
        }
    }
    
    /**
     * Validate a session token
     * 
     * @param sessionToken Encrypted session token
     * @return Account number if valid, null otherwise
     */
    public static String validateSessionToken(String sessionToken) {
        try {
            // Decrypt the token
            String decrypted = decrypt(sessionToken);
            String[] parts = decrypted.split("\\|");
            
            if (parts.length != 3) {
                AuditLogger.log(AuditLogger.SECURITY, "ValidateSessionToken", 
                        "Invalid session token format", AuditLogger.FAILURE);
                return null;
            }
            
            // Extract information
            String accountNo = parts[1];
            long expirationTime = Long.parseLong(parts[2]);
            
            // Check if session has expired
            if (Instant.now().getEpochSecond() > expirationTime) {
                AuditLogger.logSecurity(accountNo, "ValidateSessionToken", 
                        "Session expired", AuditLogger.FAILURE);
                return null;
            }
            
            return accountNo;
            
        } catch (Exception e) {
            // Log security error
            AuditLogger.log(AuditLogger.SECURITY, "ValidateSessionToken", 
                    "Failed to validate session token: " + e.getMessage(), AuditLogger.ERROR);
            return null;
        }
    }
}

/* 
 * AGENT GENERATED SUMMARY:
 * Implementation requirements addressed:
 * - SEC-005: Implemented proper password hashing with PBKDF2
 * - SEC-006: Created encryption utilities for secure data handling
 * - PDF-003: Added secure download mechanism for PDF statements
 * - INFRA-001: Integrated with AuditLogger for security events
 * 
 * Human review required:
 * - The MASTER_KEY_BASE64 should be stored securely in a configuration file or environment variable
 * - Review encryption algorithm choices and parameters for compliance with security policies
 * - Consider implementing proper key rotation procedures
 * 
 * Agent run identifier: Claude-3.7-Sonnet-20250219
 */