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

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Modified Signup1.java to implement:
 * 1. SQL injection remediation with parameterized queries
 * 2. Enhanced input validation
 * 3. Integration with 2FA signup flow
 */
public class Signup1 extends JFrame implements ActionListener{
    
    long fnum;
    JTextField nameTextField, fnameTextField, mnameTextField, dobTextField, emailTextField, addressTextField, cityTextField, stateTextField, natTextField;
    JButton next;
    JRadioButton male, female, other, married, unmarried;
    JButton npage;
    boolean True;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Added validation labels and indicators
     */
    private JLabel validationStatusLabel;
    private HashMap<String, Boolean> validationStatus;
    private JLabel securityInfoLabel;
    
    Signup1(){
        
        setLayout(null);
        
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
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Added security info label to inform users about multi-step secure registration
         */
        securityInfoLabel = new JLabel("<html>This is step 1 of 3 in our secure registration process.<br>" +
            "After completing personal details, you'll set up account credentials and 2FA verification.</html>");
        securityInfoLabel.setFont(new Font("Raleway", Font.ITALIC, 14));
        securityInfoLabel.setBounds(150, 120, 600, 40);
        securityInfoLabel.setForeground(new Color(0, 102, 204));
        add(securityInfoLabel);
        
        JLabel name = new JLabel("Name:");
        name.setFont(new Font("Raleway", Font.BOLD, 18));
        name.setBounds(100, 150, 100, 30);
        add(name);
        nameTextField = new JTextField();
        nameTextField.setBounds(250, 150, 400, 28);
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Added input validation listener
         */
        nameTextField.addKeyListener(createValidationListener("name"));
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
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Added DOB validation listener
         */
        dobTextField.addKeyListener(createValidationListener("dob"));
        add(dobTextField);
        
        JLabel email = new JLabel("Email Address:");
        email.setFont(new Font("Raleway", Font.BOLD, 18));
        email.setBounds(100, 400, 190, 30);
        add(email);
        emailTextField = new JTextField();
        emailTextField.setBounds(250, 400, 400, 28);
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Added email validation listener
         */
        emailTextField.addKeyListener(createValidationListener("email"));
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
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Added validation status label
         */
        validationStatusLabel = new JLabel("");
        validationStatusLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        validationStatusLabel.setBounds(250, 680, 400, 20);
        add(validationStatusLabel);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Initialize validation status tracking
         */
        validationStatus = new HashMap<>();
        validationStatus.put("name", false);
        validationStatus.put("dob", false);
        validationStatus.put("email", false);
          
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
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Helper method to create validation listeners for different fields
     */
    private KeyAdapter createValidationListener(final String fieldType) {
        return new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                JTextField field = (JTextField) e.getSource();
                String value = field.getText();
                
                boolean isValid = false;
                
                switch (fieldType) {
                    case "name":
                        // Name should be at least 2 characters and contain only letters
                        isValid = value.length() >= 2 && value.matches("[a-zA-Z\\s]+");
                        break;
                    case "dob":
                        // Simple date format validation (DD-MM-YYYY)
                        isValid = value.matches("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(19|20)\\d\\d$");
                        break;
                    case "email":
                        // Basic email validation
                        isValid = ValidationUtils.isValidEmail(value);
                        break;
                }
                
                // Update validation status
                validationStatus.put(fieldType, isValid);
                
                // Visual feedback
                field.setBackground(isValid ? Color.WHITE : new Color(255, 235, 235));
                
                // Update validation status message
                updateValidationStatus();
            }
        };
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Method to update validation status message
     */
    private void updateValidationStatus() {
        boolean allValid = true;
        StringBuilder message = new StringBuilder();
        
        for (Map.Entry<String, Boolean> entry : validationStatus.entrySet()) {
            if (!entry.getValue()) {
                allValid = false;
                
                switch (entry.getKey()) {
                    case "name":
                        message.append("Name must be at least 2 characters (letters only). ");
                        break;
                    case "dob":
                        message.append("Date of Birth must be in DD-MM-YYYY format. ");
                        break;
                    case "email":
                        message.append("Please enter a valid email address. ");
                        break;
                }
            }
        }
        
        if (allValid) {
            validationStatusLabel.setText("All inputs are valid!");
            validationStatusLabel.setForeground(new Color(0, 128, 0));
        } else {
            validationStatusLabel.setText(message.toString());
            validationStatusLabel.setForeground(Color.RED);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Enhanced action performed method with proper validation and security
     */
    public void actionPerformed(final ActionEvent ae) {
        // Get form data
        String formno = "" + fnum;
        String name = ValidationUtils.sanitizeInput(nameTextField.getText());
        String fname = ValidationUtils.sanitizeInput(fnameTextField.getText());
        String mname = ValidationUtils.sanitizeInput(mnameTextField.getText());
        String gender = null;
        if (male.isSelected()){
            gender = "Male";
        } else if(female.isSelected()){
            gender = "Female";
        }
        String dob = ValidationUtils.sanitizeInput(dobTextField.getText());
        String email = ValidationUtils.sanitizeInput(emailTextField.getText());
        String marital = "null";
        if (married.isSelected()) {
            marital = "Married";
        } else if(unmarried.isSelected()){
            marital = "Unmarried";           
        } else if(other.isSelected()){
            marital = "Other";  
        }
        String address = ValidationUtils.sanitizeInput(addressTextField.getText());
        String city = ValidationUtils.sanitizeInput(cityTextField.getText());
        String state = ValidationUtils.sanitizeInput(stateTextField.getText());
        String nat = ValidationUtils.sanitizeInput(natTextField.getText());
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Enhanced validation logic
         */
        try {
            boolean isValid = true;
            StringBuilder errorMessage = new StringBuilder("Please correct the following errors:\n");
            
            // Required field validation
            if (name.trim().isEmpty()) {
                errorMessage.append("• Name is required\n");
                isValid = false;
            } else if (!name.matches("[a-zA-Z\\s]+")) {
                errorMessage.append("• Name must contain only letters\n");
                isValid = false;
            }
            
            if (gender == null) {
                errorMessage.append("• Gender selection is required\n");
                isValid = false;
            }
            
            if (dob.trim().isEmpty()) {
                errorMessage.append("• Date of Birth is required\n");
                isValid = false;
            } else if (!dob.matches("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(19|20)\\d\\d$")) {
                errorMessage.append("• Date of Birth must be in DD-MM-YYYY format\n");
                isValid = false;
            }
            
            if (email.trim().isEmpty()) {
                errorMessage.append("• Email is required\n");
                isValid = false;
            } else if (!ValidationUtils.isValidEmail(email)) {
                errorMessage.append("• Please enter a valid email address\n");
                isValid = false;
            }
            
            if (marital.equals("null")) {
                errorMessage.append("• Marital Status selection is required\n");
                isValid = false;
            }
            
            if (address.trim().isEmpty()) {
                errorMessage.append("• Address is required\n");
                isValid = false;
            }
            
            if (city.trim().isEmpty()) {
                errorMessage.append("• City is required\n");
                isValid = false;
            }
            
            if (state.trim().isEmpty()) {
                errorMessage.append("• State is required\n");
                isValid = false;
            }
            
            if (nat.trim().isEmpty()) {
                errorMessage.append("• Nationality is required\n");
                isValid = false;
            }
            
            if (!isValid) {
                JOptionPane.showMessageDialog(null, errorMessage.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * Use ConnectionSql with parameterized query to prevent SQL injection
             */
            ConnectionSql c = new ConnectionSql();
            PreparedStatement pstmt = c.getConnection().prepareStatement(
                "INSERT INTO signup1 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
            
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * Audit logging for security
             */
            AuditLogger.logActivity(formno, "Signup Step 1 completed", "Registration");
            
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * Navigate to next signup step with 2FA setup
             */
            setVisible(false);
            new Signup2(formno).setVisible(true);
            
        } catch(Exception e) {
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * Use proper error handling
             */
            ErrorHandler.handleException(e, "Error during signup process");
        }
    }
    
    public static void main(final String[] args){
        new Signup1();
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-SU1-001, TC-SU1-002, TC-SU1-003, TC-SEC-002
 * Requirement IDs: US-1 (Account Login & Authentication)
 * Agent Run: AGENT-20251127-01
 */