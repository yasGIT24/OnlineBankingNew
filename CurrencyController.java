package banking.management.system;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

/**
 * Controller class for currency conversion UI.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
 * This class provides the user interface for currency conversion with
 * comprehensive currency list and real-time exchange rates.
 */
public class CurrencyController extends JFrame implements ActionListener {
    
    private JLabel titleLabel, fromLabel, toLabel, amountLabel, resultLabel, rateLabel;
    private JComboBox<CurrencyService.CurrencyInfo> fromCurrencyCombo, toCurrencyCombo;
    private JTextField amountField;
    private JButton convertButton, resetButton, backButton, historyButton;
    private JPanel mainPanel, resultPanel, historyPanel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    
    private String pin;
    private String accountNo;
    private CurrencyService currencyService;
    
    /**
     * Constructor initializes the currency conversion UI.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param pin Account PIN/password
     * @param accountNo Account number
     */
    public CurrencyController(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        this.currencyService = new CurrencyService();
        
        // Set up the frame
        setTitle("Currency Conversion");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize UI components
        initComponents();
        
        setVisible(true);
    }
    
    /**
     * Initializes UI components.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    private void initComponents() {
        // Main panel with background color
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(0, 51, 102));
        setContentPane(mainPanel);
        
        // Bank logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image logoImg = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledLogoIcon = new ImageIcon(logoImg);  
        JLabel logoLabel = new JLabel(scaledLogoIcon);
        logoLabel.setBounds(50, 10, 100, 100);
        mainPanel.add(logoLabel);
        
        // Title
        titleLabel = new JLabel("CURRENCY CONVERSION");
        titleLabel.setFont(new Font("Osward", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(170, 30, 500, 40);
        mainPanel.add(titleLabel);
        
        // From Currency section
        fromLabel = new JLabel("From Currency:");
        fromLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        fromLabel.setForeground(Color.WHITE);
        fromLabel.setBounds(100, 140, 150, 30);
        mainPanel.add(fromLabel);
        
        // From Currency dropdown
        List<CurrencyService.CurrencyInfo> currencies = currencyService.getAllCurrencies();
        fromCurrencyCombo = new JComboBox<>();
        
        for (CurrencyService.CurrencyInfo currency : currencies) {
            fromCurrencyCombo.addItem(currency);
        }
        
        fromCurrencyCombo.setRenderer(new CurrencyListRenderer());
        fromCurrencyCombo.setBounds(260, 140, 300, 30);
        fromCurrencyCombo.setBackground(Color.WHITE);
        mainPanel.add(fromCurrencyCombo);
        
        // To Currency section
        toLabel = new JLabel("To Currency:");
        toLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        toLabel.setForeground(Color.WHITE);
        toLabel.setBounds(100, 190, 150, 30);
        mainPanel.add(toLabel);
        
        // To Currency dropdown
        toCurrencyCombo = new JComboBox<>();
        
        for (CurrencyService.CurrencyInfo currency : currencies) {
            toCurrencyCombo.addItem(currency);
        }
        
        // Default selection
        fromCurrencyCombo.setSelectedIndex(findCurrencyIndex(currencies, "USD"));
        toCurrencyCombo.setSelectedIndex(findCurrencyIndex(currencies, "INR"));
        
        toCurrencyCombo.setRenderer(new CurrencyListRenderer());
        toCurrencyCombo.setBounds(260, 190, 300, 30);
        toCurrencyCombo.setBackground(Color.WHITE);
        mainPanel.add(toCurrencyCombo);
        
        // Amount section
        amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setBounds(100, 240, 150, 30);
        mainPanel.add(amountLabel);
        
        // Amount input field
        amountField = new JTextField();
        amountField.setFont(new Font("Raleway", Font.PLAIN, 18));
        amountField.setBounds(260, 240, 300, 30);
        mainPanel.add(amountField);
        
        // Convert button
        convertButton = new JButton("CONVERT");
        convertButton.setBounds(260, 290, 150, 40);
        convertButton.setBackground(new Color(204, 229, 255));
        convertButton.setFont(new Font("Arial", Font.BOLD, 14));
        convertButton.addActionListener(this);
        mainPanel.add(convertButton);
        
        // Reset button
        resetButton = new JButton("RESET");
        resetButton.setBounds(420, 290, 140, 40);
        resetButton.setBackground(new Color(204, 229, 255));
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.addActionListener(this);
        mainPanel.add(resetButton);
        
        // Back button
        backButton = new JButton("BACK");
        backButton.setBounds(100, 290, 140, 40);
        backButton.setBackground(new Color(204, 229, 255));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(this);
        mainPanel.add(backButton);
        
        // History button
        historyButton = new JButton("HISTORY");
        historyButton.setBounds(570, 290, 140, 40);
        historyButton.setBackground(new Color(204, 229, 255));
        historyButton.setFont(new Font("Arial", Font.BOLD, 14));
        historyButton.addActionListener(this);
        mainPanel.add(historyButton);
        
        // Result panel
        resultPanel = new JPanel();
        resultPanel.setBounds(100, 350, 600, 100);
        resultPanel.setBackground(new Color(0, 31, 63));
        resultPanel.setBorder(new LineBorder(Color.WHITE));
        resultPanel.setLayout(null);
        mainPanel.add(resultPanel);
        
        // Result label
        resultLabel = new JLabel("Enter an amount and press Convert");
        resultLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBounds(20, 20, 560, 30);
        resultPanel.add(resultLabel);
        
        // Exchange rate label
        rateLabel = new JLabel("");
        rateLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        rateLabel.setForeground(new Color(204, 229, 255));
        rateLabel.setBounds(20, 60, 560, 25);
        resultPanel.add(rateLabel);
        
        // History panel (initially hidden)
        historyPanel = new JPanel();
        historyPanel.setBounds(100, 460, 600, 200);
        historyPanel.setBackground(new Color(0, 31, 63));
        historyPanel.setBorder(new LineBorder(Color.WHITE));
        historyPanel.setLayout(new BorderLayout());
        historyPanel.setVisible(false);
        mainPanel.add(historyPanel);
        
        // History table
        String[] columnNames = {"Date", "From", "To", "Amount", "Result", "Rate"};
        historyTableModel = new DefaultTableModel(columnNames, 0);
        historyTable = new JTable(historyTableModel);
        historyTable.setBackground(Color.WHITE);
        historyTable.setForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Action event handler for buttons.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param ae Action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == convertButton) {
            convertCurrency();
        } else if (ae.getSource() == resetButton) {
            resetForm();
        } else if (ae.getSource() == backButton) {
            setVisible(false);
            new Transactions(pin, accountNo).setVisible(true);
        } else if (ae.getSource() == historyButton) {
            toggleHistoryPanel();
        }
    }
    
    /**
     * Performs currency conversion and updates UI with result.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    private void convertCurrency() {
        try {
            String amountStr = amountField.getText().trim();
            
            if (amountStr.isEmpty()) {
                showError("Please enter an amount");
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                showError("Invalid amount. Please enter a valid number");
                return;
            }
            
            if (amount <= 0) {
                showError("Amount must be greater than zero");
                return;
            }
            
            CurrencyService.CurrencyInfo fromCurrency = 
                    (CurrencyService.CurrencyInfo) fromCurrencyCombo.getSelectedItem();
            CurrencyService.CurrencyInfo toCurrency = 
                    (CurrencyService.CurrencyInfo) toCurrencyCombo.getSelectedItem();
            
            if (fromCurrency == null || toCurrency == null) {
                showError("Please select currencies");
                return;
            }
            
            // Perform conversion
            CurrencyService.ConversionResult result = currencyService.convertCurrency(
                    accountNo, fromCurrency.getCode(), toCurrency.getCode(), amount);
            
            // Format the result with proper currency symbols
            DecimalFormat fromFormat = new DecimalFormat("#,##0." + "0".repeat(fromCurrency.getDecimals()));
            DecimalFormat toFormat = new DecimalFormat("#,##0." + "0".repeat(toCurrency.getDecimals()));
            
            String formattedResult = fromCurrency.getSymbol() + " " + fromFormat.format(amount) + 
                                   " = " + toCurrency.getSymbol() + " " + toFormat.format(result.getToAmount());
            
            resultLabel.setText(formattedResult);
            resultLabel.setForeground(Color.WHITE);
            
            DecimalFormat rateFormat = new DecimalFormat("#,##0.0000");
            rateLabel.setText("Exchange Rate: 1 " + fromCurrency.getCode() + " = " + 
                            rateFormat.format(result.getExchangeRate()) + " " + toCurrency.getCode() + 
                            " (Updated: Just now)");
            
            // If history panel is visible, refresh it
            if (historyPanel.isVisible()) {
                loadConversionHistory();
            }
            
        } catch (Exception e) {
            showError("Conversion error: " + e.getMessage());
        }
    }
    
    /**
     * Resets the form to default values.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    private void resetForm() {
        List<CurrencyService.CurrencyInfo> currencies = currencyService.getAllCurrencies();
        fromCurrencyCombo.setSelectedIndex(findCurrencyIndex(currencies, "USD"));
        toCurrencyCombo.setSelectedIndex(findCurrencyIndex(currencies, "INR"));
        amountField.setText("");
        resultLabel.setText("Enter an amount and press Convert");
        resultLabel.setForeground(Color.WHITE);
        rateLabel.setText("");
    }
    
    /**
     * Toggles the visibility of the conversion history panel.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    private void toggleHistoryPanel() {
        historyPanel.setVisible(!historyPanel.isVisible());
        
        if (historyPanel.isVisible()) {
            loadConversionHistory();
            setSize(800, 720); // Expand window to show history
        } else {
            setSize(800, 600); // Shrink window when hiding history
        }
    }
    
    /**
     * Loads conversion history from repository and populates table.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    private void loadConversionHistory() {
        // Clear table
        historyTableModel.setRowCount(0);
        
        try {
            // Get last 10 conversion records
            List<ConversionHistoryRepository.ConversionRecord> history = 
                    currencyService.getConversionHistory(accountNo, 10);
            
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            DecimalFormat rateFormat = new DecimalFormat("#,##0.0000");
            
            for (ConversionHistoryRepository.ConversionRecord record : history) {
                String[] row = {
                    record.getTimestamp().substring(0, 16), // Date and time
                    record.getFromCurrency(),
                    record.getToCurrency(),
                    decimalFormat.format(record.getFromAmount()),
                    decimalFormat.format(record.getToAmount()),
                    rateFormat.format(record.getRate())
                };
                historyTableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Error loading conversion history: " + e.getMessage());
        }
    }
    
    /**
     * Displays error message in the result label.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param message Error message
     */
    private void showError(String message) {
        resultLabel.setText(message);
        resultLabel.setForeground(Color.RED);
    }
    
    /**
     * Finds the index of a currency by code in the currencies list.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param currencies List of currency info objects
     * @param code Currency code to find
     * @return Index of the currency, or 0 if not found
     */
    private int findCurrencyIndex(List<CurrencyService.CurrencyInfo> currencies, String code) {
        for (int i = 0; i < currencies.size(); i++) {
            if (currencies.get(i).getCode().equals(code)) {
                return i;
            }
        }
        return 0; // Default to first currency if not found
    }
    
    /**
     * Custom renderer for currency dropdown items.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     */
    private class CurrencyListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                                                    int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof CurrencyService.CurrencyInfo) {
                CurrencyService.CurrencyInfo currency = (CurrencyService.CurrencyInfo) value;
                setText(currency.getCode() + " - " + currency.getName() + " " + currency.getSymbol());
            }
            
            return this;
        }
    }
    
    /**
     * Main method for testing.
     * [AGENT GENERATED CODE - REQUIREMENT:CURRENCY_CONVERSION_ENHANCEMENT]
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        new CurrencyController("", "");
    }
}