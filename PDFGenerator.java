package banking.management.system;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.UUID;

/**
 * @author Claude AI
 * PDF Generator utility for statement downloads
 */
/* [AGENT GENERATED CODE - REQUIREMENT:REQ001]
 * PDF Generator class to create and save account statements in PDF format
 */
public class PDFGenerator {
    
    // Path to store generated PDF files
    private static final String PDF_STORAGE_PATH = System.getProperty("user.home") + "/OnlineBankingStatements/";
    
    public PDFGenerator() {
        // Create storage directory if it doesn't exist
        File directory = new File(PDF_STORAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Generate PDF statement for an account based on date range
     * 
     * @param accountNo The account number
     * @param fromDate Starting date for statement
     * @param toDate Ending date for statement
     * @return boolean indicating success or failure
     */
    public boolean generateStatement(String accountNo, String fromDate, String toDate) {
        try {
            // Generate unique filename
            String filename = generateSecureFilename(accountNo);
            
            // Get account and transaction data
            ConnectionSql connection = new ConnectionSql();
            
            // Get account holder information
            ResultSet accountInfo = connection.s.executeQuery(
                "SELECT * FROM signup3 WHERE Account_No = '" + accountNo + "'"
            );
            
            if (!accountInfo.next()) {
                JOptionPane.showMessageDialog(null, "Account information not found.");
                return false;
            }
            
            // Get transactions for the date range
            ResultSet transactions = connection.getTransactionsByDateRange(accountNo, fromDate, toDate);
            
            // Create PDF document
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(PDF_STORAGE_PATH + filename));
            document.open();
            
            // Add bank logo and header
            addBankHeader(document);
            
            // Add statement title
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("ACCOUNT STATEMENT", 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" "));
            
            // Add account details
            document.add(new com.itextpdf.text.Paragraph("Account Number: " + maskAccountNumber(accountNo)));
            document.add(new com.itextpdf.text.Paragraph("Account Holder: " + accountInfo.getString("Name")));
            document.add(new com.itextpdf.text.Paragraph("Statement Period: " + fromDate + " to " + toDate));
            document.add(new com.itextpdf.text.Paragraph("Generation Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            document.add(new com.itextpdf.text.Paragraph(" "));
            
            // Create transaction table
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4);
            table.setWidthPercentage(100);
            
            // Add table headers
            table.addCell(createHeaderCell("Date"));
            table.addCell(createHeaderCell("Description"));
            table.addCell(createHeaderCell("Amount (Rs)"));
            table.addCell(createHeaderCell("Balance (Rs)"));
            
            // Add transaction data
            int runningBalance = 0;
            boolean hasTransactions = false;
            
            while (transactions.next()) {
                hasTransactions = true;
                String type = transactions.getString("type");
                int amount = Integer.parseInt(transactions.getString("amount"));
                
                if (type.equals("Deposit")) {
                    runningBalance += amount;
                } else {
                    runningBalance -= amount;
                }
                
                table.addCell(transactions.getString("date"));
                table.addCell(type);
                table.addCell(amount + " " + (type.equals("Deposit") ? "CR" : "DR"));
                table.addCell(String.valueOf(runningBalance));
            }
            
            if (!hasTransactions) {
                document.add(new com.itextpdf.text.Paragraph("No transactions found for the selected period."));
            } else {
                document.add(table);
                
                // Add summary section
                document.add(new com.itextpdf.text.Paragraph(" "));
                document.add(new com.itextpdf.text.Paragraph("Closing Balance: Rs " + runningBalance, 
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD)));
            }
            
            // Add footer with disclaimer
            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("This is a computer-generated statement and does not require a signature.", 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8)));
            
            document.close();
            
            // Log this activity for compliance
            connection.logActivity(accountNo, "Statement Downloaded", "PDF Statement generated for period: " + fromDate + " to " + toDate);
            connection.close();
            
            // Open the generated PDF file
            openPDF(PDF_STORAGE_PATH + filename);
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Generate a PDF with only current balance information
     * 
     * @param accountNo The account number
     * @param accountInfo ResultSet containing account information
     * @return boolean indicating success or failure
     */
    public boolean generateBalanceStatement(String accountNo, ResultSet accountInfo) {
        try {
            // Generate unique filename
            String filename = generateSecureFilename(accountNo);
            
            ConnectionSql connection = new ConnectionSql();
            
            // Calculate current balance
            int balance = 0;
            while (accountInfo.next()) {
                if (accountInfo.getString("type").equals("Deposit")) {
                    balance += Integer.parseInt(accountInfo.getString("amount"));
                } else {
                    balance -= Integer.parseInt(accountInfo.getString("amount"));
                }
            }
            
            // Create PDF document
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(PDF_STORAGE_PATH + filename));
            document.open();
            
            // Add bank logo and header
            addBankHeader(document);
            
            // Add balance statement title
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("BALANCE CERTIFICATE", 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" "));
            
            // Add account details
            document.add(new com.itextpdf.text.Paragraph("Account Number: " + maskAccountNumber(accountNo)));
            document.add(new com.itextpdf.text.Paragraph("Generation Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            document.add(new com.itextpdf.text.Paragraph(" "));
            
            // Add balance information
            com.itextpdf.text.Paragraph balanceInfo = new com.itextpdf.text.Paragraph(
                "Current Account Balance: Rs " + balance,
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD)
            );
            balanceInfo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(balanceInfo);
            
            // Add footer with disclaimer
            document.add(new com.itextpdf.text.Paragraph(" "));
            document.add(new com.itextpdf.text.Paragraph("This is a computer-generated statement and does not require a signature.", 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8)));
            
            document.close();
            
            // Log this activity for compliance
            connection.logActivity(accountNo, "Balance Statement Downloaded", "PDF Balance Certificate generated");
            connection.close();
            
            // Open the generated PDF file
            openPDF(PDF_STORAGE_PATH + filename);
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper methods
    
    private String generateSecureFilename(String accountNo) {
        // Generate a unique filename with UUID to avoid any security issues
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String uniqueID = UUID.randomUUID().toString().substring(0, 8);
        return "Statement_" + timestamp + "_" + uniqueID + ".pdf";
    }
    
    private String maskAccountNumber(String accountNo) {
        // Mask account number for security, showing only last 4 digits
        int len = accountNo.length();
        if (len <= 4) {
            return accountNo;
        }
        return "XXXX-XXXX-" + accountNo.substring(len - 4);
    }
    
    private void addBankHeader(com.itextpdf.text.Document document) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Paragraph header = new com.itextpdf.text.Paragraph("ONLINE BANKING SYSTEM", 
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD));
        header.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(header);
        
        com.itextpdf.text.Paragraph address = new com.itextpdf.text.Paragraph("123 Banking Street, Financial District, City", 
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10));
        address.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(address);
        document.add(new com.itextpdf.text.Paragraph(" "));
    }
    
    private com.itextpdf.text.pdf.PdfPCell createHeaderCell(String text) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
            new com.itextpdf.text.Phrase(text, 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD))
        );
        cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
    
    private void openPDF(String filePath) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(filePath));
            } else {
                JOptionPane.showMessageDialog(null, 
                    "PDF generated successfully at: " + filePath + "\nPlease open it manually.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "PDF generated successfully at: " + filePath + "\nError opening file: " + e.getMessage());
        }
    }
}
/* [END AGENT GENERATED CODE] */

/* 
 * Requirements implemented:
 * REQ001: PDF Statement Downloads
 * Agent Run Identifier: CLAUDE-3-SONNET-20250219
 */