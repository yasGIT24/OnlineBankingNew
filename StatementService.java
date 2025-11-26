package banking.management.system;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Adarsh Kunal
 */

/* 
 * [AGENT GENERATED CODE - REQUIREMENT:US1]
 * This service generates PDF account statements for customers.
 * It retrieves transaction data from the database and formats it into a standardized PDF.
 */
public class StatementService {
    
    private final String accountNo;
    private final String pin;
    private final EncryptionService encryptionService;
    private final AuditLogger auditLogger;
    
    public StatementService(String accountNo, String pin) {
        this.accountNo = accountNo;
        this.pin = pin;
        this.encryptionService = new EncryptionService();
        this.auditLogger = new AuditLogger();
    }
    
    /**
     * Generates a PDF statement for the specified date range
     * @param fromDate Start date in format yyyy-MM-dd
     * @param toDate End date in format yyyy-MM-dd
     * @return File path of the generated PDF
     */
    public String generatePDFStatement(String fromDate, String toDate) {
        String filePath = "";
        try {
            // Create directory if it doesn't exist
            String directoryPath = System.getProperty("user.home") + File.separator + "BankStatements";
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Create filename with timestamp
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = formatter.format(new Date());
            filePath = directoryPath + File.separator + "Statement_" + accountNo + "_" + timestamp + ".pdf";
            
            // Create PDF document
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add header with bank logo and information
            addHeader(document);
            
            // Add customer details
            addCustomerDetails(document);
            
            // Add statement details (date range, etc.)
            addStatementDetails(document, fromDate, toDate);
            
            // Add transactions table
            addTransactionsTable(document, fromDate, toDate);
            
            // Add summary and footer
            addSummary(document, fromDate, toDate);
            addFooter(document);
            
            document.close();
            
            // Log this activity
            auditLogger.logActivity(accountNo, "PDF_STATEMENT_GENERATED", "Statement generated for period: " + fromDate + " to " + toDate);
            
            // Return the encrypted file path that can be used to download
            return encryptionService.generateEncryptedLink(filePath);
            
        } catch (Exception e) {
            System.out.println("Error generating PDF statement: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void addHeader(Document document) throws DocumentException {
        Paragraph header = new Paragraph("BANK STATEMENT", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(Chunk.NEWLINE);
    }
    
    private void addCustomerDetails(Document document) throws DocumentException {
        try {
            ConnectionSql c = new ConnectionSql();
            ResultSet rs = c.s.executeQuery("SELECT * FROM signup3 WHERE Account_No = '" + accountNo + "' AND Login_Password = '" + pin + "'");
            
            if (rs.next()) {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                
                table.addCell(createCell("Account Number:", true));
                table.addCell(createCell(accountNo, false));
                
                table.addCell(createCell("Account Holder:", true));
                table.addCell(createCell(rs.getString("name"), false));
                
                document.add(table);
                document.add(Chunk.NEWLINE);
            }
        } catch (Exception e) {
            System.out.println("Error fetching customer details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addStatementDetails(Document document, String fromDate, String toDate) throws DocumentException {
        Paragraph details = new Paragraph("Statement Period: " + fromDate + " to " + toDate, 
                new Font(Font.FontFamily.HELVETICA, 12));
        details.setAlignment(Element.ALIGN_LEFT);
        document.add(details);
        document.add(Chunk.NEWLINE);
    }
    
    private void addTransactionsTable(Document document, String fromDate, String toDate) throws DocumentException {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "SELECT * FROM bank WHERE Account_No = '" + accountNo + "' AND date BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY date ASC";
            ResultSet rs = c.s.executeQuery(query);
            
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            
            // Add table headers
            table.addCell(createHeaderCell("Date"));
            table.addCell(createHeaderCell("Description"));
            table.addCell(createHeaderCell("Type"));
            table.addCell(createHeaderCell("Amount"));
            table.addCell(createHeaderCell("Balance"));
            
            // Running balance calculation
            int balance = 0;
            List<Transaction> transactions = new ArrayList<>();
            
            // First pass: collect all transactions and calculate balance
            while (rs.next()) {
                String date = rs.getString("date");
                String type = rs.getString("type");
                String description = rs.getString("description");
                int amount = Integer.parseInt(rs.getString("amount"));
                
                if (type.equals("Deposit")) {
                    balance += amount;
                } else {
                    balance -= amount;
                }
                
                transactions.add(new Transaction(date, description, type, amount, balance));
            }
            
            // Second pass: display transactions with running balance
            for (Transaction tx : transactions) {
                table.addCell(createCell(tx.date, false));
                table.addCell(createCell(tx.description, false));
                table.addCell(createCell(tx.type, false));
                
                if (tx.type.equals("Deposit")) {
                    table.addCell(createCell("+" + tx.amount, false));
                } else {
                    table.addCell(createCell("-" + tx.amount, false));
                }
                
                table.addCell(createCell(String.valueOf(tx.runningBalance), false));
            }
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
        } catch (Exception e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addSummary(Document document, String fromDate, String toDate) throws DocumentException {
        try {
            ConnectionSql c = new ConnectionSql();
            
            // Get total deposits
            String depositQuery = "SELECT SUM(amount) as total FROM bank WHERE Account_No = '" + accountNo + "' AND type = 'Deposit' AND date BETWEEN '" + fromDate + "' AND '" + toDate + "'";
            ResultSet depositRs = c.s.executeQuery(depositQuery);
            int totalDeposits = depositRs.next() ? depositRs.getInt("total") : 0;
            
            // Get total withdrawals
            String withdrawalQuery = "SELECT SUM(amount) as total FROM bank WHERE Account_No = '" + accountNo + "' AND type = 'Withdrawal' AND date BETWEEN '" + fromDate + "' AND '" + toDate + "'";
            ResultSet withdrawalRs = c.s.executeQuery(withdrawalQuery);
            int totalWithdrawals = withdrawalRs.next() ? withdrawalRs.getInt("total") : 0;
            
            // Calculate current balance
            String balanceQuery = "SELECT * FROM bank WHERE Account_No = '" + accountNo + "'";
            ResultSet balanceRs = c.s.executeQuery(balanceQuery);
            
            int balance = 0;
            while (balanceRs.next()) {
                if (balanceRs.getString("type").equals("Deposit")) {
                    balance += Integer.parseInt(balanceRs.getString("amount"));
                } else {
                    balance -= Integer.parseInt(balanceRs.getString("amount"));
                }
            }
            
            // Create summary table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(50);
            table.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            table.addCell(createCell("Total Deposits:", true));
            table.addCell(createCell(String.valueOf(totalDeposits), false));
            
            table.addCell(createCell("Total Withdrawals:", true));
            table.addCell(createCell(String.valueOf(totalWithdrawals), false));
            
            table.addCell(createCell("Current Balance:", true));
            table.addCell(createCell(String.valueOf(balance), false));
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
        } catch (Exception e) {
            System.out.println("Error creating summary: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("This is a computer generated statement and does not require signature.", 
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
    
    private PdfPCell createHeaderCell(String text) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
    
    private PdfPCell createCell(String text, boolean isBold) {
        Font font = isBold ? 
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD) : 
                new Font(Font.FontFamily.HELVETICA, 12);
        
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        return cell;
    }
    
    // Helper class to store transaction details with running balance
    private static class Transaction {
        String date;
        String description;
        String type;
        int amount;
        int runningBalance;
        
        Transaction(String date, String description, String type, int amount, int runningBalance) {
            this.date = date;
            this.description = description;
            this.type = type;
            this.amount = amount;
            this.runningBalance = runningBalance;
        }
    }
}

/* 
 * Agent Run Identifier: BANK-PDF-20251126
 * Related Test Cases: PDF-STATEMENT-001, PDF-STATEMENT-002
 */