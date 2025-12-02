package banking.management.system;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

/**
 * [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-01, REQ-PDF-02, REQ-PDF-03, REQ-PDF-04]
 * Service for generating account statements as PDF documents
 */
public class StatementPdfService {
    
    private static final Logger LOGGER = Logger.getLogger(StatementPdfService.class.getName());
    private static final String PDF_DIRECTORY = "statements";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    private TransactionHistoryService transactionHistoryService;
    private SecureRandom secureRandom;
    
    /**
     * Constructor initializes dependencies
     */
    public StatementPdfService() {
        this.transactionHistoryService = new TransactionHistoryService();
        this.secureRandom = new SecureRandom();
        
        // Create the statements directory if it doesn't exist
        File directory = new File(PDF_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Returns the directory where PDF statements are stored
     * 
     * @return PDF directory path
     */
    // [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-02]
    // Added getter method for the PDF directory path
    public String getPdfDirectory() {
        return PDF_DIRECTORY;
    }
    
    /**
     * Generates a PDF statement for the specified account within the given date range
     * 
     * @param accountNo The account number
     * @param startDate Start date for the statement period
     * @param endDate End date for the statement period
     * @return An EncryptedDownloadLink object containing the download information
     * @throws Exception If PDF generation fails
     */
    public EncryptedDownloadLink generateStatement(String accountNo, Date startDate, Date endDate) throws Exception {
        // Validate parameters
        if (accountNo == null || accountNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        // Fetch account details
        AccountDetails accountDetails = fetchAccountDetails(accountNo);
        if (accountDetails == null) {
            throw new IllegalArgumentException("Account not found: " + accountNo);
        }
        
        // Fetch transactions for the date range
        List<Transaction> transactions = fetchTransactions(accountNo, startDate, endDate);
        
        // Generate the PDF file
        String fileName = generateFileName(accountNo, startDate, endDate);
        String filePath = PDF_DIRECTORY + "/" + fileName;
        
        createPdf(filePath, accountDetails, transactions, startDate, endDate);
        
        // Create encrypted download link
        EncryptedDownloadLink downloadLink = createEncryptedDownloadLink(accountNo, fileName, startDate, endDate);
        
        // Log the statement generation in the database
        logStatementDownload(accountNo, downloadLink, startDate, endDate, "GENERATED");
        
        return downloadLink;
    }
    
    /**
     * Creates the actual PDF document
     * 
     * @param filePath Output file path
     * @param accountDetails Account information
     * @param transactions List of transactions
     * @param startDate Statement start date
     * @param endDate Statement end date
     * @throws Exception If PDF creation fails
     */
    private void createPdf(String filePath, AccountDetails accountDetails, List<Transaction> transactions, 
            Date startDate, Date endDate) throws Exception {
        
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add bank logo and header
            addBankHeader(document);
            
            // Add statement details
            addStatementInfo(document, accountDetails, startDate, endDate);
            
            // Add account details
            addAccountDetails(document, accountDetails);
            
            // Add transactions table
            addTransactionsTable(document, transactions);
            
            // Add summary section
            addSummarySection(document, transactions);
            
            // Add footer
            addFooter(document);
            
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
    
    /**
     * Adds bank logo and header to the document
     * 
     * @param document PDF document
     * @throws IOException If image loading fails
     * @throws DocumentException If document manipulation fails
     */
    private void addBankHeader(Document document) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0, 0, 128));
        Paragraph title = new Paragraph("Online Banking System", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Font subtitleFont = new Font(Font.HELVETICA, 14, Font.NORMAL, new Color(100, 100, 100));
        Paragraph subtitle = new Paragraph("Account Statement", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
    }
    
    /**
     * Adds statement period information
     * 
     * @param document PDF document
     * @param accountDetails Account information
     * @param startDate Statement start date
     * @param endDate Statement end date
     * @throws DocumentException If document manipulation fails
     */
    private void addStatementInfo(Document document, AccountDetails accountDetails, Date startDate, Date endDate) 
            throws DocumentException {
        
        Font infoFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        
        Paragraph statementInfo = new Paragraph();
        statementInfo.setFont(infoFont);
        statementInfo.add("Statement Period: " + DATE_FORMAT.format(startDate) + " to " + DATE_FORMAT.format(endDate));
        statementInfo.setSpacingAfter(10);
        document.add(statementInfo);
        
        Paragraph generatedDate = new Paragraph();
        generatedDate.setFont(infoFont);
        generatedDate.add("Generated on: " + DATETIME_FORMAT.format(new Date()));
        generatedDate.setSpacingAfter(20);
        document.add(generatedDate);
    }
    
    /**
     * Adds account details section
     * 
     * @param document PDF document
     * @param accountDetails Account information
     * @throws DocumentException If document manipulation fails
     */
    private void addAccountDetails(Document document, AccountDetails accountDetails) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 0, 128));
        Font detailsFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        
        Paragraph accountSection = new Paragraph("Account Information", sectionFont);
        accountSection.setSpacingAfter(10);
        document.add(accountSection);
        
        PdfPTable accountTable = new PdfPTable(2);
        accountTable.setWidthPercentage(100);
        accountTable.setSpacingAfter(20);
        
        // Mask account number for security
        String maskedAccountNo = maskAccountNumber(accountDetails.getAccountNo());
        
        addTableRow(accountTable, "Account Holder:", accountDetails.getName(), detailsFont);
        addTableRow(accountTable, "Account Number:", maskedAccountNo, detailsFont);
        addTableRow(accountTable, "Account Type:", accountDetails.getAccountType(), detailsFont);
        
        document.add(accountTable);
    }
    
    /**
     * Adds a row to a PDF table
     * 
     * @param table The PdfPTable
     * @param label Label text
     * @param value Value text
     * @param font Font to use
     */
    private void addTableRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    /**
     * Adds transaction table to the document
     * 
     * @param document PDF document
     * @param transactions List of transactions
     * @throws DocumentException If document manipulation fails
     */
    private void addTransactionsTable(Document document, List<Transaction> transactions) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 0, 128));
        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(255, 255, 255));
        Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
        
        Paragraph transactionSection = new Paragraph("Transaction History", sectionFont);
        transactionSection.setSpacingAfter(10);
        document.add(transactionSection);
        
        PdfPTable transactionTable = new PdfPTable(new float[]{0.15f, 0.35f, 0.15f, 0.15f, 0.2f});
        transactionTable.setWidthPercentage(100);
        transactionTable.setSpacingAfter(20);
        
        // Table header
        PdfPCell dateHeader = new PdfPCell(new Phrase("Date", headerFont));
        styleHeaderCell(dateHeader);
        
        PdfPCell descHeader = new PdfPCell(new Phrase("Description", headerFont));
        styleHeaderCell(descHeader);
        
        PdfPCell typeHeader = new PdfPCell(new Phrase("Type", headerFont));
        styleHeaderCell(typeHeader);
        
        PdfPCell amountHeader = new PdfPCell(new Phrase("Amount", headerFont));
        styleHeaderCell(amountHeader);
        
        PdfPCell balanceHeader = new PdfPCell(new Phrase("Balance", headerFont));
        styleHeaderCell(balanceHeader);
        
        transactionTable.addCell(dateHeader);
        transactionTable.addCell(descHeader);
        transactionTable.addCell(typeHeader);
        transactionTable.addCell(amountHeader);
        transactionTable.addCell(balanceHeader);
        
        // Table rows
        double runningBalance = 0.0;
        for (Transaction txn : transactions) {
            // Update running balance
            if ("Deposit".equals(txn.getType()) || "Transfer In".equals(txn.getType())) {
                runningBalance += txn.getAmount();
            } else {
                runningBalance -= txn.getAmount();
            }
            
            PdfPCell dateCell = new PdfPCell(new Phrase(DATE_FORMAT.format(txn.getDate()), cellFont));
            styleCell(dateCell);
            
            PdfPCell descCell = new PdfPCell(new Phrase(txn.getDescription(), cellFont));
            styleCell(descCell);
            
            PdfPCell typeCell = new PdfPCell(new Phrase(txn.getType(), cellFont));
            styleCell(typeCell);
            
            String amountStr = String.format("%.2f", txn.getAmount());
            PdfPCell amountCell = new PdfPCell(new Phrase(amountStr, cellFont));
            styleCell(amountCell);
            amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            String balanceStr = String.format("%.2f", runningBalance);
            PdfPCell balanceCell = new PdfPCell(new Phrase(balanceStr, cellFont));
            styleCell(balanceCell);
            balanceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            transactionTable.addCell(dateCell);
            transactionTable.addCell(descCell);
            transactionTable.addCell(typeCell);
            transactionTable.addCell(amountCell);
            transactionTable.addCell(balanceCell);
        }
        
        document.add(transactionTable);
    }
    
    /**
     * Styles a header cell
     * 
     * @param cell The cell to style
     */
    private void styleHeaderCell(PdfPCell cell) {
        cell.setBackgroundColor(new Color(0, 0, 128));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }
    
    /**
     * Styles a regular cell
     * 
     * @param cell The cell to style
     */
    private void styleCell(PdfPCell cell) {
        cell.setPadding(5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }
    
    /**
     * Adds summary section to the document
     * 
     * @param document PDF document
     * @param transactions List of transactions
     * @throws DocumentException If document manipulation fails
     */
    private void addSummarySection(Document document, List<Transaction> transactions) throws DocumentException {
        Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(0, 0, 128));
        Font summaryFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        Font totalFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        
        Paragraph summarySection = new Paragraph("Summary", sectionFont);
        summarySection.setSpacingAfter(10);
        document.add(summarySection);
        
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(60);
        summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryTable.setSpacingAfter(20);
        
        // Calculate summary values
        double totalDeposits = 0.0;
        double totalWithdrawals = 0.0;
        int numDeposits = 0;
        int numWithdrawals = 0;
        
        for (Transaction txn : transactions) {
            if ("Deposit".equals(txn.getType()) || "Transfer In".equals(txn.getType())) {
                totalDeposits += txn.getAmount();
                numDeposits++;
            } else {
                totalWithdrawals += txn.getAmount();
                numWithdrawals++;
            }
        }
        
        // Final balance
        double finalBalance = totalDeposits - totalWithdrawals;
        
        // Add summary rows
        addSummaryRow(summaryTable, "Total Deposits:", numDeposits + " transactions", 
                String.format("%.2f", totalDeposits), summaryFont);
        
        addSummaryRow(summaryTable, "Total Withdrawals:", numWithdrawals + " transactions", 
                String.format("%.2f", totalWithdrawals), summaryFont);
        
        // Add final balance with different styling
        PdfPCell labelCell = new PdfPCell(new Phrase("Closing Balance:", totalFont));
        labelCell.setBorder(Rectangle.TOP);
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(String.format("%.2f", finalBalance), totalFont));
        valueCell.setBorder(Rectangle.TOP);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        summaryTable.addCell(labelCell);
        summaryTable.addCell(valueCell);
        
        document.add(summaryTable);
    }
    
    /**
     * Adds a summary row to the table
     * 
     * @param table The table
     * @param label Label text
     * @param subtitle Subtitle text
     * @param value Value text
     * @param font Font to use
     */
    private void addSummaryRow(PdfPTable table, String label, String subtitle, String value, Font font) {
        Paragraph labelPara = new Paragraph();
        labelPara.setFont(font);
        labelPara.add(label + "\n");
        Font subtitleFont = new Font(font.getFamily(), font.getSize() - 2, Font.ITALIC);
        labelPara.add(new Chunk(subtitle, subtitleFont));
        
        PdfPCell labelCell = new PdfPCell(labelPara);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    /**
     * Adds footer to the document
     * 
     * @param document PDF document
     * @throws DocumentException If document manipulation fails
     */
    private void addFooter(Document document) throws DocumentException {
        Font footerFont = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(128, 128, 128));
        
        Paragraph footer = new Paragraph();
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setFont(footerFont);
        footer.add("This statement is computer generated and does not require a signature.\n");
        footer.add("Please contact our customer service for any discrepancies in the statement.");
        
        document.add(footer);
    }
    
    /**
     * Retrieves account details from the database
     * 
     * @param accountNo Account number
     * @return AccountDetails object or null if not found
     */
    private AccountDetails fetchAccountDetails(String accountNo) {
        ConnectionSql connectionSql = new ConnectionSql();
        AccountDetails details = null;
        
        try {
            String query = "SELECT s1.Name, 'Savings' as AccountType FROM signup1 s1 " +
                           "JOIN login l ON l.Form_No = s1.FormNo " +
                           "WHERE l.Account_No = ?";
            
            PreparedStatement ps = connectionSql.prepareStatement(query);
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                details = new AccountDetails();
                details.setAccountNo(accountNo);
                details.setName(rs.getString("Name"));
                details.setAccountType(rs.getString("AccountType"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching account details", e);
        }
        
        return details;
    }
    
    /**
     * Fetches transactions for the account in the specified date range
     * 
     * @param accountNo Account number
     * @param startDate Start date
     * @param endDate End date
     * @return List of Transaction objects
     */
    private List<Transaction> fetchTransactions(String accountNo, Date startDate, Date endDate) {
        try {
            return transactionHistoryService.getTransactionHistory(accountNo, startDate, endDate, null, 0.0);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching transactions", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Generates a unique filename for the statement PDF
     * 
     * @param accountNo Account number
     * @param startDate Start date
     * @param endDate End date
     * @return Generated filename
     */
    private String generateFileName(String accountNo, Date startDate, Date endDate) {
        SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd");
        String maskedAccount = accountNo.substring(accountNo.length() - 4);
        String dateRange = fileFormat.format(startDate) + "_" + fileFormat.format(endDate);
        String timestamp = fileFormat.format(new Date());
        String randomPart = generateRandomString(8);
        
        return "statement_" + maskedAccount + "_" + dateRange + "_" + timestamp + "_" + randomPart + ".pdf";
    }
    
    /**
     * Creates an encrypted download link for a statement file
     * 
     * @param accountNo Account number
     * @param fileName The PDF file name
     * @param startDate Statement start date
     * @param endDate Statement end date
     * @return EncryptedDownloadLink object
     * @throws Exception If encryption fails
     */
    private EncryptedDownloadLink createEncryptedDownloadLink(String accountNo, String fileName, 
            Date startDate, Date endDate) throws Exception {
        
        // Create a unique download ID
        String downloadId = generateRandomString(32);
        
        // Set expiry time (24 hours from now)
        Date expiryDate = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
        
        // Create a token containing the file information
        String token = accountNo + "|" + fileName + "|" + expiryDate.getTime();
        
        // Encrypt the token
        String encryptedToken = encrypt(token);
        
        // Return the encrypted download link
        EncryptedDownloadLink link = new EncryptedDownloadLink();
        link.setDownloadId(downloadId);
        link.setToken(encryptedToken);
        link.setFileName(fileName);
        link.setExpiryDate(expiryDate);
        
        return link;
    }
    
    /**
     * Logs a statement download event to the database
     * 
     * @param accountNo Account number
     * @param link Download link information
     * @param startDate Statement start date
     * @param endDate Statement end date
     * @param status Download status
     */
    private void logStatementDownload(String accountNo, EncryptedDownloadLink link, 
            Date startDate, Date endDate, String status) {
        
        ConnectionSql connectionSql = new ConnectionSql();
        
        try {
            // Create the statement_downloads table if it doesn't exist
            String createTableQuery = 
                "CREATE TABLE IF NOT EXISTS statement_downloads (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "account_no VARCHAR(20) NOT NULL, " +
                "download_date DATETIME NOT NULL, " +
                "statement_period_start DATE NOT NULL, " +
                "statement_period_end DATE NOT NULL, " +
                "file_name VARCHAR(255) NOT NULL, " +
                "download_ip VARCHAR(50), " +
                "download_status VARCHAR(20) NOT NULL, " +
                "encrypted_link_id VARCHAR(255) NOT NULL, " +
                "link_expiry DATETIME NOT NULL, " +
                "INDEX idx_account_no (account_no), " +
                "INDEX idx_download_date (download_date)" +
                ")";
            
            PreparedStatement createTableStmt = connectionSql.prepareStatement(createTableQuery);
            createTableStmt.execute();
            
            // Insert the download record
            String insertQuery = 
                "INSERT INTO statement_downloads " +
                "(account_no, download_date, statement_period_start, statement_period_end, " +
                "file_name, download_status, encrypted_link_id, link_expiry) " +
                "VALUES (?, NOW(), ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement ps = connectionSql.prepareStatement(insertQuery);
            ps.setString(1, accountNo);
            ps.setDate(2, new java.sql.Date(startDate.getTime()));
            ps.setDate(3, new java.sql.Date(endDate.getTime()));
            ps.setString(4, link.getFileName());
            ps.setString(5, status);
            ps.setString(6, link.getDownloadId());
            ps.setTimestamp(7, new java.sql.Timestamp(link.getExpiryDate().getTime()));
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error logging statement download", e);
        }
    }
    
    /**
     * Updates an existing download record when a download is completed
     * 
     * @param downloadId Download ID
     * @param ip Client IP address
     * @return true if update successful, false otherwise
     */
    public boolean updateDownloadStatus(String downloadId, String ip) {
        ConnectionSql connectionSql = new ConnectionSql();
        
        try {
            String updateQuery = 
                "UPDATE statement_downloads " +
                "SET download_status = 'COMPLETED', download_ip = ? " +
                "WHERE encrypted_link_id = ? AND link_expiry > NOW()";
            
            PreparedStatement ps = connectionSql.prepareStatement(updateQuery);
            ps.setString(1, ip);
            ps.setString(2, downloadId);
            
            int rows = ps.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating download status", e);
            return false;
        }
    }
    
    /**
     * Validates a download token and returns the file path if valid
     * 
     * @param downloadId Download ID
     * @param token Encrypted token
     * @return File path or null if invalid
     */
    public String validateDownloadToken(String downloadId, String token) {
        try {
            // Decrypt the token
            String decryptedToken = decrypt(token);
            
            // Parse token parts
            String[] parts = decryptedToken.split("\\|");
            if (parts.length != 3) {
                return null;
            }
            
            String accountNo = parts[0];
            String fileName = parts[1];
            long expiryTime = Long.parseLong(parts[2]);
            
            // Check if token is expired
            if (System.currentTimeMillis() > expiryTime) {
                return null;
            }
            
            // Verify token against database
            ConnectionSql connectionSql = new ConnectionSql();
            String query = 
                "SELECT * FROM statement_downloads " +
                "WHERE encrypted_link_id = ? AND file_name = ? AND link_expiry > NOW()";
            
            PreparedStatement ps = connectionSql.prepareStatement(query);
            ps.setString(1, downloadId);
            ps.setString(2, fileName);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return PDF_DIRECTORY + "/" + fileName;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating download token", e);
        }
        
        return null;
    }
    
    /**
     * Generates a random alphanumeric string of the specified length
     * 
     * @param length String length
     * @return Random string
     */
    private String generateRandomString(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        
        return sb.toString();
    }
    
    /**
     * Masks an account number for security (shows only last 4 digits)
     * 
     * @param accountNo Account number to mask
     * @return Masked account number
     */
    private String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() <= 4) {
            return accountNo;
        }
        
        int visibleChars = 4;
        int maskLength = accountNo.length() - visibleChars;
        StringBuilder masked = new StringBuilder();
        
        for (int i = 0; i < maskLength; i++) {
            masked.append("X");
        }
        
        masked.append(accountNo.substring(maskLength));
        return masked.toString();
    }
    
    /**
     * Simple encryption method (for demonstration purposes)
     * In a production environment, use a proper encryption library
     * 
     * @param input Text to encrypt
     * @return Encrypted text
     */
    private String encrypt(String input) throws Exception {
        // This is a simplified encryption for demonstration
        // In a real application, use a proper encryption library
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        String key = Base64.getEncoder().encodeToString(hash).substring(0, 16);
        
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            char k = key.charAt(i % key.length());
            encrypted.append((char)(c ^ k));
        }
        
        return Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }
    
    /**
     * Simple decryption method (for demonstration purposes)
     * 
     * @param input Text to decrypt
     * @return Decrypted text
     */
    private String decrypt(String input) throws Exception {
        // Matching decrypt method for the simplified encryption
        byte[] decodedBytes = Base64.getDecoder().decode(input);
        String encoded = new String(decodedBytes);
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(encoded.getBytes());
        String key = Base64.getEncoder().encodeToString(hash).substring(0, 16);
        
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            char k = key.charAt(i % key.length());
            decrypted.append((char)(c ^ k));
        }
        
        return decrypted.toString();
    }
    
    /**
     * Account details class
     */
    public static class AccountDetails {
        private String accountNo;
        private String name;
        private String accountType;
        
        public String getAccountNo() {
            return accountNo;
        }
        
        public void setAccountNo(String accountNo) {
            this.accountNo = accountNo;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getAccountType() {
            return accountType;
        }
        
        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }
    }
    
    /**
     * Transaction class
     */
    public static class Transaction {
        private Date date;
        private String description;
        private String type;
        private double amount;
        
        public Date getDate() {
            return date;
        }
        
        public void setDate(Date date) {
            this.date = date;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
    
    /**
     * Encrypted download link class
     */
    public static class EncryptedDownloadLink {
        private String downloadId;
        private String token;
        private String fileName;
        private Date expiryDate;
        
        public String getDownloadId() {
            return downloadId;
        }
        
        public void setDownloadId(String downloadId) {
            this.downloadId = downloadId;
        }
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public Date getExpiryDate() {
            return expiryDate;
        }
        
        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }
        
        public String getDownloadUrl() {
            return "statement-download?id=" + downloadId + "&token=" + token;
        }
    }
}

// [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-01, REQ-PDF-02, REQ-PDF-03, REQ-PDF-04]
// This file implements the PDF statement generation service for account statements.
// It creates PDF statements with transaction history, account details, and summary totals.
// It also handles encrypted download links and activity logging for compliance purposes.
// Agent run identifier: AGENT-PDF-SERV-2025-12-02