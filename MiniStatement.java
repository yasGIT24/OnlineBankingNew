package banking.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.border.EmptyBorder;

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 4: Transaction History]
 * This class implements the transaction history UI with:
 * 1. Display transactions from the last 6 months
 * 2. Transaction filtering by type, date range, and amount
 * 3. Export options (CSV/Excel)
 * 4. Detailed transaction information display
 *
 * Linked to Value Stream Step: Transaction History Retrieval
 * Linked to test cases: HIST-UI-01, HIST-UI-02, HIST-UI-03
 */
public class MiniStatement extends JFrame implements ActionListener {
    // UI Components
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JButton backButton, exportButton, filterButton, resetButton;
    private JComboBox<String> typeComboBox;
    private JTextField startDateField, endDateField, minAmountField, maxAmountField;
    private JLabel balanceLabel;
    
    // User data
    private String pin;
    private String accountNo;
    
    // Service
    private TransactionHistoryService transactionService;
    private List<TransactionHistoryService.Transaction> currentTransactions;
    
    /**
     * Constructor
     * 
     * @param pin User's PIN
     * @param accountNo User's account number
     */
    public MiniStatement(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        transactionService = new TransactionHistoryService();
        
        // Set up window
        setTitle("Transaction History");
        setSize(1600, 1200);
        setLayout(new BorderLayout());
        
        // Validate session before proceeding
        LoginModel loginModel = new LoginModel();
        if (!loginModel.isSessionValid()) {
            JOptionPane.showMessageDialog(this, 
                "Your session has expired. Please login again.", 
                "Session Timeout", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Create panels
        JPanel headerPanel = createHeaderPanel();
        JPanel filterPanel = createFilterPanel();
        JPanel tablePanel = createTablePanel();
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to frame
        add(headerPanel, BorderLayout.NORTH);
        add(filterPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load initial transaction data
        loadTransactions(null, null, null, null, null);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    /**
     * Create the header panel with title and logo
     * 
     * @return Header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(204, 229, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Bank logo
        ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image h2 = h1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon h3 = new ImageIcon(h2);  
        JLabel logo = new JLabel(h3);
        panel.add(logo, BorderLayout.WEST);
        
        // Header
        JLabel header = new JLabel("Transaction History");
        header.setFont(new Font("Osward", Font.BOLD, 32));
        header.setHorizontalAlignment(JLabel.CENTER);
        header.setForeground(Color.BLACK);
        panel.add(header, BorderLayout.CENTER);
        
        // Account info panel
        JPanel accountPanel = new JPanel(new GridLayout(2, 1));
        accountPanel.setBackground(new Color(204, 229, 255));
        
        JLabel accountLabel = new JLabel("Account: " + accountNo);
        accountLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        accountLabel.setHorizontalAlignment(JLabel.RIGHT);
        accountPanel.add(accountLabel);
        
        balanceLabel = new JLabel("Balance: Loading...");
        balanceLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        balanceLabel.setHorizontalAlignment(JLabel.RIGHT);
        balanceLabel.setForeground(Color.BLUE);
        accountPanel.add(balanceLabel);
        
        panel.add(accountPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Create the filter panel with date range, type, and amount filters
     * 
     * @return Filter panel
     */
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(204, 229, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Filters"));
        panel.setPreferredSize(new Dimension(300, 400));
        
        // Date range filter
        JPanel datePanel = new JPanel(new GridLayout(4, 1, 5, 5));
        datePanel.setBackground(new Color(204, 229, 255));
        datePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel dateLabel = new JLabel("Date Range:");
        dateLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        datePanel.add(dateLabel);
        
        // Create date placeholders with default 6-month range
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String todayStr = dateFormat.format(calendar.getTime());
        
        calendar.add(Calendar.MONTH, -6);
        String sixMonthsAgoStr = dateFormat.format(calendar.getTime());
        
        JPanel startDatePanel = new JPanel(new BorderLayout());
        startDatePanel.setBackground(new Color(204, 229, 255));
        startDatePanel.add(new JLabel("From:"), BorderLayout.WEST);
        startDateField = new JTextField(sixMonthsAgoStr);
        startDatePanel.add(startDateField, BorderLayout.CENTER);
        datePanel.add(startDatePanel);
        
        JPanel endDatePanel = new JPanel(new BorderLayout());
        endDatePanel.setBackground(new Color(204, 229, 255));
        endDatePanel.add(new JLabel("To:"), BorderLayout.WEST);
        endDateField = new JTextField(todayStr);
        endDatePanel.add(endDateField, BorderLayout.CENTER);
        datePanel.add(endDatePanel);
        
        panel.add(datePanel);
        
        // Transaction type filter
        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setBackground(new Color(204, 229, 255));
        typePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel typeLabel = new JLabel("Transaction Type:");
        typeLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        typePanel.add(typeLabel, BorderLayout.NORTH);
        
        typeComboBox = new JComboBox<>(new String[]{
            TransactionHistoryService.TYPE_ALL,
            TransactionHistoryService.TYPE_DEPOSIT,
            TransactionHistoryService.TYPE_WITHDRAWAL,
            TransactionHistoryService.TYPE_TRANSFER_IN,
            TransactionHistoryService.TYPE_TRANSFER_OUT
        });
        typeComboBox.setFont(new Font("Raleway", Font.PLAIN, 14));
        typePanel.add(typeComboBox, BorderLayout.CENTER);
        
        panel.add(typePanel);
        
        // Amount range filter
        JPanel amountPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        amountPanel.setBackground(new Color(204, 229, 255));
        amountPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel amountLabel = new JLabel("Amount Range:");
        amountLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        amountPanel.add(amountLabel);
        
        JPanel minAmountPanel = new JPanel(new BorderLayout());
        minAmountPanel.setBackground(new Color(204, 229, 255));
        minAmountPanel.add(new JLabel("Min:"), BorderLayout.WEST);
        minAmountField = new JTextField();
        minAmountPanel.add(minAmountField, BorderLayout.CENTER);
        amountPanel.add(minAmountPanel);
        
        JPanel maxAmountPanel = new JPanel(new BorderLayout());
        maxAmountPanel.setBackground(new Color(204, 229, 255));
        maxAmountPanel.add(new JLabel("Max:"), BorderLayout.WEST);
        maxAmountField = new JTextField();
        maxAmountPanel.add(maxAmountField, BorderLayout.CENTER);
        amountPanel.add(maxAmountPanel);
        
        panel.add(amountPanel);
        
        // Filter buttons
        JPanel filterButtonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        filterButtonPanel.setBackground(new Color(204, 229, 255));
        filterButtonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        filterButton = new JButton("Apply Filters");
        filterButton.setBackground(Color.BLACK);
        filterButton.setForeground(Color.WHITE);
        filterButton.setFont(new Font("Raleway", Font.BOLD, 14));
        filterButton.addActionListener(this);
        filterButtonPanel.add(filterButton);
        
        resetButton = new JButton("Reset Filters");
        resetButton.setBackground(Color.GRAY);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Raleway", Font.BOLD, 14));
        resetButton.addActionListener(this);
        filterButtonPanel.add(resetButton);
        
        panel.add(filterButtonPanel);
        
        panel.add(Box.createVerticalGlue()); // Add spacing
        
        return panel;
    }
    
    /**
     * Create the table panel for displaying transactions
     * 
     * @return Table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create table model with columns
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        tableModel.addColumn("Date");
        tableModel.addColumn("Type");
        tableModel.addColumn("Reference");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Balance");
        
        // Create the table with the model
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Raleway", Font.PLAIN, 14));
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Raleway", Font.BOLD, 16));
        transactionTable.getTableHeader().setBackground(new Color(204, 229, 255));
        
        // Set column widths
        TableColumnModel columnModel = transactionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(150); // Date
        columnModel.getColumn(1).setPreferredWidth(200); // Type
        columnModel.getColumn(2).setPreferredWidth(150); // Reference
        columnModel.getColumn(3).setPreferredWidth(100); // Amount
        columnModel.getColumn(4).setPreferredWidth(100); // Balance
        
        // Custom cell renderer for amounts (credit/debit colors)
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 3) { // Amount column
                    String amount = (String) value;
                    if (amount.startsWith("-")) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(new Color(0, 128, 0)); // Dark green
                    }
                } else {
                    c.setForeground(table.getForeground());
                }
                
                return c;
            }
        };
        amountRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        columnModel.getColumn(3).setCellRenderer(amountRenderer);
        columnModel.getColumn(4).setCellRenderer(amountRenderer);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create the button panel with back and export buttons
     * 
     * @return Button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(204, 229, 255));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        exportButton = new JButton("Export");
        exportButton.setBackground(new Color(0, 102, 204));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFont(new Font("Raleway", Font.BOLD, 14));
        exportButton.addActionListener(this);
        panel.add(exportButton);
        
        backButton = new JButton("Back");
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Raleway", Font.BOLD, 14));
        backButton.addActionListener(this);
        panel.add(backButton);
        
        return panel;
    }
    
    /**
     * Load transactions with the given filters
     * 
     * @param startDate Start date
     * @param endDate End date
     * @param type Transaction type
     * @param minAmount Minimum amount
     * @param maxAmount Maximum amount
     */
    private void loadTransactions(Date startDate, Date endDate, String type, Double minAmount, Double maxAmount) {
        try {
            // Clear existing table data
            tableModel.setRowCount(0);
            
            // If type is the "All" option, set to null for the service
            String filterType = type;
            if (type != null && type.equals(TransactionHistoryService.TYPE_ALL)) {
                filterType = null;
            }
            
            // Get transactions from service
            currentTransactions = transactionService.getTransactionHistory(
                accountNo, startDate, endDate, filterType, minAmount, maxAmount, pin);
            
            // Populate table with transactions
            for (TransactionHistoryService.Transaction transaction : currentTransactions) {
                String amountDisplay = transaction.getAmount();
                if (!transaction.isCredit()) {
                    amountDisplay = "-" + amountDisplay;
                }
                
                tableModel.addRow(new Object[]{
                    formatDateTime(transaction.getDate()),
                    transaction.getType(),
                    transaction.getReference(),
                    amountDisplay,
                    transaction.getBalance()
                });
            }
            
            // Update balance label with the latest balance
            if (!currentTransactions.isEmpty()) {
                TransactionHistoryService.Transaction latestTransaction = currentTransactions.get(0);
                balanceLabel.setText("Balance: Rs. " + latestTransaction.getBalance());
            } else {
                balanceLabel.setText("Balance: Rs. 0.00");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading transactions: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Format datetime string to more readable format
     * 
     * @param dateTimeStr DateTime string
     * @return Formatted date string
     */
    private String formatDateTime(String dateTimeStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
            Date date = inputFormat.parse(dateTimeStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateTimeStr;
        }
    }
    
    /**
     * Parse date from string
     * 
     * @param dateStr Date string
     * @return Date object
     */
    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Parse amount from string
     * 
     * @param amountStr Amount string
     * @return Amount as Double
     */
    private Double parseAmount(String amountStr) {
        try {
            if (amountStr == null || amountStr.trim().isEmpty()) {
                return null;
            }
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Export transactions to file
     * 
     * @param fileType Export file type
     */
    private void exportTransactions(String fileType) {
        if (currentTransactions == null || currentTransactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No transactions to export", 
                "Export Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save " + fileType.toUpperCase() + " File");
            
            if (fileType.equals("csv") || fileType.equals("excel")) {
                fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
                fileChooser.setSelectedFile(new File("transactions.csv"));
            } else if (fileType.equals("pdf")) {
                fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
                fileChooser.setSelectedFile(new File("transactions.pdf"));
            }
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                boolean success = false;
                if (fileType.equals("csv")) {
                    success = transactionService.exportToCSV(currentTransactions, selectedFile.getAbsolutePath());
                } else if (fileType.equals("excel")) {
                    success = transactionService.exportToExcel(currentTransactions, selectedFile.getAbsolutePath());
                } else if (fileType.equals("pdf")) {
                    success = transactionService.exportToPDF(currentTransactions, selectedFile.getAbsolutePath());
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Transactions exported successfully to " + selectedFile.getAbsolutePath(), 
                        "Export Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to export transactions", 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error exporting transactions: " + e.getMessage(), 
                "Export Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Show export options dialog
     */
    private void showExportOptions() {
        String[] options = {"CSV", "Excel", "PDF", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Choose export format:", 
            "Export Options", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[0]);
        
        if (choice == 0) {
            exportTransactions("csv");
        } else if (choice == 1) {
            exportTransactions("excel");
        } else if (choice == 2) {
            exportTransactions("pdf");
        }
    }
    
    /**
     * Handle button actions
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == backButton) {
                // Go back to transactions menu
                setVisible(false);
                new Transactions(pin, accountNo).setVisible(true);
                
            } else if (ae.getSource() == exportButton) {
                // Show export options
                showExportOptions();
                
            } else if (ae.getSource() == filterButton) {
                // Apply filters
                Date startDate = parseDate(startDateField.getText());
                Date endDate = parseDate(endDateField.getText());
                String type = (String) typeComboBox.getSelectedItem();
                Double minAmount = parseAmount(minAmountField.getText());
                Double maxAmount = parseAmount(maxAmountField.getText());
                
                loadTransactions(startDate, endDate, type, minAmount, maxAmount);
                
            } else if (ae.getSource() == resetButton) {
                // Reset filters to default
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                endDateField.setText(dateFormat.format(calendar.getTime()));
                
                calendar.add(Calendar.MONTH, -6);
                startDateField.setText(dateFormat.format(calendar.getTime()));
                
                typeComboBox.setSelectedIndex(0);
                minAmountField.setText("");
                maxAmountField.setText("");
                
                // Reload transactions with default filters
                loadTransactions(null, null, null, null, null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        new MiniStatement("", "");
    }
}

/* 
 * Test cases:
 * HIST-UI-01: Verify transaction table displays correctly with date, type, amount, and balance
 * HIST-UI-02: Verify filter functionality by date range, type, and amount
 * HIST-UI-03: Verify export functionality for CSV/Excel formats
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */