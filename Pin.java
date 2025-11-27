package banking.management.system;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

/* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
 * Modified to support multiple linked accounts by updating
 * PIN changes across all linked accounts for a user
 */
public class Pin extends JFrame implements ActionListener{
    
    JPasswordField t1,t2;
    JButton b1,b2;                               
    JLabel l1,l2,l3;
    String pin;
    String Accountno;
    JCheckBox applyToAllAccountsCheckbox; // Added checkbox for multiple accounts
    
    Pin(String pin, String Accountno){
        this.pin = pin;
        this.Accountno = Accountno;
        
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
        text.setForeground(Color.black);
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
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
         * Added checkbox to apply PIN change to all linked accounts
         */
        applyToAllAccountsCheckbox = new JCheckBox("Apply to all linked accounts");
        applyToAllAccountsCheckbox.setFont(new Font("System", Font.BOLD, 16));
        applyToAllAccountsCheckbox.setBounds(370, 470, 250, 20);
        applyToAllAccountsCheckbox.setForeground(Color.black);
        applyToAllAccountsCheckbox.setBackground(Color.white);
        add(applyToAllAccountsCheckbox);
        
        // Show checkbox only if user has linked accounts
        checkForLinkedAccounts();
        
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
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
     * Added method to check if user has linked accounts
     */
    private void checkForLinkedAccounts() {
        try {
            ConnectionSql c = new ConnectionSql();
            // Extract customer ID from account number using the linked_accounts table
            ResultSet customerRs = c.s.executeQuery(
                "SELECT customer_id FROM linked_accounts WHERE account_number = '" + Accountno + "'"
            );
            
            if (customerRs.next()) {
                String customerId = customerRs.getString("customer_id");
                
                // Count linked accounts for this customer
                ResultSet countRs = c.s.executeQuery(
                    "SELECT COUNT(*) as count FROM linked_accounts WHERE customer_id = '" + customerId + "'"
                );
                
                if (countRs.next() && countRs.getInt("count") > 1) {
                    // Only show the checkbox if there are multiple accounts
                    applyToAllAccountsCheckbox.setVisible(true);
                } else {
                    applyToAllAccountsCheckbox.setVisible(false);
                }
            } else {
                applyToAllAccountsCheckbox.setVisible(false);
            }
        } catch (Exception e) {
            System.out.println("Error checking for linked accounts: " + e);
            applyToAllAccountsCheckbox.setVisible(false);
        }
    }

    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
     * Modified to update PIN across all linked accounts if selected
     */
    public void actionPerformed(ActionEvent ae){
        try{
            
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
                
                ConnectionSql c1 = new ConnectionSql();
                
                if (applyToAllAccountsCheckbox.isVisible() && applyToAllAccountsCheckbox.isSelected()) {
                    // Update PIN across all linked accounts
                    // First, get customer ID from the current account
                    ResultSet customerRs = c1.s.executeQuery(
                        "SELECT customer_id FROM linked_accounts WHERE account_number = '" + Accountno + "'"
                    );
                    
                    if (customerRs.next()) {
                        String customerId = customerRs.getString("customer_id");
                        
                        // Get all account numbers for this customer
                        ResultSet accountsRs = c1.s.executeQuery(
                            "SELECT account_number FROM linked_accounts WHERE customer_id = '" + customerId + "'"
                        );
                        
                        // Update PIN for each linked account
                        while (accountsRs.next()) {
                            String linkedAccount = accountsRs.getString("account_number");
                            String q1 = "UPDATE bank SET Login_Password = '" + rpin + "' WHERE Account_No = '" + linkedAccount + "' AND Login_Password = '" + pin + "'";
                            String q2 = "UPDATE login SET Login_Password = '" + rpin + "' WHERE Account_No = '" + linkedAccount + "' AND Login_Password = '" + pin + "'";
                            String q3 = "UPDATE signup3 SET Login_Password = '" + rpin + "' WHERE Account_No = '" + linkedAccount + "' AND Login_Password = '" + pin + "'";
                            
                            c1.s.executeUpdate(q1);
                            c1.s.executeUpdate(q2);
                            c1.s.executeUpdate(q3);
                        }
                        
                        JOptionPane.showMessageDialog(null, "PIN changed successfully for all linked accounts");
                    } else {
                        // Fallback to updating just the current account
                        updateSingleAccountPin(c1, rpin);
                    }
                } else {
                    // Update only the current account
                    updateSingleAccountPin(c1, rpin);
                }
                
                setVisible(false);
                new Transactions(rpin,Accountno).setVisible(true);
            
            }else if(ae.getSource()==b2){
                new Transactions(pin,Accountno).setVisible(true);
                setVisible(false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
     * Helper method to update PIN for a single account
     */
    private void updateSingleAccountPin(ConnectionSql c1, String rpin) throws SQLException {
        String q1 = "UPDATE bank SET Login_Password = '" + rpin + "' WHERE Account_No = '" + Accountno + "' AND Login_Password = '" + pin + "'";
        String q2 = "UPDATE login SET Login_Password = '" + rpin + "' WHERE Account_No = '" + Accountno + "' AND Login_Password = '" + pin + "'";
        String q3 = "UPDATE signup3 SET Login_Password = '" + rpin + "' WHERE Account_No = '" + Accountno + "' AND Login_Password = '" + pin + "'";

        c1.s.executeUpdate(q1);
        c1.s.executeUpdate(q2);
        c1.s.executeUpdate(q3);

        JOptionPane.showMessageDialog(null, "PIN changed successfully");
    }

    public static void main(String[] args){
        new Pin("","");
    }
}

/* 
 * Test Cases: TC-US1-03, TC-US1-05
 * Agent Run ID: AR-2025-11-27-001
 */