package banking.management.system;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Currency;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for currency conversion operations.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
 * This class provides functionality for currency conversion with comprehensive
 * currency metadata and precise calculations.
 */
public class CurrencyService {
    
    private static final Logger logger = Logger.getLogger(CurrencyService.class.getName());
    private final ExchangeRateAPI exchangeRateAPI;
    private final ConversionHistoryRepository historyRepository;
    private final Map<String, CurrencyInfo> currencyMetadata;
    
    /**
     * Constructor initializes required services and currency metadata.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    public CurrencyService() {
        this.exchangeRateAPI = new ExchangeRateAPI();
        this.historyRepository = new ConversionHistoryRepository();
        this.currencyMetadata = initializeCurrencyMetadata();
    }
    
    /**
     * Performs currency conversion calculation.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param accountNo Account number for history tracking
     * @param fromCurrency Source currency ISO code
     * @param toCurrency Target currency ISO code
     * @param amount Amount to convert
     * @return ConversionResult with conversion details
     * @throws Exception If conversion fails
     */
    public ConversionResult convertCurrency(String accountNo, String fromCurrency, String toCurrency, 
                                           double amount) throws Exception {
        // Validate currencies
        validateCurrency(fromCurrency);
        validateCurrency(toCurrency);
        
        // Validate amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        // Get exchange rate
        double rate = exchangeRateAPI.getConversionRate(fromCurrency, toCurrency);
        
        // Calculate converted amount with precision
        BigDecimal amountBd = BigDecimal.valueOf(amount);
        BigDecimal rateBd = BigDecimal.valueOf(rate);
        BigDecimal resultBd = amountBd.multiply(rateBd);
        
        // Round according to currency decimals
        int decimals = getDecimalPlaces(toCurrency);
        resultBd = resultBd.setScale(decimals, RoundingMode.HALF_UP);
        
        double convertedAmount = resultBd.doubleValue();
        
        // Create result
        ConversionResult result = new ConversionResult(
                fromCurrency,
                toCurrency,
                amount,
                convertedAmount,
                rate,
                getCurrencySymbol(fromCurrency),
                getCurrencySymbol(toCurrency)
        );
        
        // Save to history if account number is provided
        if (accountNo != null && !accountNo.isEmpty()) {
            historyRepository.saveConversion(
                    accountNo, fromCurrency, toCurrency, amount, convertedAmount, rate);
        }
        
        return result;
    }
    
    /**
     * Gets all available currencies with metadata.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @return List of currency information objects
     */
    public List<CurrencyInfo> getAllCurrencies() {
        List<CurrencyInfo> currencies = new ArrayList<>(currencyMetadata.values());
        Collections.sort(currencies, (c1, c2) -> c1.getName().compareTo(c2.getName()));
        return currencies;
    }
    
    /**
     * Gets currency information by ISO code.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param currencyCode ISO 4217 currency code
     * @return CurrencyInfo object
     * @throws IllegalArgumentException If currency not found
     */
    public CurrencyInfo getCurrencyInfo(String currencyCode) {
        CurrencyInfo info = currencyMetadata.get(currencyCode);
        if (info == null) {
            throw new IllegalArgumentException("Unknown currency code: " + currencyCode);
        }
        return info;
    }
    
    /**
     * Gets conversion history for an account.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param accountNo Account number
     * @param limit Maximum number of records to retrieve
     * @return List of conversion records
     */
    public List<ConversionHistoryRepository.ConversionRecord> getConversionHistory(
            String accountNo, int limit) {
        return historyRepository.getConversionHistory(accountNo, limit);
    }
    
    /**
     * Gets popular currency pairs based on usage.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param limit Maximum number of pairs to retrieve
     * @return List of currency pair usage statistics
     */
    public List<ConversionHistoryRepository.CurrencyPairUsage> getPopularCurrencyPairs(int limit) {
        return historyRepository.getPopularCurrencyPairs(limit);
    }
    
    /**
     * Validates currency code existence.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param currencyCode ISO currency code
     * @throws IllegalArgumentException If currency not found
     */
    private void validateCurrency(String currencyCode) {
        if (!currencyMetadata.containsKey(currencyCode)) {
            throw new IllegalArgumentException("Unknown currency code: " + currencyCode);
        }
    }
    
    /**
     * Gets the number of decimal places for a currency.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param currencyCode ISO currency code
     * @return Number of decimal places
     */
    private int getDecimalPlaces(String currencyCode) {
        CurrencyInfo info = currencyMetadata.get(currencyCode);
        return info != null ? info.getDecimals() : 2; // Default to 2 if not found
    }
    
    /**
     * Gets currency symbol.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param currencyCode ISO currency code
     * @return Currency symbol
     */
    private String getCurrencySymbol(String currencyCode) {
        CurrencyInfo info = currencyMetadata.get(currencyCode);
        return info != null ? info.getSymbol() : currencyCode;
    }
    
    /**
     * Initializes the currency metadata map with ISO codes, symbols, and names.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @return Map of currency codes to CurrencyInfo objects
     */
    private Map<String, CurrencyInfo> initializeCurrencyMetadata() {
        Map<String, CurrencyInfo> metadata = new HashMap<>();
        
        // Major currencies
        metadata.put("USD", new CurrencyInfo("USD", "$", "US Dollar", 2));
        metadata.put("EUR", new CurrencyInfo("EUR", "€", "Euro", 2));
        metadata.put("GBP", new CurrencyInfo("GBP", "£", "British Pound", 2));
        metadata.put("JPY", new CurrencyInfo("JPY", "¥", "Japanese Yen", 0));
        metadata.put("CNY", new CurrencyInfo("CNY", "¥", "Chinese Yuan", 2));
        metadata.put("INR", new CurrencyInfo("INR", "₹", "Indian Rupee", 2));
        metadata.put("AUD", new CurrencyInfo("AUD", "A$", "Australian Dollar", 2));
        metadata.put("CAD", new CurrencyInfo("CAD", "C$", "Canadian Dollar", 2));
        metadata.put("CHF", new CurrencyInfo("CHF", "Fr", "Swiss Franc", 2));
        metadata.put("HKD", new CurrencyInfo("HKD", "HK$", "Hong Kong Dollar", 2));
        metadata.put("SGD", new CurrencyInfo("SGD", "S$", "Singapore Dollar", 2));
        
        // More currencies
        metadata.put("AED", new CurrencyInfo("AED", "د.إ", "UAE Dirham", 2));
        metadata.put("AFN", new CurrencyInfo("AFN", "؋", "Afghan Afghani", 2));
        metadata.put("ALL", new CurrencyInfo("ALL", "L", "Albanian Lek", 2));
        metadata.put("AMD", new CurrencyInfo("AMD", "֏", "Armenian Dram", 2));
        metadata.put("ARS", new CurrencyInfo("ARS", "$", "Argentine Peso", 2));
        metadata.put("BDT", new CurrencyInfo("BDT", "৳", "Bangladeshi Taka", 2));
        metadata.put("BHD", new CurrencyInfo("BHD", ".د.ب", "Bahraini Dinar", 3));
        metadata.put("BRL", new CurrencyInfo("BRL", "R$", "Brazilian Real", 2));
        metadata.put("CLP", new CurrencyInfo("CLP", "$", "Chilean Peso", 0));
        metadata.put("COP", new CurrencyInfo("COP", "$", "Colombian Peso", 2));
        metadata.put("CZK", new CurrencyInfo("CZK", "Kč", "Czech Koruna", 2));
        metadata.put("DKK", new CurrencyInfo("DKK", "kr", "Danish Krone", 2));
        metadata.put("EGP", new CurrencyInfo("EGP", "£", "Egyptian Pound", 2));
        metadata.put("HUF", new CurrencyInfo("HUF", "Ft", "Hungarian Forint", 2));
        metadata.put("IDR", new CurrencyInfo("IDR", "Rp", "Indonesian Rupiah", 2));
        metadata.put("ILS", new CurrencyInfo("ILS", "₪", "Israeli New Shekel", 2));
        metadata.put("KRW", new CurrencyInfo("KRW", "₩", "South Korean Won", 0));
        metadata.put("KWD", new CurrencyInfo("KWD", "د.ك", "Kuwaiti Dinar", 3));
        metadata.put("MXN", new CurrencyInfo("MXN", "$", "Mexican Peso", 2));
        metadata.put("MYR", new CurrencyInfo("MYR", "RM", "Malaysian Ringgit", 2));
        metadata.put("NOK", new CurrencyInfo("NOK", "kr", "Norwegian Krone", 2));
        metadata.put("NZD", new CurrencyInfo("NZD", "NZ$", "New Zealand Dollar", 2));
        metadata.put("PHP", new CurrencyInfo("PHP", "₱", "Philippine Peso", 2));
        metadata.put("PKR", new CurrencyInfo("PKR", "₨", "Pakistani Rupee", 2));
        metadata.put("PLN", new CurrencyInfo("PLN", "zł", "Polish Złoty", 2));
        metadata.put("RUB", new CurrencyInfo("RUB", "₽", "Russian Ruble", 2));
        metadata.put("SAR", new CurrencyInfo("SAR", "﷼", "Saudi Riyal", 2));
        metadata.put("SEK", new CurrencyInfo("SEK", "kr", "Swedish Krona", 2));
        metadata.put("THB", new CurrencyInfo("THB", "฿", "Thai Baht", 2));
        metadata.put("TRY", new CurrencyInfo("TRY", "₺", "Turkish Lira", 2));
        metadata.put("TWD", new CurrencyInfo("TWD", "NT$", "New Taiwan Dollar", 2));
        metadata.put("ZAR", new CurrencyInfo("ZAR", "R", "South African Rand", 2));
        
        return metadata;
    }
    
    /**
     * Currency information data model.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    public static class CurrencyInfo {
        private String code;
        private String symbol;
        private String name;
        private int decimals;
        
        public CurrencyInfo(String code, String symbol, String name, int decimals) {
            this.code = code;
            this.symbol = symbol;
            this.name = name;
            this.decimals = decimals;
        }
        
        public String getCode() { return code; }
        public String getSymbol() { return symbol; }
        public String getName() { return name; }
        public int getDecimals() { return decimals; }
        
        @Override
        public String toString() {
            return name + " (" + code + ", " + symbol + ")";
        }
    }
    
    /**
     * Currency conversion result data model.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    public static class ConversionResult {
        private String fromCurrency;
        private String toCurrency;
        private double fromAmount;
        private double toAmount;
        private double exchangeRate;
        private String fromSymbol;
        private String toSymbol;
        
        public ConversionResult(String fromCurrency, String toCurrency, 
                              double fromAmount, double toAmount, 
                              double exchangeRate, String fromSymbol, String toSymbol) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.fromAmount = fromAmount;
            this.toAmount = toAmount;
            this.exchangeRate = exchangeRate;
            this.fromSymbol = fromSymbol;
            this.toSymbol = toSymbol;
        }
        
        public String getFromCurrency() { return fromCurrency; }
        public String getToCurrency() { return toCurrency; }
        public double getFromAmount() { return fromAmount; }
        public double getToAmount() { return toAmount; }
        public double getExchangeRate() { return exchangeRate; }
        public String getFromSymbol() { return fromSymbol; }
        public String getToSymbol() { return toSymbol; }
        
        @Override
        public String toString() {
            return fromAmount + " " + fromCurrency + " = " + toAmount + " " + toCurrency + 
                   " (Rate: " + exchangeRate + ")";
        }
    }
}