package banking.management.system;

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for encryption and decryption operations.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
 * This class provides secure encryption and hashing functionality
 * for sensitive data protection in the banking application.
 */
public class EncryptionUtils {
    
    private static final Logger logger = Logger.getLogger(EncryptionUtils.class.getName());
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    private SecretKey secretKey;
    private byte[] iv;
    
    /**
     * Constructor that initializes encryption keys.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    public EncryptionUtils() {
        try {
            // In a production environment, these should be securely stored and loaded
            // rather than generated each time
            generateKey();
            generateIV();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error initializing encryption utils", e);
        }
    }
    
    /**
     * Encrypts plaintext data.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param plainText Text to encrypt
     * @return Base64 encoded encrypted string
     */
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Encryption error", e);
            return null;
        }
    }
    
    /**
     * Decrypts encrypted data.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param encryptedText Base64 encoded encrypted string
     * @return Decrypted plaintext
     */
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Decryption error", e);
            return null;
        }
    }
    
    /**
     * Generates a secure hash of text (for passwords).
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param text Text to hash
     * @param salt Salt value for the hash
     * @return Base64 encoded hash
     */
    public String generateSecureHash(String text, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashedBytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Hashing error", e);
            return null;
        }
    }
    
    /**
     * Generates a random salt for hashing.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param length Length of salt
     * @return Random salt string
     */
    public String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Generates a secure download URL token.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param resourceId Resource identifier
     * @return Encrypted URL token
     */
    public String generateSecureDownloadToken(String accountNo, String resourceId) {
        String tokenData = accountNo + ":" + resourceId + ":" + System.currentTimeMillis();
        return encrypt(tokenData);
    }
    
    /**
     * Validates a download URL token.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param token Encrypted token
     * @return true if valid, false otherwise
     */
    public boolean validateDownloadToken(String token) {
        try {
            String decryptedToken = decrypt(token);
            String[] parts = decryptedToken.split(":");
            
            if (parts.length != 3) {
                return false;
            }
            
            // Check if token is expired (15 minutes validity)
            long tokenTime = Long.parseLong(parts[2]);
            long currentTime = System.currentTimeMillis();
            long validityPeriod = 900000; // 15 minutes in milliseconds
            
            return (currentTime - tokenTime <= validityPeriod);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Download token validation failed", e);
            return false;
        }
    }
    
    /**
     * Generates encryption key.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    private void generateKey() throws Exception {
        // In production, this should be loaded from a secure keystore
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(256);
        secretKey = keyGen.generateKey();
    }
    
    /**
     * Generates initialization vector for encryption.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    private void generateIV() {
        // In production, this should be handled more securely
        SecureRandom random = new SecureRandom();
        iv = new byte[16];
        random.nextBytes(iv);
    }
}