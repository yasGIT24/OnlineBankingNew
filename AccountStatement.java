package banking.management.system;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-01, REQ-PDF-04, REQ-PDF-05]
 * UI component for generating and downloading account statements in PDF format
 */
public class AccountStatement extends JFrame implements ActionListener {

    private JDateChooser startDateChooser, endDateChooser;
    private JButton generateButton, downloadButton, backButton;
    private JLabel statusLabel, downloadLinkLabel;
    private JPanel mainPanel, datePanel, buttonPanel;
    private JLabel titleLabel, subtitleLabel, accountLabel;
    private String accountNumber;
    private String pin;
    private StatementPdfService statementService;
    private StatementPdfService.EncryptedDownloadLink currentDownloadLink;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMMM d, yyyy");

    /**
     * Constructor
     * 
     * @param accountNumber User's account number
     * @param pin User's PIN
     */
    public AccountStatement(String accountNumber, String pin) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.statementService = new StatementPdfService();
        
        setLayout(null);
        setTitle("Account Statement");
        
        // Set up the main panel
        setupUI();
        
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 248, 255)); // Light blue background
        setVisible(true);
    }

    /**
     * Set up the user interface components
     */
    private void setupUI() {
        // Title and header
        titleLabel = new JLabel("Account Statement");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(new Color(0, 0, 128)); // Navy blue
        titleLabel.setBounds(250, 30, 300, 40);
        add(titleLabel);

        subtitleLabel = new JLabel("Generate and download your account statements");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setBounds(210, 70, 400, 30);
        add(subtitleLabel);

        accountLabel = new JLabel("Account: " + maskAccountNumber(accountNumber));
        accountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        accountLabel.setBounds(300, 100, 200, 30);
        add(accountLabel);

        // Date selection panel
        datePanel = new JPanel();
        datePanel.setLayout(null);
        datePanel.setBackground(Color.WHITE);
        datePanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        datePanel.setBounds(50, 150, 700, 150);
        add(datePanel);

        // Date selection components
        JLabel selectDateLabel = new JLabel("Select Statement Period:");
        selectDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectDateLabel.setBounds(20, 20, 200, 20);
        datePanel.add(selectDateLabel);

        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        startDateLabel.setBounds(20, 60, 100, 25);
        datePanel.add(startDateLabel);

        startDateChooser = new JDateChooser();
        startDateChooser.setBounds(120, 60, 180, 25);
        startDateChooser.setDate(getDefaultStartDate());
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        datePanel.add(startDateChooser);

        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        endDateLabel.setBounds(350, 60, 100, 25);
        datePanel.add(endDateLabel);

        endDateChooser = new JDateChooser();
        endDateChooser.setBounds(450, 60, 180, 25);
        endDateChooser.setDate(new Date()); // Default to today
        endDateChooser.setDateFormatString("yyyy-MM-dd");
        datePanel.add(endDateChooser);

        generateButton = new JButton("Generate Statement");
        generateButton.setBackground(new Color(0, 102, 204)); // Blue
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.setBounds(250, 100, 200, 30);
        generateButton.addActionListener(this);
        datePanel.add(generateButton);

        // Status and download section
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setBounds(150, 320, 500, 25);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        add(statusLabel);

        downloadLinkLabel = new JLabel("");
        downloadLinkLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        downloadLinkLabel.setBounds(50, 350, 700, 25);
        downloadLinkLabel.setHorizontalAlignment(JLabel.CENTER);
        downloadLinkLabel.setForeground(new Color(0, 102, 204));
        add(downloadLinkLabel);

        downloadButton = new JButton("Download PDF");
        downloadButton.setBackground(new Color(0, 153, 0)); // Green
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFont(new Font("Arial", Font.BOLD, 14));
        downloadButton.setBounds(300, 390, 200, 40);
        downloadButton.addActionListener(this);
        downloadButton.setEnabled(false);
        add(downloadButton);

        backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBounds(325, 460, 150, 30);
        backButton.addActionListener(this);
        add(backButton);
    }

    /**
     * Action handler for button clicks
     * 
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            generateStatement();
        } else if (e.getSource() == downloadButton) {
            downloadStatement();
        } else if (e.getSource() == backButton) {
            dispose();
            new Transactions(pin, accountNumber).setVisible(true);
        }
    }

    /**
     * Generate account statement PDF
     */
    // [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-01, REQ-PDF-02]
    private void generateStatement() {
        Date startDate = startDateChooser.getDate();
        Date endDate = endDateChooser.getDate();
        
        // Validate dates
        if (startDate == null || endDate == null) {
            statusLabel.setText("Please select both start and end dates");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (startDate.after(endDate)) {
            statusLabel.setText("Start date must be before end date");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (endDate.after(new Date())) {
            statusLabel.setText("End date cannot be in the future");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        // Show processing message
        statusLabel.setText("Generating statement... Please wait");
        statusLabel.setForeground(Color.BLUE);
        downloadLinkLabel.setText("");
        downloadButton.setEnabled(false);
        
        // Use SwingWorker to prevent UI freezing during PDF generation
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Generate statement PDF
                    currentDownloadLink = statementService.generateStatement(accountNumber, startDate, endDate);
                    return null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw ex;
                }
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    
                    // Update UI
                    statusLabel.setText("Statement generated successfully for period: " + 
                            displayDateFormat.format(startDate) + " to " + displayDateFormat.format(endDate));
                    statusLabel.setForeground(new Color(0, 128, 0)); // Dark green
                    
                    downloadLinkLabel.setText("Your statement is ready for download. Click the button below.");
                    downloadButton.setEnabled(true);
                    
                } catch (Exception ex) {
                    statusLabel.setText("Error generating statement: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                    ex.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }

    /**
     * Download the generated statement
     */
    // [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-03, REQ-PDF-04]
    private void downloadStatement() {
        if (currentDownloadLink == null) {
            statusLabel.setText("No statement available. Please generate a statement first.");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Account Statement");
        
        // Set default file name
        String defaultFileName = "Account_Statement_" + dateFormat.format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        // Set filter for PDF files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
        fileChooser.setFileFilter(filter);
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure the file has .pdf extension
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
                fileToSave = new File(filePath);
            }
            
            try {
                // Get the source PDF file
                File sourceFile = new File(statementService.getPdfDirectory() + "/" + currentDownloadLink.getFileName());
                
                // Copy the file to the user's chosen location
                java.nio.file.Files.copy(sourceFile.toPath(), fileToSave.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // Update the download status in the database
                statementService.updateDownloadStatus(
                        currentDownloadLink.getDownloadId(), 
                        java.net.InetAddress.getLocalHost().getHostAddress());
                
                statusLabel.setText("Statement downloaded successfully to: " + fileToSave.getName());
                statusLabel.setForeground(new Color(0, 128, 0)); // Dark green
                
            } catch (Exception ex) {
                statusLabel.setText("Error downloading statement: " + ex.getMessage());
                statusLabel.setForeground(Color.RED);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Get default start date (1 month ago)
     * 
     * @return Date object for 1 month ago
     */
    private Date getDefaultStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * Masks an account number for security (shows only last 4 digits)
     * 
     * @param accountNo Account number to mask
     * @return Masked account number
     */
    private String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() <= 4) {
            return accountNo;
        }
        
        int visibleChars = 4;
        int maskLength = accountNo.length() - visibleChars;
        StringBuilder masked = new StringBuilder();
        
        for (int i = 0; i < maskLength; i++) {
            masked.append("X");
        }
        
        masked.append(accountNo.substring(maskLength));
        return masked.toString();
    }

    /**
     * Main method for testing
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        new AccountStatement("1234567890", "1234");
    }
}

// [AGENT GENERATED CODE - REQUIREMENT:REQ-PDF-01, REQ-PDF-03, REQ-PDF-04, REQ-PDF-05]
// This file implements the UI for generating and downloading account statements in PDF format.
// It allows users to select a date range, generate a statement PDF, and download it securely.
// The implementation includes date validation, masked account numbers, and download activity logging.
// Agent run identifier: AGENT-PDF-UI-2025-12-02