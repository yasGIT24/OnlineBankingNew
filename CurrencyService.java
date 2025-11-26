package banking.management.system;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import org.json.*;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US3]
 * This service handles currency conversion operations.
 * It provides methods to convert between currencies and manage conversion history.
 */
public class CurrencyService {
    
    private final String accountNo;
    private final String pin;
    private final ExchangeRateProvider exchangeRateProvider;
    private final AuditLogger auditLogger;
    private static final Map<String, Currency> CURRENCIES = new HashMap<>();
    
    // Initialize currencies
    static {
        loadCurrencies();
    }
    
    public CurrencyService(String accountNo, String pin) {
        this.accountNo = accountNo;
        this.pin = pin;
        this.exchangeRateProvider = new ExchangeRateProvider();
        this.auditLogger = new AuditLogger();
    }
    
    /**
     * Get all available currencies
     * 
     * @return Map of currency codes to Currency objects
     */
    public static Map<String, Currency> getAllCurrencies() {
        return Collections.unmodifiableMap(CURRENCIES);
    }
    
    /**
     * Convert an amount from one currency to another
     * 
     * @param fromCurrency The source currency code
     * @param toCurrency The target currency code
     * @param amount The amount to convert
     * @return ConversionResult object containing the result and details
     */
    public ConversionResult convertCurrency(String fromCurrency, String toCurrency, double amount) {
        try {
            // Validate currencies
            if (!CURRENCIES.containsKey(fromCurrency)) {
                throw new IllegalArgumentException("Invalid source currency: " + fromCurrency);
            }
            
            if (!CURRENCIES.containsKey(toCurrency)) {
                throw new IllegalArgumentException("Invalid target currency: " + toCurrency);
            }
            
            // Get exchange rate
            double exchangeRate = exchangeRateProvider.getExchangeRate(fromCurrency, toCurrency);
            
            // Calculate converted amount
            double convertedAmount = amount * exchangeRate;
            
            // Create result object
            ConversionResult result = new ConversionResult(
                fromCurrency,
                toCurrency,
                amount,
                convertedAmount,
                exchangeRate,
                LocalDateTime.now()
            );
            
            // Save conversion history
            saveConversionHistory(result);
            
            // Log this activity
            auditLogger.logActivity(accountNo, "CURRENCY_CONVERSION",
                "Converted " + amount + " " + fromCurrency + " to " + convertedAmount + " " + toCurrency);
            
            return result;
        } catch (Exception e) {
            System.out.println("Error converting currency: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Save conversion history to the database
     * 
     * @param result The conversion result to save
     */
    private void saveConversionHistory(ConversionResult result) {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "INSERT INTO conversion_history " +
                          "(account_no, from_currency, to_currency, amount, converted_amount, exchange_rate, conversion_date) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, accountNo);
            pstmt.setString(2, result.getFromCurrency());
            pstmt.setString(3, result.getToCurrency());
            pstmt.setDouble(4, result.getAmount());
            pstmt.setDouble(5, result.getConvertedAmount());
            pstmt.setDouble(6, result.getExchangeRate());
            pstmt.setTimestamp(7, Timestamp.valueOf(result.getConversionDate()));
            
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error saving conversion history: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get conversion history for the current account
     * 
     * @return List of ConversionResult objects
     */
    public List<ConversionResult> getConversionHistory() {
        List<ConversionResult> history = new ArrayList<>();
        
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "SELECT * FROM conversion_history " +
                          "WHERE account_no = ? " +
                          "ORDER BY conversion_date DESC";
            
            PreparedStatement pstmt = c.c.prepareStatement(query);
            pstmt.setString(1, accountNo);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                history.add(new ConversionResult(
                    rs.getString("from_currency"),
                    rs.getString("to_currency"),
                    rs.getDouble("amount"),
                    rs.getDouble("converted_amount"),
                    rs.getDouble("exchange_rate"),
                    rs.getTimestamp("conversion_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error getting conversion history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return history;
    }
    
    /**
     * Ensure necessary tables exist in the database
     * This should be called during application initialization
     */
    public void ensureCurrencyTablesExist() {
        try {
            ConnectionSql c = new ConnectionSql();
            DatabaseMetaData meta = c.c.getMetaData();
            
            // Check if conversion_history table exists
            ResultSet rs = meta.getTables(null, null, "conversion_history", null);
            if (!rs.next()) {
                String createHistoryTable = 
                    "CREATE TABLE conversion_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "account_no VARCHAR(20) NOT NULL, " +
                    "from_currency VARCHAR(3) NOT NULL, " +
                    "to_currency VARCHAR(3) NOT NULL, " +
                    "amount DECIMAL(15,4) NOT NULL, " +
                    "converted_amount DECIMAL(15,4) NOT NULL, " +
                    "exchange_rate DECIMAL(15,6) NOT NULL, " +
                    "conversion_date DATETIME NOT NULL" +
                    ")";
                
                c.s.executeUpdate(createHistoryTable);
            }
        } catch (Exception e) {
            System.out.println("Error creating currency tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load currencies from the built-in list
     * This creates a comprehensive list of global currencies with ISO codes and symbols
     */
    private static void loadCurrencies() {
        try {
            // Load common currencies
            CURRENCIES.put("USD", new Currency("USD", "US Dollar", "$", "United States"));
            CURRENCIES.put("EUR", new Currency("EUR", "Euro", "€", "Eurozone"));
            CURRENCIES.put("GBP", new Currency("GBP", "British Pound", "£", "United Kingdom"));
            CURRENCIES.put("JPY", new Currency("JPY", "Japanese Yen", "¥", "Japan"));
            CURRENCIES.put("AUD", new Currency("AUD", "Australian Dollar", "$", "Australia"));
            CURRENCIES.put("CAD", new Currency("CAD", "Canadian Dollar", "$", "Canada"));
            CURRENCIES.put("CHF", new Currency("CHF", "Swiss Franc", "CHF", "Switzerland"));
            CURRENCIES.put("CNY", new Currency("CNY", "Chinese Yuan", "¥", "China"));
            CURRENCIES.put("HKD", new Currency("HKD", "Hong Kong Dollar", "$", "Hong Kong"));
            CURRENCIES.put("NZD", new Currency("NZD", "New Zealand Dollar", "$", "New Zealand"));
            CURRENCIES.put("SEK", new Currency("SEK", "Swedish Krona", "kr", "Sweden"));
            CURRENCIES.put("KRW", new Currency("KRW", "South Korean Won", "₩", "South Korea"));
            CURRENCIES.put("SGD", new Currency("SGD", "Singapore Dollar", "$", "Singapore"));
            CURRENCIES.put("NOK", new Currency("NOK", "Norwegian Krone", "kr", "Norway"));
            CURRENCIES.put("MXN", new Currency("MXN", "Mexican Peso", "$", "Mexico"));
            CURRENCIES.put("INR", new Currency("INR", "Indian Rupee", "₹", "India"));
            CURRENCIES.put("RUB", new Currency("RUB", "Russian Ruble", "₽", "Russia"));
            CURRENCIES.put("ZAR", new Currency("ZAR", "South African Rand", "R", "South Africa"));
            CURRENCIES.put("TRY", new Currency("TRY", "Turkish Lira", "₺", "Turkey"));
            CURRENCIES.put("BRL", new Currency("BRL", "Brazilian Real", "R$", "Brazil"));
            CURRENCIES.put("TWD", new Currency("TWD", "New Taiwan Dollar", "NT$", "Taiwan"));
            CURRENCIES.put("DKK", new Currency("DKK", "Danish Krone", "kr", "Denmark"));
            CURRENCIES.put("PLN", new Currency("PLN", "Polish Złoty", "zł", "Poland"));
            CURRENCIES.put("THB", new Currency("THB", "Thai Baht", "฿", "Thailand"));
            CURRENCIES.put("IDR", new Currency("IDR", "Indonesian Rupiah", "Rp", "Indonesia"));
            CURRENCIES.put("HUF", new Currency("HUF", "Hungarian Forint", "Ft", "Hungary"));
            CURRENCIES.put("CZK", new Currency("CZK", "Czech Koruna", "Kč", "Czech Republic"));
            CURRENCIES.put("ILS", new Currency("ILS", "Israeli New Shekel", "₪", "Israel"));
            CURRENCIES.put("CLP", new Currency("CLP", "Chilean Peso", "$", "Chile"));
            CURRENCIES.put("PHP", new Currency("PHP", "Philippine Peso", "₱", "Philippines"));
            CURRENCIES.put("AED", new Currency("AED", "United Arab Emirates Dirham", "د.إ", "United Arab Emirates"));
            CURRENCIES.put("COP", new Currency("COP", "Colombian Peso", "$", "Colombia"));
            CURRENCIES.put("SAR", new Currency("SAR", "Saudi Riyal", "ر.س", "Saudi Arabia"));
            CURRENCIES.put("MYR", new Currency("MYR", "Malaysian Ringgit", "RM", "Malaysia"));
            CURRENCIES.put("RON", new Currency("RON", "Romanian Leu", "lei", "Romania"));
            CURRENCIES.put("BGN", new Currency("BGN", "Bulgarian Lev", "лв", "Bulgaria"));
            CURRENCIES.put("ARS", new Currency("ARS", "Argentine Peso", "$", "Argentina"));
            CURRENCIES.put("HRK", new Currency("HRK", "Croatian Kuna", "kn", "Croatia"));
            CURRENCIES.put("VND", new Currency("VND", "Vietnamese Đồng", "₫", "Vietnam"));
            CURRENCIES.put("NGN", new Currency("NGN", "Nigerian Naira", "₦", "Nigeria"));
            CURRENCIES.put("PKR", new Currency("PKR", "Pakistani Rupee", "₨", "Pakistan"));
            CURRENCIES.put("EGP", new Currency("EGP", "Egyptian Pound", "E£", "Egypt"));
            CURRENCIES.put("BDT", new Currency("BDT", "Bangladeshi Taka", "৳", "Bangladesh"));
            CURRENCIES.put("UAH", new Currency("UAH", "Ukrainian Hryvnia", "₴", "Ukraine"));
            CURRENCIES.put("MAD", new Currency("MAD", "Moroccan Dirham", "د.م.", "Morocco"));
            CURRENCIES.put("KES", new Currency("KES", "Kenyan Shilling", "KSh", "Kenya"));
            CURRENCIES.put("QAR", new Currency("QAR", "Qatari Riyal", "ر.ق", "Qatar"));
            CURRENCIES.put("DZD", new Currency("DZD", "Algerian Dinar", "د.ج", "Algeria"));
            CURRENCIES.put("KWD", new Currency("KWD", "Kuwaiti Dinar", "د.ك", "Kuwait"));
            
            // And many more could be added for a truly comprehensive list...
            
        } catch (Exception e) {
            System.out.println("Error loading currencies: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Class representing a currency
     */
    public static class Currency {
        private final String code;
        private final String name;
        private final String symbol;
        private final String country;
        
        public Currency(String code, String name, String symbol, String country) {
            this.code = code;
            this.name = name;
            this.symbol = symbol;
            this.country = country;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getName() {
            return name;
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public String getCountry() {
            return country;
        }
        
        public String getDisplayName() {
            return code + " - " + name + " (" + symbol + ")";
        }
    }
    
    /**
     * Class representing a conversion result
     */
    public static class ConversionResult {
        private final String fromCurrency;
        private final String toCurrency;
        private final double amount;
        private final double convertedAmount;
        private final double exchangeRate;
        private final LocalDateTime conversionDate;
        
        public ConversionResult(String fromCurrency, String toCurrency, 
                              double amount, double convertedAmount, 
                              double exchangeRate, LocalDateTime conversionDate) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
            this.convertedAmount = convertedAmount;
            this.exchangeRate = exchangeRate;
            this.conversionDate = conversionDate;
        }
        
        public String getFromCurrency() {
            return fromCurrency;
        }
        
        public String getToCurrency() {
            return toCurrency;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public double getConvertedAmount() {
            return convertedAmount;
        }
        
        public double getExchangeRate() {
            return exchangeRate;
        }
        
        public LocalDateTime getConversionDate() {
            return conversionDate;
        }
    }
}

/* 
 * Agent Run Identifier: BANK-CURRENCY-20251126
 * Related Test Cases: CURRENCY-001, CURRENCY-002
 */