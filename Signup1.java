package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 *
 * @author Adarsh Kunal
 */

public class Signup1 extends JFrame implements ActionListener{
    
    long fnum;
    JTextField nameTextField, fnameTextField, mnameTextField, dobTextField, emailTextField, addressTextField, cityTextField, stateTextField, natTextField;
    // [AGENT GENERATED CODE - REQUIREMENT:WALLET-001]
    // Added fields for wallet preferences
    JCheckBox googlePayCheck, applePayCheck, payPalCheck;
    JLabel walletLabel;
    
    // [AGENT GENERATED CODE - REQUIREMENT:CURR-001]
    // Added fields for currency preferences
    JComboBox<String> preferredCurrencyCombo;
    JLabel currencyLabel;
    
    JButton next;
    JRadioButton male, female, other, married, unmarried;
    JButton npage;
    boolean True;
    
    Signup1(){
        
        setLayout(null);
       // setUndecorated(true);
        
        Random ran = new Random();
        fnum = (Math.abs(ran.nextLong()%900L + 100000L));
        
        JLabel fromno = new JLabel("APPLICATION FORM NO:" + fnum);
        fromno.setFont(new Font("Raleway", Font.BOLD, 40));
        fromno.setBounds(150, 30, 800, 40);
        add(fromno);
        
        JLabel pdetails = new JLabel("PERSONAL DETAILS");
        pdetails.setFont(new Font("Raleway", Font.BOLD, 20));
        pdetails.setBounds(300, 90, 600, 30);
        add(pdetails);
        
        JLabel name = new JLabel("Name:");
        name.setFont(new Font("Raleway", Font.BOLD, 18));
        name.setBounds(100, 150, 100, 30);
        add(name);
        nameTextField = new JTextField();
        nameTextField.setBounds(250, 150, 400, 28);
        add(nameTextField);
        
        JLabel fname = new JLabel("Father's Name:");
        fname.setFont(new Font("Raleway", Font.BOLD, 18));
        fname.setBounds(100, 200, 190, 30);
        add(fname);
        fnameTextField = new JTextField();
        fnameTextField.setBounds(250, 200, 400, 28);
        add(fnameTextField);
        
        JLabel mname = new JLabel("Mother's Name:");
        mname.setFont(new Font("Raleway", Font.BOLD, 18));
        mname.setBounds(100, 250, 190, 30);
        add(mname);
        mnameTextField = new JTextField();
        mnameTextField.setBounds(250, 250, 400, 28);
        add(mnameTextField);
        
        JLabel gender = new JLabel("Gender:");
        gender.setFont(new Font("Raleway", Font.BOLD, 18));
        gender.setBounds(100, 300, 190, 30);
        add(gender);
        male = new JRadioButton("Male");
        male.setBounds(250, 300, 60, 30);
        male.setBackground(new Color(204, 255, 255));
        male.setFont(new Font("Raleway", Font.BOLD, 15));
        add(male);
        
        female = new JRadioButton("Female");
        female.setBounds(370, 300, 80, 30);
        female.setBackground(new Color(204, 255, 255));
        female.setFont(new Font("Raleway", Font.BOLD, 15));
        add(female);
        
        ButtonGroup gendergroup = new ButtonGroup();
        gendergroup.add(male);
        gendergroup.add(female);
        
        
        JLabel dob = new JLabel("Date of Birth:");
        dob.setFont(new Font("Raleway", Font.BOLD, 18));
        dob.setBounds(100, 350, 190, 30);
        add(dob);
        dobTextField = new JTextField();
        dobTextField.setBounds(250, 350, 400, 28);
        add(dobTextField);
        
        JLabel email = new JLabel("Email Address:");
        email.setFont(new Font("Raleway", Font.BOLD, 18));
        email.setBounds(100, 400, 190, 30);
        add(email);
        emailTextField = new JTextField();
        emailTextField.setBounds(250, 400, 400, 28);
        add(emailTextField);
        
        JLabel marital = new JLabel("Marital Status:");
        marital.setFont(new Font("Raleway", Font.BOLD, 18));
        marital.setBounds(100, 450, 190, 30);
        add(marital);
        
        married = new JRadioButton("Married");
        married.setBounds(250, 450, 100, 30);
        married.setBackground(new Color(204, 255, 255));
        married.setFont(new Font("Raleway", Font.BOLD, 15));
        add(married);
        
        unmarried = new JRadioButton("Unmarried");
        unmarried.setBounds(380, 450, 120, 30);
        unmarried.setBackground(new Color(204, 255, 255));
        unmarried.setFont(new Font("Raleway", Font.BOLD, 15));
        add(unmarried);
        
        other = new JRadioButton("Other");
        other.setBounds(540, 450, 100, 30);
        other.setBackground(new Color(204, 255, 255));
        other.setFont(new Font("Raleway", Font.BOLD, 15));
        add(other);
        
        ButtonGroup gendergroup2 = new ButtonGroup();
        gendergroup2.add(married);
        gendergroup2.add(unmarried);
        gendergroup2.add(other);
        
        JLabel address = new JLabel("Address:");
        address.setFont(new Font("Raleway", Font.BOLD, 18));
        address.setBounds(100, 500, 190, 30);
        add(address);
        addressTextField = new JTextField();
        addressTextField.setBounds(250, 500, 500, 28);
        add(addressTextField);
        
        JLabel city = new JLabel("City:");
        city.setFont(new Font("Raleway", Font.BOLD, 18));
        city.setBounds(100, 550, 190, 30);
        add(city);
        cityTextField = new JTextField();
        cityTextField.setBounds(250, 550, 400, 28);
        add(cityTextField);
        
        JLabel state = new JLabel("State:");
        state.setFont(new Font("Raleway", Font.BOLD, 18));
        state.setBounds(100, 600, 190, 30);
        add(state);
        stateTextField = new JTextField();
        stateTextField.setBounds(250, 600, 400, 28);
        add(stateTextField);
        
        JLabel nat = new JLabel("Nationality:");
        nat.setFont(new Font("Raleway", Font.BOLD, 18));
        nat.setBounds(100, 650, 190, 30);
        add(nat);
        natTextField = new JTextField();
        natTextField.setBounds(250, 650, 400, 28);
        add(natTextField);
          
        /* [AGENT GENERATED CODE - REQUIREMENT:WALLET-001]
         * Added wallet preferences section to signup form
         */
        walletLabel = new JLabel("Digital Wallet Preferences:");
        walletLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        walletLabel.setBounds(100, 700, 250, 30);
        add(walletLabel);
        
        googlePayCheck = new JCheckBox("Google Pay");
        googlePayCheck.setBounds(370, 700, 100, 30);
        googlePayCheck.setBackground(new Color(204, 255, 255));
        googlePayCheck.setFont(new Font("Raleway", Font.BOLD, 15));
        add(googlePayCheck);
        
        applePayCheck = new JCheckBox("Apple Pay");
        applePayCheck.setBounds(470, 700, 100, 30);
        applePayCheck.setBackground(new Color(204, 255, 255));
        applePayCheck.setFont(new Font("Raleway", Font.BOLD, 15));
        add(applePayCheck);
        
        payPalCheck = new JCheckBox("PayPal");
        payPalCheck.setBounds(570, 700, 100, 30);
        payPalCheck.setBackground(new Color(204, 255, 255));
        payPalCheck.setFont(new Font("Raleway", Font.BOLD, 15));
        add(payPalCheck);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:CURR-001]
         * Added preferred currency selection to signup form
         */
        currencyLabel = new JLabel("Preferred Currency:");
        currencyLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        currencyLabel.setBounds(100, 750, 190, 30);
        add(currencyLabel);
        
        String[] currencies = {"USD - US Dollar", "EUR - Euro", "GBP - British Pound", 
                               "JPY - Japanese Yen", "INR - Indian Rupee", "CAD - Canadian Dollar",
                               "AUD - Australian Dollar", "CHF - Swiss Franc"};
        preferredCurrencyCombo = new JComboBox<>(currencies);
        preferredCurrencyCombo.setBounds(370, 750, 300, 28);
        preferredCurrencyCombo.setBackground(Color.WHITE);
        preferredCurrencyCombo.setFont(new Font("Raleway", Font.BOLD, 14));
        add(preferredCurrencyCombo);
        
        npage = new JButton("NEXT PAGE");
        npage.setBounds(360, 810, 120, 35);
        npage.setBackground(Color.black);
        npage.setForeground(Color.white);
        npage.addActionListener(this);
        add(npage);
        

        getContentPane().setBackground(new Color(204, 255, 255));
        setSize(850,900);
        setVisible(true);
    }
    
    public void actionPerformed(final ActionEvent ae) {
      /* [AGENT GENERATED CODE - REQUIREMENT:SEC-003]
       * Fix SQL injection vulnerability by replacing string concatenation
       * with PreparedStatement using parameterized queries
       */
      String formno = "" + fnum;
      String name = nameTextField.getText();
      String fname = fnameTextField.getText();
      String mname = mnameTextField.getText();
      String gender = null;
      if (male.isSelected()){
          gender = "Male";
      } else if(female.isSelected()){
          gender = "Female";
                  
      }
      String dob = dobTextField.getText();
      String email = emailTextField.getText();
      String marital = "null";
      if (married.isSelected()) {
          marital = "Married";
      } else if(unmarried.isSelected()){
          marital = "Unmarried";           
      } else if(other.isSelected()){
          marital = "Other";  
      }
      String address = addressTextField.getText();
      String city = cityTextField.getText();
      String state = stateTextField.getText();
      String nat = natTextField.getText();
      
      // [AGENT GENERATED CODE - REQUIREMENT:WALLET-001]
      // Collect wallet preferences
      StringBuilder walletPrefs = new StringBuilder();
      if (googlePayCheck.isSelected()) walletPrefs.append("GooglePay,");
      if (applePayCheck.isSelected()) walletPrefs.append("ApplePay,");
      if (payPalCheck.isSelected()) walletPrefs.append("PayPal,");
      String walletPreferences = walletPrefs.length() > 0 ? 
              walletPrefs.substring(0, walletPrefs.length() - 1) : "";
      
      // [AGENT GENERATED CODE - REQUIREMENT:CURR-001]
      // Get preferred currency
      String preferredCurrency = (String) preferredCurrencyCombo.getSelectedItem();
      String currencyCode = preferredCurrency.substring(0, 3);
      
      try {
         // Input validation
         if (name.equals("")) {
             JOptionPane.showMessageDialog(null, "Name is required");
             return;
         }
         
         // Simple email validation
         if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
             JOptionPane.showMessageDialog(null, "Please enter a valid email address");
             return;
         }
         
         ConnectionSql c = new ConnectionSql();
         
         // First, alter the table if it doesn't have the new columns
         try {
             c.s.execute("ALTER TABLE signup1 ADD COLUMN IF NOT EXISTS wallet_preferences VARCHAR(100)");
             c.s.execute("ALTER TABLE signup1 ADD COLUMN IF NOT EXISTS preferred_currency VARCHAR(10)");
         } catch (Exception e) {
             // Table modification might fail if database doesn't support this syntax
             // Log this but continue with the insert
             AuditLogger.log(AuditLogger.SYSTEM, "DatabaseUpdate", 
                     "Failed to add new columns: " + e.getMessage(), AuditLogger.WARNING);
         }
             
         // Create parameterized query with prepared statement
         String query = "INSERT INTO signup1 (formno, name, fname, mname, dob, gender, email, " +
                 "marital, address, city, state, nat, wallet_preferences, preferred_currency) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         
         PreparedStatement ps = c.prepareStatement(query);
         ps.setString(1, formno);
         ps.setString(2, name);
         ps.setString(3, fname);
         ps.setString(4, mname);
         ps.setString(5, dob);
         ps.setString(6, gender);
         ps.setString(7, email);
         ps.setString(8, marital);
         ps.setString(9, address);
         ps.setString(10, city);
         ps.setString(11, state);
         ps.setString(12, nat);
         ps.setString(13, walletPreferences);
         ps.setString(14, currencyCode);
         
         // Execute the query
         ps.executeUpdate();
         
         // Log the signup activity
         AuditLogger.log(AuditLogger.USER, "UserSignup", 
                 "New user signup: " + formno, AuditLogger.SUCCESS);
         
         setVisible(false);
         new Signup2(formno).setVisible(true);
     } catch(Exception e) {
         AuditLogger.log(AuditLogger.SYSTEM, "UserSignup", 
                 "Signup error: " + e.getMessage(), AuditLogger.ERROR);
         JOptionPane.showMessageDialog(null, "Error during registration. Please try again.");
         e.printStackTrace();
     }
   }
   
 public static void main(final String[] args){
        new Signup1();
 }
}

/* 
 * AGENT GENERATED SUMMARY:
 * Implementation requirements addressed:
 * - SEC-003: Fixed SQL injection vulnerability in Signup1.java
 * - WALLET-001: Added wallet preferences to user registration
 * - CURR-001: Added currency preferences to user registration
 * - INFRA-001: Added audit logging for user actions
 * 
 * Security improvements:
 * - Replaced vulnerable string concatenation with parameterized PreparedStatement
 * - Added input validation for email
 * - Added comprehensive error handling with audit logging
 * 
 * Enhancement improvements:
 * - Added wallet integration preferences during signup
 * - Added currency preferences with ISO codes
 * - Made form adaptable with column existence checks
 *
 * Human review required:
 * - Database schema needs to support new columns
 * - Ensure Signup2.java properly handles the additional fields
 * - Form layout may need adjustment for different screen sizes
 * 
 * Agent run identifier: Claude-3.7-Sonnet-20250219
 */