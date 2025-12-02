package banking.management.system;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.io.File;

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

    /* [AGENT GENERATED CODE - REQUIREMENT:Download Account Statement in PDF Format]
     * Enhanced method to retrieve transactions with account details for PDF statement generation
     * This method extends the original getTransactionHistory to include additional account information
     * needed for complete statement generation.
     */
    public Map<String, Object> getAccountStatementData(String accountNo, Date startDate, Date endDate, String pin) {
        Map<String, Object> statementData = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Validate PIN for security
            if (!validatePin(accountNo, pin)) {
                LOGGER.log(Level.WARNING, "Invalid PIN provided for account statement: {0}", accountNo);
                return statementData; // Return empty map on invalid PIN
            }
            
            // Get basic account information
            String query = "SELECT l.name, l.Account_No, l.Account_Type FROM login l WHERE l.Account_No = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                statementData.put("accountHolder", rs.getString("name"));
                statementData.put("accountNumber", rs.getString("Account_No"));
                statementData.put("accountType", rs.getString("Account_Type"));
            } else {
                LOGGER.log(Level.WARNING, "Account not found: {0}", accountNo);
                return statementData; // Return empty map if account not found
            }
            
            rs.close();
            ps.close();
            
            // Get customer address from signup tables
            query = "SELECT s1.formno, s1.name, s1.fname, s1.email, s2.address, s2.city, s2.state, s2.pin " +
                    "FROM signup1 s1 JOIN signup2 s2 ON s1.formno = s2.formno " +
                    "JOIN login l ON s1.formno = l.formno WHERE l.Account_No = ?";
            ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Map<String, String> customerDetails = new HashMap<>();
                customerDetails.put("name", rs.getString("name"));
                customerDetails.put("fatherName", rs.getString("fname"));
                customerDetails.put("email", rs.getString("email"));
                customerDetails.put("address", rs.getString("address"));
                customerDetails.put("city", rs.getString("city"));
                customerDetails.put("state", rs.getString("state"));
                customerDetails.put("pinCode", rs.getString("pin"));
                statementData.put("customerDetails", customerDetails);
            }
            
            // Get transactions within date range
            List<Transaction> transactions = getTransactionHistory(accountNo, startDate, endDate, TYPE_ALL, null, null, pin);
            statementData.put("transactions", transactions);
            
            // Calculate summary information
            double totalDeposits = 0;
            double totalWithdrawals = 0;
            for (Transaction transaction : transactions) {
                double amount = Double.parseDouble(transaction.getAmount());
                if (transaction.isCredit()) {
                    totalDeposits += amount;
                } else {
                    totalWithdrawals += amount;
                }
            }
            
            Map<String, Double> summary = new HashMap<>();
            summary.put("totalDeposits", totalDeposits);
            summary.put("totalWithdrawals", totalWithdrawals);
            summary.put("netChange", totalDeposits - totalWithdrawals);
            
            // Get current balance
            if (!transactions.isEmpty()) {
                summary.put("closingBalance", Double.parseDouble(transactions.get(0).getBalance()));
                summary.put("openingBalance", calculateOpeningBalance(transactions));
            }
            
            statementData.put("summary", summary);
            
            // Add statement period
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            statementData.put("startDate", dateFormat.format(startDate));
            statementData.put("endDate", dateFormat.format(endDate));
            statementData.put("generatedDate", dateFormat.format(new Date()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error retrieving account statement data: {0}", e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
        
        return statementData;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:Download Account Statement in PDF Format]
     * Calculate the opening balance for the statement period
     */
    private double calculateOpeningBalance(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            return 0.0;
        }
        
        // Sort by date, oldest first
        List<Transaction> sortedTransactions = new ArrayList<>(transactions);
        Collections.sort(sortedTransactions, (t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        
        // Get first transaction and its balance
        Transaction firstTransaction = sortedTransactions.get(0);
        double currentBalance = Double.parseDouble(firstTransaction.getBalance());
        
        // Subtract/add the first transaction to get opening balance
        if (firstTransaction.isCredit()) {
            currentBalance -= Double.parseDouble(firstTransaction.getAmount());
        } else {
            currentBalance += Double.parseDouble(firstTransaction.getAmount());
        }
        
        return currentBalance;
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
    
    /* [AGENT GENERATED CODE - REQUIREMENT:Download Account Statement in PDF Format]
     * Generate a PDF file with transaction history using PDFGeneratorUtil
     * This method has been updated to use the actual PDF generation functionality
     */
    public boolean exportToPDF(List<Transaction> transactions, String filePath) {
        try {
            // Create account statement data map with minimal data for backward compatibility
            Map<String, Object> statementData = new HashMap<>();
            statementData.put("transactions", transactions);
            statementData.put("generatedDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            
            // Use PDFGeneratorUtil to generate PDF
            PDFGeneratorUtil pdfGenerator = new PDFGeneratorUtil();
            return pdfGenerator.generateTransactionPDF(statementData, filePath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating PDF: {0}", e.getMessage());
            return false;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:Download Account Statement in PDF Format]
     * Generate a complete account statement PDF with all details
     */
    public boolean generateAccountStatementPDF(String accountNo, Date startDate, Date endDate, 
                                            String filePath, String pin) {
        try {
            // Get all statement data including account details and transactions
            Map<String, Object> statementData = getAccountStatementData(accountNo, startDate, endDate, pin);
            
            if (statementData.isEmpty() || !statementData.containsKey("transactions")) {
                LOGGER.log(Level.WARNING, "No data available for account statement");
                return false;
            }
            
            // Use PDFGeneratorUtil to generate PDF
            PDFGeneratorUtil pdfGenerator = new PDFGeneratorUtil();
            return pdfGenerator.generateStatementPDF(statementData, filePath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating account statement PDF: {0}", e.getMessage());
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
 * STMT-01: Verify PDF statement generation with complete account details
 * STMT-02: Verify PDF formatting and content
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */