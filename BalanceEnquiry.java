package banking.management.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author Adarsh Kunal
 */

/* [AGENT GENERATED CODE - REQUIREMENT:US1-AC2,US1-AC4] 
 * Modified to support:
 * - Display of pending transactions in balance calculation
 * - Integration with refresh functionality
 * - Multiple linked accounts view
 */
class BalanceEnquiry extends JFrame implements ActionListener {
    JButton back;
    JButton refreshButton; // New refresh button
    JButton viewAllAccountsButton; // New button for linked accounts
    JLabel l1, l3;
    JLabel balanceLabel; // Made balance display a class field for refresh updates
    
    String pin;
    String Accountno;
    RefreshManager refreshManager; // Integration with RefreshManager
    
    BalanceEnquiry(String pin, String Accountno) {
       setSize(1600, 1200);
      // setUndecorated(true);
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
  
        
        back = new JButton("BACK");
        back.setBounds(300, 633, 150, 35);
        back.addActionListener(this);
        back.setBackground(Color.black);
        back.setForeground(Color.WHITE);
        add(back);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
         * Added manual refresh button functionality
         */
        refreshButton = new JButton("REFRESH");
        refreshButton.setBounds(150, 633, 150, 35);
        refreshButton.addActionListener(this);
        refreshButton.setBackground(Color.black);
        refreshButton.setForeground(Color.WHITE);
        add(refreshButton);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
         * Added button to view all linked accounts
         */
        viewAllAccountsButton = new JButton("VIEW ALL ACCOUNTS");
        viewAllAccountsButton.setBounds(450, 633, 200, 35);
        viewAllAccountsButton.addActionListener(this);
        viewAllAccountsButton.setBackground(Color.black);
        viewAllAccountsButton.setForeground(Color.WHITE);
        add(viewAllAccountsButton);
        
        // Initialize balance label
        balanceLabel = new JLabel();
        balanceLabel.setForeground(Color.red);
        balanceLabel.setBounds(150, 300, 800, 30);
        balanceLabel.setFont(new Font("Raleway", Font.BOLD, 25));
        add(balanceLabel);
        
        // Initialize refresh manager with 30 second auto-refresh
        refreshManager = new RefreshManager(this, 30);
        refreshManager.startAutoRefresh();
        
        // Calculate and display initial balance
        updateBalance();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC2,US1-AC4] 
     * New method to update balance display - extracted from constructor
     * Modified to include pending transactions and support refresh
     */
    public void updateBalance() {
        ConnectionSql c = new ConnectionSql();
        int balance1 = 0;
        int pendingBalance = 0;
        
        try {
            // Query for completed transactions
            ResultSet rs = c.s.executeQuery("select * from bank where Login_Password = '" + pin + "' and Account_No = '" + Accountno + "' and status = 'completed'");
            
            while (rs.next()) {
                if (rs.getString("type").equals("Deposit")) {
                    balance1 += Integer.parseInt(rs.getString("amount"));
                } else {
                    balance1 -= Integer.parseInt(rs.getString("amount"));
                }
            }
            
            // Query for pending transactions
            ResultSet pendingRs = c.s.executeQuery("select * from bank where Login_Password = '" + pin + "' and Account_No = '" + Accountno + "' and status = 'pending'");
            
            while (pendingRs.next()) {
                if (pendingRs.getString("type").equals("Deposit")) {
                    pendingBalance += Integer.parseInt(pendingRs.getString("amount"));
                } else {
                    pendingBalance -= Integer.parseInt(pendingRs.getString("amount"));
                }
            }
            
            // Update the balance display with both completed and pending amounts
            balanceLabel.setText("Your Current Account Balance is Rs " + balance1 + 
                               " (Pending: Rs " + pendingBalance + ")");
                               
        } catch(Exception e) {
            System.out.println(e);
            balanceLabel.setText("Error retrieving balance information");
        }
    }

    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Modified to handle refresh button and view all accounts button
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            refreshManager.stopAutoRefresh(); // Stop refresh when leaving
            setVisible(false);
            new Transactions(pin, Accountno).setVisible(true);
        } else if (ae.getSource() == refreshButton) {
            // Manual refresh triggered
            updateBalance();
        } else if (ae.getSource() == viewAllAccountsButton) {
            refreshManager.stopAutoRefresh(); // Stop refresh when leaving
            setVisible(false);
            new LinkedAccountsView(pin, Accountno).setVisible(true);
        }
    }

    public static void main(String[] args) {
        new BalanceEnquiry("","");
    }
}

/* 
 * Test Cases: TC-US1-01, TC-US1-02, TC-US1-03, TC-US1-04
 * Agent Run ID: AR-2025-11-27-001
 */