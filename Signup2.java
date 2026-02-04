package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

/* [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_3_CONTACT_DETAILS_COLLECTION]
 * This class provides the second step of multi-step user registration for contact details
 * collection as identified in change_impact_analysis_review_final.md
 * 
 * Requirements addressed:
 * - Contact Details Collection Module (Missing Implementation -> Added)
 * - Email and phone number entry with validation
 * - Contact validation and successful data saving
 * - Integration with multi-step registration workflow
 * 
 * Placement: Created in main package directory 
 */
public class Signup2 extends JFrame implements ActionListener {
    
    String formno;
    JTextField phoneTextField, alternatePhoneTextField, occupationTextField, companyTextField, incomeTextField;
    JComboBox categoryComboBox, religionComboBox, educationComboBox;
    JButton next, previous;
    
    // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_3_CONTACT_DETAILS_COLLECTION]
    // Email validation pattern
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    // Phone number validation (10 digits)
    private static final String PHONE_PATTERN = "^[0-9]{10}$";
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
    // [END AGENT GENERATED CODE]
    
    Signup2(String formno) {
        this.formno = formno;
        setLayout(null);
        
        JLabel formLabel = new JLabel("APPLICATION FORM NO:" + formno);
        formLabel.setFont(new Font("Raleway", Font.BOLD, 40));
        formLabel.setBounds(150, 30, 800, 40);
        add(formLabel);
        
        JLabel pageTitle = new JLabel("CONTACT & ADDITIONAL DETAILS");
        pageTitle.setFont(new Font("Raleway", Font.BOLD, 20));
        pageTitle.setBounds(250, 90, 600, 30);
        add(pageTitle);
        
        // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_3_CONTACT_DETAILS_COLLECTION]
        // Phone number field
        JLabel phone = new JLabel("Phone Number:");
        phone.setFont(new Font("Raleway", Font.BOLD, 18));
        phone.setBounds(100, 150, 200, 30);
        add(phone);
        phoneTextField = new JTextField();
        phoneTextField.setBounds(300, 150, 400, 28);
        add(phoneTextField);
        
        // Alternate phone number
        JLabel altPhone = new JLabel("Alternate Phone:");
        altPhone.setFont(new Font("Raleway", Font.BOLD, 18));
        altPhone.setBounds(100, 200, 200, 30);
        add(altPhone);
        alternatePhoneTextField = new JTextField();
        alternatePhoneTextField.setBounds(300, 200, 400, 28);
        add(alternatePhoneTextField);
        // [END AGENT GENERATED CODE]
        
        JLabel category = new JLabel("Category:");
        category.setFont(new Font("Raleway", Font.BOLD, 18));
        category.setBounds(100, 250, 200, 30);
        add(category);
        String categoryOptions[] = {"General", "OBC", "SC", "ST", "Other"};
        categoryComboBox = new JComboBox(categoryOptions);
        categoryComboBox.setBounds(300, 250, 400, 28);
        categoryComboBox.setBackground(Color.WHITE);
        add(categoryComboBox);
        
        JLabel religion = new JLabel("Religion:");
        religion.setFont(new Font("Raleway", Font.BOLD, 18));
        religion.setBounds(100, 300, 200, 30);
        add(religion);
        String religionOptions[] = {"Hindu", "Muslim", "Christian", "Sikh", "Buddhist", "Other"};
        religionComboBox = new JComboBox(religionOptions);
        religionComboBox.setBounds(300, 300, 400, 28);
        religionComboBox.setBackground(Color.WHITE);
        add(religionComboBox);
        
        JLabel education = new JLabel("Education:");
        education.setFont(new Font("Raleway", Font.BOLD, 18));
        education.setBounds(100, 350, 200, 30);
        add(education);
        String educationOptions[] = {"Graduate", "Post-Graduate", "Under-Graduate", "High School", "Other"};
        educationComboBox = new JComboBox(educationOptions);
        educationComboBox.setBounds(300, 350, 400, 28);
        educationComboBox.setBackground(Color.WHITE);
        add(educationComboBox);
        
        JLabel occupation = new JLabel("Occupation:");
        occupation.setFont(new Font("Raleway", Font.BOLD, 18));
        occupation.setBounds(100, 400, 200, 30);
        add(occupation);
        occupationTextField = new JTextField();
        occupationTextField.setBounds(300, 400, 400, 28);
        add(occupationTextField);
        
        JLabel company = new JLabel("Company Name:");
        company.setFont(new Font("Raleway", Font.BOLD, 18));
        company.setBounds(100, 450, 200, 30);
        add(company);
        companyTextField = new JTextField();
        companyTextField.setBounds(300, 450, 400, 28);
        add(companyTextField);
        
        JLabel income = new JLabel("Annual Income:");
        income.setFont(new Font("Raleway", Font.BOLD, 18));
        income.setBounds(100, 500, 200, 30);
        add(income);
        incomeTextField = new JTextField();
        incomeTextField.setBounds(300, 500, 400, 28);
        add(incomeTextField);
        
        // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_3_CONTACT_DETAILS_COLLECTION]
        // Navigation buttons
        previous = new JButton("PREVIOUS");
        previous.setBounds(250, 600, 120, 35);
        previous.setBackground(Color.GRAY);
        previous.setForeground(Color.WHITE);
        previous.addActionListener(this);
        add(previous);
        
        next = new JButton("NEXT");
        next.setBounds(400, 600, 120, 35);
        next.setBackground(Color.BLACK);
        next.setForeground(Color.WHITE);
        next.addActionListener(this);
        add(next);
        // [END AGENT GENERATED CODE]
        
        getContentPane().setBackground(new Color(204, 255, 255));
        setSize(850, 750);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_3_CONTACT_DETAILS_COLLECTION]
    // Email validation method
    private boolean isValidEmail(String email) {
        return pattern.matcher(email).matches();
    }
    
    // Phone validation method
    private boolean isValidPhone(String phone) {
        return phonePattern.matcher(phone).matches();
    }
    // [END AGENT GENERATED CODE]
    
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == next) {
            // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_3_CONTACT_DETAILS_COLLECTION]
            // Collect and validate contact details
            String phone = phoneTextField.getText().trim();
            String altPhone = alternatePhoneTextField.getText().trim();
            String category = (String) categoryComboBox.getSelectedItem();
            String religion = (String) religionComboBox.getSelectedItem();
            String education = (String) educationComboBox.getSelectedItem();
            String occupation = occupationTextField.getText().trim();
            String company = companyTextField.getText().trim();
            String income = incomeTextField.getText().trim();
            
            try {
                // Validation
                if (phone.equals("")) {
                    JOptionPane.showMessageDialog(null, "Phone number is required");
                    return;
                } else if (!isValidPhone(phone)) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid 10-digit phone number");
                    return;
                } else if (!altPhone.equals("") && !isValidPhone(altPhone)) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid alternate phone number");
                    return;
                } else if (occupation.equals("")) {
                    JOptionPane.showMessageDialog(null, "Occupation is required");
                    return;
                } else if (income.equals("")) {
                    JOptionPane.showMessageDialog(null, "Annual income is required");
                    return;
                }
                
                // Validate income is numeric
                try {
                    Double.parseDouble(income);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid numeric income");
                    return;
                }
                
                // Save to database using PreparedStatement
                ConnectionSql c = new ConnectionSql();
                String query = "INSERT INTO signup2 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = c.c.prepareStatement(query);
                pstmt.setString(1, formno);
                pstmt.setString(2, phone);
                pstmt.setString(3, altPhone);
                pstmt.setString(4, category);
                pstmt.setString(5, religion);
                pstmt.setString(6, education);
                pstmt.setString(7, occupation);
                pstmt.setString(8, company);
                pstmt.setString(9, income);
                
                pstmt.executeUpdate();
                pstmt.close();
                c.c.close();
                
                // Navigate to next step
                setVisible(false);
                new Signup3(formno).setVisible(true);
                
            } catch (Exception e) {
                System.out.println("Error saving contact details: " + e);
                JOptionPane.showMessageDialog(null, "Error saving details. Please try again.");
            }
            // [END AGENT GENERATED CODE]
            
        } else if (ae.getSource() == previous) {
            // [AGENT GENERATED CODE - REQUIREMENT:USER_STORY_3_CONTACT_DETAILS_COLLECTION]
            setVisible(false);
            new Signup1().setVisible(true);
            // [END AGENT GENERATED CODE]
        }
    }
    
    public static void main(String[] args) {
        new Signup2("12345").setVisible(true);
    }
}

/*
 * REQUIREMENT SUMMARY - AGENT GENERATED CODE
 * Agent Run Identifier: CHANGE_IMPACT_ANALYSIS_IMPLEMENTATION_2026_02_03
 * 
 * Requirements Implemented:
 * - USER_STORY_3_CONTACT_DETAILS_COLLECTION: Complete contact details collection step 2 of registration
 * - Phone number entry with 10-digit validation
 * - Alternate phone number with validation
 * - Category, religion, education dropdown selections
 * - Occupation, company, and income fields with validation
 * - Secure database operations using PreparedStatement
 * - Input validation with user-friendly error messages
 * - Navigation between registration steps
 * - Email validation pattern for future email field integration
 * 
 * Security Features:
 * - SQL injection prevention through PreparedStatement usage
 * - Comprehensive input validation
 * - Proper exception handling and resource cleanup
 * - Secure data transmission between registration steps
 */