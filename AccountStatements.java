package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;
import java.text.SimpleDateFormat;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.LineBorder;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US1]
 * This class provides the UI for downloading account statements in PDF format.
 * It allows users to select a date range and download a statement.
 */
public class AccountStatements extends JFrame implements ActionListener {
    
    private final String pin;
    private final String accountNo;
    private final StatementService statementService;
    private final AuditLogger auditLogger;
    
    private JDateChooser fromDateChooser, toDateChooser;
    private JButton generateButton, downloadButton, backButton;
    private JPanel downloadPanel;
    private JLabel downloadLinkLabel;
    private String currentDownloadToken;
    
    public AccountStatements(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        this.statementService = new StatementService(accountNo, pin);
        this.auditLogger = new AuditLogger();
        
        // Ensure audit table exists
        auditLogger.ensureAuditTableExists();
        
        // Set up the UI
        setTitle("Account Statements");
        setSize(1600, 1200);
        setLayout(null);
        getContentPane().setBackground(new Color(204, 229, 255));
        
        // Add bank logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image logoImg = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledLogoIcon = new ImageIcon(logoImg);
        JLabel logoLabel = new JLabel(scaledLogoIcon);
        logoLabel.setBounds(70, 10, 100, 100);
        add(logoLabel);
        
        // Add statement image
        ImageIcon statementIcon = new ImageIcon(ClassLoader.getSystemResource("icons/statement.jpg"));
        Image statementImg = statementIcon.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT);
        ImageIcon scaledStatementIcon = new ImageIcon(statementImg);
        JLabel statementImageLabel = new JLabel(scaledStatementIcon);
        statementImageLabel.setBounds(800, 150, 500, 500);
        add(statementImageLabel);
        
        // Title
        JLabel title = new JLabel("ACCOUNT STATEMENTS");
        title.setFont(new Font("Osward", Font.BOLD, 32));
        title.setBounds(250, 40, 450, 40);
        title.setForeground(Color.BLACK);
        add(title);
        
        // Date range selection
        JLabel dateRangeLabel = new JLabel("Select Date Range:");
        dateRangeLabel.setFont(new Font("Raleway", Font.BOLD, 20));
        dateRangeLabel.setBounds(150, 150, 250, 30);
        add(dateRangeLabel);
        
        JLabel fromDateLabel = new JLabel("From Date:");
        fromDateLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        fromDateLabel.setBounds(150, 200, 100, 30);
        add(fromDateLabel);
        
        fromDateChooser = new JDateChooser();
        fromDateChooser.setBounds(260, 200, 200, 30);
        fromDateChooser.setFont(new Font("Raleway", Font.PLAIN, 16));
        fromDateChooser.setDateFormatString("yyyy-MM-dd");
        add(fromDateChooser);
        
        JLabel toDateLabel = new JLabel("To Date:");
        toDateLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        toDateLabel.setBounds(150, 250, 100, 30);
        add(toDateLabel);
        
        toDateChooser = new JDateChooser();
        toDateChooser.setBounds(260, 250, 200, 30);
        toDateChooser.setFont(new Font("Raleway", Font.PLAIN, 16));
        toDateChooser.setDateFormatString("yyyy-MM-dd");
        add(toDateChooser);
        
        // Generate button
        generateButton = new JButton("GENERATE STATEMENT");
        generateButton.setFont(new Font("Raleway", Font.BOLD, 16));
        generateButton.setBounds(220, 320, 250, 40);
        generateButton.setBackground(new Color(0, 51, 102));
        generateButton.setForeground(Color.WHITE);
        generateButton.addActionListener(this);
        add(generateButton);
        
        // Download panel (initially hidden)
        downloadPanel = new JPanel();
        downloadPanel.setBounds(150, 380, 500, 120);
        downloadPanel.setBackground(new Color(240, 248, 255));
        downloadPanel.setBorder(new LineBorder(new Color(0, 51, 102), 1));
        downloadPanel.setLayout(null);
        downloadPanel.setVisible(false);
        add(downloadPanel);
        
        JLabel downloadTitleLabel = new JLabel("Your Statement is Ready!");
        downloadTitleLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        downloadTitleLabel.setBounds(20, 10, 300, 30);
        downloadPanel.add(downloadTitleLabel);
        
        downloadLinkLabel = new JLabel("Click the button below to download your statement");
        downloadLinkLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        downloadLinkLabel.setBounds(20, 40, 400, 20);
        downloadPanel.add(downloadLinkLabel);
        
        downloadButton = new JButton("DOWNLOAD PDF");
        downloadButton.setFont(new Font("Raleway", Font.BOLD, 14));
        downloadButton.setBounds(150, 70, 200, 30);
        downloadButton.setBackground(new Color(0, 102, 0));
        downloadButton.setForeground(Color.WHITE);
        downloadButton.addActionListener(this);
        downloadPanel.add(downloadButton);
        
        // Back button
        backButton = new JButton("BACK");
        backButton.setFont(new Font("Raleway", Font.BOLD, 16));
        backButton.setBounds(220, 530, 100, 40);
        backButton.setBackground(Color.BLACK);
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(this);
        add(backButton);
        
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == generateButton) {
            handleGenerateStatement();
        } else if (ae.getSource() == downloadButton) {
            handleDownloadStatement();
        } else if (ae.getSource() == backButton) {
            setVisible(false);
            new Transactions(pin, accountNo).setVisible(true);
        }
    }
    
    private void handleGenerateStatement() {
        // Validate date range
        if (fromDateChooser.getDate() == null || toDateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select both from and to dates");
            return;
        }
        
        // Format dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = sdf.format(fromDateChooser.getDate());
        String toDate = sdf.format(toDateChooser.getDate());
        
        // Check if from date is before to date
        if (fromDateChooser.getDate().after(toDateChooser.getDate())) {
            JOptionPane.showMessageDialog(this, "From date must be before to date");
            return;
        }
        
        // Show processing message
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Generate the statement in a separate thread to avoid UI freeze
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                // Generate the PDF statement
                return statementService.generatePDFStatement(fromDate, toDate);
            }
            
            @Override
            protected void done() {
                try {
                    currentDownloadToken = get();
                    if (currentDownloadToken != null) {
                        // Show the download panel
                        downloadPanel.setVisible(true);
                        JOptionPane.showMessageDialog(AccountStatements.this, 
                            "Statement generated successfully!");
                        
                        // Log the activity
                        auditLogger.logActivity(accountNo, "PDF_STATEMENT_GENERATED",
                            "Statement generated for period: " + fromDate + " to " + toDate);
                    } else {
                        JOptionPane.showMessageDialog(AccountStatements.this, 
                            "Error generating statement. Please try again.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AccountStatements.this, 
                        "Error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        
        worker.execute();
    }
    
    private void handleDownloadStatement() {
        if (currentDownloadToken == null) {
            JOptionPane.showMessageDialog(this, "No statement available to download");
            return;
        }
        
        // Show processing message
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Download the statement in a separate thread
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                EncryptionService encryptionService = new EncryptionService();
                return encryptionService.getDecryptedFile(currentDownloadToken);
            }
            
            @Override
            protected void done() {
                try {
                    String filePath = get();
                    if (filePath != null) {
                        // Open the PDF file
                        File file = new File(filePath);
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(file);
                        } else {
                            JOptionPane.showMessageDialog(AccountStatements.this, 
                                "File saved to: " + filePath);
                        }
                        
                        // Log the download activity
                        auditLogger.logActivity(accountNo, "PDF_STATEMENT_DOWNLOADED",
                            "Statement downloaded successfully");
                    } else {
                        JOptionPane.showMessageDialog(AccountStatements.this, 
                            "Error downloading statement. Please try again.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AccountStatements.this, 
                        "Error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        
        worker.execute();
    }
    
    public static void main(String[] args) {
        new AccountStatements("", "");
    }
}

/* 
 * Agent Run Identifier: BANK-PDF-UI-20251126
 * Related Test Cases: PDF-UI-001, PDF-UI-002
 */