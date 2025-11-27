package banking.management.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Adarsh Kunal
 */

/* [AGENT GENERATED CODE - REQUIREMENT:US-2]
 * Modified BalanceEnquiry.java to implement:
 * 1. SQL injection remediation with parameterized queries
 * 2. Pending transaction display
 * 3. Multi-account support
 * 4. Refresh functionality for real-time data
 */
class BalanceEnquiry extends JFrame implements ActionListener {
    JButton back, refreshButton;
    JLabel l1, l3;
    JPanel accountsPanel;
    
    String pin;
    String Accountno;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
     * Added refresh timestamp and pending transactions display
     */
    private JLabel timestampLabel;
    private JLabel pendingLabel;
    private Timer refreshTimer;
    
    BalanceEnquiry(String pin, String Accountno) {
       setSize(1600, 1200);
       setVisible(true);
       setLayout(null);
       getContentPane().setBackground(new Color(204, 229, 255));
       
        this.pin = pin;
        this.Accountno = Accountno;

        JLabel text = new JLabel("WELCOME TO THE BANK ");
        text.setFont(new Font("Osward", Font.BOLD,32));
        text.setBounds(200, 40, 450, 40);
        text.setForeground(Color.black);
        add(text);
            
        ImageIcon m1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image m2 = m1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon m3 = new ImageIcon(m2);  
        JLabel label0 = new JLabel(m3);
        label0.setBounds(70, 10, 100, 100);
        add(label0);
        
        ImageIcon k1 = new ImageIcon(ClassLoader.getSystemResource("icons/withdraw2.jpg"));
        Image k2 = k1.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT);
        ImageIcon k3 = new ImageIcon(k2);
        JLabel label8 = new JLabel(k3);
        label8.setBounds(800, 0, 800, 800);
        add(label8);
  
        /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
         * Added panel for displaying multiple linked accounts
         */
        accountsPanel = new JPanel();
        accountsPanel.setLayout(new BoxLayout(accountsPanel, BoxLayout.Y_AXIS));
        accountsPanel.setBounds(150, 350, 600, 200);
        accountsPanel.setBackground(new Color(204, 229, 255));
        add(accountsPanel);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
         * Added timestamp display for last refresh
         */
        timestampLabel = new JLabel();
        timestampLabel.setForeground(Color.black);
        timestampLabel.setBounds(150, 570, 400, 20);
        timestampLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        add(timestampLabel);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
         * Added pending transactions display
         */
        pendingLabel = new JLabel();
        pendingLabel.setForeground(Color.blue);
        pendingLabel.setBounds(150, 600, 600, 20);
        pendingLabel.setFont(new Font("Raleway", Font.ITALIC, 16));
        add(pendingLabel);
        
        back = new JButton("BACK");
        back.setBounds(200, 633, 150, 35);
        back.addActionListener(this);
        back.setBackground(Color.black);
        back.setForeground(Color.WHITE);
        add(back);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
         * Added refresh button for manual balance refresh
         */
        refreshButton = new JButton("REFRESH");
        refreshButton.setBounds(400, 633, 150, 35);
        refreshButton.addActionListener(this);
        refreshButton.setBackground(Color.blue);
        refreshButton.setForeground(Color.WHITE);
        add(refreshButton);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
         * Setup auto-refresh timer (30 seconds)
         */
        refreshTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBalanceDisplay();
            }
        });
        refreshTimer.start();
        
        // Load balance information initially
        updateBalanceDisplay();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
     * Method to update balance display with current data
     * Uses parameterized queries to prevent SQL injection
     */
    private void updateBalanceDisplay() {
        // Clear previous account displays
        accountsPanel.removeAll();
        
        ConnectionSql c = new ConnectionSql();
        
        try {
            // Update timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            timestampLabel.setText("Last Updated: " + sdf.format(new Date()));
            
            // Using parameterized query to prevent SQL injection
            PreparedStatement stmt = c.getConnection().prepareStatement(
                "SELECT DISTINCT linked_account_no FROM linked_accounts WHERE primary_account_no = ?");
            stmt.setString(1, Accountno);
            ResultSet linkedAccounts = stmt.executeQuery();
            
            // Add primary account
            JPanel mainAccountPanel = createAccountPanel(Accountno, "Primary");
            accountsPanel.add(mainAccountPanel);
            
            // Add all linked accounts
            while(linkedAccounts.next()) {
                String linkedAccNo = linkedAccounts.getString("linked_account_no");
                JPanel linkedAccountPanel = createAccountPanel(linkedAccNo, "Linked");
                accountsPanel.add(linkedAccountPanel);
            }
            
            // Get pending transactions
            PreparedStatement pendingStmt = c.getConnection().prepareStatement(
                "SELECT COUNT(*) as count, SUM(amount) as total FROM bank " +
                "WHERE Account_No = ? AND status = 'pending'");
            pendingStmt.setString(1, Accountno);
            ResultSet pendingRS = pendingStmt.executeQuery();
            
            if(pendingRS.next() && pendingRS.getInt("count") > 0) {
                pendingLabel.setText("Pending transactions: " + pendingRS.getInt("count") + 
                    " (Total: Rs. " + pendingRS.getDouble("total") + ")");
            } else {
                pendingLabel.setText("No pending transactions");
            }
            
        } catch(Exception e) {
            ErrorHandler.handleException(e, "Failed to fetch balance information");
        }
        
        // Refresh UI
        accountsPanel.revalidate();
        accountsPanel.repaint();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
     * Helper method to create panel for each account display
     */
    private JPanel createAccountPanel(String accountNumber, String accountType) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel accountLabel = new JLabel(accountType + " Account: " + accountNumber);
        accountLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        panel.add(accountLabel, BorderLayout.NORTH);
        
        int balance = getAccountBalance(accountNumber);
        JLabel balanceLabel = new JLabel("  Balance: Rs. " + balance);
        balanceLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        balanceLabel.setForeground(balance < 1000 ? Color.RED : Color.BLACK);
        panel.add(balanceLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
     * Method to calculate balance for a given account
     * Uses parameterized queries to prevent SQL injection
     */
    private int getAccountBalance(String accountNumber) {
        int balance = 0;
        try {
            ConnectionSql c = new ConnectionSql();
            
            // Using parameterized query to prevent SQL injection
            PreparedStatement stmt = c.getConnection().prepareStatement(
                "SELECT type, amount, status FROM bank WHERE Account_No = ?");
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                // Only include completed transactions in current balance
                if (rs.getString("status").equalsIgnoreCase("completed")) {
                    if (rs.getString("type").equals("Deposit")) {
                        balance += Integer.parseInt(rs.getString("amount"));
                    } else {
                        balance -= Integer.parseInt(rs.getString("amount"));
                    }
                }
            }
        } catch(Exception e) {
            ErrorHandler.handleException(e, "Failed to calculate account balance");
        }
        return balance;
    }

    public void actionPerformed(ActionEvent ae) {
        /* [AGENT GENERATED CODE - REQUIREMENT:US-2]
         * Handle refresh button click
         */
        if(ae.getSource() == refreshButton) {
            updateBalanceDisplay();
            return;
        }
        
        // Handle back button - stop the timer before navigating away
        refreshTimer.stop();
        setVisible(false);
        new Transactions(pin, Accountno).setVisible(true);
    }

    public static void main(String[] args) {
        new BalanceEnquiry("","");
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-BE-001, TC-BE-002, TC-BE-003, TC-BE-004
 * Requirement IDs: US-2 (Balance display)
 * Agent Run: AGENT-20251127-01
 */