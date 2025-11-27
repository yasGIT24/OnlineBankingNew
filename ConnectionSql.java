package banking.management.system;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created ConnectionSql.java to implement:
 * 1. Secure database connection management
 * 2. Connection pooling for performance
 * 3. Proper exception handling
 * 4. Integration with DatabaseSecurityManager
 */
public class ConnectionSql {
    private static final String CONFIG_FILE = "config.properties";
    private static final int CONNECTION_TIMEOUT = 30; // seconds
    
    private Connection c;
    Statement s;
    private static DataSource dataSource;
    private static boolean usePooling = false;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Connection cache for performance
     */
    private static final ConcurrentHashMap<Thread, Connection> connectionCache = new ConcurrentHashMap<>();
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Default database credentials - in production these would be loaded from secure storage
     */
    private static String DB_URL = "jdbc:mysql://localhost:3306/bankmanagement";
    private static String DB_USER = "bank_app";
    private static String DB_PASSWORD = ""; // In production, this would be loaded securely
    
    static {
        /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
         * Load database configuration from properties file
         */
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            props.load(fis);
            
            DB_URL = props.getProperty("db.url", DB_URL);
            DB_USER = props.getProperty("db.user", DB_USER);
            DB_PASSWORD = props.getProperty("db.password", DB_PASSWORD);
            usePooling = Boolean.parseBoolean(props.getProperty("db.usePooling", "false"));
            
            if (usePooling) {
                // Initialize connection pool
                try {
                    InitialContext ctx = new InitialContext();
                    dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/bankDS");
                } catch (NamingException e) {
                    ErrorHandler.handleException(e, "Failed to initialize connection pool");
                    // Fall back to direct connections
                    usePooling = false;
                }
            }
            
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            
        } catch (IOException | ClassNotFoundException e) {
            ErrorHandler.handleException(e, "Error initializing database configuration");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Constructor with secure connection initialization
     */
    public ConnectionSql() {
        try {
            // Check if we already have a connection for this thread
            Thread currentThread = Thread.currentThread();
            c = connectionCache.get(currentThread);
            
            // If no connection exists or it's closed, create a new one
            if (c == null || c.isClosed()) {
                if (usePooling && dataSource != null) {
                    c = dataSource.getConnection();
                } else {
                    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
                     * Use DatabaseSecurityManager for secure connection
                     */
                    c = DatabaseSecurityManager.getSecureConnection(DB_URL, DB_USER, DB_PASSWORD);
                }
                
                // Configure connection properties
                c.setAutoCommit(true);
                c.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                
                // Cache the connection
                connectionCache.put(currentThread, c);
            }
            
            // Create statement with secure settings
            s = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
        } catch (SQLException e) {
            ErrorHandler.handleException(e, "Database connection error");
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get connection for use with prepared statements
     */
    public Connection getConnection() {
        return c;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Create a prepared statement with security checks
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        // Use DatabaseSecurityManager to check for potential SQL injection
        String sanitizedSql = DatabaseSecurityManager.validateSqlQuery(sql);
        return c.prepareStatement(sanitizedSql);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Execute query with security checks
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        // Use DatabaseSecurityManager to check for potential SQL injection
        String sanitizedSql = DatabaseSecurityManager.validateSqlQuery(sql);
        return s.executeQuery(sanitizedSql);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Execute update with security checks
     */
    public int executeUpdate(String sql) throws SQLException {
        // Use DatabaseSecurityManager to check for potential SQL injection
        String sanitizedSql = DatabaseSecurityManager.validateSqlQuery(sql);
        return s.executeUpdate(sanitizedSql);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Method to explicitly close the connection
     */
    public void close() {
        try {
            if (s != null) s.close();
            if (c != null) {
                c.close();
                connectionCache.remove(Thread.currentThread());
            }
        } catch (SQLException e) {
            ErrorHandler.handleException(e, "Error closing database connection");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Static method to close all pooled connections (for application shutdown)
     */
    public static void closeAllConnections() {
        for (Connection conn : connectionCache.values()) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                ErrorHandler.handleException(e, "Error closing pooled connection");
            }
        }
        connectionCache.clear();
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-DB-001, TC-DB-002, TC-SEC-004
 * Requirement IDs: US-1 (Secure database access)
 * Agent Run: AGENT-20251127-01
 */