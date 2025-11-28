package banking.management.system;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Adarsh Kunal
 */

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 3: Fund Transfer]
 * This file was missing from OnlineBankingNew_main but is referenced by Transactions.java.
 * Security features implemented:
 * 1. SQL injection prevention using PreparedStatement
 * 2. Input validation for amount (numeric only and positive value)
 * 3. Proper exception handling with user feedback
 * 4. Session validation to prevent unauthorized access
 * 
 * Linked to Value Stream Step: Fund Transfer, Transfer Processing
 * Linked to test cases: DEP-01, DEP-02, SEC-04
 */
public class Deposit extends JFrame implements ActionListener 
{

    JLabel l1, l3;
    JTextField t1;
    JButton b1, b2;
    String pin;
    String Accountno;
    Deposit(String pin, String Accountno) {
        this.pin = pin;
        this.Accountno= Accountno;
        setLayout(null);
        
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
        
         ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
         Image h2 = h1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
         ImageIcon h3 = new ImageIcon(h2);  
         JLabel label = new JLabel(h3);
         label.setBounds(70, 30, 100, 100);
         add(label);
        
        
        ImageIcon j1 = new ImageIcon(ClassLoader.getSystemResource("icons/deposit1.jpg"));
        Image j2 = j1.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT);
        ImageIcon j3 = new ImageIcon(j2);
        JLabel label6 = new JLabel(j3);
        label6.setBounds(800, 0, 800, 800);
        add(label6);
     
        JLabel text = new JLabel("Dear Customer, Welcome");
        text.setFont(new Font("Osward", Font.BOLD, 32));
        text.setForeground(Color.black);
        text.setBounds(200, 70, 450, 40);
        add(text);
        
        JLabel l1 = new JLabel("ENTER AMOUNT YOU WANT TO DEPOSIT:");
        l1.setFont(new Font("Osward", Font.BOLD,20));
        l1.setForeground(Color.black);
        l1.setBounds(220, 220, 440, 40);
        add(l1);
        
        
        t1 = new JTextField();
        t1.setFont(new Font("Raleway", 1, 20));
        t1.setBounds(220, 280, 500, 40);
        t1.setBackground(Color.white);
        add(t1);
  
        
        b1 = new JButton("DEPOSIT");
        b1.setBounds(570, 400, 150, 40);
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.white);
        b1.setFont(new Font("Raleway", 1, 15));
        
        b1.addActionListener(this);
        add(b1);
        
        b2 = new JButton("BACK");
        b2.setBounds(570, 458, 150, 40);
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.white);
        b2.setFont(new Font("Raleway", 1, 15));
        b2.addActionListener(this);
        add(b2);

       getContentPane().setBackground(new Color(204, 229, 255));
        setSize(1600, 1200);
       // setUndecorated(true);
        // setLocation(500, 0);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String amount = t1.getText();
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = formatter.format(date);
            
            if (ae.getSource() == b1) {
                if (amount.equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter the Amount you want to Deposit");
                    return;
                } 
                
                // Validate input - ensure amount is numeric and positive
                try {
                    double amountValue = Double.parseDouble(amount);
                    if (amountValue <= 0) {
                        JOptionPane.showMessageDialog(null, 
                            "Please enter a positive amount", 
                            "Invalid Amount", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, 
                        "Please enter a valid numeric amount", 
                        "Invalid Input", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Use PreparedStatement to prevent SQL injection
                ConnectionSql c = new ConnectionSql();
                PreparedStatement ps = null;
                
                try {
                    String query = "INSERT INTO bank VALUES (?, ?, ?, ?, ?)";
                    ps = c.prepareStatement(query);
                    ps.setString(1, pin);
                    ps.setString(2, Accountno);
                    ps.setString(3, formattedDate);
                    ps.setString(4, "Deposit");
                    ps.setString(5, amount);
                    
                    int rowsAffected = ps.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Rs. " + amount + " Deposited Successfully");
                        setVisible(false);
                        new Transactions(pin, Accountno).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, 
                            "Transaction failed. Please try again.", 
                            "Transaction Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    System.out.println("Database error: " + e.getMessage());
                    JOptionPane.showMessageDialog(null, 
                        "Error processing your deposit. Please try again later.", 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        if (ps != null) ps.close();
                        c.closeConnection();
                    } catch (SQLException e) {
                        System.out.println("Error closing resources: " + e.getMessage());
                    }
                }
            } else if (ae.getSource() == b2) {
                setVisible(false);
                new Transactions(pin, Accountno).setVisible(true);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "An unexpected error occurred. Please try again.", 
                "System Error", 
                JOptionPane.ERROR_MESSAGE);
        }
   }

    public static void main(String[] args) {
        new Deposit("", "");
    }
}

/* 
 * Test cases:
 * DEP-01: Verify successful deposit with valid amount
 * DEP-02: Verify input validation for negative or invalid amounts
 * SEC-04: Verify SQL injection prevention in deposit operation
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */