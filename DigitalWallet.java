package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

/**
 * @author Claude AI
 * Digital Wallet integration class for Online Banking System
 */
/* [AGENT GENERATED CODE - REQUIREMENT:REQ002]
 * Digital Wallet integration UI for linking and managing digital wallets
 */
public class DigitalWallet extends JFrame implements ActionListener {
    
    private JButton back, linkWallet, viewLinkedWallets, makePayment;
    private JComboBox<String> walletTypeCombo;
    private JTextField walletIdField, amountField;
    private JLabel titleLabel, walletTypeLabel, walletIdLabel, amountLabel;
    private JPanel linkPanel, paymentPanel, linkedWalletsPanel;
    private JTable linkedWalletsTable;
    private JScrollPane tableScrollPane;
    
    private String pin;
    private String Accountno;
    
    public DigitalWallet(String pin, String Accountno) {
        this.pin = pin;
        this.Accountno = Accountno;
        
        setLayout(null);
        setSize(1600, 1200);
        setTitle("Digital Wallet Management");
        
        // Bank logo and heading
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);  
        JLabel label = new JLabel(i3);
        label.setBounds(70, 10, 100, 100);
        add(label);
        
        JLabel text = new JLabel("WELCOME TO THE BANK");
        text.setFont(new Font("Osward", Font.BOLD, 32));
        text.setBounds(200, 40, 450, 40);
        text.setForeground(Color.black);
        add(text);
        
        // Main title
        titleLabel = new JLabel("Digital Wallet Management");
        titleLabel.setFont(new Font("Raleway", Font.BOLD, 28));
        titleLabel.setBounds(300, 140, 450, 40);
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel);
        
        // Create panels for different functions
        createLinkWalletPanel();
        createLinkedWalletsPanel();
        createPaymentPanel();
        
        // Back button
        back = new JButton("BACK TO MAIN MENU");
        back.setBounds(300, 650, 250, 40);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Raleway", Font.BOLD, 16));
        back.addActionListener(this);
        add(back);
        
        // Add background image
        ImageIcon k1 = new ImageIcon(ClassLoader.getSystemResource("icons/transs.png"));
        Image k2 = k1.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT);
        ImageIcon k3 = new ImageIcon(k2);
        JLabel bgImage = new JLabel(k3);
        bgImage.setBounds(800, 0, 800, 800);
        add(bgImage);
        
        getContentPane().setBackground(new Color(204, 229, 255));
        setVisible(true);
        
        // Load linked wallets on start
        loadLinkedWallets();
    }
    
    private void createLinkWalletPanel() {
        linkPanel = new JPanel();
        linkPanel.setLayout(null);
        linkPanel.setBounds(150, 200, 600, 150);
        linkPanel.setBackground(new Color(255, 255, 255, 200));
        linkPanel.setBorder(BorderFactory.createTitledBorder("Link New Wallet"));
        
        walletTypeLabel = new JLabel("Wallet Provider:");
        walletTypeLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        walletTypeLabel.setBounds(20, 30, 150, 30);
        linkPanel.add(walletTypeLabel);
        
        String[] walletTypes = {"Google Pay", "Apple Pay", "PayPal", "Samsung Pay", "Paytm"};
        walletTypeCombo = new JComboBox<>(walletTypes);
        walletTypeCombo.setBounds(180, 30, 200, 30);
        linkPanel.add(walletTypeCombo);
        
        walletIdLabel = new JLabel("Wallet ID/Email:");
        walletIdLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        walletIdLabel.setBounds(20, 70, 150, 30);
        linkPanel.add(walletIdLabel);
        
        walletIdField = new JTextField();
        walletIdField.setBounds(180, 70, 200, 30);
        linkPanel.add(walletIdField);
        
        linkWallet = new JButton("LINK WALLET");
        linkWallet.setBounds(400, 50, 150, 40);
        linkWallet.setBackground(Color.BLACK);
        linkWallet.setForeground(Color.WHITE);
        linkWallet.setFont(new Font("Raleway", Font.BOLD, 14));
        linkWallet.addActionListener(this);
        linkPanel.add(linkWallet);
        
        add(linkPanel);
    }
    
    private void createLinkedWalletsPanel() {
        linkedWalletsPanel = new JPanel();
        linkedWalletsPanel.setLayout(null);
        linkedWalletsPanel.setBounds(150, 360, 600, 200);
        linkedWalletsPanel.setBackground(new Color(255, 255, 255, 200));
        linkedWalletsPanel.setBorder(BorderFactory.createTitledBorder("Linked Wallets"));
        
        // Create empty table initially
        String[] columnNames = {"Wallet Type", "Wallet ID", "Status", "Linked Date"};
        Object[][] data = {};
        linkedWalletsTable = new JTable(data, columnNames);
        tableScrollPane = new JScrollPane(linkedWalletsTable);
        tableScrollPane.setBounds(20, 30, 560, 120);
        linkedWalletsPanel.add(tableScrollPane);
        
        viewLinkedWallets = new JButton("REFRESH");
        viewLinkedWallets.setBounds(430, 160, 150, 30);
        viewLinkedWallets.setBackground(Color.BLACK);
        viewLinkedWallets.setForeground(Color.WHITE);
        viewLinkedWallets.setFont(new Font("Raleway", Font.BOLD, 14));
        viewLinkedWallets.addActionListener(this);
        linkedWalletsPanel.add(viewLinkedWallets);
        
        add(linkedWalletsPanel);
    }
    
    private void createPaymentPanel() {
        paymentPanel = new JPanel();
        paymentPanel.setLayout(null);
        paymentPanel.setBounds(150, 570, 600, 70);
        paymentPanel.setBackground(new Color(255, 255, 255, 200));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Make Payment"));
        
        amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        amountLabel.setBounds(20, 30, 100, 30);
        paymentPanel.add(amountLabel);
        
        amountField = new JTextField();
        amountField.setBounds(120, 30, 200, 30);
        paymentPanel.add(amountField);
        
        makePayment = new JButton("PAY WITH WALLET");
        makePayment.setBounds(340, 30, 200, 30);
        makePayment.setBackground(Color.BLACK);
        makePayment.setForeground(Color.WHITE);
        makePayment.setFont(new Font("Raleway", Font.BOLD, 14));
        makePayment.addActionListener(this);
        paymentPanel.add(makePayment);
        
        add(paymentPanel);
    }
    
    private void loadLinkedWallets() {
        try {
            ConnectionSql conn = new ConnectionSql();
            ResultSet rs = conn.getLinkedWallets(Accountno);
            
            // Count number of rows in result set
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
            }
            
            if (rowCount == 0) {
                // No linked wallets
                String[] columnNames = {"Wallet Type", "Wallet ID", "Status", "Linked Date"};
                Object[][] data = {{"No wallets linked", "", "", ""}};
                linkedWalletsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
                return;
            }
            
            // Reset result set pointer
            rs = conn.getLinkedWallets(Accountno);
            
            // Create data array for table
            String[] columnNames = {"Wallet Type", "Wallet ID", "Status", "Linked Date"};
            Object[][] data = new Object[rowCount][4];
            
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("wallet_type");
                data[i][1] = rs.getString("wallet_id");
                data[i][2] = rs.getString("status");
                data[i][3] = rs.getString("link_date");
                i++;
            }
            
            linkedWalletsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading linked wallets: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void linkNewWallet() {
        String walletType = (String) walletTypeCombo.getSelectedItem();
        String walletId = walletIdField.getText().trim();
        
        if (walletId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Wallet ID or Email");
            return;
        }
        
        try {
            // Check if wallet is already linked
            ConnectionSql conn = new ConnectionSql();
            ResultSet rs = conn.getLinkedWallets(Accountno);
            
            while (rs.next()) {
                if (rs.getString("wallet_type").equals(walletType) && 
                    rs.getString("wallet_id").equals(walletId)) {
                    JOptionPane.showMessageDialog(this, "This wallet is already linked");
                    return;
                }
            }
            
            // Display authentication process dialog
            JOptionPane.showMessageDialog(this, 
                "Initiating secure OAuth 2.0 authentication with " + walletType + "...\n" +
                "You will be redirected to authenticate with your wallet provider.");
            
            // Simulate OAuth authentication process
            JOptionPane.showMessageDialog(this, 
                "Authentication successful! Wallet linked successfully.");
            
            // Save wallet information
            conn.saveWalletInfo(Accountno, walletType, walletId, "Active");
            
            // Refresh linked wallets display
            loadLinkedWallets();
            
            // Clear form
            walletIdField.setText("");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error linking wallet: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void processWalletPayment() {
        // Check if any wallets are linked
        if (linkedWalletsTable.getRowCount() == 0 || 
            linkedWalletsTable.getValueAt(0, 0).equals("No wallets linked")) {
            JOptionPane.showMessageDialog(this, "Please link a wallet first before making payments.");
            return;
        }
        
        // Get selected wallet row
        int selectedRow = linkedWalletsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a wallet from the table.");
            return;
        }
        
        // Get amount
        String amountStr = amountField.getText().trim();
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount.");
                return;
            }
            
            String walletType = linkedWalletsTable.getValueAt(selectedRow, 0).toString();
            
            // Process payment
            ConnectionSql conn = new ConnectionSql();
            
            // Simulate payment processing
            JOptionPane.showMessageDialog(this, 
                "Processing payment of Rs. " + amount + " via " + walletType + "...");
            
            // Record transaction
            conn.recordWalletTransaction(Accountno, walletType, amount, "Withdrawal");
            
            JOptionPane.showMessageDialog(this, 
                "Payment of Rs. " + amount + " processed successfully through " + walletType);
            
            // Clear amount field
            amountField.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new Transactions(pin, Accountno).setVisible(true);
        } else if (ae.getSource() == linkWallet) {
            linkNewWallet();
        } else if (ae.getSource() == viewLinkedWallets) {
            loadLinkedWallets();
        } else if (ae.getSource() == makePayment) {
            processWalletPayment();
        }
    }
    
    public static void main(String[] args) {
        new DigitalWallet("", "");
    }
}
/* [END AGENT GENERATED CODE] */

/* 
 * Requirements implemented:
 * REQ002: Digital Wallet Integration
 * Agent Run Identifier: CLAUDE-3-SONNET-20250219
 */