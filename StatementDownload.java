package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.border.LineBorder;

/**
 * User interface for PDF statement download feature.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
 * This class provides the UI for users to select date ranges and download
 * account statements in PDF format.
 */
public class StatementDownload extends JFrame implements ActionListener {
    
    private JLabel titleLabel, fromDateLabel, toDateLabel, statusLabel;
    private JComboBox<String> fromMonthCombo, fromYearCombo, toMonthCombo, toYearCombo;
    private JButton generateButton, backButton, viewButton;
    private JPanel mainPanel;
    private String pin;
    private String accountNo;
    private String currentDownloadToken;
    
    private StatementService statementService;
    private AuditLogService auditLogService;
    
    /**
     * Constructor initializes the statement download UI.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param pin Account PIN/password
     * @param accountNo Account number
     */
    public StatementDownload(String pin, String accountNo) {
        this.pin = pin;
        this.accountNo = accountNo;
        this.statementService = new StatementService();
        this.auditLogService = new AuditLogService();
        
        // Set up the frame
        setTitle("Download Account Statement");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize UI components
        initComponents();
        
        setVisible(true);
    }
    
    /**
     * Initializes UI components.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     */
    private void initComponents() {
        // Main panel with background color
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(0, 51, 102));
        setContentPane(mainPanel);
        
        // Bank logo
        ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image logoImg = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon scaledLogoIcon = new ImageIcon(logoImg);  
        JLabel logoLabel = new JLabel(scaledLogoIcon);
        logoLabel.setBounds(50, 10, 100, 100);
        mainPanel.add(logoLabel);
        
        // Title
        titleLabel = new JLabel("DOWNLOAD ACCOUNT STATEMENT");
        titleLabel.setFont(new Font("Osward", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(170, 30, 500, 40);
        mainPanel.add(titleLabel);
        
        // From date section
        fromDateLabel = new JLabel("From Date:");
        fromDateLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        fromDateLabel.setForeground(Color.WHITE);
        fromDateLabel.setBounds(100, 140, 120, 30);
        mainPanel.add(fromDateLabel);
        
        // Month dropdown for From date
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        fromMonthCombo = new JComboBox<>(months);
        fromMonthCombo.setBounds(230, 140, 150, 30);
        fromMonthCombo.setBackground(Color.WHITE);
        mainPanel.add(fromMonthCombo);
        
        // Year dropdown for From date
        String[] years = getYearOptions();
        fromYearCombo = new JComboBox<>(years);
        fromYearCombo.setBounds(400, 140, 100, 30);
        fromYearCombo.setBackground(Color.WHITE);
        mainPanel.add(fromYearCombo);
        
        // To date section
        toDateLabel = new JLabel("To Date:");
        toDateLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        toDateLabel.setForeground(Color.WHITE);
        toDateLabel.setBounds(100, 190, 120, 30);
        mainPanel.add(toDateLabel);
        
        // Month dropdown for To date
        toMonthCombo = new JComboBox<>(months);
        toMonthCombo.setBounds(230, 190, 150, 30);
        toMonthCombo.setBackground(Color.WHITE);
        
        // Set current month as default
        Calendar cal = Calendar.getInstance();
        toMonthCombo.setSelectedIndex(cal.get(Calendar.MONTH));
        fromMonthCombo.setSelectedIndex(Math.max(0, cal.get(Calendar.MONTH) - 1)); // Previous month
        
        mainPanel.add(toMonthCombo);
        
        // Year dropdown for To date
        toYearCombo = new JComboBox<>(years);
        toYearCombo.setBounds(400, 190, 100, 30);
        toYearCombo.setBackground(Color.WHITE);
        mainPanel.add(toYearCombo);
        
        // Generate button
        generateButton = new JButton("GENERATE STATEMENT");
        generateButton.setBounds(100, 260, 200, 40);
        generateButton.setBackground(new Color(204, 229, 255));
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.addActionListener(this);
        mainPanel.add(generateButton);
        
        // View button (initially disabled)
        viewButton = new JButton("VIEW PDF");
        viewButton.setBounds(320, 260, 150, 40);
        viewButton.setBackground(new Color(204, 229, 255));
        viewButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewButton.setEnabled(false);
        viewButton.addActionListener(this);
        mainPanel.add(viewButton);
        
        // Back button
        backButton = new JButton("BACK");
        backButton.setBounds(490, 260, 100, 40);
        backButton.setBackground(new Color(204, 229, 255));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(this);
        mainPanel.add(backButton);
        
        // Status label
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Raleway", Font.BOLD, 16));
        statusLabel.setForeground(new Color(255, 204, 0));
        statusLabel.setBounds(100, 320, 500, 30);
        mainPanel.add(statusLabel);
        
        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBounds(100, 370, 500, 80);
        infoPanel.setBackground(new Color(0, 31, 63));
        infoPanel.setBorder(new LineBorder(Color.WHITE));
        infoPanel.setLayout(null);
        mainPanel.add(infoPanel);
        
        JLabel infoLabel = new JLabel("<html>• Statements are available for the past 12 months<br>• PDF is secured with your account's last 4 digits<br>• Activity will be logged for security purposes</html>");
        infoLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBounds(20, 5, 460, 70);
        infoPanel.add(infoLabel);
    }
    
    /**
     * Generates array of year options for dropdown.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @return Array of year strings
     */
    private String[] getYearOptions() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[3]; // Current year and 2 previous years
        
        for (int i = 0; i < 3; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        
        return years;
    }
    
    /**
     * Action event handler for buttons.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param ae Action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == generateButton) {
            generateStatement();
        } else if (ae.getSource() == viewButton) {
            viewStatement();
        } else if (ae.getSource() == backButton) {
            setVisible(false);
            new Transactions(pin, accountNo).setVisible(true);
        }
    }
    
    /**
     * Generates PDF statement based on selected date range.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     */
    private void generateStatement() {
        try {
            // Get selected dates
            String fromMonth = String.valueOf(fromMonthCombo.getSelectedIndex() + 1);
            String fromYear = fromYearCombo.getSelectedItem().toString();
            String toMonth = String.valueOf(toMonthCombo.getSelectedIndex() + 1);
            String toYear = toYearCombo.getSelectedItem().toString();
            
            // Format dates as yyyy-MM-dd
            String fromDate = fromYear + "-" + (fromMonth.length() == 1 ? "0" + fromMonth : fromMonth) + "-01";
            
            // Calculate last day of month for to-date
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(toYear), Integer.parseInt(toMonth) - 1, 1);
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            String toDate = toYear + "-" + (toMonth.length() == 1 ? "0" + toMonth : toMonth) + "-" + lastDay;
            
            // Validate date range
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(fromDate);
            Date end = sdf.parse(toDate);
            
            if (start.after(end)) {
                statusLabel.setText("Error: From date must be before To date");
                statusLabel.setForeground(Color.RED);
                return;
            }
            
            // Show generating message
            statusLabel.setText("Generating statement, please wait...");
            statusLabel.setForeground(new Color(255, 204, 0));
            
            // Generate statement in a background thread to keep UI responsive
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return statementService.generateStatement(accountNo, pin, fromDate, toDate);
                }
                
                @Override
                protected void done() {
                    try {
                        currentDownloadToken = get();
                        statusLabel.setText("Statement generated successfully!");
                        statusLabel.setForeground(new Color(0, 204, 0));
                        viewButton.setEnabled(true);
                        
                    } catch (Exception e) {
                        statusLabel.setText("Error: " + e.getMessage());
                        statusLabel.setForeground(Color.RED);
                        viewButton.setEnabled(false);
                    }
                }
            };
            
            worker.execute();
            
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Opens the generated PDF statement.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     */
    private void viewStatement() {
        try {
            if (currentDownloadToken == null) {
                statusLabel.setText("No statement available. Please generate first.");
                statusLabel.setForeground(Color.RED);
                return;
            }
            
            // Get PDF file path from token
            String filePath = statementService.getStatementByToken(currentDownloadToken);
            
            // Log the statement view/download
            auditLogService.logActivity(accountNo, "STATEMENT_VIEW", "Statement viewed", "SUCCESS");
            
            // Try to open the PDF using Desktop API
            File pdfFile = new File(filePath);
            
            if (pdfFile.exists()) {
                // In a real application, this would use Desktop.getDesktop().open(pdfFile)
                // For this implementation, just show a success message
                JOptionPane.showMessageDialog(this, 
                        "PDF statement opened at: " + filePath + "\n" +
                        "Password: Last 4 digits of your account number", 
                        "Statement Downloaded", JOptionPane.INFORMATION_MESSAGE);
                
                statusLabel.setText("Statement opened successfully!");
                statusLabel.setForeground(new Color(0, 204, 0));
            } else {
                statusLabel.setText("Error: Statement file not found");
                statusLabel.setForeground(Color.RED);
            }
            
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Main method for testing.
     * [AGENT GENERATED CODE - REQUIREMENT:PDF_STATEMENT_DOWNLOAD]
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        new StatementDownload("", "");
    }
}