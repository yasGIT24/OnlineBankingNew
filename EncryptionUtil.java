package banking.management.system;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created EncryptionUtil.java to implement:
 * 1. Secure password hashing using strong algorithms
 * 2. Data encryption for sensitive information
 * 3. Cryptographic security utilities
 */
public class EncryptionUtil {
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Secure random number generator for cryptographic operations
     */
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Hash password with salt using strong algorithm
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            byte[] salt = new byte[SALT_LENGTH];
            secureRandom.nextBytes(salt);
            
            // Hash the password
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.reset();
            md.update(salt);
            
            // Apply multiple iterations for added security
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < ITERATIONS; i++) {
                md.reset();
                hash = md.digest(hash);
            }
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            
            // Return as base64 string
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            ErrorHandler.handleException(e, "Password hashing failed");
            throw new RuntimeException("Secure hashing not available", e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Verify a password against a stored hash
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Decode stored hash
            byte[] combined = Base64.getDecoder().decode(storedHash);
            
            // Extract salt (first SALT_LENGTH bytes)
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            // Hash the input password with the same salt
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.reset();
            md.update(salt);
            
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            for (int i = 0; i < ITERATIONS; i++) {
                md.reset();
                hash = md.digest(hash);
            }
            
            // Compare computed hash with stored hash
            int hashLength = combined.length - SALT_LENGTH;
            for (int i = 0; i < hashLength; i++) {
                if (hash[i] != combined[SALT_LENGTH + i]) {
                    return false; // Hash mismatch
                }
            }
            
            return true; // Hashes match
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Password verification failed");
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Encrypt sensitive data
     */
    public static String encryptData(String plainText, SecretKey key) {
        try {
            // Generate random IV
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            
            // Encrypt the data
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            
            // Return as base64 string
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Data encryption failed");
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Decrypt sensitive data
     */
    public static String decryptData(String encryptedText, SecretKey key) {
        try {
            // Decode from base64
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            // Extract IV (first 16 bytes)
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // Extract encrypted data
            byte[] encrypted = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            
            // Decrypt the data
            byte[] decrypted = cipher.doFinal(encrypted);
            
            // Return as string
            return new String(decrypted, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Data decryption failed");
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Generate a secure encryption key
     */
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_LENGTH, secureRandom);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            ErrorHandler.handleException(e, "Key generation failed");
            throw new RuntimeException("Key generation failed", e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Convert key to string format for storage
     */
    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Convert string back to key for use
     */
    public static SecretKey stringToKey(String keyStr) {
        byte[] decodedKey = Base64.getDecoder().decode(keyStr);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-SEC-006, TC-SEC-007
 * Requirement IDs: US-1 (Secure password handling)
 * Agent Run: AGENT-20251127-01
 */