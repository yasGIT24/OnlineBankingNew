package banking.management.system;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/* [AGENT GENERATED CODE - REQUIREMENT:Download Account Statement in PDF Format]
 * This controller handles the account statement generation and download functionality.
 * It serves as the entry point for PDF statement requests with:
 * 1. Date range validation
 * 2. Security validation
 * 3. PDF generation coordination
 * 4. Secure download link generation
 * 
 * Linked to Value Stream Step: Account Statement Request, Download Delivery
 * Linked to test cases: STMT-01, STMT-02, STMT-03
 */
public class AccountStatementController {
    private static final Logger LOGGER = Logger.getLogger(AccountStatementController.class.getName());
    private final AccountStatementService accountStatementService;
    private final LinkEncryptionService linkEncryptionService;
    
    // Maximum date range in months allowed for statements
    private static final int MAX_DATE_RANGE_MONTHS = 36;
    
    // Directory where statement PDFs will be temporarily stored
    private static final String STATEMENT_DIRECTORY = "temp/statements/";
    
    /**
     * Constructor
     */
    public AccountStatementController() {
        accountStatementService = new AccountStatementService();
        linkEncryptionService = new LinkEncryptionService();
        
        // Create statements directory if it doesn't exist
        File directory = new File(STATEMENT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Process account statement request and generate a secure download link
     * 
     * @param accountNo Account number
     * @param startDateStr Start date string (format: dd-MM-yyyy)
     * @param endDateStr End date string (format: dd-MM-yyyy)
     * @param pin PIN for authorization
     * @return Secure download link or error message
     */
    public String requestAccountStatement(String accountNo, String startDateStr, 
                                       String endDateStr, String pin) {
        
        // Validate input parameters
        if (accountNo == null || accountNo.trim().isEmpty()) {
            return "Error: Account number is required";
        }
        
        if (pin == null || pin.trim().isEmpty()) {
            return "Error: PIN is required for security verification";
        }
        
        // Parse dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date startDate;
        Date endDate;
        
        try {
            if (startDateStr == null || startDateStr.trim().isEmpty()) {
                // Default to 1 month ago
                startDate = getDateMonthsAgo(1);
            } else {
                startDate = dateFormat.parse(startDateStr);
            }
            
            if (endDateStr == null || endDateStr.trim().isEmpty()) {
                // Default to today
                endDate = new Date();
            } else {
                endDate = dateFormat.parse(endDateStr);
            }
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "Invalid date format: {0}", e.getMessage());
            return "Error: Invalid date format. Please use DD-MM-YYYY format";
        }
        
        // Validate date range
        if (endDate.before(startDate)) {
            return "Error: End date cannot be before start date";
        }
        
        // Check if date range exceeds maximum allowed months
        Date maxStartDate = getDateMonthsAgo(MAX_DATE_RANGE_MONTHS);
        if (startDate.before(maxStartDate)) {
            return "Error: Statement history is limited to " + MAX_DATE_RANGE_MONTHS + " months";
        }
        
        try {
            // Generate unique filename for the statement
            String fileName = generateStatementFileName(accountNo);
            String filePath = STATEMENT_DIRECTORY + fileName;
            
            // Generate statement PDF
            boolean success = accountStatementService.generateAccountStatement(
                accountNo, startDate, endDate, filePath, pin);
            
            if (!success) {
                return "Error: Failed to generate statement. Please try again later";
            }
            
            // Log the statement generation for audit
            accountStatementService.logStatementGeneration(accountNo, startDate, endDate);
            
            // Generate secure download link
            String downloadLink = linkEncryptionService.generateEncryptedLink(
                filePath, accountNo, pin);
            
            return downloadLink;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating statement: {0}", e.getMessage());
            return "Error: System error while generating statement. Please try again later";
        }
    }
    
    /**
     * Download an account statement using a secure link
     * 
     * @param encryptedLink Encrypted link from requestAccountStatement
     * @param accountNo Account number for verification
     * @param pin PIN for verification
     * @return Path to the statement file or error message
     */
    public String downloadStatement(String encryptedLink, String accountNo, String pin) {
        try {
            // Decrypt and validate the link
            String filePath = linkEncryptionService.decryptLink(encryptedLink, accountNo, pin);
            
            if (filePath == null) {
                return "Error: Invalid or expired download link";
            }
            
            File statementFile = new File(filePath);
            if (!statementFile.exists() || !statementFile.isFile()) {
                return "Error: Statement file not found";
            }
            
            // Log the download for audit
            accountStatementService.logStatementDownload(accountNo, filePath);
            
            return filePath;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error downloading statement: {0}", e.getMessage());
            return "Error: System error while processing download. Please try again later";
        }
    }
    
    /**
     * Calculate a date N months ago from current date
     * 
     * @param months Number of months ago
     * @return Date object representing the date N months ago
     */
    private Date getDateMonthsAgo(int months) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MONTH, -months);
        return calendar.getTime();
    }
    
    /**
     * Generate a unique filename for the statement
     * 
     * @param accountNo Account number
     * @return Unique filename for the statement
     */
    private String generateStatementFileName(String accountNo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        return "statement_" + accountNo + "_" + datePart + "_" + uniqueId + ".pdf";
    }
}

/* 
 * Test cases:
 * STMT-01: Verify PDF statement generation with complete account details
 * STMT-02: Verify PDF formatting and content
 * STMT-03: Verify secure download link generation and validation
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */