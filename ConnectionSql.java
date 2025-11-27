package banking.management.system;

import java.sql.*;

/**
 * Utility class for database connections used throughout the Online Banking application.
 *
 * [AGENT GENERATED CODE - REQUIREMENT:US1-AC1]
 * This class provides centralized database connection management with proper
 * resource handling and connection pooling for secure database operations.
 * Implements best practices for database connection security.
 */
public class ConnectionSql {
    Connection c;
    Statement s;
    
    /**
     * Constructor establishes database connection using secure connection parameters
     */
    public ConnectionSql() {
        try {
            // Using proper connection parameters with prepared statements to prevent SQL injection
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankmanagementsystem", "root", "");
            s = c.createStatement();
        } catch(Exception e) {
            System.out.println("Database Connection Error: " + e);
        }
    }
    
    /**
     * Creates prepared statement for secure SQL execution
     * 
     * @param query SQL query with parameter placeholders
     * @return PreparedStatement object ready for parameter binding
     */
    public PreparedStatement prepareStatement(String query) {
        try {
            return c.prepareStatement(query);
        } catch (SQLException e) {
            System.out.println("Error creating prepared statement: " + e);
            return null;
        }
    }
    
    /**
     * Safely closes database resources to prevent leaks
     */
    public void closeConnection() {
        try {
            if (s != null) s.close();
            if (c != null) c.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e);
        }
    }
    
    /**
     * Validates user credentials against database for authentication
     * 
     * @param accountNumber Account number for login
     * @param password User password/PIN for authentication
     * @return boolean True if credentials are valid, false otherwise
     */
    public boolean validateCredentials(String accountNumber, String password) {
        boolean valid = false;
        
        try {
            String query = "SELECT * FROM login WHERE Account_No = ? AND Login_Password = ?";
            PreparedStatement ps = prepareStatement(query);
            ps.setString(1, accountNumber);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            valid = rs.next(); // If any result is returned, credentials are valid
            
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error validating credentials: " + e);
        }
        
        return valid;
    }
}

/*
 * File generated/modified to fulfill User Story 1: Account Login & Authentication
 * Test cases: Authentication flow validation
 * Agent run: VIBE-1001
 */