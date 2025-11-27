package banking.management.system;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

/* [AGENT GENERATED CODE - REQUIREMENT:US1-AC1] 
 * Created Login component to implement first acceptance criteria:
 * "Balance is displayed immediately after login"
 */
public class Login extends JFrame implements ActionListener {
    JLabel l1, l2, l3;
    JTextField tf1;
    JPasswordField pf2;
    JButton b1, b2, b3;

    Login() {
        setTitle("BANK MANAGEMENT SYSTEM");
        
        setLayout(null);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l11 = new JLabel(i3);
        l11.setBounds(70, 10, 100, 100);
        add(l11);
        
        l1 = new JLabel("WELCOME TO BANK");
        l1.setFont(new Font("Osward", Font.BOLD, 38));
        l1.setBounds(200, 40, 450, 40);
        add(l1);
        
        l2 = new JLabel("Account No:");
        l2.setFont(new Font("Raleway", Font.BOLD, 28));
        l2.setBounds(125, 150, 375, 30);
        add(l2);
        
        tf1 = new JTextField(15);
        tf1.setBounds(300, 150, 230, 30);
        tf1.setFont(new Font("Arial", Font.BOLD, 14));
        add(tf1);
        
        l3 = new JLabel("PIN:");
        l3.setFont(new Font("Raleway", Font.BOLD, 28));
        l3.setBounds(125, 220, 375, 30);
        add(l3);
        
        pf2 = new JPasswordField(15);
        pf2.setFont(new Font("Arial", Font.BOLD, 14));
        pf2.setBounds(300, 220, 230, 30);
        add(pf2);
                
        b1 = new JButton("SIGN IN");
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.WHITE);
        b1.setFont(new Font("Arial", Font.BOLD, 14));
        b1.setBounds(300, 300, 100, 30);
        b1.addActionListener(this);
        add(b1);
        
        b2 = new JButton("CLEAR");
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.WHITE);
        b2.setFont(new Font("Arial", Font.BOLD, 14));
        b2.setBounds(430, 300, 100, 30);
        b2.addActionListener(this);
        add(b2);
        
        b3 = new JButton("SIGN UP");
        b3.setBackground(Color.BLACK);
        b3.setForeground(Color.WHITE);
        b3.setFont(new Font("Arial", Font.BOLD, 14));
        b3.setBounds(365, 350, 100, 30);
        b3.addActionListener(this);
        add(b3);
        
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/transs.png"));
        Image i5 = i4.getImage().getScaledInstance(600, 600, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel l4 = new JLabel(i6);
        l4.setBounds(700, 10, 600, 600);
        add(l4);
        
        getContentPane().setBackground(Color.WHITE);
        
        setSize(1350, 700);
        setLocation(300, 200);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == b1) {
                ConnectionSql c1 = new ConnectionSql();
                
                String accountNo = tf1.getText();
                String pin = pf2.getText();
                
                // Check if fields are empty
                if (accountNo.equals("") || pin.equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter Account No and PIN");
                    return;
                }
                
                String q = "select * from login where Account_No = '" + accountNo + "' and Login_Password = '" + pin + "'";
                ResultSet rs = c1.s.executeQuery(q);
                
                if (rs.next()) {
                    setVisible(false);
                    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC1] 
                     * Direct navigation to Transactions screen to show balance immediately
                     * Transactions screen has been modified to display balance without
                     * requiring explicit navigation to balance enquiry
                     */
                    new Transactions(pin, accountNo).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Account No or PIN");
                }
                
            } else if (ae.getSource() == b2) {
                tf1.setText("");
                pf2.setText("");
            } else if (ae.getSource() == b3) {
                setVisible(false);
                new Signup1().setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Login();
    }
}

/* 
 * Test Cases: TC-US1-01, TC-US1-06
 * Agent Run ID: AR-2025-11-27-001
 */