package banking.management.system;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;
import java.text.DecimalFormat;
import com.toedter.calendar.JDateChooser;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US2]
 * This class provides the UI for digital wallet integration.
 * It allows users to link, view, and manage digital wallets.
 */
public class DigitalWalletSection extends JFrame implements ActionListener {
    
    private final String pin;
    private final String accountNo;
    private final WalletIntegrationService walletService;
    private final RecurringTransactionService recurringService;
    private final AuditLogger auditLogger;
    
    private JTabbedPane tabbedPane;
    private JPanel linkWalletPanel, viewWalletsPanel, transactionsPanel, recurringPanel;
    private JComboBox<String> walletTypeCombo;
    private JTextField walletIdField;
    private JButton linkButton, refreshButton, backButton;
    private JButton createTransactionButton, viewTransactionsButton;
    private JButton createRecurringButton, cancelRecurringButton;
    private JTable walletsTable, transactionsTable, recurringTable;
    private DefaultTableModel walletsTableModel, transactionsTableModel, recurringTableModel;
    
    public DigitalWalletSection(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        this.walletService = new WalletIntegrationService(accountNo, pin);
        this.recurringService = new RecurringTransactionService(accountNo, pin);
        this.auditLogger = new AuditLogger();
        
        // Initialize tables and services
        walletService.ensureWalletTablesExist();
        recurringService.ensureRecurringTransactionTablesExist();
        
        // Set up the UI
        setTitle("Digital Wallets");
        setSize(1600, 1200);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(204, 229, 255));
        
        // Create header
        createHeader();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Raleway", Font.BOLD, 14));
        
        // Create tabs
        createLinkWalletTab();
        createViewWalletsTab();
        createTransactionsTab();
        createRecurringTransactionsTab();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create footer
        createFooter();
        
        // Load initial data
        refreshWalletData();
        
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
        JLabel title = new JLabel("DIGITAL WALLET INTEGRATION");
        title.setFont(new Font("Osward", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(title, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createLinkWalletTab() {
        linkWalletPanel = new JPanel();
        linkWalletPanel.setLayout(null);
        linkWalletPanel.setBackground(new Color(240, 248, 255));
        
        // Add wallet type selection
        JLabel walletTypeLabel = new JLabel("Select Wallet Type:");
        walletTypeLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        walletTypeLabel.setBounds(150, 100, 200, 30);
        linkWalletPanel.add(walletTypeLabel);
        
        walletTypeCombo = new JComboBox<>(new String[]{"GOOGLE_PAY", "APPLE_PAY", "PAYPAL"});
        walletTypeCombo.setFont(new Font("Raleway", Font.PLAIN, 14));
        walletTypeCombo.setBounds(350, 100, 200, 30);
        linkWalletPanel.add(walletTypeCombo);
        
        // Add wallet ID field
        JLabel walletIdLabel = new JLabel("Enter Wallet ID/Email:");
        walletIdLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        walletIdLabel.setBounds(150, 150, 200, 30);
        linkWalletPanel.add(walletIdLabel);
        
        walletIdField = new JTextField();
        walletIdField.setFont(new Font("Raleway", Font.PLAIN, 14));
        walletIdField.setBounds(350, 150, 300, 30);
        linkWalletPanel.add(walletIdField);
        
        // Add auth description
        JLabel authLabel = new JLabel("Authentication Process:");
        authLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        authLabel.setBounds(150, 200, 200, 30);
        linkWalletPanel.add(authLabel);
        
        JTextArea authDescription = new JTextArea(
            "When you click the 'Link Wallet' button, you'll be redirected to the wallet provider's\n" +
            "website to authenticate and authorize access to your wallet. Once authorization\n" +
            "is complete, you'll be redirected back to this application."
        );
        authDescription.setFont(new Font("Raleway", Font.PLAIN, 14));
        authDescription.setBounds(150, 240, 600, 80);
        authDescription.setEditable(false);
        authDescription.setBackground(new Color(240, 248, 255));
        linkWalletPanel.add(authDescription);
        
        // Add link button
        linkButton = new JButton("LINK WALLET");
        linkButton.setFont(new Font("Raleway", Font.BOLD, 16));
        linkButton.setBounds(350, 350, 200, 40);
        linkButton.setBackground(new Color(0, 102, 204));
        linkButton.setForeground(Color.WHITE);
        linkButton.addActionListener(this);
        linkWalletPanel.add(linkButton);
        
        // Add image
        ImageIcon walletIcon = new ImageIcon(ClassLoader.getSystemResource("icons/wallet.png"));
        if (walletIcon.getIconWidth() > 0) {
            Image walletImg = walletIcon.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT);
            ImageIcon scaledWalletIcon = new ImageIcon(walletImg);
            JLabel walletImageLabel = new JLabel(scaledWalletIcon);
            walletImageLabel.setBounds(800, 100, 400, 400);
            linkWalletPanel.add(walletImageLabel);
        }
        
        tabbedPane.addTab("Link Wallet", linkWalletPanel);
    }
    
    private void createViewWalletsTab() {
        viewWalletsPanel = new JPanel();
        viewWalletsPanel.setLayout(new BorderLayout());
        viewWalletsPanel.setBackground(new Color(240, 248, 255));
        
        // Create table for linked wallets
        String[] columns = {"Wallet Type", "Wallet Name", "Wallet ID", "Date Linked", "Balance"};
        walletsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        walletsTable = new JTable(walletsTableModel);
        walletsTable.setFont(new Font("Raleway", Font.PLAIN, 14));
        walletsTable.getTableHeader().setFont(new Font("Raleway", Font.BOLD, 14));
        walletsTable.setRowHeight(30);
        
        JScrollPane walletsScrollPane = new JScrollPane(walletsTable);
        walletsScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        viewWalletsPanel.add(walletsScrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        refreshButton = new JButton("REFRESH");
        refreshButton.setFont(new Font("Raleway", Font.BOLD, 16));
        refreshButton.setBackground(new Color(0, 102, 204));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(this);
        buttonPanel.add(refreshButton);
        
        createTransactionButton = new JButton("NEW TRANSACTION");
        createTransactionButton.setFont(new Font("Raleway", Font.BOLD, 16));
        createTransactionButton.setBackground(new Color(0, 102, 204));
        createTransactionButton.setForeground(Color.WHITE);
        createTransactionButton.addActionListener(this);
        buttonPanel.add(createTransactionButton);
        
        viewWalletsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("My Wallets", viewWalletsPanel);
    }
    
    private void createTransactionsTab() {
        transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new BorderLayout());
        transactionsPanel.setBackground(new Color(240, 248, 255));
        
        // Create table for wallet transactions
        String[] columns = {"Wallet Type", "Transaction ID", "Amount", "Description", "Date"};
        transactionsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionsTable = new JTable(transactionsTableModel);
        transactionsTable.setFont(new Font("Raleway", Font.PLAIN, 14));
        transactionsTable.getTableHeader().setFont(new Font("Raleway", Font.BOLD, 14));
        transactionsTable.setRowHeight(30);
        
        JScrollPane transactionsScrollPane = new JScrollPane(transactionsTable);
        transactionsScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        transactionsPanel.add(transactionsScrollPane, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        viewTransactionsButton = new JButton("VIEW TRANSACTIONS");
        viewTransactionsButton.setFont(new Font("Raleway", Font.BOLD, 16));
        viewTransactionsButton.setBackground(new Color(0, 102, 204));
        viewTransactionsButton.setForeground(Color.WHITE);
        viewTransactionsButton.addActionListener(this);
        buttonPanel.add(viewTransactionsButton);
        
        transactionsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Transactions", transactionsPanel);
    }
    
    private void createRecurringTransactionsTab() {
        recurringPanel = new JPanel();
        recurringPanel.setLayout(new BorderLayout());
        recurringPanel.setBackground(new Color(240, 248, 255));
        
        // Create table for recurring transactions
        String[] columns = {"ID", "Wallet Type", "Amount", "Description", "Frequency", "Start Date", "End Date", "Status"};
        recurringTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recurringTable = new JTable(recurringTableModel);
        recurringTable.setFont(new Font("Raleway", Font.PLAIN, 14));
        recurringTable.getTableHeader().setFont(new Font("Raleway", Font.BOLD, 14));
        recurringTable.setRowHeight(30);
        
        JScrollPane recurringScrollPane = new JScrollPane(recurringTable);
        recurringScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        recurringPanel.add(recurringScrollPane, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));
        
        createRecurringButton = new JButton("CREATE RECURRING TRANSACTION");
        createRecurringButton.setFont(new Font("Raleway", Font.BOLD, 16));
        createRecurringButton.setBackground(new Color(0, 102, 204));
        createRecurringButton.setForeground(Color.WHITE);
        createRecurringButton.addActionListener(this);
        buttonPanel.add(createRecurringButton);
        
        cancelRecurringButton = new JButton("CANCEL SELECTED");
        cancelRecurringButton.setFont(new Font("Raleway", Font.BOLD, 16));
        cancelRecurringButton.setBackground(new Color(204, 0, 0));
        cancelRecurringButton.setForeground(Color.WHITE);
        cancelRecurringButton.addActionListener(this);
        buttonPanel.add(cancelRecurringButton);
        
        recurringPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Recurring Transactions", recurringPanel);
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
        if (ae.getSource() == linkButton) {
            handleLinkWallet();
        } else if (ae.getSource() == refreshButton) {
            refreshWalletData();
        } else if (ae.getSource() == createTransactionButton) {
            handleCreateTransaction();
        } else if (ae.getSource() == viewTransactionsButton) {
            handleViewTransactions();
        } else if (ae.getSource() == createRecurringButton) {
            handleCreateRecurringTransaction();
        } else if (ae.getSource() == cancelRecurringButton) {
            handleCancelRecurringTransaction();
        } else if (ae.getSource() == backButton) {
            setVisible(false);
            new Transactions(pin, accountNo).setVisible(true);
        }
    }
    
    private void handleLinkWallet() {
        String walletType = (String) walletTypeCombo.getSelectedItem();
        String walletId = walletIdField.getText().trim();
        
        if (walletId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a wallet ID or email address");
            return;
        }
        
        // In a real application, this would redirect to the wallet provider's OAuth page
        // For this implementation, we'll simulate the OAuth flow
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "This will redirect you to " + walletType + " for authorization.\n" +
            "Do you want to continue?",
            "Authorize Wallet", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Simulate opening the browser for OAuth
                JOptionPane.showMessageDialog(this, 
                    "You'll now be redirected to the provider's website.\n" +
                    "After authorization, you'll return to this application.");
                
                // Simulate successful OAuth
                String authToken = UUID.randomUUID().toString();
                
                // Link the wallet
                boolean success = walletService.linkWallet(walletType, walletId, authToken);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        walletType + " wallet linked successfully!");
                    
                    // Clear form and refresh data
                    walletIdField.setText("");
                    refreshWalletData();
                    
                    // Switch to My Wallets tab
                    tabbedPane.setSelectedIndex(1);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to link wallet. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshWalletData() {
        // Clear existing data
        walletsTableModel.setRowCount(0);
        
        // Get linked wallets
        List<WalletIntegrationService.LinkedWallet> wallets = walletService.getLinkedWallets();
        
        // Formatter for currency values
        DecimalFormat df = new DecimalFormat("#,##0.00");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        // Add data to table
        for (WalletIntegrationService.LinkedWallet wallet : wallets) {
            walletsTableModel.addRow(new Object[]{
                wallet.getType(),
                wallet.getName(),
                wallet.getId(),
                sdf.format(wallet.getLinkDate()),
                "$" + df.format(wallet.getBalance())
            });
        }
        
        // Refresh recurring transactions
        refreshRecurringTransactions();
    }
    
    private void handleCreateTransaction() {
        // Check if any wallets are linked
        if (walletsTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "You don't have any linked wallets. Please link a wallet first.",
                "No Wallets", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected wallet
        int selectedRow = walletsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a wallet from the table.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String walletType = (String) walletsTable.getValueAt(selectedRow, 0);
        String walletName = (String) walletsTable.getValueAt(selectedRow, 1);
        
        // Show transaction dialog
        JTextField amountField = new JTextField();
        JTextField descriptionField = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Wallet: " + walletName));
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Create Transaction", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String description = descriptionField.getText().trim();
                
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a transaction description.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Process the transaction
                boolean success = walletService.processWalletTransaction(
                    walletType, amount, description);
                    
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Transaction processed successfully!");
                        
                    // Refresh data
                    refreshWalletData();
                    
                    // Switch to Transactions tab
                    tabbedPane.setSelectedIndex(2);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to process transaction. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void handleViewTransactions() {
        // Clear existing data
        transactionsTableModel.setRowCount(0);
        
        // Check if any wallets are linked
        if (walletsTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "You don't have any linked wallets. Please link a wallet first.",
                "No Wallets", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected wallet
        int selectedRow = walletsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a wallet from the table.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String walletType = (String) walletsTable.getValueAt(selectedRow, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        // Get transaction history
        List<WalletIntegrationService.WalletTransaction> transactions = 
            walletService.getWalletTransactionHistory(walletType);
            
        // Add data to table
        for (WalletIntegrationService.WalletTransaction transaction : transactions) {
            transactionsTableModel.addRow(new Object[]{
                walletType,
                transaction.getId(),
                "$" + df.format(transaction.getAmount()),
                transaction.getDescription(),
                sdf.format(transaction.getDate())
            });
        }
        
        // Switch to Transactions tab
        tabbedPane.setSelectedIndex(2);
    }
    
    private void handleCreateRecurringTransaction() {
        // Check if any wallets are linked
        if (walletsTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "You don't have any linked wallets. Please link a wallet first.",
                "No Wallets", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the selected wallet
        int selectedRow = walletsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a wallet from the table.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String walletType = (String) walletsTable.getValueAt(selectedRow, 0);
        String walletName = (String) walletsTable.getValueAt(selectedRow, 1);
        
        // Create recurring transaction dialog
        JTextField amountField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<String> frequencyCombo = new JComboBox<>(new String[]{"DAILY", "WEEKLY", "MONTHLY"});
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();
        JCheckBox noEndDateCheckbox = new JCheckBox("No End Date");
        
        noEndDateCheckbox.addActionListener(e -> {
            endDateChooser.setEnabled(!noEndDateCheckbox.isSelected());
        });
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Wallet: " + walletName));
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Frequency:"));
        panel.add(frequencyCombo);
        panel.add(new JLabel("Start Date:"));
        panel.add(startDateChooser);
        panel.add(new JLabel("End Date:"));
        panel.add(endDateChooser);
        panel.add(noEndDateCheckbox);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Create Recurring Transaction", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String description = descriptionField.getText().trim();
                String frequency = (String) frequencyCombo.getSelectedItem();
                Date startDate = startDateChooser.getDate();
                Date endDate = noEndDateCheckbox.isSelected() ? null : endDateChooser.getDate();
                
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a transaction description.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (startDate == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select a start date.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!noEndDateCheckbox.isSelected() && endDate == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Please select an end date or check 'No End Date'.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Setup recurring transaction
                boolean success = walletService.setupRecurringTransaction(
                    walletType, amount, description, frequency, startDate, endDate);
                    
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Recurring transaction setup successfully!");
                        
                    // Refresh data
                    refreshRecurringTransactions();
                    
                    // Switch to Recurring Transactions tab
                    tabbedPane.setSelectedIndex(3);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to setup recurring transaction. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void refreshRecurringTransactions() {
        // Clear existing data
        recurringTableModel.setRowCount(0);
        
        // Get recurring transactions
        List<RecurringTransactionService.RecurringTransaction> transactions = 
            recurringService.getRecurringTransactions(accountNo);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("#,##0.00");
        
        // Add data to table
        for (RecurringTransactionService.RecurringTransaction txn : transactions) {
            recurringTableModel.addRow(new Object[]{
                txn.getId(),
                txn.getWalletType(),
                "$" + df.format(txn.getAmount()),
                txn.getDescription(),
                txn.getFrequency(),
                sdf.format(txn.getStartDate()),
                txn.getEndDate() != null ? sdf.format(txn.getEndDate()) : "No End Date",
                txn.isActive() ? "Active" : "Cancelled"
            });
        }
    }
    
    private void handleCancelRecurringTransaction() {
        // Get the selected recurring transaction
        int selectedRow = recurringTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a recurring transaction from the table.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String recurringId = (String) recurringTable.getValueAt(selectedRow, 0);
        String status = (String) recurringTable.getValueAt(selectedRow, 7);
        
        if (status.equals("Cancelled")) {
            JOptionPane.showMessageDialog(this, 
                "This recurring transaction is already cancelled.",
                "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel this recurring transaction?",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            boolean success = recurringService.cancelRecurringTransaction(accountNo, recurringId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Recurring transaction cancelled successfully!");
                    
                // Refresh data
                refreshRecurringTransactions();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to cancel recurring transaction. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        new DigitalWalletSection("", "");
    }
}

/* 
 * Agent Run Identifier: BANK-WALLET-UI-20251126
 * Related Test Cases: WALLET-UI-001, WALLET-UI-002
 */