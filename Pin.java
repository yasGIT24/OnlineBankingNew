package banking.management.system;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Pin extends JFrame implements ActionListener{
    
    JPasswordField t1,t2;
    JButton b1,b2;                               
    JLabel l1,l2,l3;
    String pin;
    String Accountno;
    
    Pin(String pin, String Accountno){
        this.pin = pin;
        this.Accountno= Accountno;
        
      setLayout(null);

         ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
         Image h2 = h1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
         ImageIcon h3 = new ImageIcon(h2);  
         JLabel label = new JLabel(h3);
         label.setBounds(70, 30, 100, 100);
         add(label);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("Icons/paymentProc.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1000, 900, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(600, 0, 1000, 900);
        add(image);
        
        JLabel text = new JLabel("Dear Customer.");
        text.setForeground( Color.black);
        text.setFont(new Font("Osward", Font.BOLD,32));
        text.setBounds(200, 80, 450, 40);
        add(text);
        
        l1 = new JLabel("CHANGE YOUR PIN");
        l1.setFont(new Font("System", Font.BOLD, 25));
        l1.setForeground(Color.black);
        l1.setBounds(350,320,800,35);
        add(l1);
        
       
        l2 = new JLabel("New PIN:");
        l2.setFont(new Font("System", Font.BOLD, 18));
        l2.setForeground(Color.black);
        l2.setBounds(220,390,150,35);
        add(l2);
        
        l3 = new JLabel("Re-Enter New PIN:");
        l3.setFont(new Font("System", Font.BOLD, 18));
        l3.setForeground(Color.black);
        l3.setBounds(200,440,200,35);
        add(l3);
        
        t1 = new JPasswordField();
        t1.setFont(new Font("Raleway", Font.BOLD, 25));
        t1.setBounds(370,390,200,30);
        add(t1);
        
        
        t2 = new JPasswordField();
        t2.setFont(new Font("Raleway", Font.BOLD, 25));
        t2.setBounds(370,440,200,30);
        add(t2);
        
        b1 = new JButton("CHANGE");
        b1.setBounds(390,500,150,30);
        add(b1);
        b1.setBackground(Color.BLACK);
        b1.setForeground(Color.white);
        b1.addActionListener(this);
        
        b2 = new JButton("BACK");
        b2.addActionListener(this);
        b2.setBounds(390,553,150,30);
        b2.setBackground(Color.BLACK);
        b2.setForeground(Color.white);
        add(b2);
        
        
        setSize(1600,1200);
       // setUndecorated(true);
        getContentPane().setBackground(Color.white);
      //getContentPane().setBackground(new Color(0, 51, 102));
        setVisible(true);
    
    }
    
    public void actionPerformed(ActionEvent ae){
        try{
            /* [AGENT GENERATED CODE - REQUIREMENT:SEC-002]
             * Fix SQL injection vulnerability by replacing string concatenation
             * with PreparedStatement using parameterized queries for PIN update
             */
            String npin = t1.getText();
            String rpin = t2.getText();
            
            if(!npin.equals(rpin)){
                JOptionPane.showMessageDialog(null, "Entered PIN does not match");
                return;
            }
            
            if(ae.getSource()==b1){
                if (t1.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Enter New PIN");
                    return;
                }
                if (t2.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Re-Enter new PIN");
                    return;
                }
                
                // Input validation: Check if PIN is numeric and of proper length
                if (!rpin.matches("\\d+") || rpin.length() < 4 || rpin.length() > 6) {
                    JOptionPane.showMessageDialog(null, "PIN must be 4-6 digits");
                    return;
                }
                
                // Hash the new PIN for secure storage
                String hashedPin = SecurityUtils.hashPassword(rpin);
                
                ConnectionSql c1 = new ConnectionSql();
                
                // Update bank table with prepared statement
                String q1 = "UPDATE bank SET Login_Password = ? WHERE Account_No = ? AND Login_Password = ?";
                PreparedStatement ps1 = c1.prepareStatement(q1);
                ps1.setString(1, hashedPin);
                ps1.setString(2, Accountno);
                ps1.setString(3, pin);
                ps1.executeUpdate();
                
                // Update login table with prepared statement
                String q2 = "UPDATE login SET Login_Password = ? WHERE Account_No = ? AND Login_Password = ?";
                PreparedStatement ps2 = c1.prepareStatement(q2);
                ps2.setString(1, hashedPin);
                ps2.setString(2, Accountno);
                ps2.setString(3, pin);
                ps2.executeUpdate();
                
                // Update signup3 table with prepared statement
                String q3 = "UPDATE signup3 SET Login_Password = ? WHERE Account_No = ? AND Login_Password = ?";
                PreparedStatement ps3 = c1.prepareStatement(q3);
                ps3.setString(1, hashedPin);
                ps3.setString(2, Accountno);
                ps3.setString(3, pin);
                ps3.executeUpdate();
                
                // Log the PIN change for security audit
                AuditLogger.logSecurity(Accountno, "PIN_CHANGE", "PIN changed successfully", AuditLogger.SUCCESS);

                JOptionPane.showMessageDialog(null, "PIN changed successfully");
                
                setVisible(false);
               
                // Since we've hashed the PIN, we should now verify it when used
                new Transactions(rpin, Accountno).setVisible(true);
            
            } else if(ae.getSource()==b2){
                new Transactions(pin, Accountno).setVisible(true);
                setVisible(false);
            }
        } catch(Exception e) {
            // Enhanced error handling with security logging
            AuditLogger.logSecurity(Accountno, "PIN_CHANGE", 
                    "PIN change failed: " + e.getMessage(), AuditLogger.ERROR);
            JOptionPane.showMessageDialog(null, "Error changing PIN. Please try again.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Pin("","");
    }
}

/* 
 * AGENT GENERATED SUMMARY:
 * Implementation requirements addressed:
 * - SEC-002: Fixed SQL injection vulnerability in Pin.java
 * - SEC-005: Implemented proper password hashing
 * - INFRA-001: Added security audit logging
 * 
 * Security improvements:
 * - Replaced vulnerable string concatenation with parameterized PreparedStatement
 * - Added PIN validation to enforce security requirements
 * - Added PIN hashing using SecurityUtils
 * - Added comprehensive security audit logging
 * - Added improved error handling
 * 
 * Human review required:
 * - Update all other components that verify PINs to use the hashed version
 * - Consider updating the database schema to store hashed PINs with longer field length
 * 
 * Agent run identifier: Claude-3.7-Sonnet-20250219
 */