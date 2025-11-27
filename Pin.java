package banking.management.system;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Modified Pin.java to implement:
 * 1. SQL injection remediation with parameterized queries
 * 2. Strong password validation rules
 * 3. Password hashing for secure storage
 */
public class Pin extends JFrame implements ActionListener{
    
    JPasswordField t1,t2;
    JButton b1,b2;                               
    JLabel l1,l2,l3;
    String pin;
    String Accountno;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Added labels for password requirements
     */
    JLabel passwordRequirementsLabel;
    JLabel passwordStrengthLabel;
    
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
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Added key listener to evaluate password strength in real-time
         */
        t1.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updatePasswordStrengthIndicator();
            }
        });
        add(t1);
        
        
        t2 = new JPasswordField();
        t2.setFont(new Font("Raleway", Font.BOLD, 25));
        t2.setBounds(370,440,200,30);
        add(t2);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Added password requirements and strength indicator
         */
        passwordRequirementsLabel = new JLabel("<html>Password must contain:<br>" +
            "• At least 8 characters<br>" +
            "• At least one uppercase letter<br>" +
            "• At least one lowercase letter<br>" +
            "• At least one number<br>" +
            "• At least one special character</html>");
        passwordRequirementsLabel.setFont(new Font("System", Font.PLAIN, 14));
        passwordRequirementsLabel.setBounds(150, 240, 400, 100);
        add(passwordRequirementsLabel);
        
        passwordStrengthLabel = new JLabel("Password Strength: ");
        passwordStrengthLabel.setFont(new Font("System", Font.PLAIN, 14));
        passwordStrengthLabel.setBounds(370, 420, 300, 20);
        add(passwordStrengthLabel);
        
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
        getContentPane().setBackground(Color.white);
        setVisible(true);
    
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Method to validate password against strong password rules
     */
    private boolean validatePassword(String password) {
        // Check for minimum length
        if (password.length() < 8) {
            return false;
        }
        
        // Check for uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        
        // Check for lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        
        // Check for number
        if (!password.matches(".*\\d.*")) {
            return false;
        }
        
        // Check for special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }
        
        return true;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Method to evaluate password strength and update UI indicator
     */
    private void updatePasswordStrengthIndicator() {
        char[] passwordChars = t1.getPassword();
        String password = new String(passwordChars);
        
        int score = 0;
        
        // Length check
        if (password.length() >= 8) score++;
        if (password.length() >= 10) score++;
        
        // Character type checks
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
        
        // Update strength label
        if (password.isEmpty()) {
            passwordStrengthLabel.setText("Password Strength: ");
            passwordStrengthLabel.setForeground(Color.BLACK);
        } else if (score < 3) {
            passwordStrengthLabel.setText("Password Strength: Weak");
            passwordStrengthLabel.setForeground(Color.RED);
        } else if (score < 5) {
            passwordStrengthLabel.setText("Password Strength: Medium");
            passwordStrengthLabel.setForeground(Color.ORANGE);
        } else {
            passwordStrengthLabel.setText("Password Strength: Strong");
            passwordStrengthLabel.setForeground(Color.GREEN);
        }
    }
    
    public void actionPerformed(ActionEvent ae){
        try{
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * Get password securely without using getText() on JPasswordField
             */
            char[] passwordChars = t1.getPassword();
            char[] confirmChars = t2.getPassword();
            String npin = new String(passwordChars);
            String rpin = new String(confirmChars);
            
            // Clear the passwords from memory after use
            java.util.Arrays.fill(passwordChars, '0');
            java.util.Arrays.fill(confirmChars, '0');
            
            if(!npin.equals(rpin)){
                JOptionPane.showMessageDialog(null, "Entered PIN does not match");
                return;
            }
            
            if(ae.getSource()==b1){
                if (npin.equals("")){
                    JOptionPane.showMessageDialog(null, "Enter New PIN");
                    return;
                }
                if (rpin.equals("")){
                    JOptionPane.showMessageDialog(null, "Re-Enter new PIN");
                    return;
                }
                
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Validate password against strong password rules
                 */
                if (!validatePassword(npin)) {
                    JOptionPane.showMessageDialog(null, 
                        "Password does not meet security requirements.\n" +
                        "Please ensure it contains at least 8 characters, including:\n" +
                        "- Uppercase letters\n" +
                        "- Lowercase letters\n" +
                        "- Numbers\n" +
                        "- Special characters");
                    return;
                }
                
                ConnectionSql c1 = new ConnectionSql();
                
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Hash password before storing in database
                 */
                String hashedPin = EncryptionUtil.hashPassword(npin);
                
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Use parameterized queries to prevent SQL injection
                 */
                PreparedStatement bankStmt = c1.getConnection().prepareStatement(
                    "UPDATE bank SET Login_Password = ? WHERE Account_No = ? AND Login_Password = ?");
                bankStmt.setString(1, hashedPin);
                bankStmt.setString(2, Accountno);
                bankStmt.setString(3, pin);
                bankStmt.executeUpdate();
                
                PreparedStatement loginStmt = c1.getConnection().prepareStatement(
                    "UPDATE login SET Login_Password = ? WHERE Account_No = ? AND Login_Password = ?");
                loginStmt.setString(1, hashedPin);
                loginStmt.setString(2, Accountno);
                loginStmt.setString(3, pin);
                loginStmt.executeUpdate();
                
                PreparedStatement signupStmt = c1.getConnection().prepareStatement(
                    "UPDATE signup3 SET Login_Password = ? WHERE Account_No = ? AND Login_Password = ?");
                signupStmt.setString(1, hashedPin);
                signupStmt.setString(2, Accountno);
                signupStmt.setString(3, pin);
                signupStmt.executeUpdate();
                
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Log the password change for security audit
                 */
                AuditLogger.logActivity(Accountno, "Password changed", "Security");

                JOptionPane.showMessageDialog(null, "PIN changed successfully");
                
                setVisible(false);
               
                /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                 * Use the hashed value for future authentication
                 */
                new Transactions(hashedPin, Accountno).setVisible(true);
            
            } else if(ae.getSource()==b2){
                new Transactions(pin, Accountno).setVisible(true);
                setVisible(false);
            }
        }catch(Exception e){
            /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
             * Use error handler for proper error handling
             */
            ErrorHandler.handleException(e, "Error changing PIN");
        }
    }

    public static void main(String[] args){
        new Pin("","");
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-PIN-001, TC-PIN-002, TC-PIN-003, TC-SEC-001
 * Requirement IDs: US-1 (Account Login & Authentication)
 * Agent Run: AGENT-20251127-01
 */