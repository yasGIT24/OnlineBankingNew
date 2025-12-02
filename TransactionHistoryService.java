package banking.management.system;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 4: Transaction History]
 * This class implements transaction history functionality with:
 * 1. View 6-month transaction history
 * 2. Filter by date range, transaction type or amount
 * 3. Export options (PDF/Excel format as CSV)
 * 
 * Linked to Value Stream Step: Transaction History Retrieval
 * Linked to test cases: HIST-01, HIST-02, HIST-03, HIST-04
 */
public class TransactionHistoryService {
    private final ConnectionSql connectionSql;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(TransactionHistoryService.class.getName());
    
    // Transaction types
    public static final String TYPE_ALL = "All";
    public static final String TYPE_DEPOSIT = "Deposit";
    public static final String TYPE_WITHDRAWAL = "Withdrawal";
    public static final String TYPE_TRANSFER_OUT = "Transfer Out";
    public static final String TYPE_TRANSFER_IN = "Transfer In";
    
    /**
     * Constructor
     */
    public TransactionHistoryService() {
        connectionSql = new ConnectionSql();
        connection = connectionSql.c;
        if (connection == null) {
            LOGGER.log(Level.SEVERE, "Database connection failed in TransactionHistoryService");
        }
    }
    
    /**
     * Get transaction history for an account with optional filters
     * 
     * @param accountNo Account number
     * @param startDate Start date (null for 6 months ago)
     * @param endDate End date (null for today)
     * @param type Transaction type (null for all)
     * @param minAmount Minimum amount (0.0 for no minimum)
     * @return List of Transaction objects
     */
    // [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-01]
    // Updated method signature to support PDF statement generation
    public List<Transaction> getTransactionHistory(String accountNo, Date startDate, Date endDate, 
                                            String type, Double minAmount) {
        return getTransactionHistory(accountNo, startDate, endDate, type, minAmount, null, null);
    }

    /**
     * Get transaction history for an account with optional filters
     * 
     * @param accountNo Account number
     * @param startDate Start date (null for 6 months ago)
     * @param endDate End date (null for today)
     * @param type Transaction type (null for all)
     * @param minAmount Minimum amount (null for no minimum)
     * @param maxAmount Maximum amount (null for no maximum)
     * @param pin PIN for authorization
     * @return List of Transaction objects
     */
    public List<Transaction> getTransactionHistory(String accountNo, Date startDate, Date endDate, 
                                                String type, Double minAmount, Double maxAmount,
                                                String pin) {
        
        List<Transaction> transactions = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Validate PIN for account if provided
            if (pin != null && !pin.trim().isEmpty()) {
                if (!validatePin(accountNo, pin)) {
                    LOGGER.log(Level.WARNING, "Invalid PIN attempted for account: {0}", accountNo);
                    return transactions; // Return empty list on invalid PIN
                }
            }
            
            // Set default dates if not provided (6 months ago to today)
            Calendar calendar = Calendar.getInstance();
            if (endDate == null) {
                endDate = new Date(calendar.getTimeInMillis());
            }
            
            if (startDate == null) {
                calendar.add(Calendar.MONTH, -6);
                startDate = new Date(calendar.getTimeInMillis());
            }
            
            SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDateStr = sqlDateFormat.format(startDate);
            String endDateStr = sqlDateFormat.format(endDate);
            
            // Prepare base query
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM bank WHERE Account_No = ? AND date BETWEEN ? AND ?");
            
            // Add type filter if provided
            if (type != null && !type.equals(TYPE_ALL)) {
                if (type.equals(TYPE_DEPOSIT)) {
                    queryBuilder.append(" AND type = 'Deposit'");
                } else if (type.equals(TYPE_WITHDRAWAL)) {
                    queryBuilder.append(" AND type NOT LIKE 'Deposit%' AND type NOT LIKE 'Transfer from%'");
                } else if (type.equals(TYPE_TRANSFER_OUT)) {
                    queryBuilder.append(" AND type LIKE 'Transfer to%'");
                } else if (type.equals(TYPE_TRANSFER_IN)) {
                    queryBuilder.append(" AND type LIKE 'Transfer from%'");
                }
            }
            
            // Add amount filters if provided
            if (minAmount != null) {
                queryBuilder.append(" AND CAST(amount AS DECIMAL) >= ?");
            }
            
            if (maxAmount != null) {
                queryBuilder.append(" AND CAST(amount AS DECIMAL) <= ?");
            }
            
            // Order by date, most recent first
            queryBuilder.append(" ORDER BY date DESC");
            
            // Prepare statement
            ps = connection.prepareStatement(queryBuilder.toString());
            
            // Set parameters
            int paramIndex = 1;
            ps.setString(paramIndex++, accountNo);
            ps.setString(paramIndex++, startDateStr);
            ps.setString(paramIndex++, endDateStr);
            
            // Set amount filters if provided
            if (minAmount != null) {
                ps.setDouble(paramIndex++, minAmount);
            }
            
            if (maxAmount != null) {
                ps.setDouble(paramIndex++, maxAmount);
            }
            
            // Execute query
            rs = ps.executeQuery();
            
            // Process results
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setAccountNo(rs.getString("Account_No"));
                transaction.setDate(rs.getString("date"));
                transaction.setType(rs.getString("type"));
                transaction.setAmount(rs.getString("amount"));
                
                // Determine if credit or debit
                String type = rs.getString("type");
                boolean isCredit = type.equals("Deposit") || type.startsWith("Transfer from");
                transaction.setCredit(isCredit);
                
                // Set the current balance (would require additional calculation in real scenario)
                transaction.setReference(generateReferenceFromInfo(rs.getString("Account_No"), 
                                                               rs.getString("date"), 
                                                               rs.getString("type")));
                
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during transaction history retrieval: {0}", e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
        
        // Calculate running balance for each transaction
        calculateRunningBalance(transactions);
        
        return transactions;
    }
    
    /**
     * Calculate running balance for each transaction in the list
     * 
     * @param transactions List of transactions
     */
    private void calculateRunningBalance(List<Transaction> transactions) {
        // Sort transactions by date, oldest first
        Collections.sort(transactions, (t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        
        double balance = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.isCredit()) {
                balance += Double.parseDouble(transaction.getAmount());
            } else {
                balance -= Double.parseDouble(transaction.getAmount());
            }
            transaction.setBalance(String.valueOf(balance));
        }
        
        // Sort transactions by date, newest first (for display)
        Collections.sort(transactions, (t1, t2) -> t2.getDate().compareTo(t1.getDate()));
    }
    
    /**
     * Export transactions to CSV file
     * 
     * @param transactions List of transactions to export
     * @param filePath Path to export file
     * @return true if successful, false otherwise
     */
    public boolean exportToCSV(List<Transaction> transactions, String filePath) {
        try {
            FileWriter writer = new FileWriter(new File(filePath));
            
            // Write header
            writer.write("Date,Type,Reference,Amount,Balance\n");
            
            // Write transactions
            for (Transaction transaction : transactions) {
                StringBuilder line = new StringBuilder();
                line.append(transaction.getDate()).append(",");
                line.append(transaction.getType()).append(",");
                line.append(transaction.getReference()).append(",");
                
                if (transaction.isCredit()) {
                    line.append(transaction.getAmount());
                } else {
                    line.append("-").append(transaction.getAmount());
                }
                line.append(",");
                
                line.append(transaction.getBalance()).append("\n");
                
                writer.write(line.toString());
            }
            
            writer.close();
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error exporting to CSV: {0}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Export transactions to Excel (actually CSV format for simplicity)
     * 
     * @param transactions List of transactions to export
     * @param filePath Path to export file
     * @return true if successful, false otherwise
     */
    public boolean exportToExcel(List<Transaction> transactions, String filePath) {
        // For simplicity, we're using CSV format for Excel
        return exportToCSV(transactions, filePath);
    }
    
    /**
     * Generate a PDF file with transaction history
     * 
     * @param transactions List of transactions to export
     * @param filePath Path to export file
     * @return true if successful, false otherwise
     */
    // [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-02]
    // Implemented PDF export by integrating with StatementPdfService
    public boolean exportToPDF(List<Transaction> transactions, String filePath) {
        try {
            // Get the account number from the first transaction
            if (transactions.isEmpty()) {
                LOGGER.log(Level.WARNING, "No transactions to export to PDF");
                return false;
            }
            
            String accountNo = transactions.get(0).getAccountNo();
            
            // Create a StatementPdfService instance
            StatementPdfService pdfService = new StatementPdfService();
            
            // Determine date range from transactions
            Date startDate = null;
            Date endDate = null;
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Transaction transaction : transactions) {
                try {
                    Date transactionDate = dateFormat.parse(transaction.getDate());
                    if (startDate == null || transactionDate.before(startDate)) {
                        startDate = transactionDate;
                    }
                    if (endDate == null || transactionDate.after(endDate)) {
                        endDate = transactionDate;
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error parsing transaction date: {0}", e.getMessage());
                }
            }
            
            // If dates couldn't be determined, use default values
            if (startDate == null) {
                startDate = new Date(System.currentTimeMillis() - (180 * 24 * 60 * 60 * 1000L)); // 180 days ago
            }
            if (endDate == null) {
                endDate = new Date();
            }
            
            // Generate PDF statement
            StatementPdfService.EncryptedDownloadLink downloadLink = 
                pdfService.generateStatement(accountNo, startDate, endDate);
            
            // Copy the generated PDF to the requested file path
            if (downloadLink != null) {
                File sourceFile = new File(pdfService.getPdfDirectory() + "/" + downloadLink.getFileName());
                File destFile = new File(filePath);
                
                // Create necessary directories
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                
                // Copy file
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error exporting to PDF: {0}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate PIN for a given account
     * 
     * @param accountNo Account number
     * @param pin PIN to validate
     * @return true if PIN is valid, false otherwise
     */
    private boolean validatePin(String accountNo, String pin) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String query = "SELECT * FROM login WHERE Account_No = ? AND Login_Password = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating PIN: {0}", e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Generate a reference number based on transaction info
     * 
     * @param accountNo Account number
     * @param date Transaction date
     * @param type Transaction type
     * @return Reference number
     */
    private String generateReferenceFromInfo(String accountNo, String date, String type) {
        // In a real system, references would be stored in the database
        // For this example, we'll generate one from the transaction info
        
        // Extract date components
        String dateStr = date.replaceAll("[^0-9]", "");
        if (dateStr.length() > 8) {
            dateStr = dateStr.substring(0, 8);
        }
        
        // Generate type code
        String typeCode = "TX";
        if (type.startsWith("Deposit")) {
            typeCode = "DEP";
        } else if (type.startsWith("Transfer to")) {
            typeCode = "TRO";
        } else if (type.startsWith("Transfer from")) {
            typeCode = "TRI";
        } else if (type.startsWith("Withdrawal")) {
            typeCode = "WDR";
        }
        
        // Last 4 digits of account
        String accSuffix = accountNo;
        if (accountNo.length() > 4) {
            accSuffix = accountNo.substring(accountNo.length() - 4);
        }
        
        // Generate reference
        return typeCode + "-" + dateStr + "-" + accSuffix;
    }
    
    /**
     * Close database resources
     */
    public void closeConnection() {
        connectionSql.closeConnection();
    }
    
    /**
     * Transaction class to store transaction data
     */
    public static class Transaction {
        private String accountNo;
        private String date;
        private String type;
        private String amount;
        private String balance;
        private boolean isCredit;
        private String reference;
        
        // Getters and setters
        public String getAccountNo() { return accountNo; }
        public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getAmount() { return amount; }
        public void setAmount(String amount) { this.amount = amount; }
        
        public String getBalance() { return balance; }
        public void setBalance(String balance) { this.balance = balance; }
        
        public boolean isCredit() { return isCredit; }
        public void setCredit(boolean isCredit) { this.isCredit = isCredit; }
        
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
    }
}

/* 
 * Test cases:
 * HIST-01: Verify 6-month transaction history retrieval
 * HIST-02: Verify filtering by date range, type and amount
 * HIST-03: Verify running balance calculation
 * HIST-04: Verify export to CSV/Excel
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */

// [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-01, REQ-PDF-02]
// This file has been updated to integrate with StatementPdfService for PDF statement generation.
// Changes include:
// 1. New overloaded getTransactionHistory method to support PDF statements
// 2. Implementation of the exportToPDF method to generate PDF statements with transaction data
// Agent run identifier: AGENT-PDF-GEN-2025-12-02