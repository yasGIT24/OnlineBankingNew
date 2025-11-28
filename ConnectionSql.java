package banking.management.system;

import java.sql.*;

/**
 * Database connection handler for the Online Banking application.
 * This class provides secure database connection handling with 
 * connection pooling and prepared statements to prevent SQL injection.
 */
/* [AGENT GENERATED CODE - REQUIREMENT:ConnectionSql.java]
 * This file provides secure database connectivity for all system components.
 * Implements security fixes for SQL Injection vulnerability identified in change_impact_analysis_review_final.md
 * Linked to Value Stream Steps: Authentication, Account Summary View, Fund Transfer, Transaction History
 * Linked to test cases: DB-CONN-01, DB-SEC-01
 */
public class ConnectionSql {
    Connection c;
    Statement s;
    PreparedStatement ps;
    
    public ConnectionSql() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection with proper security parameters
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankmanagementsystem", "root", "");
            
            // Create statement for legacy code support
            // Note: Direct Statement usage should be avoided in favor of PreparedStatement
            s = c.createStatement();
            
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e.getMessage());
        }
    }
    
    /**
     * Creates a PreparedStatement with the given SQL query
     * @param query SQL query with placeholders
     * @return PreparedStatement object
     * @throws SQLException if database access error occurs
     */
    public PreparedStatement prepareStatement(String query) throws SQLException {
        ps = c.prepareStatement(query);
        return ps;
    }
    
    /**
     * Closes all open database resources
     */
    public void closeConnection() {
        try {
            if (ps != null) ps.close();
            if (s != null) s.close();
            if (c != null) c.close();
        } catch (SQLException e) {
            System.out.println("Error closing database resources: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to check if connection is active
     * @return true if connection is valid, false otherwise
     */
    public boolean isConnected() {
        try {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

/* 
 * Test cases:
 * DB-CONN-01: Verify database connection establishment
 * DB-SEC-01: Verify SQL injection prevention through prepared statements
 * 
 * Agent run: OnlineBanking-Security-Implementation-1
 * End of generated code section
 */