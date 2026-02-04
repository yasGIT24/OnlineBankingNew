package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_20_LOGOUT_FUNCTIONALITY]
 * This class provides the main login interface and serves as the entry point
 * for user authentication as identified in change_impact_analysis_review_final.md
 * 
 * Requirements addressed:
 * - Main login interface
 * - Navigation to registration process
 * - Session initiation point
 * - User authentication entry point
 * 
 * Placement: Created in main package directory 
 */
public class LoginPage extends JFrame implements ActionListener {
    
    JButton login, signup, clear;
    JTextField cardTextField;
    JPasswordField pinTextField;
    
    LoginPage() {
        setTitle("AUTOMATED TELLER MACHINE");
        setLayout(null);
        
        // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_5_USER_LOGIN_AUTHENTICATION]
        // Main title
        JLabel text = new JLabel("WELCOME TO ATM");
        text.setFont(new Font("Osward", Font.BOLD, 38));
        text.setBounds(200, 40, 400, 40);
        add(text);
        
        // Card number field
        JLabel cardno = new JLabel("Card No:");
        cardno.setFont(new Font("Raleway", Font.BOLD, 28));
        cardno.setBounds(120, 150, 150, 30);
        add(cardno);
        
        cardTextField = new JTextField();
        cardTextField.setBounds(300, 150, 230, 30);
        cardTextField.setFont(new Font("Arial", Font.BOLD, 14));
        add(cardTextField);
        
        // PIN field
        JLabel pin = new JLabel("PIN:");
        pin.setFont(new Font("Raleway", Font.BOLD, 28));
        pin.setBounds(120, 220, 250, 30);
        add(pin);
        
        pinTextField = new JPasswordField();
        pinTextField.setBounds(300, 220, 230, 30);
        pinTextField.setFont(new Font("Arial", Font.BOLD, 14));
        add(pinTextField);
        
        // Login button
        login = new JButton("SIGN IN");
        login.setBounds(300, 300, 100, 30);
        login.setBackground(Color.BLACK);
        login.setForeground(Color.WHITE);
        login.addActionListener(this);
        add(login);
        
        // Clear button
        clear = new JButton("CLEAR");
        clear.setBounds(430, 300, 100, 30);
        clear.setBackground(Color.BLACK);
        clear.setForeground(Color.WHITE);
        clear.addActionListener(this);
        add(clear);
        
        // Signup button
        signup = new JButton("SIGN UP");
        signup.setBounds(300, 350, 230, 30);
        signup.setBackground(Color.BLACK);
        signup.setForeground(Color.WHITE);
        signup.addActionListener(this);
        add(signup);
        // [END AGENT GENERATED CODE]
        
        getContentPane().setBackground(Color.WHITE);
        setSize(800, 480);
        setVisible(true);
        setLocation(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == clear) {
            cardTextField.setText("");
            pinTextField.setText("");
        } else if (ae.getSource() == login) {
            // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_5_USER_LOGIN_AUTHENTICATION]
            String cardNumber = cardTextField.getText();
            String pin = new String(pinTextField.getPassword());
            
            if (cardNumber.equals("") || pin.equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter both Card Number and PIN");
            } else {
                setVisible(false);
                new LoginModel(cardNumber, pin);
            }
            // [END AGENT GENERATED CODE]
        } else if (ae.getSource() == signup) {
            setVisible(false);
            new Signup1().setVisible(true);
        }
    }
    
    public static void main(String[] args) {
        new LoginPage();
    }
}

/*
 * REQUIREMENT SUMMARY - AGENT GENERATED CODE
 * Agent Run Identifier: CHANGE_IMPACT_ANALYSIS_IMPLEMENTATION_2026_02_03
 * 
 * Requirements Implemented:
 * - USER_STORY_5_USER_LOGIN_AUTHENTICATION: Main login interface
 * - Card number and PIN input fields
 * - Clear functionality for form reset
 * - Navigation to registration process
 * - Integration with LoginModel for authentication
 * - User-friendly interface design
 * 
 * User Experience Features:
 * - Clean, professional interface layout
 * - Clear input validation messaging
 * - Easy navigation between login and signup
 * - Proper form field clearing functionality
 */