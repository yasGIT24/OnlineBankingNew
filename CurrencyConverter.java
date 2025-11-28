package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * CurrencyConverter provides currency conversion functionality with 
 * comprehensive currency list and precise calculations
 */
public class CurrencyConverter extends JFrame implements ActionListener {

    /* [AGENT GENERATED CODE - REQUIREMENT:CURR-001, CURR-002, CURR-003]
     * Implementation of Currency Conversion feature:
     * - Comprehensive currency list with ISO codes and symbols
     * - Exchange rate service with API integration placeholder
     * - Precision calculation for currency conversion
     * - Conversion history tracking
     */
    
    private String pin;
    private String accountNo;
    private JComboBox<String> fromCurrencyCombo, toCurrencyCombo;
    private JTextField amountField, resultField;
    private JButton convertButton, backButton, saveButton;
    private JTable historyTable;
    private JScrollPane historyScrollPane;
    private HashMap<String, Currency> currencies;
    
    // Decimal formatter for currency display
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    
    /**
     * Currency class to store currency information
     */
    private class Currency {
        String code;      // ISO code (e.g., USD)
        String name;      // Currency name (e.g., US Dollar)
        String symbol;    // Currency symbol (e.g., $)
        
        public Currency(String code, String name, String symbol) {
            this.code = code;
            this.name = name;
            this.symbol = symbol;
        }
        
        @Override
        public String toString() {
            return code + " - " + name + " (" + symbol + ")";
        }
    }
    
    /**
     * Conversion history record
     */
    private class ConversionRecord {
        Date timestamp;
        String fromCurrency;
        String toCurrency;
        BigDecimal amount;
        BigDecimal result;
        BigDecimal rate;
        
        public ConversionRecord(Date timestamp, String fromCurrency, String toCurrency,
                BigDecimal amount, BigDecimal result, BigDecimal rate) {
            this.timestamp = timestamp;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
            this.result = result;
            this.rate = rate;
        }
        
        public Object[] toTableRow() {
            return new Object[]{
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp),
                fromCurrency,
                toCurrency,
                decimalFormat.format(amount),
                decimalFormat.format(result),
                rate.setScale(6, RoundingMode.HALF_UP).toString()
            };
        }
    }
    
    /**
     * Constructor for CurrencyConverter
     * 
     * @param pin User's PIN
     * @param accountNo User's account number
     */
    public CurrencyConverter(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        
        // Set up currencies
        initializeCurrencies();
        
        // Set up the JFrame
        setTitle("Currency Converter");
        setLayout(null);
        setSize(800, 700);
        getContentPane().setBackground(new Color(204, 229, 255));
        
        // Add header with logo and title
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image i2 = i1.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);  
        JLabel label = new JLabel(i3);
        label.setBounds(20, 10, 80, 80);
        add(label);
        
        JLabel title = new JLabel("CURRENCY CONVERTER");
        title.setFont(new Font("Raleway", Font.BOLD, 26));
        title.setBounds(220, 20, 400, 40);
        add(title);
        
        // Conversion panel
        JPanel conversionPanel = new JPanel();
        conversionPanel.setLayout(null);
        conversionPanel.setBounds(20, 100, 745, 200);
        conversionPanel.setBackground(new Color(225, 240, 255));
        
        // From currency
        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        fromLabel.setBounds(20, 20, 120, 25);
        conversionPanel.add(fromLabel);
        
        fromCurrencyCombo = new JComboBox<>();
        for (Currency c : currencies.values()) {
            fromCurrencyCombo.addItem(c.toString());
        }
        fromCurrencyCombo.setBounds(150, 20, 250, 25);
        conversionPanel.add(fromCurrencyCombo);
        
        // Find and select user's preferred currency if set
        String preferredCurrency = getUserPreferredCurrency();
        if (preferredCurrency != null && !preferredCurrency.isEmpty()) {
            for (int i = 0; i < fromCurrencyCombo.getItemCount(); i++) {
                if (fromCurrencyCombo.getItemAt(i).startsWith(preferredCurrency)) {
                    fromCurrencyCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // To currency
        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        toLabel.setBounds(20, 60, 120, 25);
        conversionPanel.add(toLabel);
        
        toCurrencyCombo = new JComboBox<>();
        for (Currency c : currencies.values()) {
            toCurrencyCombo.addItem(c.toString());
        }
        toCurrencyCombo.setBounds(150, 60, 250, 25);
        conversionPanel.add(toCurrencyCombo);
        
        // Default to USD for "to" currency if different from preferred
        for (int i = 0; i < toCurrencyCombo.getItemCount(); i++) {
            if (toCurrencyCombo.getItemAt(i).startsWith("USD")) {
                toCurrencyCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        amountLabel.setBounds(20, 100, 120, 25);
        conversionPanel.add(amountLabel);
        
        amountField = new JTextField();
        amountField.setBounds(150, 100, 150, 25);
        conversionPanel.add(amountField);
        
        // Result
        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setFont(new Font("Raleway", Font.BOLD, 14));
        resultLabel.setBounds(320, 100, 80, 25);
        conversionPanel.add(resultLabel);
        
        resultField = new JTextField();
        resultField.setBounds(400, 100, 150, 25);
        resultField.setEditable(false);
        conversionPanel.add(resultField);
        
        // Convert button
        convertButton = new JButton("Convert");
        convertButton.setBounds(150, 150, 100, 30);
        convertButton.setBackground(Color.BLACK);
        convertButton.setForeground(Color.WHITE);
        convertButton.addActionListener(this);
        conversionPanel.add(convertButton);
        
        // Save conversion button
        saveButton = new JButton("Save Conversion");
        saveButton.setBounds(270, 150, 150, 30);
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(this);
        saveButton.setEnabled(false); // Enable after conversion
        conversionPanel.add(saveButton);
        
        add(conversionPanel);
        
        // Conversion history table
        JLabel historyLabel = new JLabel("Conversion History");
        historyLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        historyLabel.setBounds(20, 320, 200, 30);
        add(historyLabel);
        
        String[] columnNames = {"Date/Time", "From", "To", "Amount", "Result", "Rate"};
        historyTable = new JTable(new Object[0][columnNames.length], columnNames);
        historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setBounds(20, 360, 745, 250);
        add(historyScrollPane);
        
        // Load conversion history
        loadConversionHistory();
        
        // Back button
        backButton = new JButton("BACK");
        backButton.setBounds(20, 620, 100, 30);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
        
        // Log access to currency converter
        AuditLogger.logUserActivity(accountNo, "CURRENCY_CONVERTER_ACCESS", 
                "User accessed currency converter", AuditLogger.INFO);
        
        setVisible(true);
    }
    
    /**
     * Initialize currency list with ISO codes, names and symbols
     */
    private void initializeCurrencies() {
        currencies = new HashMap<>();
        
        // Add major world currencies with their symbols
        addCurrency("USD", "US Dollar", "$");
        addCurrency("EUR", "Euro", "€");
        addCurrency("GBP", "British Pound", "£");
        addCurrency("JPY", "Japanese Yen", "¥");
        addCurrency("CNY", "Chinese Yuan", "¥");
        addCurrency("INR", "Indian Rupee", "₹");
        addCurrency("AUD", "Australian Dollar", "A$");
        addCurrency("CAD", "Canadian Dollar", "C$");
        addCurrency("CHF", "Swiss Franc", "Fr");
        addCurrency("HKD", "Hong Kong Dollar", "HK$");
        addCurrency("SGD", "Singapore Dollar", "S$");
        addCurrency("SEK", "Swedish Krona", "kr");
        addCurrency("KRW", "South Korean Won", "₩");
        addCurrency("NOK", "Norwegian Krone", "kr");
        addCurrency("NZD", "New Zealand Dollar", "NZ$");
        addCurrency("MXN", "Mexican Peso", "$");
        addCurrency("BRL", "Brazilian Real", "R$");
        addCurrency("RUB", "Russian Ruble", "₽");
        addCurrency("ZAR", "South African Rand", "R");
        addCurrency("TRY", "Turkish Lira", "₺");
    }
    
    /**
     * Add a currency to the currencies map
     * 
     * @param code ISO currency code
     * @param name Currency name
     * @param symbol Currency symbol
     */
    private void addCurrency(String code, String name, String symbol) {
        currencies.put(code, new Currency(code, name, symbol));
    }
    
    /**
     * Get user's preferred currency from database
     * 
     * @return ISO code of preferred currency or null
     */
    private String getUserPreferredCurrency() {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "SELECT preferred_currency FROM signup1 WHERE Account_No = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("preferred_currency");
            }
        } catch (Exception e) {
            // If column doesn't exist or query fails, return null
            System.err.println("Error getting preferred currency: " + e.getMessage());
        }
        
        return "INR"; // Default to Indian Rupee
    }
    
    /**
     * Perform currency conversion with precise calculations
     * 
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @param amount Amount to convert
     * @return Converted amount
     */
    private BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal amount) {
        /* [AGENT GENERATED CODE - REQUIREMENT:CURR-003]
         * Precision calculation for currency conversion
         */
        try {
            // Get the exchange rate (in a real implementation, this would come from an API)
            BigDecimal rate = getExchangeRate(fromCurrency, toCurrency);
            
            // Perform conversion with proper rounding
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error during conversion: " + e.getMessage(), 
                    "Conversion Error", JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Get exchange rate between two currencies
     * In a real implementation, this would call an external API
     * 
     * @param fromCurrency Source currency code
     * @param toCurrency Target currency code
     * @return Exchange rate
     */
    private BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        /* [AGENT GENERATED CODE - REQUIREMENT:CURR-002]
         * Exchange rate service (simplified implementation)
         */
        
        // NOTE: In a production environment, this would use an API like Open Exchange Rates,
        // Fixer.io, or a bank's internal rate service. For this placeholder, we use 
        // hardcoded sample rates based on INR.
        
        // Base rates against INR (Indian Rupee as base = 1.0)
        HashMap<String, BigDecimal> baseRates = new HashMap<>();
        baseRates.put("INR", BigDecimal.ONE);
        baseRates.put("USD", new BigDecimal("0.012"));    // 1 INR = 0.012 USD
        baseRates.put("EUR", new BigDecimal("0.011"));    // 1 INR = 0.011 EUR
        baseRates.put("GBP", new BigDecimal("0.0095"));   // 1 INR = 0.0095 GBP
        baseRates.put("JPY", new BigDecimal("1.78"));     // 1 INR = 1.78 JPY
        baseRates.put("CNY", new BigDecimal("0.087"));    // 1 INR = 0.087 CNY
        baseRates.put("AUD", new BigDecimal("0.018"));    // 1 INR = 0.018 AUD
        baseRates.put("CAD", new BigDecimal("0.016"));    // 1 INR = 0.016 CAD
        baseRates.put("CHF", new BigDecimal("0.011"));    // 1 INR = 0.011 CHF
        baseRates.put("HKD", new BigDecimal("0.094"));    // 1 INR = 0.094 HKD
        baseRates.put("SGD", new BigDecimal("0.016"));    // 1 INR = 0.016 SGD
        baseRates.put("SEK", new BigDecimal("0.125"));    // 1 INR = 0.125 SEK
        baseRates.put("KRW", new BigDecimal("16.25"));    // 1 INR = 16.25 KRW
        baseRates.put("NOK", new BigDecimal("0.127"));    // 1 INR = 0.127 NOK
        baseRates.put("NZD", new BigDecimal("0.019"));    // 1 INR = 0.019 NZD
        baseRates.put("MXN", new BigDecimal("0.21"));     // 1 INR = 0.21 MXN
        baseRates.put("BRL", new BigDecimal("0.068"));    // 1 INR = 0.068 BRL
        baseRates.put("RUB", new BigDecimal("1.11"));     // 1 INR = 1.11 RUB
        baseRates.put("ZAR", new BigDecimal("0.225"));    // 1 INR = 0.225 ZAR
        baseRates.put("TRY", new BigDecimal("0.38"));     // 1 INR = 0.38 TRY
        
        // If both currencies are in our list, calculate the cross rate
        if (baseRates.containsKey(fromCurrency) && baseRates.containsKey(toCurrency)) {
            // Convert via the base currency (INR)
            BigDecimal fromRate = baseRates.get(fromCurrency);
            BigDecimal toRate = baseRates.get(toCurrency);
            
            // Calculate cross rate: toRate / fromRate
            return toRate.divide(fromRate, 10, RoundingMode.HALF_UP);
        } else {
            // Default 1:1 rate for unknown currencies
            return BigDecimal.ONE;
        }
    }
    
    /**
     * Save a conversion record to the database
     * 
     * @param fromCurrency Source currency
     * @param toCurrency Target currency
     * @param amount Amount converted
     * @param result Conversion result
     * @param rate Exchange rate used
     */
    private void saveConversion(String fromCurrency, String toCurrency, 
            BigDecimal amount, BigDecimal result, BigDecimal rate) {
        /* [AGENT GENERATED CODE - REQUIREMENT:CURR-004]
         * Conversion history tracking
         */
        try {
            ConnectionSql c = new ConnectionSql();
            
            // Check if conversion_history table exists, create if not
            try {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS conversion_history (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "account_no VARCHAR(20), " +
                        "timestamp DATETIME, " +
                        "from_currency VARCHAR(10), " +
                        "to_currency VARCHAR(10), " +
                        "amount DECIMAL(15,2), " +
                        "result DECIMAL(15,2), " +
                        "rate DECIMAL(15,10))";
                c.s.execute(createTableSQL);
            } catch (Exception e) {
                System.err.println("Error creating table: " + e.getMessage());
                // Continue even if table creation fails, it might already exist
            }
            
            // Insert the conversion record
            String query = "INSERT INTO conversion_history " +
                    "(account_no, timestamp, from_currency, to_currency, amount, result, rate) " +
                    "VALUES (?, NOW(), ?, ?, ?, ?, ?)";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, accountNo);
            ps.setString(2, fromCurrency);
            ps.setString(3, toCurrency);
            ps.setBigDecimal(4, amount);
            ps.setBigDecimal(5, result);
            ps.setBigDecimal(6, rate);
            ps.executeUpdate();
            
            // Log the conversion
            AuditLogger.logCurrencyConversion(accountNo, fromCurrency, toCurrency, 
                    amount.doubleValue(), result.doubleValue(), AuditLogger.SUCCESS);
            
            // Reload conversion history
            loadConversionHistory();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Error saving conversion: " + e.getMessage(), 
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            
            AuditLogger.logCurrencyConversion(accountNo, fromCurrency, toCurrency, 
                    amount.doubleValue(), result.doubleValue(), AuditLogger.ERROR);
        }
    }
    
    /**
     * Load conversion history from database
     */
    private void loadConversionHistory() {
        ArrayList<ConversionRecord> history = new ArrayList<>();
        
        try {
            ConnectionSql c = new ConnectionSql();
            
            String query = "SELECT timestamp, from_currency, to_currency, amount, result, rate " +
                    "FROM conversion_history WHERE account_no = ? ORDER BY timestamp DESC LIMIT 50";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                history.add(new ConversionRecord(
                        rs.getTimestamp("timestamp"),
                        rs.getString("from_currency"),
                        rs.getString("to_currency"),
                        rs.getBigDecimal("amount"),
                        rs.getBigDecimal("result"),
                        rs.getBigDecimal("rate")
                ));
            }
            
        } catch (Exception e) {
            // Table may not exist yet, that's OK
            System.err.println("Error loading conversion history: " + e.getMessage());
        }
        
        // Update the history table
        if (!history.isEmpty()) {
            Object[][] data = new Object[history.size()][6];
            for (int i = 0; i < history.size(); i++) {
                data[i] = history.get(i).toTableRow();
            }
            
            String[] columnNames = {"Date/Time", "From", "To", "Amount", "Result", "Rate"};
            historyTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        }
    }
    
    /**
     * Extract currency code from combo box selection
     * 
     * @param selection Combo box selection string
     * @return Currency ISO code
     */
    private String extractCurrencyCode(String selection) {
        return selection.substring(0, 3);
    }
    
    /**
     * Handle button actions
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backButton) {
            setVisible(false);
            // Return to transactions screen
        }
        else if (ae.getSource() == convertButton) {
            try {
                // Get selected currencies
                String fromCurrency = extractCurrencyCode((String)fromCurrencyCombo.getSelectedItem());
                String toCurrency = extractCurrencyCode((String)toCurrencyCombo.getSelectedItem());
                
                // Validate amount input
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an amount to convert");
                    return;
                }
                
                // Parse amount with proper precision
                BigDecimal amount;
                try {
                    amount = new BigDecimal(amountText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number amount");
                    return;
                }
                
                // Check for negative amounts
                if (amount.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Amount cannot be negative");
                    return;
                }
                
                // Perform conversion
                BigDecimal result = convert(fromCurrency, toCurrency, amount);
                
                // Display result
                String fromSymbol = currencies.get(fromCurrency).symbol;
                String toSymbol = currencies.get(toCurrency).symbol;
                resultField.setText(toSymbol + " " + decimalFormat.format(result));
                
                // Enable save button
                saveButton.setEnabled(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error during conversion: " + e.getMessage(), 
                        "Conversion Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        else if (ae.getSource() == saveButton) {
            try {
                // Get selected currencies
                String fromCurrency = extractCurrencyCode((String)fromCurrencyCombo.getSelectedItem());
                String toCurrency = extractCurrencyCode((String)toCurrencyCombo.getSelectedItem());
                
                // Get amount and result
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                
                // Get result (strip currency symbol if present)
                String resultText = resultField.getText().trim();
                String toSymbol = currencies.get(toCurrency).symbol;
                if (resultText.startsWith(toSymbol)) {
                    resultText = resultText.substring(toSymbol.length()).trim();
                }
                resultText = resultText.replace(",", ""); // Remove commas
                BigDecimal result = new BigDecimal(resultText);
                
                // Calculate rate used
                BigDecimal rate = result.divide(amount, 10, RoundingMode.HALF_UP);
                
                // Save the conversion
                saveConversion(fromCurrency, toCurrency, amount, result, rate);
                
                JOptionPane.showMessageDialog(this, "Conversion saved successfully");
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error saving conversion: " + e.getMessage(), 
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        new CurrencyConverter("", "");
    }
}

/* 
 * AGENT GENERATED SUMMARY:
 * Implementation requirements addressed:
 * - CURR-001: Created comprehensive currency list with ISO codes and symbols
 * - CURR-002: Implemented exchange rate service (placeholder with sample rates)
 * - CURR-003: Added precision calculation for currency conversion with BigDecimal
 * - CURR-004: Implemented conversion history tracking
 * - INFRA-001: Added audit logging for currency operations
 * 
 * Implementation notes:
 * - Added 20 major world currencies with symbols
 * - Uses BigDecimal for precise financial calculations
 * - Includes user's preferred currency selection
 * - Stores conversion history in database
 * - Provides UI for conversion and history viewing
 * 
 * Human review required:
 * - Replace sample exchange rates with actual API integration
 * - Review precision and rounding for financial calculations
 * - Consider adding more currencies for comprehensive coverage
 * 
 * Agent run identifier: Claude-3.7-Sonnet-20250219
 */