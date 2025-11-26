package banking.management.system;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US2]
 * This service handles recurring transactions for digital wallets.
 * It provides methods to schedule, process, and manage recurring transactions.
 */
public class RecurringTransactionService {
    
    private final AuditLogger auditLogger;
    private final WalletIntegrationService walletService;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    public RecurringTransactionService(String accountNo, String pin) {
        this.auditLogger = new AuditLogger();
        this.walletService = new WalletIntegrationService(accountNo, pin);
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Start the recurring transaction processor
     * This should be called during application initialization
     */
    public void startScheduler() {
        if (isRunning.compareAndSet(false, true)) {
            // Schedule the recurring transaction processor to run every hour
            scheduler.scheduleAtFixedRate(this::processRecurringTransactions, 0, 1, TimeUnit.HOURS);
        }
    }
    
    /**
     * Stop the recurring transaction processor
     * This should be called during application shutdown
     */
    public void stopScheduler() {
        if (isRunning.compareAndSet(true, false)) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Process pending recurring transactions
     * This method checks for and processes all pending recurring transactions
     */
    private void processRecurringTransactions() {
        try {
            ConnectionSql c = new ConnectionSql();
            Date currentDate = new Date();
            
            // Get all active recurring transactions that are due today
            String query = "SELECT * FROM recurring_transactions " +
                          "WHERE is_active = TRUE " +
                          "AND (end_date IS NULL OR end_date >= CURRENT_DATE()) " +
                          "AND start_date <= CURRENT_DATE()";
            
            Statement stmt = c.c.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String id = rs.getString("id");
                String accountNo = rs.getString("account_no");
                String walletType = rs.getString("wallet_type");
                String walletId = rs.getString("wallet_id");
                double amount = rs.getDouble("amount");
                String description = rs.getString("description");
                String frequency = rs.getString("frequency");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                
                // Check if this transaction should run today based on frequency
                if (shouldRunToday(startDate, frequency)) {
                    // Check if this transaction has already run today
                    if (!hasRunToday(id)) {
                        // Process the transaction
                        boolean success = walletService.processWalletTransaction(
                            walletType, amount, description + " (Recurring)");
                        
                        if (success) {
                            // Log the successful transaction
                            logRecurringTransaction(id, accountNo, walletType, walletId, amount);
                            
                            // Check if this was the last scheduled transaction
                            if (isLastTransaction(frequency, currentDate, endDate)) {
                                // Mark the recurring transaction as inactive
                                deactivateRecurringTransaction(id);
                            }
                        } else {
                            // Log the failed transaction
                            logFailedTransaction(id, accountNo, walletType, walletId, amount);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing recurring transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if a transaction should run today based on its frequency
     * 
     * @param startDate The start date of the recurring transaction
     * @param frequency The frequency of the recurring transaction
     * @return boolean indicating if the transaction should run today
     */
    private boolean shouldRunToday(Date startDate, String frequency) {
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        
        Calendar today = Calendar.getInstance();
        
        switch (frequency) {
            case "DAILY":
                return true;
            case "WEEKLY":
                // Check if today is the same day of the week as the start date
                return today.get(Calendar.DAY_OF_WEEK) == start.get(Calendar.DAY_OF_WEEK);
            case "MONTHLY":
                // Check if today is the same day of the month as the start date
                return today.get(Calendar.DAY_OF_MONTH) == start.get(Calendar.DAY_OF_MONTH);
            default:
                return false;
        }
    }
    
    /**
     * Check if a recurring transaction has already run today
     * 
     * @param recurringId The ID of the recurring transaction
     * @return boolean indicating if the transaction has run today
     */
    private boolean hasRunToday(String recurringId) {
        try {
            ConnectionSql c = new ConnectionSql();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            
            String query = "SELECT COUNT(*) FROM recurring_transaction_log " +
                          "WHERE recurring_transaction_id = ? " +
                          "AND DATE(transaction_date) = ?";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, recurringId);
            pstmt.setString(2, today);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("Error checking if transaction has run today: " + e.getMessage());
            e.printStackTrace();
        }
        
        // If there was an error, assume it has run to prevent duplicate transactions
        return true;
    }
    
    /**
     * Log a successful recurring transaction
     * 
     * @param recurringId The ID of the recurring transaction
     * @param accountNo The account number
     * @param walletType The wallet type
     * @param walletId The wallet ID
     * @param amount The transaction amount
     */
    private void logRecurringTransaction(String recurringId, String accountNo, 
                                         String walletType, String walletId, double amount) {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "INSERT INTO recurring_transaction_log " +
                          "(recurring_transaction_id, account_no, wallet_type, wallet_id, amount, status, transaction_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, recurringId);
            pstmt.setString(2, accountNo);
            pstmt.setString(3, walletType);
            pstmt.setString(4, walletId);
            pstmt.setDouble(5, amount);
            pstmt.setString(6, "SUCCESS");
            
            pstmt.executeUpdate();
            
            // Log this activity
            auditLogger.logActivity(accountNo, "RECURRING_TRANSACTION_EXECUTED", 
                "Recurring transaction processed successfully: " + walletType + " for " + amount);
        } catch (Exception e) {
            System.out.println("Error logging recurring transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Log a failed recurring transaction
     * 
     * @param recurringId The ID of the recurring transaction
     * @param accountNo The account number
     * @param walletType The wallet type
     * @param walletId The wallet ID
     * @param amount The transaction amount
     */
    private void logFailedTransaction(String recurringId, String accountNo, 
                                      String walletType, String walletId, double amount) {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "INSERT INTO recurring_transaction_log " +
                          "(recurring_transaction_id, account_no, wallet_type, wallet_id, amount, status, transaction_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, recurringId);
            pstmt.setString(2, accountNo);
            pstmt.setString(3, walletType);
            pstmt.setString(4, walletId);
            pstmt.setDouble(5, amount);
            pstmt.setString(6, "FAILED");
            
            pstmt.executeUpdate();
            
            // Log this activity
            auditLogger.logActivity(accountNo, "RECURRING_TRANSACTION_FAILED", 
                "Recurring transaction failed: " + walletType + " for " + amount);
        } catch (Exception e) {
            System.out.println("Error logging failed transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if this is the last scheduled transaction
     * 
     * @param frequency The frequency of the recurring transaction
     * @param currentDate The current date
     * @param endDate The end date of the recurring transaction
     * @return boolean indicating if this is the last transaction
     */
    private boolean isLastTransaction(String frequency, Date currentDate, Date endDate) {
        if (endDate == null) {
            return false;
        }
        
        Calendar current = Calendar.getInstance();
        current.setTime(currentDate);
        
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        
        switch (frequency) {
            case "DAILY":
                // Check if today is the end date
                return current.get(Calendar.YEAR) == end.get(Calendar.YEAR) &&
                       current.get(Calendar.DAY_OF_YEAR) == end.get(Calendar.DAY_OF_YEAR);
            case "WEEKLY":
                // Check if today is the end date or within 7 days of it
                Calendar weekLater = (Calendar) current.clone();
                weekLater.add(Calendar.DAY_OF_MONTH, 7);
                return !end.after(weekLater) && !end.before(current);
            case "MONTHLY":
                // Check if today is the end date or within 31 days of it
                Calendar monthLater = (Calendar) current.clone();
                monthLater.add(Calendar.MONTH, 1);
                return !end.after(monthLater) && !end.before(current);
            default:
                return false;
        }
    }
    
    /**
     * Deactivate a recurring transaction
     * 
     * @param recurringId The ID of the recurring transaction
     */
    private void deactivateRecurringTransaction(String recurringId) {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "UPDATE recurring_transactions SET is_active = FALSE WHERE id = ?";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, recurringId);
            
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error deactivating recurring transaction: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all recurring transactions for an account
     * 
     * @param accountNo The account number
     * @return List of recurring transaction details
     */
    public List<RecurringTransaction> getRecurringTransactions(String accountNo) {
        List<RecurringTransaction> transactions = new ArrayList<>();
        
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "SELECT * FROM recurring_transactions WHERE account_no = ? ORDER BY start_date DESC";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, accountNo);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(new RecurringTransaction(
                    rs.getString("id"),
                    rs.getString("wallet_type"),
                    rs.getString("wallet_id"),
                    rs.getDouble("amount"),
                    rs.getString("description"),
                    rs.getString("frequency"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getBoolean("is_active")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error getting recurring transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Cancel a recurring transaction
     * 
     * @param accountNo The account number
     * @param recurringId The ID of the recurring transaction
     * @return boolean indicating success or failure
     */
    public boolean cancelRecurringTransaction(String accountNo, String recurringId) {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "UPDATE recurring_transactions SET is_active = FALSE " +
                          "WHERE id = ? AND account_no = ?";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, recurringId);
            pstmt.setString(2, accountNo);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                // Log this activity
                auditLogger.logActivity(accountNo, "RECURRING_TRANSACTION_CANCELLED", 
                    "Recurring transaction cancelled: " + recurringId);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error cancelling recurring transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Ensure necessary tables exist in the database
     * This should be called during application initialization
     */
    public void ensureRecurringTransactionTablesExist() {
        try {
            ConnectionSql c = new ConnectionSql();
            DatabaseMetaData meta = c.c.getMetaData();
            
            // Check if recurring_transaction_log table exists
            ResultSet rs = meta.getTables(null, null, "recurring_transaction_log", null);
            if (!rs.next()) {
                String createLogTable = 
                    "CREATE TABLE recurring_transaction_log (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "recurring_transaction_id VARCHAR(50) NOT NULL, " +
                    "account_no VARCHAR(20) NOT NULL, " +
                    "wallet_type VARCHAR(50) NOT NULL, " +
                    "wallet_id VARCHAR(100) NOT NULL, " +
                    "amount DECIMAL(10,2) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL, " +
                    "transaction_date DATETIME NOT NULL" +
                    ")";
                
                c.s.executeUpdate(createLogTable);
            }
        } catch (Exception e) {
            System.out.println("Error creating recurring transaction tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Class representing a recurring transaction
     */
    public static class RecurringTransaction {
        private final String id;
        private final String walletType;
        private final String walletId;
        private final double amount;
        private final String description;
        private final String frequency;
        private final Date startDate;
        private final Date endDate;
        private final boolean isActive;
        
        public RecurringTransaction(String id, String walletType, String walletId, double amount, 
                                   String description, String frequency, Date startDate, 
                                   Date endDate, boolean isActive) {
            this.id = id;
            this.walletType = walletType;
            this.walletId = walletId;
            this.amount = amount;
            this.description = description;
            this.frequency = frequency;
            this.startDate = startDate;
            this.endDate = endDate;
            this.isActive = isActive;
        }
        
        public String getId() {
            return id;
        }
        
        public String getWalletType() {
            return walletType;
        }
        
        public String getWalletId() {
            return walletId;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getFrequency() {
            return frequency;
        }
        
        public Date getStartDate() {
            return startDate;
        }
        
        public Date getEndDate() {
            return endDate;
        }
        
        public boolean isActive() {
            return isActive;
        }
    }
}

/* 
 * Agent Run Identifier: BANK-RECURRING-20251126
 * Related Test Cases: RECURRING-001, RECURRING-002
 */