package banking.management.system;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author Adarsh Kunal
 */

class BalanceEnquiry extends JFrame implements ActionListener {
    JButton back;
    /* [AGENT GENERATED CODE - REQUIREMENT:REQ001]
     * Adding PDF export button for PDF statement downloads
     */
    JButton exportPDF;
    /* [END AGENT GENERATED CODE] */
    JLabel l1, l3;
    
    String pin;
    String Accountno;
    BalanceEnquiry(String pin, String Accountno) {
       setSize(1600, 1200);
      // setUndecorated(true);
       setVisible(true);
       setLayout(null);
       getContentPane().setBackground(new Color(204, 229, 255));
       
        this.pin = pin;
        this.Accountno = Accountno;

        
        
     JLabel text = new JLabel("WELCOME TO THE BANK ");
     text.setFont(new Font("Osward", Font.BOLD,32));
     text.setBounds(200, 40, 450, 40);
     text.setForeground(Color.black);
     add(text);
            
     ImageIcon m1 = new ImageIcon(ClassLoader.getSystemResource("icons/logo.jpg"));
     Image m2 = m1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
     ImageIcon m3 = new ImageIcon(m2);  
     JLabel label0 = new JLabel(m3);
     label0.setBounds(70, 10, 100, 100);
     add(label0);
        
        ImageIcon k1 = new ImageIcon(ClassLoader.getSystemResource("icons/withdraw2.jpg"));
        Image k2 = k1.getImage().getScaledInstance(800, 800, Image.SCALE_DEFAULT);
        ImageIcon k3 = new ImageIcon(k2);
        JLabel label8 = new JLabel(k3);
        label8.setBounds(800, 0, 800, 800);
        add(label8);
  
        
        back = new JButton("BACK");
        back.setBounds(200, 633, 150, 35);
        back.addActionListener(this);
        back.setBackground(Color.black);
        back.setForeground(Color.WHITE);
        add(back);
        
        /* [AGENT GENERATED CODE - REQUIREMENT:REQ001]
         * Adding PDF export button to the UI to allow downloading statement as PDF
         */
        exportPDF = new JButton("EXPORT AS PDF");
        exportPDF.setBounds(400, 633, 150, 35);
        exportPDF.addActionListener(this);
        exportPDF.setBackground(Color.black);
        exportPDF.setForeground(Color.WHITE);
        add(exportPDF);
        /* [END AGENT GENERATED CODE] */
        
         ConnectionSql c = new ConnectionSql();
         int balance1 = 0;
        try{
           
            ResultSet rs = c.s.executeQuery("select * from bank where Login_Password = '" + pin + "' and Account_No = '" + Accountno + "'");
            
            while (rs.next()) {
                if (rs.getString("type").equals("Deposit")) {
                    balance1 += Integer.parseInt(rs.getString("amount"));
                } else {
                    balance1 -= Integer.parseInt(rs.getString("amount"));
                }
            }
        }catch(Exception e){
       System.out.println(e);
        }
            JLabel bl =new JLabel("Your Current Account Balance is Rs "+balance1);
            bl.setForeground(Color.red);
            bl.setBounds(150, 300, 800, 30);
            bl.setFont(new Font("Raleway", Font.BOLD, 25));
            add(bl);
    }
    

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new Transactions(pin, Accountno).setVisible(true);
        }
        /* [AGENT GENERATED CODE - REQUIREMENT:REQ001]
         * Handling action event for PDF export button
         * This will call the PDF generation utility
         */
        else if (ae.getSource() == exportPDF) {
            try {
                // Get current balance and account information for the PDF
                ConnectionSql c = new ConnectionSql();
                ResultSet accountInfo = c.s.executeQuery("select * from bank where Login_Password = '" + pin + "' and Account_No = '" + Accountno + "'");
                
                // Call the PDF generator service to create the statement PDF
                PDFGenerator pdfGen = new PDFGenerator();
                boolean success = pdfGen.generateBalanceStatement(Accountno, accountInfo);
                
                if (success) {
                    JOptionPane.showMessageDialog(null, "Statement PDF generated successfully!");
                    
                    // Log this activity for audit purposes
                    c.s.executeUpdate("INSERT INTO audit_log VALUES ('" + Accountno + "', 'Statement Downloaded', CURRENT_TIMESTAMP)");
                } else {
                    JOptionPane.showMessageDialog(null, "Error generating PDF statement. Please try again.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        /* [END AGENT GENERATED CODE] */
    }

    public static void main(String[] args) {
        new BalanceEnquiry("","");
    }
}

/* 
 * Requirements implemented:
 * REQ001: PDF Statement Downloads
 * Agent Run Identifier: CLAUDE-3-SONNET-20250219
 */