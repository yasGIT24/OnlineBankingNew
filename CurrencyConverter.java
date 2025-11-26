package banking.management.system;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.time.format.*;
import java.util.*;
import java.util.List;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US3]
 * This class provides the UI for currency conversion.
 * It allows users to convert between different currencies and view conversion history.
 */
public class CurrencyConverter extends JFrame implements ActionListener {
    
    private final String pin;
    private final String accountNo;
    private final CurrencyService currencyService;
    private final AuditLogger auditLogger;
    
    private JTabbedPane tabbedPane;
    private JPanel converterPanel, historyPanel;
    private JComboBox<String> fromCurrencyCombo, toCurrencyCombo;
    private JTextField amountField;
    private JButton convertButton, backButton, clearButton;
    private JLabel resultLabel, exchangeRateLabel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    
    // Map to store currency objects by code
    private final Map<String, CurrencyService.Currency> currencyMap;
    
    public CurrencyConverter(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        this.currencyService = new CurrencyService(accountNo, pin);
        this.auditLogger = new AuditLogger();
        this.currencyMap = CurrencyService.getAllCurrencies();
        
        // Ensure tables exist
        currencyService.ensureCurrencyTablesExist();
        
        // Set up the UI
        setTitle("Currency Converter");
        setSize(1600, 1200);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(204, 229, 255));
        
        // Create header
        createHeader();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Raleway", Font.BOLD, 14));
        
        // Create tabs
        createConverterTab();
        createHistoryTab();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create footer
        createFooter();
        
        // Load initial data
        loadConversionHistory();
        
        setVisible(true);
    }
    
    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 100));
        
        // Add bank logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image logoImg = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
        ImageIcon scaledLogoIcon = new ImageIcon(logoImg);
        JLabel logoLabel = new JLabel(scaledLogoIcon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        headerPanel.add(logoLabel, BorderLayout.WEST);
        
        // Add title
        JLabel title = new JLabel("CURRENCY CONVERTER");
        title.setFont(new Font("Osward", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(title, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createConverterTab() {
        converterPanel = new JPanel(null);
        converterPanel.setBackground(new Color(240, 248, 255));
        
        // From currency selection
        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        fromLabel.setBounds(150, 100, 200, 30);
        converterPanel.add(fromLabel);
        
        // Create sorted array of currency display names
        List<String> sortedCurrencies = new ArrayList<>();
        for (CurrencyService.Currency currency : currencyMap.values()) {
            sortedCurrencies.add(currency.getCode() + " - " + currency.getName() + " (" + currency.getSymbol() + ")");
        }
        Collections.sort(sortedCurrencies);
        
        fromCurrencyCombo = new JComboBox<>(sortedCurrencies.toArray(new String[0]));
        fromCurrencyCombo.setFont(new Font("Raleway", Font.PLAIN, 16));
        fromCurrencyCombo.setBounds(350, 100, 400, 30);
        converterPanel.add(fromCurrencyCombo);
        
        // Set default selection to USD
        for (int i = 0; i < fromCurrencyCombo.getItemCount(); i++) {
            if (fromCurrencyCombo.getItemAt(i).startsWith("USD")) {
                fromCurrencyCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // To currency selection
        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        toLabel.setBounds(150, 150, 200, 30);
        converterPanel.add(toLabel);
        
        toCurrencyCombo = new JComboBox<>(sortedCurrencies.toArray(new String[0]));
        toCurrencyCombo.setFont(new Font("Raleway", Font.PLAIN, 16));
        toCurrencyCombo.setBounds(350, 150, 400, 30);
        converterPanel.add(toCurrencyCombo);
        
        // Set default selection to EUR
        for (int i = 0; i < toCurrencyCombo.getItemCount(); i++) {
            if (toCurrencyCombo.getItemAt(i).startsWith("EUR")) {
                toCurrencyCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Amount field
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        amountLabel.setBounds(150, 200, 200, 30);
        converterPanel.add(amountLabel);
        
        amountField = new JTextField("1.00");
        amountField.setFont(new Font("Raleway", Font.PLAIN, 16));
        amountField.setBounds(350, 200, 200, 30);
        converterPanel.add(amountField);
        
        // Convert button
        convertButton = new JButton("CONVERT");
        convertButton.setFont(new Font("Raleway", Font.BOLD, 16));
        convertButton.setBounds(350, 250, 200, 40);
        convertButton.setBackground(new Color(0, 102, 204));
        convertButton.setForeground(Color.WHITE);
        convertButton.addActionListener(this);
        converterPanel.add(convertButton);
        
        // Results section
        JPanel resultPanel = new JPanel();
        resultPanel.setBounds(150, 320, 600, 150);
        resultPanel.setBackground(new Color(230, 240, 250));
        resultPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Conversion Result", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Raleway", Font.BOLD, 14)
        ));
        resultPanel.setLayout(null);
        converterPanel.add(resultPanel);
        
        // Result label
        resultLabel = new JLabel("Enter an amount and click Convert");
        resultLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        resultLabel.setBounds(20, 40, 560, 30);
        resultPanel.add(resultLabel);
        
        // Exchange rate label
        exchangeRateLabel = new JLabel("Exchange rate will appear here");
        exchangeRateLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        exchangeRateLabel.setBounds(20, 80, 560, 30);
        resultPanel.add(exchangeRateLabel);
        
        // Clear button
        clearButton = new JButton("CLEAR");
        clearButton.setFont(new Font("Raleway", Font.BOLD, 16));
        clearButton.setBounds(580, 250, 120, 40);
        clearButton.setBackground(new Color(192, 192, 192));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(this);
        converterPanel.add(clearButton);
        
        // Add image
        ImageIcon currencyIcon = new ImageIcon(ClassLoader.getSystemResource("icons/currency.png"));
        if (currencyIcon.getIconWidth() > 0) {
            Image currencyImg = currencyIcon.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT);
            ImageIcon scaledCurrencyIcon = new ImageIcon(currencyImg);
            JLabel currencyImageLabel = new JLabel(scaledCurrencyIcon);
            currencyImageLabel.setBounds(850, 100, 400, 400);
            converterPanel.add(currencyImageLabel);
        }
        
        tabbedPane.addTab("Convert", converterPanel);
    }
    
    private void createHistoryTab() {
        historyPanel = new JPanel();
        historyPanel.setLayout(new BorderLayout());
        historyPanel.setBackground(new Color(240, 248, 255));
        
        // Create table for conversion history
        String[] columns = {"Date & Time", "From Currency", "To Currency", "Amount", "Converted Amount", "Exchange Rate"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setFont(new Font("Raleway", Font.PLAIN, 14));
        historyTable.getTableHeader().setFont(new Font("Raleway", Font.BOLD, 14));
        historyTable.setRowHeight(30);
        
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("History", historyPanel);
    }
    
    private void createFooter() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(0, 51, 102));
        footerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        backButton = new JButton("BACK TO MAIN MENU");
        backButton.setFont(new Font("Raleway", Font.BOLD, 16));
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        footerPanel.add(backButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == convertButton) {
            handleConversion();
        } else if (ae.getSource() == clearButton) {
            clearForm();
        } else if (ae.getSource() == backButton) {
            setVisible(false);
            new Transactions(pin, accountNo).setVisible(true);
        }
    }
    
    private void handleConversion() {
        try {
            // Parse selected currencies
            String fromCurrencyFull = (String) fromCurrencyCombo.getSelectedItem();
            String toCurrencyFull = (String) toCurrencyCombo.getSelectedItem();
            
            String fromCurrencyCode = fromCurrencyFull.substring(0, 3);
            String toCurrencyCode = toCurrencyFull.substring(0, 3);
            
            // Parse amount
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Show wait cursor
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Perform conversion in a background thread
            SwingWorker<CurrencyService.ConversionResult, Void> worker = 
                new SwingWorker<CurrencyService.ConversionResult, Void>() {
                    @Override
                    protected CurrencyService.ConversionResult doInBackground() throws Exception {
                        return currencyService.convertCurrency(fromCurrencyCode, toCurrencyCode, amount);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            CurrencyService.ConversionResult result = get();
                            displayConversionResult(result);
                            loadConversionHistory(); // Refresh history
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(CurrencyConverter.this, 
                                "Error performing conversion: " + e.getMessage(),
                                "Conversion Error", JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        } finally {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }
                };
            
            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void displayConversionResult(CurrencyService.ConversionResult result) {
        if (result == null) {
            resultLabel.setText("Conversion failed. Please try again.");
            exchangeRateLabel.setText("");
            return;
        }
        
        // Format the result with currency symbols
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String fromCurrencySymbol = getCurrencySymbol(result.getFromCurrency());
        String toCurrencySymbol = getCurrencySymbol(result.getToCurrency());
        
        // Update result labels
        String resultText = String.format("%s %s = %s %s", 
            fromCurrencySymbol, df.format(result.getAmount()),
            toCurrencySymbol, df.format(result.getConvertedAmount()));
        resultLabel.setText(resultText);
        
        // Format exchange rate with 6 decimal places for precision
        DecimalFormat rateDf = new DecimalFormat("#,##0.######");
        String rateText = String.format("Exchange Rate: 1 %s = %s %s", 
            result.getFromCurrency(), rateDf.format(result.getExchangeRate()), 
            result.getToCurrency());
        exchangeRateLabel.setText(rateText);
        
        // Switch to the History tab
        tabbedPane.setSelectedIndex(1);
    }
    
    private String getCurrencySymbol(String currencyCode) {
        CurrencyService.Currency currency = currencyMap.get(currencyCode);
        return currency != null ? currency.getSymbol() : currencyCode;
    }
    
    private void clearForm() {
        amountField.setText("1.00");
        resultLabel.setText("Enter an amount and click Convert");
        exchangeRateLabel.setText("Exchange rate will appear here");
    }
    
    private void loadConversionHistory() {
        // Clear existing data
        historyTableModel.setRowCount(0);
        
        // Get conversion history
        List<CurrencyService.ConversionResult> history = currencyService.getConversionHistory();
        
        // Format for display
        DecimalFormat df = new DecimalFormat("#,##0.00");
        DecimalFormat rateDf = new DecimalFormat("#,##0.######");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // Add data to table
        for (CurrencyService.ConversionResult conversion : history) {
            historyTableModel.addRow(new Object[]{
                conversion.getConversionDate().format(dtf),
                conversion.getFromCurrency() + " " + getCurrencySymbol(conversion.getFromCurrency()),
                conversion.getToCurrency() + " " + getCurrencySymbol(conversion.getToCurrency()),
                df.format(conversion.getAmount()),
                df.format(conversion.getConvertedAmount()),
                rateDf.format(conversion.getExchangeRate())
            });
        }
    }
    
    public static void main(String[] args) {
        new CurrencyConverter("", "");
    }
}

/* 
 * Agent Run Identifier: BANK-CURRENCY-UI-20251126
 * Related Test Cases: CURRENCY-UI-001, CURRENCY-UI-002
 */