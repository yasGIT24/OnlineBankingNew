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

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 2: View Account Balance]
 * Security fixes:
 * 1. SQL injection vulnerability fixed by using PreparedStatement
 * 2. Added proper exception handling with user feedback
 * 3. Added session validation to prevent unauthorized access
 * 
 * Linked to Value Stream Step: Account Summary View
 * Linked to test cases: BAL-01, BAL-02, SEC-03
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
        
        // Use secure connection and prepared statement to prevent SQL injection
        ConnectionSql c = new ConnectionSql();
        int balance1 = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String query = "SELECT * FROM bank WHERE Login_Password = ? AND Account_No = ?";
            ps = c.prepareStatement(query);
            ps.setString(1, pin);
            ps.setString(2, Accountno);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                if (rs.getString("type").equals("Deposit")) {
                    balance1 += Integer.parseInt(rs.getString("amount"));
                } else {
                    balance1 -= Integer.parseInt(rs.getString("amount"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error retrieving account balance. Please try again later.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                // Connection will be closed by ConnectionSql's closeConnection method
                c.closeConnection();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
        
        JLabel bl = new JLabel("Your Current Account Balance is Rs "+balance1);
        bl.setForeground(Color.red);
        bl.setBounds(150, 300, 800, 30);
        bl.setFont(new Font("Raleway", Font.BOLD, 25));
        add(bl);
    }
    

    public void actionPerformed(ActionEvent ae) {
        setVisible(false);
        new Transactions(pin, Accountno).setVisible(true);
    }

    // Main method for testing purposes only - should be removed in production
    public static void main(String[] args) {
        new BalanceEnquiry("","");
    }
}

/* 
 * Test cases:
 * BAL-01: Verify balance calculation correctly adds deposits and subtracts withdrawals
 * BAL-02: Verify proper display of account balance with currency
 * SEC-03: Verify SQL injection prevention in balance query
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */