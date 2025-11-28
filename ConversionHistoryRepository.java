package banking.management.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository class for currency conversion history operations.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
 * This class manages the storage and retrieval of currency conversion history
 * for users' reference and auditing purposes.
 */
public class ConversionHistoryRepository {
    
    private static final Logger logger = Logger.getLogger(ConversionHistoryRepository.class.getName());
    
    /**
     * Saves a currency conversion transaction to history.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param accountNo Account number
     * @param fromCurrency Source currency ISO code
     * @param toCurrency Target currency ISO code
     * @param fromAmount Source amount
     * @param toAmount Converted amount
     * @param rate Exchange rate used
     * @return true if save successful
     */
    public boolean saveConversion(String accountNo, String fromCurrency, String toCurrency, 
                                 double fromAmount, double toAmount, double rate) {
        ConnectionSql connection = new ConnectionSql();
        boolean success = false;
        
        try {
            String query = "INSERT INTO currency_conversions " +
                          "(account_no, from_currency, to_currency, from_amount, to_amount, rate, timestamp) " +
                          "VALUES (?, ?, ?, ?, ?, ?, NOW())";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, fromCurrency);
            ps.setString(3, toCurrency);
            ps.setDouble(4, fromAmount);
            ps.setDouble(5, toAmount);
            ps.setDouble(6, rate);
            
            int rowsAffected = ps.executeUpdate();
            success = rowsAffected > 0;
            
            if (success) {
                logger.info("Currency conversion saved for account: " + maskAccountNumber(accountNo));
            } else {
                logger.warning("Failed to save currency conversion for account: " + maskAccountNumber(accountNo));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error saving conversion", e);
        } finally {
            connection.closeConnection();
        }
        
        return success;
    }
    
    /**
     * Retrieves conversion history for an account.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param accountNo Account number
     * @param limit Maximum number of entries to retrieve
     * @return List of conversion history entries
     */
    public List<ConversionRecord> getConversionHistory(String accountNo, int limit) {
        List<ConversionRecord> history = new ArrayList<>();
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "SELECT * FROM currency_conversions WHERE account_no = ? " +
                          "ORDER BY timestamp DESC LIMIT ?";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setInt(2, limit);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ConversionRecord record = new ConversionRecord(
                    rs.getInt("id"),
                    rs.getString("from_currency"),
                    rs.getString("to_currency"),
                    rs.getDouble("from_amount"),
                    rs.getDouble("to_amount"),
                    rs.getDouble("rate"),
                    rs.getTimestamp("timestamp").toString()
                );
                history.add(record);
            }
            
            logger.info("Retrieved " + history.size() + " conversion records for account: " + 
                       maskAccountNumber(accountNo));
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error retrieving conversion history", e);
        } finally {
            connection.closeConnection();
        }
        
        return history;
    }
    
    /**
     * Retrieves popular currency pairs based on usage frequency.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param limit Maximum number of pairs to retrieve
     * @return List of currency pairs with usage count
     */
    public List<CurrencyPairUsage> getPopularCurrencyPairs(int limit) {
        List<CurrencyPairUsage> popularPairs = new ArrayList<>();
        ConnectionSql connection = new ConnectionSql();
        
        try {
            String query = "SELECT from_currency, to_currency, COUNT(*) as count " +
                          "FROM currency_conversions " +
                          "GROUP BY from_currency, to_currency " +
                          "ORDER BY count DESC LIMIT ?";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, limit);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                CurrencyPairUsage pair = new CurrencyPairUsage(
                    rs.getString("from_currency"),
                    rs.getString("to_currency"),
                    rs.getInt("count")
                );
                popularPairs.add(pair);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error retrieving popular currency pairs", e);
        } finally {
            connection.closeConnection();
        }
        
        return popularPairs;
    }
    
    /**
     * Clears conversion history older than specified days.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param accountNo Account number
     * @param days Age threshold in days
     * @return Number of records deleted
     */
    public int clearOldHistory(String accountNo, int days) {
        ConnectionSql connection = new ConnectionSql();
        int recordsDeleted = 0;
        
        try {
            String query = "DELETE FROM currency_conversions WHERE account_no = ? " +
                          "AND timestamp < DATE_SUB(NOW(), INTERVAL ? DAY)";
            
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setInt(2, days);
            
            recordsDeleted = ps.executeUpdate();
            
            logger.info("Cleared " + recordsDeleted + " old conversion records for account: " + 
                       maskAccountNumber(accountNo));
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error clearing old history", e);
        } finally {
            connection.closeConnection();
        }
        
        return recordsDeleted;
    }
    
    /**
     * Masks account number for logging (shows only last 4 digits).
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param accountNo Full account number
     * @return Masked account number
     */
    private String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() <= 4) {
            return "****";
        }
        
        int length = accountNo.length();
        return "****" + accountNo.substring(length - 4, length);
    }
    
    /**
     * Currency conversion record data model.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    public static class ConversionRecord {
        private int id;
        private String fromCurrency;
        private String toCurrency;
        private double fromAmount;
        private double toAmount;
        private double rate;
        private String timestamp;
        
        public ConversionRecord(int id, String fromCurrency, String toCurrency, 
                              double fromAmount, double toAmount, double rate, String timestamp) {
            this.id = id;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.fromAmount = fromAmount;
            this.toAmount = toAmount;
            this.rate = rate;
            this.timestamp = timestamp;
        }
        
        public int getId() { return id; }
        public String getFromCurrency() { return fromCurrency; }
        public String getToCurrency() { return toCurrency; }
        public double getFromAmount() { return fromAmount; }
        public double getToAmount() { return toAmount; }
        public double getRate() { return rate; }
        public String getTimestamp() { return timestamp; }
    }
    
    /**
     * Currency pair usage statistics model.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    public static class CurrencyPairUsage {
        private String fromCurrency;
        private String toCurrency;
        private int usageCount;
        
        public CurrencyPairUsage(String fromCurrency, String toCurrency, int usageCount) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.usageCount = usageCount;
        }
        
        public String getFromCurrency() { return fromCurrency; }
        public String getToCurrency() { return toCurrency; }
        public int getUsageCount() { return usageCount; }
    }
}