package banking.management.system;

import java.sql.*;

/**
 * @author Adarsh Kunal
 */

/*
 * [AGENT GENERATED CODE - REQUIREMENT:DATABASE_CONNECTION]
 * This class provides a database connection used throughout the application.
 * It establishes a connection to a MySQL database using JDBC.
 */
public class ConnectionSql {

    Connection c;
    Statement s;

    public ConnectionSql() {
        try {
            // Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection to the database
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankmanagementsystem", "root", "");
            
            // Create statement
            s = c.createStatement();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

/* 
 * Agent Run Identifier: BANK-DB-20251126
 * Related Test Cases: DATABASE-CONN-001
 */