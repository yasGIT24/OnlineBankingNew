package banking.management.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Repository class for accessing transaction data.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
 * This class provides data access methods for retrieving transaction history
 * for PDF statement generation and other transaction-related operations.
 */
public class TransactionRepository {
    
    private static final Logger logger = Logger.getLogger(TransactionRepository.class.getName());
    
    /**
     * Retrieves transactions for an account within a date range.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param pin Login PIN/password
     * @param fromDate Start date in format yyyy-MM-dd
     * @param toDate End date in format yyyy-MM-dd
     * @return List of transactions
     */
    public List<Transaction> getTransactionsByDateRange(String accountNo, String pin, String fromDate, String toDate) {
        List<Transaction> transactions = new ArrayList<>();
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "SELECT * FROM bank WHERE Account_No = ? AND Login_Password = ? " +
                           "AND date BETWEEN ? AND ? ORDER BY date, time";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            ps.setString(3, fromDate);
            ps.setString(4, toDate);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getString("Account_No"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getString("type"),
                    rs.getString("amount"),
                    rs.getString("number")
                );
                transactions.add(transaction);
            }
            
            logger.info("Retrieved " + transactions.size() + " transactions for account ending with " + 
                    accountNo.substring(Math.max(0, accountNo.length() - 4)));
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving transactions", e);
        } finally {
            connection.closeConnection();
        }
        
        return transactions;
    }
    
    /**
     * Retrieves account holder information for statement header.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @return AccountInfo object with account holder details
     */
    public AccountInfo getAccountInfo(String accountNo) {
        AccountInfo accountInfo = null;
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "SELECT s1.name, s1.address, s1.city, s1.state, s3.account_type, " +
                          "s3.services, s3.card_number FROM signup1 s1 " +
                          "JOIN signup3 s3 ON s1.form_no = s3.form_no " +
                          "WHERE s3.Account_No = ?";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                accountInfo = new AccountInfo(
                    rs.getString("name"),
                    accountNo,
                    rs.getString("address") + ", " + rs.getString("city") + ", " + rs.getString("state"),
                    rs.getString("account_type"),
                    rs.getString("card_number")
                );
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving account information", e);
        } finally {
            connection.closeConnection();
        }
        
        return accountInfo;
    }
    
    /**
     * Calculates account balance as of a specific date.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param pin Login PIN/password
     * @param asOfDate Date to calculate balance as of (yyyy-MM-dd)
     * @return Current balance
     */
    public double getBalanceAsOfDate(String accountNo, String pin, String asOfDate) {
        double balance = 0.0;
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "SELECT type, amount FROM bank WHERE Account_No = ? AND Login_Password = ? " +
                           "AND date <= ?";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            ps.setString(3, asOfDate);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = Double.parseDouble(rs.getString("amount"));
                
                if (type.equals("Deposit")) {
                    balance += amount;
                } else {
                    balance -= amount;
                }
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error calculating balance", e);
        } finally {
            connection.closeConnection();
        }
        
        return balance;
    }
    
    /**
     * Logs statement download activity.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param accountNo Account number
     * @param fromDate Start date
     * @param toDate End date
     * @param downloadToken Download token
     */
    public void logStatementDownload(String accountNo, String fromDate, String toDate, String downloadToken) {
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "INSERT INTO statement_downloads (account_no, from_date, to_date, download_token, timestamp) " +
                           "VALUES (?, ?, ?, ?, NOW())";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, fromDate);
            ps.setString(3, toDate);
            ps.setString(4, downloadToken);
            
            ps.executeUpdate();
            
            logger.info("Statement download logged for account ending with " + 
                    accountNo.substring(Math.max(0, accountNo.length() - 4)));
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging statement download", e);
        } finally {
            connection.closeConnection();
        }
    }
    
    /**
     * Transaction data model class.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     */
    public static class Transaction {
        private String accountNo;
        private String date;
        private String time;
        private String type;
        private String amount;
        private String reference;
        
        public Transaction(String accountNo, String date, String time, String type, String amount, String reference) {
            this.accountNo = accountNo;
            this.date = date;
            this.time = time;
            this.type = type;
            this.amount = amount;
            this.reference = reference;
        }
        
        public String getAccountNo() { return accountNo; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getType() { return type; }
        public String getAmount() { return amount; }
        public String getReference() { return reference; }
    }
    
    /**
     * Account information data model class.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     */
    public static class AccountInfo {
        private String accountHolderName;
        private String accountNumber;
        private String address;
        private String accountType;
        private String cardNumber;
        
        public AccountInfo(String accountHolderName, String accountNumber, String address, 
                          String accountType, String cardNumber) {
            this.accountHolderName = accountHolderName;
            this.accountNumber = accountNumber;
            this.address = address;
            this.accountType = accountType;
            this.cardNumber = cardNumber;
        }
        
        public String getAccountHolderName() { return accountHolderName; }
        public String getAccountNumber() { return accountNumber; }
        public String getAddress() { return address; }
        public String getAccountType() { return accountType; }
        public String getCardNumber() { return cardNumber; }
    }
}