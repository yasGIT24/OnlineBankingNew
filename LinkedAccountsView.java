package banking.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
 * Created LinkedAccountsView component to implement third acceptance criteria:
 * "User can view balances for multiple linked accounts"
 */
public class LinkedAccountsView extends JFrame implements ActionListener {
    private JButton backButton;
    private JButton refreshButton;
    private JTable accountsTable;
    private String pin;
    private String currentAccountNo;
    private RefreshManager refreshManager;
    private DefaultTableModel tableModel;
    
    public LinkedAccountsView(String pin, String accountNo) {
        this.pin = pin;
        this.currentAccountNo = accountNo;
        
        setTitle("Linked Accounts View");
        setSize(1000, 700);
        setLayout(null);
        getContentPane().setBackground(new Color(204, 229, 255));
        
        // Header
        JLabel title = new JLabel("YOUR LINKED ACCOUNTS");
        title.setFont(new Font("Osward", Font.BOLD, 32));
        title.setBounds(300, 40, 500, 40);
        title.setForeground(Color.black);
        add(title);
        
        // Add bank logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image logoImage = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledLogoIcon = new ImageIcon(logoImage);
        JLabel logoLabel = new JLabel(scaledLogoIcon);
        logoLabel.setBounds(70, 10, 100, 100);
        add(logoLabel);
        
        // Create table model with columns
        String[] columns = {"Account Number", "Account Type", "Balance", "Pending", "Primary Account"};
        tableModel = new DefaultTableModel(columns, 0);
        
        // Create table and scrollpane
        accountsTable = new JTable(tableModel);
        accountsTable.setFont(new Font("Raleway", Font.PLAIN, 16));
        accountsTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBounds(100, 120, 800, 400);
        add(scrollPane);
        
        // Back button
        backButton = new JButton("BACK");
        backButton.setBounds(350, 550, 150, 40);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(this);
        add(backButton);
        
        // Refresh button
        refreshButton = new JButton("REFRESH");
        refreshButton.setBounds(550, 550, 150, 40);
        refreshButton.setBackground(Color.BLACK);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(this);
        add(refreshButton);
        
        // Initialize refresh manager
        refreshManager = new RefreshManager(this, 30);
        refreshManager.startAutoRefresh();
        
        // Load linked accounts data
        loadLinkedAccounts();
        
        setVisible(true);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
     * Method to load and display all linked accounts with their balances
     */
    public void loadLinkedAccounts() {
        // Clear existing table data
        tableModel.setRowCount(0);
        
        try {
            ConnectionSql c = new ConnectionSql();
            
            // First, get the customer ID for the current account
            String customerIdQuery = "SELECT customer_id FROM linked_accounts WHERE account_number = '" + currentAccountNo + "'";
            ResultSet customerRs = c.s.executeQuery(customerIdQuery);
            
            if (customerRs.next()) {
                String customerId = customerRs.getString("customer_id");
                
                // Get all linked accounts for this customer
                String accountsQuery = "SELECT account_number, account_type, is_primary FROM linked_accounts WHERE customer_id = '" + customerId + "'";
                ResultSet accountsRs = c.s.executeQuery(accountsQuery);
                
                List<String[]> accountData = new ArrayList<>();
                
                // Process each linked account
                while (accountsRs.next()) {
                    String accountNumber = accountsRs.getString("account_number");
                    String accountType = accountsRs.getString("account_type");
                    boolean isPrimary = accountsRs.getBoolean("is_primary");
                    
                    // For each account, calculate balance
                    int balance = calculateBalance(c, accountNumber, "completed");
                    int pendingBalance = calculateBalance(c, accountNumber, "pending");
                    
                    // Add row to table
                    String[] row = {
                        accountNumber,
                        accountType,
                        "Rs " + balance,
                        "Rs " + pendingBalance,
                        isPrimary ? "Yes" : "No"
                    };
                    
                    tableModel.addRow(row);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Unable to find customer information for this account.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading linked accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC2,US1-AC3] 
     * Helper method to calculate account balance
     */
    private int calculateBalance(ConnectionSql c, String accountNumber, String status) throws SQLException {
        int balance = 0;
        String balanceQuery = "SELECT type, amount FROM bank WHERE Account_No = '" + accountNumber + 
                              "' AND status = '" + status + "'";
        
        ResultSet balanceRs = c.s.executeQuery(balanceQuery);
        while (balanceRs.next()) {
            String type = balanceRs.getString("type");
            int amount = Integer.parseInt(balanceRs.getString("amount"));
            
            if (type.equals("Deposit")) {
                balance += amount;
            } else {
                balance -= amount;
            }
        }
        
        return balance;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == backButton) {
            refreshManager.stopAutoRefresh();
            setVisible(false);
            new Transactions(pin, currentAccountNo).setVisible(true);
        } else if (ae.getSource() == refreshButton) {
            loadLinkedAccounts();
        }
    }
    
    public static void main(String[] args) {
        new LinkedAccountsView("", "");
    }
}

/* 
 * Test Cases: TC-US1-03, TC-US1-07
 * Agent Run ID: AR-2025-11-27-001
 */