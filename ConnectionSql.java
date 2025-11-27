package banking.management.system;

import java.sql.*;

/* [AGENT GENERATED CODE - REQUIREMENT:US1-AC1,US1-AC2,US1-AC3,US1-AC4] 
 * Created ConnectionSql class for database connectivity
 * References to this class were found in existing code but file was missing
 * Includes support for transaction status (pending/completed) and multiple linked accounts
 */
public class ConnectionSql {
    Connection c;
    Statement s;
    
    public ConnectionSql() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection to database
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankmanagement", "root", "");
            
            // Create statement for executing SQL queries
            s = c.createStatement();
            
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
     * Method to get all accounts linked to a customer
     */
    public ResultSet getLinkedAccounts(String customerID) {
        try {
            // Query to get all accounts linked to the customer ID
            String query = "SELECT * FROM linked_accounts WHERE customer_id = '" + customerID + "'";
            return s.executeQuery(query);
        } catch (Exception e) {
            System.out.println("Error fetching linked accounts: " + e);
            return null;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC2] 
     * Method to get transaction status
     */
    public String getTransactionStatus(String transactionID) {
        try {
            String query = "SELECT status FROM bank WHERE transaction_id = '" + transactionID + "'";
            ResultSet rs = s.executeQuery(query);
            
            if (rs.next()) {
                return rs.getString("status");
            } else {
                return "Transaction not found";
            }
        } catch (Exception e) {
            System.out.println("Error fetching transaction status: " + e);
            return "Error";
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC3] 
     * Method to link a new account to a customer
     */
    public boolean linkAccount(String customerID, String accountNumber, String accountType) {
        try {
            String query = "INSERT INTO linked_accounts (customer_id, account_number, account_type) VALUES ('" 
                        + customerID + "', '" + accountNumber + "', '" + accountType + "')";
            
            int result = s.executeUpdate(query);
            return result > 0;
        } catch (Exception e) {
            System.out.println("Error linking account: " + e);
            return false;
        }
    }
}

/* 
 * Test Cases: TC-US1-01, TC-US1-02, TC-US1-03, TC-US1-04
 * Agent Run ID: AR-2025-11-27-001
 */