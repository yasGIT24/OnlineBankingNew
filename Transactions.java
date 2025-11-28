package banking.management.system;

/**
 *@author Adarsh Kunal
 **/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Transactions extends JFrame implements ActionListener
{
  JLabel l1;
  // [AGENT GENERATED CODE - REQUIREMENT:INFRA-004]
  // Added new buttons for PDF statement, wallet, and currency conversion
  JButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b10;
  String pin;
  String Accountno;
  
  Transactions(String pin, String Accountno)
  {
    this.Accountno = Accountno;
    this.pin= pin;  
    
    setLayout(null);
      
    setTitle("Transaction Machine");
    
    ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/transs.png"));
    Image h2 = h1.getImage().getScaledInstance(791, 751, Image.SCALE_DEFAULT);
    ImageIcon h3 = new ImageIcon(h2);  
    JLabel image1 = new JLabel(h3);
    image1.setBounds(700,10,791,751);
    add(image1);
    
//    ImageIcon p1 = new ImageIcon(ClassLoader.getSystemResource("icons/deposit1.jpg"));
//    Image p2 = p1.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT);
//    ImageIcon p3 = new ImageIcon(p2);  
//    JLabel image3 = new JLabel(p3);
//    image1.setBounds(400,400,500,500);
//    add(image3);
    
    

     JLabel text = new JLabel("WELCOME TO THE BANK ");
     text.setFont(new Font("Osward", Font.BOLD,32));
     text.setBounds(200, 40, 450, 40);
     text.setForeground(Color.white);
     add(text);
            
     ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
     Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
     ImageIcon i3 = new ImageIcon(i2);  
     JLabel label = new JLabel(i3);
     label.setBounds(70, 10, 100, 100);
     add(label);


    l1 = new JLabel("Please Select Your Transaction");
    l1.setForeground(Color.WHITE);
    l1.setFont(new Font("System", 1, 20));
    l1.setFont(new Font("Arial", Font.BOLD, 20));
    l1.setBounds(150, 120, 500, 55);
    add(l1);
    
    /* [AGENT GENERATED CODE - REQUIREMENT:INFRA-004]
     * Update navigation UI to include new feature access:
     * - PDF statement download
     * - Digital wallet management
     * - Currency conversion
     */
    
    // Original buttons
     b1 = new JButton("DEPOSIT");
     b1.setBounds(150, 180, 180, 45);
     b1.setBackground(new Color(204, 229, 255));
     b1.setFont(new Font("Arial", Font.BOLD, 13));
     b1.setForeground(Color.BLACK);
     b1.addActionListener(this);
     add(b1);
    
     b2 = new JButton("WITHDRAWL");
     b2.setBounds(400, 180, 180, 45);
     b2.setBackground(new Color(204, 229, 255));
     b2.setFont(new Font("Arial", Font.BOLD, 13));
     b2.setForeground(Color.black);
     b2.addActionListener(this);
     add(b2);
    
     b3 = new JButton("FAST-CASH");
     b3.setBounds(150, 250, 180, 45);
     b3.setBackground(new Color(204, 229, 255));
     b3.setFont(new Font("Arial", Font.BOLD, 13));
     b3.setForeground(Color.black);
     b3.addActionListener(this);
     add(b3);
    
     b4 = new JButton("MINI STATEMENT");
     b4.setBounds(400, 250, 180, 45);
     b4.setBackground(new Color(204, 229, 255));
     b4.setFont(new Font("Arial", Font.BOLD, 13));
     b4.setForeground(Color.black);
     b4.addActionListener(this);
     add(b4);
    
     b5 = new JButton("CHANGE PASSWORD");
     b5.setBounds(150, 320, 180, 45);
     b5.setBackground(new Color(204, 229, 255));
     b5.setFont(new Font("Arial", Font.BOLD, 13));
     b5.setForeground(Color.black);
     b5.addActionListener(this);
     add(b5);
    
     b6 = new JButton("BALANCE ENQUIRY");
     b6.setBounds(400, 320, 180, 45);
     b6.setBackground(new Color(204, 229, 255));
     b6.setFont(new Font("Arial", Font.BOLD, 13));
     b6.setForeground(Color.black);
     b6.addActionListener(this);
     add(b6);
     
     // New buttons for new features
     b8 = new JButton("PDF STATEMENT");
     b8.setBounds(150, 390, 180, 45);
     b8.setBackground(new Color(204, 229, 255));
     b8.setFont(new Font("Arial", Font.BOLD, 13));
     b8.setForeground(Color.black);
     b8.addActionListener(this);
     add(b8);
     
     b9 = new JButton("DIGITAL WALLETS");
     b9.setBounds(400, 390, 180, 45);
     b9.setBackground(new Color(204, 229, 255));
     b9.setFont(new Font("Arial", Font.BOLD, 13));
     b9.setForeground(Color.black);
     b9.addActionListener(this);
     add(b9);
     
     b10 = new JButton("CURRENCY CONVERT");
     b10.setBounds(150, 460, 180, 45);
     b10.setBackground(new Color(204, 229, 255));
     b10.setFont(new Font("Arial", Font.BOLD, 13));
     b10.setForeground(Color.black);
     b10.addActionListener(this);
     add(b10);
    
     // Sign-out button moved to bottom
     b7 = new JButton("SIGN-OUT");
     b7.setBounds(400, 460, 180, 45);
     b7.setBackground(new Color(190, 229, 255));
     b7.setFont(new Font("Arial", Font.BOLD, 13));
     b7.setForeground(Color.black);
     b7.addActionListener(this);
     add(b7);

    getContentPane().setBackground(new Color(0, 51, 102));
	setSize(1600, 1200);
    setVisible(true);    
  }

  // String pin;
  
  public void actionPerformed(ActionEvent ae) { 
      
      if (ae.getSource() == b1) {
      setVisible(false);
      new Deposit(this.pin,this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b2) {
      setVisible(false);
      new Withdrawl(this.pin,this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b3) {
      setVisible(false);
      new FastCash(this.pin,this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b4) {
      new MiniStatement(this.pin,this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b5) {
      setVisible(false);
      new Pin(this.pin,this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b6) {
      setVisible(false);
      new BalanceEnquiry(this.pin,this.Accountno).setVisible(true);
      
    } 
    /* [AGENT GENERATED CODE - REQUIREMENT:INFRA-004]
     * Add handlers for the new feature buttons
     */
    else if (ae.getSource() == b8) {
      // PDF Statement feature
      try {
        AuditLogger.log(AuditLogger.USER, "Navigation", 
                "User accessed PDF Statement feature", AuditLogger.INFO);
        JOptionPane.showMessageDialog(null, "PDF Statement feature coming soon!");
        // Uncomment when implemented
        // setVisible(false);
        // new PDFStatement(this.pin,this.Accountno).setVisible(true);
      } catch (Exception e) {
        AuditLogger.logUserActivity(this.Accountno, "NavigationError", 
                "Error navigating to PDF Statement: " + e.getMessage(), AuditLogger.ERROR);
      }
      
    } else if (ae.getSource() == b9) {
      // Digital Wallet feature
      try {
        AuditLogger.log(AuditLogger.USER, "Navigation", 
                "User accessed Digital Wallet feature", AuditLogger.INFO);
        JOptionPane.showMessageDialog(null, "Digital Wallet feature coming soon!");
        // Uncomment when implemented
        // setVisible(false);
        // new WalletManagement(this.pin,this.Accountno).setVisible(true);
      } catch (Exception e) {
        AuditLogger.logUserActivity(this.Accountno, "NavigationError", 
                "Error navigating to Digital Wallet: " + e.getMessage(), AuditLogger.ERROR);
      }
      
    } else if (ae.getSource() == b10) {
      // Currency Conversion feature
      try {
        AuditLogger.log(AuditLogger.USER, "Navigation", 
                "User accessed Currency Conversion feature", AuditLogger.INFO);
        JOptionPane.showMessageDialog(null, "Currency Conversion feature coming soon!");
        // Uncomment when implemented
        // setVisible(false);
        // new CurrencyConverter(this.pin,this.Accountno).setVisible(true);
      } catch (Exception e) {
        AuditLogger.logUserActivity(this.Accountno, "NavigationError", 
                "Error navigating to Currency Converter: " + e.getMessage(), AuditLogger.ERROR);
      }
      
    } else if (ae.getSource() == b7) {
      // Log the signout
      AuditLogger.logUserActivity(this.Accountno, "SignOut", 
              "User signed out of the system", AuditLogger.SUCCESS);
      System.exit(0);
    }
  }
  
  
  public static void main(String[] args) {
    new Transactions("","");
  }
}

/* 
 * AGENT GENERATED SUMMARY:
 * Implementation requirements addressed:
 * - INFRA-004: Added navigation options for new features in Transactions.java
 * - PDF-001: Added button for PDF statement download feature
 * - WALLET-001: Added button for digital wallet management
 * - CURR-001: Added button for currency conversion feature
 * - INFRA-001: Added audit logging for navigation and sign-out
 * 
 * Navigation improvements:
 * - Maintained consistent UI style with existing buttons
 * - Added placeholder handlers for upcoming features
 * - Added user activity logging for security and compliance
 * - Reorganized button layout for better user experience
 * 
 * Human review required:
 * - Review UI layout for different screen resolutions
 * - Implement the actual feature screens referenced in the handlers
 * 
 * Agent run identifier: Claude-3.7-Sonnet-20250219
 */