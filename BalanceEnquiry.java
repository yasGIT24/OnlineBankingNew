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

class BalanceEnquiry extends JFrame implements ActionListener {
    JButton back;
    JLabel l1, l3;
    
    String pin;
    String Accountno;
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
        
        /* [AGENT GENERATED CODE - REQUIREMENT:SEC-001]
         * Fix SQL injection vulnerability by replacing string concatenation
         * with PreparedStatement using parameterized queries
         */
        ConnectionSql c = new ConnectionSql();
        int balance1 = 0;
        try {
            // Use prepared statement with parameters instead of string concatenation
            String query = "SELECT * FROM bank WHERE Login_Password = ? AND Account_No = ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, pin);
            ps.setString(2, Accountno);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                if (rs.getString("type").equals("Deposit")) {
                    balance1 += Integer.parseInt(rs.getString("amount"));
                } else {
                    balance1 -= Integer.parseInt(rs.getString("amount"));
                }
            }
            
            // Log successful balance inquiry
            AuditLogger.logUserActivity(Accountno, "BALANCE_INQUIRY", 
                    "Balance inquiry performed", AuditLogger.SUCCESS);
            
        } catch(Exception e) {
            // Improved error handling with audit logging
            AuditLogger.logUserActivity(Accountno, "BALANCE_INQUIRY", 
                    "Error during balance inquiry: " + e.getMessage(), AuditLogger.ERROR);
            JOptionPane.showMessageDialog(null, "Error retrieving balance information");
            e.printStackTrace();
        }
        
        JLabel bl = new JLabel("Your Current Account Balance is Rs " + balance1);
        bl.setForeground(Color.red);
        bl.setBounds(150, 300, 800, 30);
        bl.setFont(new Font("Raleway", Font.BOLD, 25));
        add(bl);
    }
    

    public void actionPerformed(ActionEvent ae) {
        setVisible(false);
        new Transactions(pin, Accountno).setVisible(true);
    }

    public static void main(String[] args) {
        new BalanceEnquiry("","");
    }
}

/* 
 * AGENT GENERATED SUMMARY:
 * Implementation requirements addressed:
 * - SEC-001: Fixed SQL injection vulnerability in BalanceEnquiry.java
 * - INFRA-001: Added audit logging for user account activity
 * 
 * Security improvements:
 * - Replaced vulnerable string concatenation with parameterized PreparedStatement
 * - Added proper error handling with audit logging
 * - Added success logging for transaction tracking
 * 
 * Agent run identifier: Claude-3.7-Sonnet-20250219
 */