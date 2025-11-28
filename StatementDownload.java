package banking.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Claude AI
 * Statement Download UI for downloading account statements in PDF format
 */
/* [AGENT GENERATED CODE - REQUIREMENT:REQ001]
 * Statement Download UI class for selecting date ranges and generating PDF statements
 */
public class StatementDownload extends JFrame implements ActionListener {
    
    JButton back, downloadPdf;
    JLabel title, fromDateLabel, toDateLabel;
    JTextField fromDateField, toDateField;
    JComboBox<String> statementTypeCombo;
    String pin;
    String Accountno;
    
    public StatementDownload(String pin, String Accountno) {
        this.pin = pin;
        this.Accountno = Accountno;
        
        setLayout(null);
        setSize(1600, 1200);
        setVisible(true);
        setTitle("Statement Download");
        getContentPane().setBackground(new Color(204, 229, 255));
        
        // Bank logo and heading
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
        Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);  
        JLabel label = new JLabel(i3);
        label.setBounds(70, 10, 100, 100);
        add(label);
        
        JLabel text = new JLabel("WELCOME TO THE BANK");
        text.setFont(new Font("Osward", Font.BOLD, 32));
        text.setBounds(200, 40, 450, 40);
        text.setForeground(Color.black);
        add(text);
        
        // Statement Download Form
        title = new JLabel("Download Account Statement");
        title.setFont(new Font("Raleway", Font.BOLD, 28));
        title.setBounds(300, 140, 450, 40);
        title.setForeground(Color.BLACK);
        add(title);
        
        // Statement type selection
        JLabel statementTypeLabel = new JLabel("Statement Type:");
        statementTypeLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        statementTypeLabel.setBounds(150, 200, 250, 30);
        add(statementTypeLabel);
        
        String[] statementTypes = {"Custom Date Range", "Last Month", "Last 3 Months", "Last 6 Months", "Year to Date"};
        statementTypeCombo = new JComboBox<>(statementTypes);
        statementTypeCombo.setBounds(350, 200, 200, 30);
        statementTypeCombo.addActionListener(this);
        add(statementTypeCombo);
        
        // Date range fields
        fromDateLabel = new JLabel("From Date (yyyy-mm-dd):");
        fromDateLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        fromDateLabel.setBounds(150, 250, 250, 30);
        add(fromDateLabel);
        
        fromDateField = new JTextField();
        fromDateField.setBounds(350, 250, 200, 30);
        fromDateField.setFont(new Font("Raleway", Font.PLAIN, 16));
        add(fromDateField);
        
        toDateLabel = new JLabel("To Date (yyyy-mm-dd):");
        toDateLabel.setFont(new Font("Raleway", Font.BOLD, 18));
        toDateLabel.setBounds(150, 300, 250, 30);
        add(toDateLabel);
        
        toDateField = new JTextField();
        toDateField.setBounds(350, 300, 200, 30);
        toDateField.setFont(new Font("Raleway", Font.PLAIN, 16));
        add(toDateField);
        
        // Set default dates to current month
        setDefaultDates();
        
        // Download button
        downloadPdf = new JButton("DOWNLOAD PDF");
        downloadPdf.setBounds(350, 380, 200, 40);
        downloadPdf.setBackground(Color.BLACK);
        downloadPdf.setForeground(Color.WHITE);
        downloadPdf.setFont(new Font("Raleway", Font.BOLD, 16));
        downloadPdf.addActionListener(this);
        add(downloadPdf);
        
        // Back button
        back = new JButton("BACK");
        back.setBounds(150, 380, 150, 40);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.setFont(new Font("Raleway", Font.BOLD, 16));
        back.addActionListener(this);
        add(back);
        
        // Add statement format information
        String statementInfo = "<html><b>Statement Information:</b><br>" +
                              "- PDF statements include full transaction history for the selected period<br>" +
                              "- Statements contain account details and transaction summaries<br>" +
                              "- Downloaded statements are encrypted for security<br>" +
                              "- Statement downloads are logged for compliance purposes</html>";
        
        JLabel infoLabel = new JLabel(statementInfo);
        infoLabel.setBounds(150, 450, 600, 100);
        infoLabel.setFont(new Font("Raleway", Font.PLAIN, 14));
        add(infoLabel);
        
        // Add an image
        ImageIcon k1 = new ImageIcon(ClassLoader.getSystemResource("icons/withdraw2.jpg"));
        Image k2 = k1.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT);
        ImageIcon k3 = new ImageIcon(k2);
        JLabel label8 = new JLabel(k3);
        label8.setBounds(800, 0, 800, 800);
        add(label8);
    }
    
    private void setDefaultDates() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        
        // Set To Date to today
        toDateField.setText(dateFormat.format(calendar.getTime()));
        
        // Set From Date to one month ago
        calendar.add(Calendar.MONTH, -1);
        fromDateField.setText(dateFormat.format(calendar.getTime()));
    }
    
    private void updateDateRangeBasedOnSelection() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        
        // Set To Date to today
        toDateField.setText(dateFormat.format(currentDate));
        
        String selected = (String) statementTypeCombo.getSelectedItem();
        calendar = Calendar.getInstance(); // Reset calendar
        
        switch(selected) {
            case "Last Month":
                calendar.add(Calendar.MONTH, -1);
                break;
            case "Last 3 Months":
                calendar.add(Calendar.MONTH, -3);
                break;
            case "Last 6 Months":
                calendar.add(Calendar.MONTH, -6);
                break;
            case "Year to Date":
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
            default:
                // Custom date range - don't change
                return;
        }
        
        fromDateField.setText(dateFormat.format(calendar.getTime()));
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new Transactions(pin, Accountno).setVisible(true);
        } else if (ae.getSource() == statementTypeCombo) {
            updateDateRangeBasedOnSelection();
        } else if (ae.getSource() == downloadPdf) {
            generateStatement();
        }
    }
    
    private void generateStatement() {
        try {
            // Validate dates
            String fromDate = fromDateField.getText();
            String toDate = toDateField.getText();
            
            if (fromDate.isEmpty() || toDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter valid dates");
                return;
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = dateFormat.parse(fromDate);
            Date endDate = dateFormat.parse(toDate);
            
            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(this, "From date cannot be after To date");
                return;
            }
            
            // Check authorization
            ConnectionSql conn = new ConnectionSql();
            ResultSet rs = conn.s.executeQuery("SELECT * FROM login WHERE Account_No = '" + Accountno + 
                                              "' AND Login_Password = '" + pin + "'");
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Unauthorized access. Please log in again.");
                setVisible(false);
                return;
            }
            
            // Generate PDF using PDFGenerator
            PDFGenerator pdfGenerator = new PDFGenerator();
            boolean success = pdfGenerator.generateStatement(Accountno, fromDate, toDate);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Statement generated successfully!");
                
                // Log this activity for compliance
                conn.logActivity(Accountno, "Statement Downloaded", 
                               "PDF Statement generated for period: " + fromDate + " to " + toDate);
            } else {
                JOptionPane.showMessageDialog(this, "Error generating statement. Please try again.");
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new StatementDownload("", "");
    }
}
/* [END AGENT GENERATED CODE] */

/* 
 * Requirements implemented:
 * REQ001: PDF Statement Downloads
 * Agent Run Identifier: CLAUDE-3-SONNET-20250219
 */