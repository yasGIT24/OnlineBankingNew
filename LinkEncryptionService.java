package banking.management.system;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* [AGENT GENERATED CODE - REQUIREMENT:Download Account Statement in PDF Format]
 * This service handles secure link generation and validation for document downloads.
 * It provides methods for:
 * 1. Creating encrypted download links
 * 2. Validating and decrypting links
 * 3. Enforcing link expiration and security policies
 * 
 * Note: This is a mock implementation with simplified encryption
 * In production, use a proper cryptographic framework and secure key management
 * 
 * Linked to Value Stream Step: Secure Link Creation, Download Delivery
 * Linked to test cases: SEC-01, SEC-02, STMT-03
 */
public class LinkEncryptionService {
    private static final Logger LOGGER = Logger.getLogger(LinkEncryptionService.class.getName());
    
    // Link expiration time in milliseconds (30 minutes)
    private static final long LINK_EXPIRY_TIME = 30 * 60 * 1000;
    
    // In-memory store of valid links (in production, use a database)
    // Map of encrypted links to link information (file path, account, expiry time)
    private static final Map<String, LinkInfo> validLinks = new HashMap<>();
    
    // Secret key for encryption (in production, use a secure key store)
    private static byte[] secretKey;
    
    /**
     * Constructor - initialize encryption
     */
    public LinkEncryptionService() {
        try {
            // Generate a secret key (in production, use a secure key store)
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // 256-bit AES key
            SecretKey key = keyGen.generateKey();
            secretKey = key.getEncoded();
            
            LOGGER.log(Level.INFO, "Link Encryption Service initialized");
            
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error initializing encryption: {0}", e.getMessage());
            // Fallback to a basic key for demo purposes only
            secretKey = "ThisIsADemoKeyForDevelopmentOnly".getBytes();
        }
    }
    
    /**
     * Generate an encrypted download link for a file
     * 
     * @param filePath Path to the file
     * @param accountNo Account number for authorization
     * @param pin PIN for authorization
     * @return Encrypted download link
     */
    public String generateEncryptedLink(String filePath, String accountNo, String pin) {
        try {
            // Create link expiry time (current time + expiry duration)
            long expiryTime = System.currentTimeMillis() + LINK_EXPIRY_TIME;
            
            // Create a link payload with file path, account, and expiry
            String linkPayload = filePath + "|" + accountNo + "|" + expiryTime;
            
            // Generate a random initialization vector for AES
            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[16]; // 16 bytes for AES
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            
            // Encrypt the link payload
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(linkPayload.getBytes());
            
            // Combine IV and encrypted data and encode
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            
            String encryptedLink = Base64.getUrlEncoder().encodeToString(combined);
            
            // Create a hash of the PIN for link validation without storing the actual PIN
            String pinHash = hashPin(pin);
            
            // Store link information in memory
            LinkInfo linkInfo = new LinkInfo(filePath, accountNo, pinHash, expiryTime);
            validLinks.put(encryptedLink, linkInfo);
            
            // Clean up expired links periodically
            cleanupExpiredLinks();
            
            return encryptedLink;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating encrypted link: {0}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Decrypt and validate a download link
     * 
     * @param encryptedLink Encrypted link to validate
     * @param accountNo Account number for verification
     * @param pin PIN for verification
     * @return File path if valid, null otherwise
     */
    public String decryptLink(String encryptedLink, String accountNo, String pin) {
        try {
            // Check if link exists in our valid links store
            LinkInfo linkInfo = validLinks.get(encryptedLink);
            if (linkInfo == null) {
                LOGGER.log(Level.WARNING, "Link not found or already used");
                return null;
            }
            
            // Check if link is expired
            if (System.currentTimeMillis() > linkInfo.expiryTime) {
                LOGGER.log(Level.WARNING, "Link expired");
                validLinks.remove(encryptedLink); // Remove expired link
                return null;
            }
            
            // Verify account number matches
            if (!linkInfo.accountNo.equals(accountNo)) {
                LOGGER.log(Level.WARNING, "Account number mismatch");
                return null;
            }
            
            // Verify PIN hash
            String pinHash = hashPin(pin);
            if (!linkInfo.pinHash.equals(pinHash)) {
                LOGGER.log(Level.WARNING, "Invalid PIN for link");
                return null;
            }
            
            // Decode the encrypted link
            byte[] combined = Base64.getUrlDecoder().decode(encryptedLink);
            
            // Extract IV and encrypted data
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);
            
            // Decrypt the link payload
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(encrypted);
            
            // Parse the link payload
            String linkPayload = new String(decrypted);
            String[] parts = linkPayload.split("\\|");
            
            if (parts.length != 3) {
                LOGGER.log(Level.WARNING, "Invalid link format");
                return null;
            }
            
            String filePath = parts[0];
            String storedAccountNo = parts[1];
            long storedExpiryTime = Long.parseLong(parts[2]);
            
            // Double check account and expiry (redundant with our linkInfo validation)
            if (!storedAccountNo.equals(accountNo) || System.currentTimeMillis() > storedExpiryTime) {
                LOGGER.log(Level.WARNING, "Link validation failed");
                return null;
            }
            
            // Link used successfully, remove it (one-time use)
            validLinks.remove(encryptedLink);
            
            return filePath;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error decrypting link: {0}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Clean up expired links from memory
     */
    private void cleanupExpiredLinks() {
        long currentTime = System.currentTimeMillis();
        validLinks.entrySet().removeIf(entry -> entry.getValue().expiryTime < currentTime);
    }
    
    /**
     * Hash PIN for secure storage and validation
     * 
     * @param pin PIN to hash
     * @return Hashed PIN
     */
    private String hashPin(String pin) {
        try {
            // In production, use a proper password hashing function with salt (like BCrypt)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((pin + "SaltValueForDemo").getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error hashing PIN: {0}", e.getMessage());
            return pin; // Fallback, should never happen
        }
    }
    
    /**
     * Inner class to store link information
     */
    private static class LinkInfo {
        final String filePath;
        final String accountNo;
        final String pinHash;
        final long expiryTime;
        
        LinkInfo(String filePath, String accountNo, String pinHash, long expiryTime) {
            this.filePath = filePath;
            this.accountNo = accountNo;
            this.pinHash = pinHash;
            this.expiryTime = expiryTime;
        }
    }
}

/* 
 * Test cases:
 * SEC-01: Verify link encryption and decryption
 * SEC-02: Verify link expiration functionality
 * STMT-03: Verify secure download link generation and validation
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */