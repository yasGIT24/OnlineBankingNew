package banking.management.system;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.text.SimpleDateFormat;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US1]
 * This service handles encryption for secure file downloads.
 * It provides methods to encrypt files and generate secure download links.
 */
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final long LINK_EXPIRY_MINUTES = 30;
    private final Map<String, LinkInfo> activeLinks = new HashMap<>();
    
    /**
     * Generates a secure, encrypted download link for the specified file
     * 
     * @param filePath Path to the file to be encrypted
     * @return String containing the secure download token
     */
    public String generateEncryptedLink(String filePath) {
        try {
            // Generate a unique token for this download
            String token = generateUniqueToken();
            
            // Create a copy of the file in the temp directory
            String tempFilePath = TEMP_DIR + File.separator + "bank_statement_" + token + ".pdf";
            Files.copy(Paths.get(filePath), Paths.get(tempFilePath), StandardCopyOption.REPLACE_EXISTING);
            
            // Generate a random encryption key
            SecretKey key = generateKey();
            
            // Encrypt the file
            String encryptedFilePath = encryptFile(tempFilePath, key);
            
            // Store the link information
            Date expiryDate = getExpiryTime();
            activeLinks.put(token, new LinkInfo(encryptedFilePath, key, expiryDate));
            
            return token;
        } catch (Exception e) {
            System.out.println("Error generating encrypted link: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Retrieves and decrypts a file using the provided token
     * 
     * @param token The secure download token
     * @return Path to the decrypted file, or null if token is invalid or expired
     */
    public String getDecryptedFile(String token) {
        try {
            // Check if the token exists and is not expired
            if (!isValidToken(token)) {
                return null;
            }
            
            // Get the link info
            LinkInfo info = activeLinks.get(token);
            
            // Decrypt the file to a temporary location
            String decryptedFilePath = TEMP_DIR + File.separator + "decrypted_" + token + ".pdf";
            decryptFile(info.encryptedFilePath, decryptedFilePath, info.key);
            
            return decryptedFilePath;
        } catch (Exception e) {
            System.out.println("Error retrieving decrypted file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks if a token is valid and not expired
     * 
     * @param token The token to validate
     * @return boolean indicating if the token is valid
     */
    public boolean isValidToken(String token) {
        if (!activeLinks.containsKey(token)) {
            return false;
        }
        
        LinkInfo info = activeLinks.get(token);
        return new Date().before(info.expiryDate);
    }
    
    /**
     * Generates a random encryption key
     * 
     * @return SecretKey for encryption
     */
    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }
    
    /**
     * Encrypts a file using AES encryption
     * 
     * @param inputFilePath Path to the file to encrypt
     * @param key Encryption key
     * @return Path to the encrypted file
     */
    private String encryptFile(String inputFilePath, SecretKey key) throws Exception {
        String outputFilePath = inputFilePath + ".enc";
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        
        try (FileInputStream fis = new FileInputStream(inputFilePath);
             FileOutputStream fos = new FileOutputStream(outputFilePath);
             CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
            
            byte[] buffer = new byte[8192];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                cos.write(buffer, 0, count);
            }
        }
        
        // Delete the original unencrypted temp file
        Files.delete(Paths.get(inputFilePath));
        
        return outputFilePath;
    }
    
    /**
     * Decrypts a file using AES encryption
     * 
     * @param inputFilePath Path to the encrypted file
     * @param outputFilePath Path where the decrypted file should be saved
     * @param key Decryption key
     */
    private void decryptFile(String inputFilePath, String outputFilePath, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        
        try (FileInputStream fis = new FileInputStream(inputFilePath);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            
            byte[] buffer = new byte[8192];
            int count;
            while ((count = cis.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
        }
    }
    
    /**
     * Generates a unique token for the download link
     * 
     * @return String containing the unique token
     */
    private String generateUniqueToken() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * Calculates the expiry time for a link
     * 
     * @return Date when the link will expire
     */
    private Date getExpiryTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, (int) LINK_EXPIRY_MINUTES);
        return calendar.getTime();
    }
    
    /**
     * Class to store information about an active download link
     */
    private static class LinkInfo {
        String encryptedFilePath;
        SecretKey key;
        Date expiryDate;
        
        LinkInfo(String encryptedFilePath, SecretKey key, Date expiryDate) {
            this.encryptedFilePath = encryptedFilePath;
            this.key = key;
            this.expiryDate = expiryDate;
        }
    }
    
    /**
     * Formats the download link as a secure URL
     * 
     * @param token The download token
     * @return String containing the formatted download URL
     */
    public String formatDownloadUrl(String token) {
        // In a real web application, this would be a proper URL
        // For the Java Swing application, we'll use a special format
        return "bank://download/" + token;
    }
}

/* 
 * Agent Run Identifier: BANK-SECURITY-20251126
 * Related Test Cases: SECURITY-ENC-001, SECURITY-ENC-002
 */