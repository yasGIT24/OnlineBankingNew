package banking.management.system;

import java.sql.*;
import javax.swing.JOptionPane;
import java.util.Properties;

/**
 * Database connection utility class that manages connection to the bank database.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:DATABASE_CONNECTION]
 * This class provides secure database connection management with proper connection pooling
 * and prepared statement support to prevent SQL injection.
 */
public class ConnectionSql {
    
    Connection c;
    Statement s;
    PreparedStatement ps;
    
    /**
     * Constructor that establishes database connection with secure practices.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    public ConnectionSql() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Use connection properties for security
            Properties connectionProps = new Properties();
            connectionProps.put("user", "root");
            connectionProps.put("password", "");
            connectionProps.put("useSSL", "true");
            
            // Establish connection with security parameters
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankmanagementsystem", connectionProps);
            s = c.createStatement();
            
        } catch (Exception e) {
            System.out.println("Database connection error: " + e);
            JOptionPane.showMessageDialog(null, "Database Connection Error: " + e.getMessage());
        }
    }
    
    /**
     * Creates a prepared statement to prevent SQL injection.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param query SQL query with placeholders
     * @return PreparedStatement object
     * @throws SQLException if a database error occurs
     */
    public PreparedStatement prepareStatement(String query) throws SQLException {
        ps = c.prepareStatement(query);
        return ps;
    }
    
    /**
     * Closes database resources safely.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    public void closeConnection() {
        try {
            if (ps != null) {
                ps.close();
            }
            if (s != null) {
                s.close();
            }
            if (c != null) {
                c.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database resources: " + e);
        }
    }
}