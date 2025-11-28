package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/* [AGENT GENERATED CODE - REQUIREMENT:User Story 3: Fund Transfer Between Accounts]
 * This class implements the user interface for fund transfers with:
 * 1. Source and destination account selection
 * 2. Transfer amount and description entry
 * 3. Confirmation screen before submission
 * 4. Transaction receipt display after completion
 *
 * Linked to Value Stream Steps: Fund Transfer Initiation, Transfer Confirmation
 * Linked to test cases: TRAN-UI-01, TRAN-UI-02, TRAN-UI-03, SEC-06
 */
public class Transfer extends JFrame implements ActionListener {
    
    // UI Components
    private JTextField destinationAccountField, amountField, descriptionField;
    private JButton transferButton, confirmButton, cancelButton, backButton;
    private JPanel mainPanel, confirmationPanel;
    private JTextArea receiptArea;
    
    // User data
    private String pin;
    private String accountNo;
    
    // Service
    private TransferService transferService;
    
    /**
     * Constructor for the Transfer screen
     * 
     * @param pin User's PIN
     * @param accountNo User's account number
     */
    public Transfer(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        transferService = new TransferService();
        
        setTitle("Fund Transfer");
        setSize(1600, 1200);
        setLayout(new CardLayout());
        
        // Validate session before proceeding
        LoginModel loginModel = new LoginModel();
        if (!loginModel.isSessionValid()) {
            JOptionPane.showMessageDialog(this, 
                "Your session has expired. Please login again.", 
                "Session Timeout", 
                JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Create main transfer panel
        createMainPanel();
        
        // Create confirmation panel
        createConfirmationPanel();
        
        // Add panels to frame
        add(mainPanel, "main");
        add(confirmationPanel, "confirmation");
        
        ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "main");
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    /**
     * Creates the main transfer form panel
     */
    private void createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(204, 229, 255));
        
        // Bank logo
        ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image h2 = h1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon h3 = new ImageIcon(h2);  
        JLabel logo = new JLabel(h3);
        logo.setBounds(70, 30, 100, 100);
        mainPanel.add(logo);
        
        // Header
        JLabel header = new JLabel("Fund Transfer");
        header.setFont(new Font("Osward", Font.BOLD, 32));
        header.setBounds(200, 40, 450, 40);
        header.setForeground(Color.BLACK);
        mainPanel.add(header);
        
        // Source account info
        JLabel sourceAccountLabel = new JLabel("From Account:");
        sourceAccountLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        sourceAccountLabel.setBounds(220, 150, 200, 30);
        mainPanel.add(sourceAccountLabel);
        
        JLabel sourceAccountValue = new JLabel(accountNo);
        sourceAccountValue.setFont(new Font("Raleway", Font.PLAIN, 20));
        sourceAccountValue.setBounds(420, 150, 300, 30);
        mainPanel.add(sourceAccountValue);
        
        JLabel balanceLabel = new JLabel("Available Balance:");
        balanceLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        balanceLabel.setBounds(220, 200, 200, 30);
        mainPanel.add(balanceLabel);
        
        double balance = transferService.getAccountBalance(accountNo);
        JLabel balanceValue = new JLabel("Rs. " + balance);
        balanceValue.setFont(new Font("Raleway", Font.PLAIN, 20));
        balanceValue.setBounds(420, 200, 300, 30);
        balanceValue.setForeground(Color.BLUE);
        mainPanel.add(balanceValue);
        
        // Destination account
        JLabel destinationAccountLabel = new JLabel("To Account:");
        destinationAccountLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        destinationAccountLabel.setBounds(220, 260, 200, 30);
        mainPanel.add(destinationAccountLabel);
        
        destinationAccountField = new JTextField();
        destinationAccountField.setFont(new Font("Raleway", Font.PLAIN, 20));
        destinationAccountField.setBounds(420, 260, 300, 30);
        mainPanel.add(destinationAccountField);
        
        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        amountLabel.setBounds(220, 320, 200, 30);
        mainPanel.add(amountLabel);
        
        amountField = new JTextField();
        amountField.setFont(new Font("Raleway", Font.PLAIN, 20));
        amountField.setBounds(420, 320, 300, 30);
        mainPanel.add(amountField);
        
        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        descriptionLabel.setBounds(220, 380, 200, 30);
        mainPanel.add(descriptionLabel);
        
        descriptionField = new JTextField();
        descriptionField.setFont(new Font("Raleway", Font.PLAIN, 20));
        descriptionField.setBounds(420, 380, 500, 30);
        mainPanel.add(descriptionField);
        
        // Transfer button
        transferButton = new JButton("TRANSFER");
        transferButton.setBounds(550, 450, 170, 40);
        transferButton.setBackground(Color.BLACK);
        transferButton.setForeground(Color.WHITE);
        transferButton.setFont(new Font("Raleway", Font.BOLD, 15));
        transferButton.addActionListener(this);
        mainPanel.add(transferButton);
        
        // Back button
        backButton = new JButton("BACK");
        backButton.setBounds(350, 450, 170, 40);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Raleway", Font.BOLD, 15));
        backButton.addActionListener(this);
        mainPanel.add(backButton);
        
        // Side image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/transfer.jpg"));
        Image i2 = i1.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(800, 0, 800, 800);
        mainPanel.add(image);
    }
    
    /**
     * Creates the confirmation panel
     */
    private void createConfirmationPanel() {
        confirmationPanel = new JPanel();
        confirmationPanel.setLayout(null);
        confirmationPanel.setBackground(new Color(204, 229, 255));
        
        // Bank logo
        ImageIcon h1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image h2 = h1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon h3 = new ImageIcon(h2);  
        JLabel logo = new JLabel(h3);
        logo.setBounds(70, 30, 100, 100);
        confirmationPanel.add(logo);
        
        // Header
        JLabel header = new JLabel("Confirm Transfer");
        header.setFont(new Font("Osward", Font.BOLD, 32));
        header.setBounds(200, 40, 450, 40);
        header.setForeground(Color.BLACK);
        confirmationPanel.add(header);
        
        // Receipt area
        receiptArea = new JTextArea();
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        receiptArea.setEditable(false);
        receiptArea.setBackground(Color.WHITE);
        receiptArea.setBounds(220, 150, 550, 350);
        receiptArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        confirmationPanel.add(receiptArea);
        
        // Confirm button
        confirmButton = new JButton("CONFIRM");
        confirmButton.setBounds(550, 520, 170, 40);
        confirmButton.setBackground(Color.BLACK);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("Raleway", Font.BOLD, 15));
        confirmButton.addActionListener(this);
        confirmationPanel.add(confirmButton);
        
        // Cancel button
        cancelButton = new JButton("CANCEL");
        cancelButton.setBounds(350, 520, 170, 40);
        cancelButton.setBackground(Color.RED);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Raleway", Font.BOLD, 15));
        cancelButton.addActionListener(this);
        confirmationPanel.add(cancelButton);
        
        // Side image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/transfer.jpg"));
        Image i2 = i1.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(800, 0, 800, 800);
        confirmationPanel.add(image);
    }
    
    /**
     * Validate transfer details
     * 
     * @return Error message or null if validation passes
     */
    private String validateTransferDetails() {
        String destinationAccount = destinationAccountField.getText().trim();
        String amount = amountField.getText().trim();
        
        if (destinationAccount.isEmpty()) {
            return "Please enter destination account number";
        }
        
        if (amount.isEmpty()) {
            return "Please enter transfer amount";
        }
        
        if (destinationAccount.equals(accountNo)) {
            return "Cannot transfer to same account";
        }
        
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                return "Amount must be greater than zero";
            }
            
            double balance = transferService.getAccountBalance(accountNo);
            if (amountValue > balance) {
                return "Insufficient funds. Available balance: Rs. " + balance;
            }
        } catch (NumberFormatException e) {
            return "Invalid amount format";
        }
        
        return null;
    }
    
    /**
     * Handle button actions
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == transferButton) {
                // Validate transfer details
                String validationError = validateTransferDetails();
                if (validationError != null) {
                    JOptionPane.showMessageDialog(this, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Show confirmation screen
                String destinationAccount = destinationAccountField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    description = "Fund Transfer";
                }
                
                // Prepare confirmation message
                StringBuilder sb = new StringBuilder();
                sb.append("TRANSFER DETAILS\n");
                sb.append("----------------\n\n");
                sb.append("From Account: ").append(accountNo).append("\n");
                sb.append("To Account: ").append(destinationAccount).append("\n");
                sb.append("Amount: Rs. ").append(amount).append("\n");
                sb.append("Description: ").append(description).append("\n\n");
                sb.append("Please confirm this transaction.");
                
                receiptArea.setText(sb.toString());
                ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "confirmation");
                
            } else if (ae.getSource() == confirmButton) {
                // Process transfer
                String destinationAccount = destinationAccountField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    description = "Fund Transfer";
                }
                
                String result = transferService.transferFunds(accountNo, destinationAccount, amount, description, pin);
                
                if (result.startsWith("TRANSACTION RECEIPT")) {
                    // Success
                    receiptArea.setText(result);
                    JOptionPane.showMessageDialog(this, "Transfer completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update receipt area with transfer receipt
                    receiptArea.setText(result);
                } else {
                    // Error
                    JOptionPane.showMessageDialog(this, result, "Transfer Failed", JOptionPane.ERROR_MESSAGE);
                    ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "main");
                }
                
            } else if (ae.getSource() == cancelButton) {
                // Go back to main transfer screen
                ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "main");
                
            } else if (ae.getSource() == backButton) {
                // Go back to transactions menu
                setVisible(false);
                new Transactions(pin, accountNo).setVisible(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        new Transfer("", "");
    }
}

/* 
 * Test cases:
 * TRAN-UI-01: Verify form validation for required fields and amount
 * TRAN-UI-02: Verify confirmation screen displays correct transfer details
 * TRAN-UI-03: Verify receipt display after successful transfer
 * SEC-06: Verify session timeout check
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */