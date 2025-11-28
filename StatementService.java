package banking.management.system;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for statement generation and download.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
 * This class provides services for generating and downloading account statements
 * in PDF format with secure access control and audit logging.
 */
public class StatementService {
    
    private static final Logger logger = Logger.getLogger(StatementService.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private final TransactionRepository transactionRepository;
    private final PDFGenerator pdfGenerator;
    private final EncryptionUtils encryptionUtils;
    private final AuditLogService auditLogService;
    private final SecurityService securityService;
    
    /**
     * Constructor initializes required services.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     */
    public StatementService() {
        this.transactionRepository = new TransactionRepository();
        this.pdfGenerator = new PDFGenerator();
        this.encryptionUtils = new EncryptionUtils();
        this.auditLogService = new AuditLogService();
        this.securityService = new SecurityService();
    }
    
    /**
     * Generates a statement for the given account and date range.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param pin Account PIN/password
     * @param fromDate Start date (yyyy-MM-dd)
     * @param toDate End date (yyyy-MM-dd)
     * @return Download token for the generated statement
     * @throws Exception If statement generation fails
     */
    public String generateStatement(String accountNo, String pin, String fromDate, String toDate) throws Exception {
        logger.info("Generating statement for account ending with " + 
                accountNo.substring(Math.max(0, accountNo.length() - 4)) + 
                " for period " + fromDate + " to " + toDate);
        
        validateDateRange(fromDate, toDate);
        
        // Get account information
        TransactionRepository.AccountInfo accountInfo = transactionRepository.getAccountInfo(accountNo);
        if (accountInfo == null) {
            throw new Exception("Unable to retrieve account information");
        }
        
        // Get transactions for the date range
        List<TransactionRepository.Transaction> transactions = 
                transactionRepository.getTransactionsByDateRange(accountNo, pin, fromDate, toDate);
        
        // Calculate opening and closing balances
        double openingBalance = calculateOpeningBalance(accountNo, pin, fromDate);
        double closingBalance = calculateClosingBalance(openingBalance, transactions);
        
        // Generate PDF
        String pdfPath = pdfGenerator.generateStatementPDF(
                accountInfo, transactions, fromDate, toDate, openingBalance, closingBalance);
        
        // Apply letterhead and security features
        pdfGenerator.applyBankLetterhead(pdfPath);
        pdfGenerator.applyDigitalSignature(pdfPath);
        
        // Generate last 4 digits of account number as default password
        String password = accountNo.substring(Math.max(0, accountNo.length() - 4));
        pdfGenerator.encryptPDF(pdfPath, password);
        
        // Generate secure download token
        String downloadToken = generateDownloadToken(accountNo, pdfPath);
        
        // Log the statement generation
        auditLogService.logStatementDownload(accountNo, fromDate, toDate, downloadToken, "GENERATED");
        
        return downloadToken;
    }
    
    /**
     * Gets the file path for a statement using its download token.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param downloadToken Secure download token
     * @return File path to the statement PDF
     * @throws Exception If token is invalid or statement not found
     */
    public String getStatementByToken(String downloadToken) throws Exception {
        if (!encryptionUtils.validateDownloadToken(downloadToken)) {
            auditLogService.logSecurityEvent("UNKNOWN", "INVALID_DOWNLOAD_TOKEN", 
                    "Invalid or expired download token", "FAILED");
            throw new Exception("Invalid or expired download token");
        }
        
        // In a real implementation, this would lookup the path from a database
        // based on the token. For this example, we're decrypting the token to get the path.
        String decryptedToken = encryptionUtils.decrypt(downloadToken);
        String[] parts = decryptedToken.split(":");
        
        if (parts.length != 3) {
            throw new Exception("Invalid download token format");
        }
        
        String accountNo = parts[0];
        String filePath = parts[1];
        
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            auditLogService.logSecurityEvent(accountNo, "STATEMENT_NOT_FOUND", 
                    "Statement file not found: " + filePath, "FAILED");
            throw new Exception("Statement file not found");
        }
        
        // Log the download access
        auditLogService.logStatementDownload(accountNo, "N/A", "N/A", downloadToken, "DOWNLOADED");
        
        return filePath;
    }
    
    /**
     * Generates a secure download token for statement access.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param filePath Path to the PDF file
     * @return Encrypted download token
     */
    private String generateDownloadToken(String accountNo, String filePath) {
        String tokenData = accountNo + ":" + filePath + ":" + System.currentTimeMillis();
        return encryptionUtils.encrypt(tokenData);
    }
    
    /**
     * Validates the requested date range.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param fromDate Start date
     * @param toDate End date
     * @throws Exception If date range is invalid
     */
    private void validateDateRange(String fromDate, String toDate) throws Exception {
        try {
            Date start = dateFormat.parse(fromDate);
            Date end = dateFormat.parse(toDate);
            Date now = new Date();
            
            if (end.after(now)) {
                throw new Exception("End date cannot be in the future");
            }
            
            if (start.after(end)) {
                throw new Exception("Start date must be before end date");
            }
            
            // Check if the date range is too large (e.g., more than 12 months)
            long diffInMillies = Math.abs(end.getTime() - start.getTime());
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
            
            if (diffInDays > 366) {
                throw new Exception("Date range cannot exceed 12 months");
            }
            
        } catch (ParseException e) {
            throw new Exception("Invalid date format. Please use yyyy-MM-dd format.");
        }
    }
    
    /**
     * Calculates the opening balance for a statement.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param pin Account PIN/password
     * @param fromDate Start date of the statement
     * @return Opening balance amount
     */
    private double calculateOpeningBalance(String accountNo, String pin, String fromDate) {
        // Calculate balance as of the day before the from date
        try {
            Date startDate = dateFormat.parse(fromDate);
            Date dayBefore = new Date(startDate.getTime() - 86400000); // 24*60*60*1000
            String asOfDate = dateFormat.format(dayBefore);
            
            return transactionRepository.getBalanceAsOfDate(accountNo, pin, asOfDate);
            
        } catch (ParseException e) {
            logger.log(Level.WARNING, "Error parsing date for opening balance calculation", e);
            // Fall back to querying all transactions before from date
            return transactionRepository.getBalanceAsOfDate(accountNo, pin, fromDate);
        }
    }
    
    /**
     * Calculates closing balance from opening balance and transactions.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param openingBalance Opening balance amount
     * @param transactions List of transactions in the statement
     * @return Closing balance amount
     */
    private double calculateClosingBalance(double openingBalance, List<TransactionRepository.Transaction> transactions) {
        double balance = openingBalance;
        
        for (TransactionRepository.Transaction transaction : transactions) {
            double amount = Double.parseDouble(transaction.getAmount());
            
            if (transaction.getType().equals("Deposit")) {
                balance += amount;
            } else {
                balance -= amount;
            }
        }
        
        return balance;
    }
}