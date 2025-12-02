package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

/**
 *
 * @author Adarsh Kunal
 */

public class Signup1 extends JFrame implements ActionListener{
    
    long fnum;
    JTextField nameTextField, fnameTextField, mnameTextField, dobTextField, emailTextField, addressTextField, cityTextField, stateTextField, natTextField;
    JButton next;
    JRadioButton male, female, other, married, unmarried;
    JButton npage;
    boolean True;
    
    Signup1(){
        
        setLayout(null);
       // setUndecorated(true);
        
        // [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01]
        // Using SecureRandom for better randomization of form numbers
        SecureRandom secureRandom = new SecureRandom();
        fnum = 100000L + Math.abs(secureRandom.nextLong() % 900000L);
        
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
          
        
        npage = new JButton("NEXT PAGE");
        npage.setBounds(360, 710, 120, 35);
        npage.setBackground(Color.black);
        npage.setForeground(Color.white);
        npage.addActionListener(this);
        add(npage);
        

        
       getContentPane().setBackground(new Color(204, 255, 255));
        setSize(850,850);
        setVisible(true);
    }
    
    // [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01]
    // Validate email format using regex
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    // [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01]
    // Validate date format (yyyy-MM-dd)
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
      
    public void actionPerformed(final ActionEvent ae) {
      
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
      
      try {
         if (name.equals("")) {
             JOptionPane.showMessageDialog(null, "Name is required");
             return; // [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01] Added return statement
         }
         
         // [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01]
         // Add input validation for all fields
         if (!isValidEmail(email)) {
             JOptionPane.showMessageDialog(null, "Please enter a valid email address");
             return;
         }
         
         if (!isValidDate(dob)) {
             JOptionPane.showMessageDialog(null, "Please enter date in format YYYY-MM-DD");
             return;
         }
         
         if (gender == null) {
             JOptionPane.showMessageDialog(null, "Please select a gender");
             return;
         }
         
         if (marital.equals("null")) {
             JOptionPane.showMessageDialog(null, "Please select marital status");
             return;
         }
         
         if (city.isEmpty() || state.isEmpty() || nat.isEmpty()) {
             JOptionPane.showMessageDialog(null, "City, State and Nationality are required fields");
             return;
         }
         
         ConnectionSql c = new ConnectionSql();
         
         // [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01]
         // Fix SQL Injection vulnerability by using PreparedStatement
         String query = "INSERT INTO signup1 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
         PreparedStatement pstmt = c.prepareStatement(query);
         pstmt.setString(1, formno);
         pstmt.setString(2, name);
         pstmt.setString(3, fname);
         pstmt.setString(4, mname);
         pstmt.setString(5, dob);
         pstmt.setString(6, gender);
         pstmt.setString(7, email);
         pstmt.setString(8, marital);
         pstmt.setString(9, address);
         pstmt.setString(10, city);
         pstmt.setString(11, state);
         pstmt.setString(12, nat);
         
         pstmt.executeUpdate(); 
         setVisible(false);
         new Signup2(formno).setVisible(true);
         
      } catch(Exception e){
          // [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01]
          // Improved error handling - don't expose stack trace to user
          System.err.println("Error in signup process: " + e.getMessage());
          JOptionPane.showMessageDialog(null, "An error occurred during signup. Please try again.");
      }
     }
   
    public static void main(final String[] args){
        new Signup1();
    }
}

// [AGENT GENERATED CODE - REQUIREMENT:REQ-SEC-01]
// This file has been updated to fix SQL injection vulnerabilities and improve security.
// Changes include:
// 1. Using PreparedStatement instead of direct string concatenation
// 2. Adding input validation for email and date formats
// 3. Using SecureRandom for form number generation
// 4. Adding required field validation
// 5. Improved error handling to prevent information leakage
// Agent run identifier: AGENT-SEC-FIX-2025-12-02