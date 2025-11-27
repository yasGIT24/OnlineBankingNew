package banking.management.system;

/**
 *@author Adarsh Kunal
 **/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1, US-3]
 * Modified Transactions.java to implement:
 * 1. Proper session management with timeout
 * 2. Secure logout functionality
 * 3. Added new fund transfer navigation option
 */
public class Transactions extends JFrame implements ActionListener
{
  JLabel l1;
  JButton b1, b2, b3, b4, b5, b6, b7, b8;
  String pin;
  String Accountno;
  
  /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
   * Added session management
   */
  private SessionManager sessionManager;
  private JLabel sessionTimerLabel;
  private Timer sessionDisplayTimer;
  
  Transactions(String pin, String Accountno)
  {
    this.Accountno = Accountno;
    this.pin = pin;  
    
    setLayout(null);
      
    setTitle("Transaction Machine");
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Initialize session manager with 15 minute timeout
     */
    sessionManager = new SessionManager(900); // 15 minutes in seconds
    sessionManager.startSession();
    
    ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/transs.png"));
    Image h2 = h1.getImage().getScaledInstance(791, 751, Image.SCALE_DEFAULT);
    ImageIcon h3 = new ImageIcon(h2);  
    JLabel image1 = new JLabel(h3);
    image1.setBounds(700,10,791,751);
    add(image1);
    
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
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Added session timer display
     */
    sessionTimerLabel = new JLabel("Session expires in: 15:00");
    sessionTimerLabel.setForeground(Color.YELLOW);
    sessionTimerLabel.setFont(new Font("Arial", Font.BOLD, 14));
    sessionTimerLabel.setBounds(400, 80, 250, 30);
    add(sessionTimerLabel);
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Update session timer display every second
     */
    sessionDisplayTimer = new Timer(1000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int remainingTime = sessionManager.getRemainingTime();
            if (remainingTime <= 0) {
                // Session timeout - perform logout
                JOptionPane.showMessageDialog(null, 
                    "Your session has timed out due to inactivity.", 
                    "Session Timeout", JOptionPane.WARNING_MESSAGE);
                performSecureLogout();
                return;
            }
            
            // Update timer display
            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            sessionTimerLabel.setText(String.format("Session expires in: %02d:%02d", minutes, seconds));
            
            // Change color when time is running low
            if (remainingTime < 60) {
                sessionTimerLabel.setForeground(Color.RED);
            }
        }
    });
    sessionDisplayTimer.start();
    
    // Deposit button
    b1 = new JButton("DEPOSIT");
    b1.setBounds(150, 180, 180, 45);
    b1.setBackground(new Color(204, 229, 255));
    b1.setFont(new Font("Arial", Font.BOLD, 13));
    b1.setForeground(Color.BLACK);
    b1.addActionListener(this);
    add(b1);
    
    // Withdrawal button
    b2 = new JButton("WITHDRAWL");
    b2.setBounds(400, 180, 180, 45);
    b2.setBackground(new Color(204, 229, 255));
    b2.setFont(new Font("Arial", Font.BOLD, 13));
    b2.setForeground(Color.black);
    b2.addActionListener(this);
    add(b2);
    
    // Fast-cash button
    b3 = new JButton("FAST-CASH");
    b3.setBounds(150, 250, 180, 45);
    b3.setBackground(new Color(204, 229, 255));
    b3.setFont(new Font("Arial", Font.BOLD, 13));
    b3.setForeground(Color.black);
    b3.addActionListener(this);
    add(b3);
    
    // Mini statement button
    b4 = new JButton("MINI STATEMENT");
    b4.setBounds(400, 250, 180, 45);
    b4.setBackground(new Color(204, 229, 255));
    b4.setFont(new Font("Arial", Font.BOLD, 13));
    b4.setForeground(Color.black);
    b4.addActionListener(this);
    add(b4);
    
    // PIN change button
    b5 = new JButton("CHANGE PASSWORD");
    b5.setBounds(150, 330, 180, 45);
    b5.setBackground(new Color(204, 229, 255));
    b5.setFont(new Font("Arial", Font.BOLD, 13));
    b5.setForeground(Color.black);
    b5.addActionListener(this);
    add(b5);
    
    // Balance enquiry button
    b6 = new JButton("BALANCE ENQUIRY");
    b6.setBounds(400, 330, 180, 45);
    b6.setBackground(new Color(204, 229, 255));
    b6.setFont(new Font("Arial", Font.BOLD, 13));
    b6.setForeground(Color.black);
    b6.addActionListener(this);
    add(b6);
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-3]
     * Added fund transfer button for new functionality
     */
    b8 = new JButton("FUND TRANSFER");
    b8.setBounds(150, 410, 180, 45);
    b8.setBackground(new Color(204, 229, 255));
    b8.setFont(new Font("Arial", Font.BOLD, 13));
    b8.setForeground(Color.black);
    b8.addActionListener(this);
    add(b8);
    
    // Sign-out button
    b7 = new JButton("SIGN-OUT");
    b7.setBounds(400, 410, 180, 45);
    b7.setBackground(new Color(190, 229, 255));
    b7.setFont(new Font("Arial", Font.BOLD, 13));
    b7.setForeground(Color.black);
    b7.addActionListener(this);
    add(b7);

    getContentPane().setBackground(new Color(0, 51, 102));
    setSize(1600, 1200);
    setVisible(true);
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Add activity listener to reset session timer on user activity
     */
    addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            sessionManager.resetTimer();
        }
    });
    
    addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            sessionManager.resetTimer();
        }
    });
  }

  /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
   * Method for secure logout
   */
  private void performSecureLogout() {
    // Stop the timers
    if (sessionDisplayTimer != null) {
        sessionDisplayTimer.stop();
    }
    
    // End the session
    if (sessionManager != null) {
        sessionManager.endSession();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Log the logout activity
     */
    try {
        AuditLogger.logActivity(Accountno, "User logged out", "Security");
    } catch (Exception e) {
        ErrorHandler.handleException(e, "Error logging logout");
    }
    
    // Navigate to login screen
    setVisible(false);
    dispose(); // Properly dispose of window resources
    new Login().setVisible(true);
  }
  
  public void actionPerformed(ActionEvent ae) { 
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Reset session timer on any button click
     */
    sessionManager.resetTimer();
      
    if (ae.getSource() == b1) {
      setVisible(false);
      new Deposit(this.pin, this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b2) {
      setVisible(false);
      new Withdrawl(this.pin, this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b3) {
      setVisible(false);
      new FastCash(this.pin, this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b4) {
      new MiniStatement(this.pin, this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b5) {
      setVisible(false);
      new Pin(this.pin, this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b6) {
      setVisible(false);
      new BalanceEnquiry(this.pin, this.Accountno).setVisible(true);
      
    } else if (ae.getSource() == b7) {
      /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
       * Replace System.exit(0) with secure logout
       */
      int response = JOptionPane.showConfirmDialog(null, 
          "Are you sure you want to log out?", "Confirm Logout",
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
          
      if (response == JOptionPane.YES_OPTION) {
          performSecureLogout();
      }
      
    } else if (ae.getSource() == b8) {
      /* [AGENT GENERATED CODE - REQUIREMENT:US-3]
       * Navigate to fund transfer
       */
      setVisible(false);
      new FundTransfer(this.pin, this.Accountno).setVisible(true);
    }
  }
  
  public static void main(String[] args) {
    new Transactions("","");
  }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-TRAN-001, TC-TRAN-002, TC-TRAN-003, TC-SEC-003
 * Requirement IDs: US-1 (Authentication), US-3 (Fund Transfer)
 * Agent Run: AGENT-20251127-01
 */