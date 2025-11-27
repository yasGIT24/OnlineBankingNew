package banking.management.system;

import java.sql.*;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created DatabaseSecurityManager.java to implement:
 * 1. SQL injection prevention
 * 2. Secure database connection handling
 * 3. Query validation and sanitization
 */
public class DatabaseSecurityManager {
    // Blacklisted SQL patterns that could indicate injection attempts
    private static final List<Pattern> SQL_INJECTION_PATTERNS = Arrays.asList(
        Pattern.compile("(?i).*\\s+OR\\s+.*[='].*"),
        Pattern.compile("(?i).*\\s+AND\\s+.*[='].*"),
        Pattern.compile("(?i).*;.*"),
        Pattern.compile("(?i).*--.*"),
        Pattern.compile("(?i).*UNION\\s+SELECT.*"),
        Pattern.compile("(?i).*DROP\\s+TABLE.*"),
        Pattern.compile("(?i).*DELETE\\s+FROM.*"),
        Pattern.compile("(?i).*INSERT\\s+INTO.*"),
        Pattern.compile("(?i).*UPDATE\\s+.*SET.*"),
        Pattern.compile("(?i).*EXEC\\s+.*"),
        Pattern.compile("(?i).*EXECUTE\\s+.*"),
        Pattern.compile("(?i).*TRUNCATE\\s+TABLE.*"),
        Pattern.compile("(?i).*ALTER\\s+TABLE.*"),
        Pattern.compile("(?i).*SELECT\\s+.*FROM.*")
    );
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Create secure database connection with timeout
     */
    public static Connection getSecureConnection(String url, String user, String password) 
            throws SQLException {
        try {
            Properties props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            
            // Security properties
            props.setProperty("connectTimeout", "10");  // 10 seconds connection timeout
            props.setProperty("socketTimeout", "30");   // 30 seconds socket timeout
            props.setProperty("useSSL", "true");        // Use SSL for connection
            props.setProperty("requireSSL", "true");    // Require SSL
            props.setProperty("verifyServerCertificate", "true"); // Verify server certificate
            
            Connection conn = DriverManager.getConnection(url, props);
            
            // Additional security settings
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            
            return conn;
        } catch (SQLException e) {
            ErrorHandler.handleException(e, "Failed to create secure database connection");
            throw e;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Validate SQL query for potential injection attempts
     */
    public static String validateSqlQuery(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SecurityException("Empty SQL statement");
        }
        
        // Check against injection patterns
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(sql).matches()) {
                // Log potential SQL injection attempt
                try {
                    AuditLogger.logActivity("SYSTEM", 
                        "Potential SQL injection detected: " + sql.substring(0, Math.min(50, sql.length())), 
                        "Security");
                } catch (Exception ignored) {
                    // AuditLogger might not be available
                }
                
                throw new SecurityException("Potential SQL injection detected");
            }
        }
        
        return sql;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Create a secure prepared statement with timeout
     */
    public static PreparedStatement prepareSecureStatement(Connection conn, String sql) 
            throws SQLException {
        // Validate SQL first
        String validatedSql = validateSqlQuery(sql);
        
        PreparedStatement stmt = conn.prepareStatement(validatedSql);
        stmt.setQueryTimeout(30); // 30 seconds timeout
        
        return stmt;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Safely close database resources
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            ErrorHandler.handleException(e, "Error closing ResultSet");
        }
        
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            ErrorHandler.handleException(e, "Error closing Statement");
        }
        
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            ErrorHandler.handleException(e, "Error closing Connection");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Sanitize string parameters for safe use in SQL
     */
    public static String sanitizeSqlParam(String param) {
        if (param == null) {
            return "";
        }
        
        return param.replace("'", "''")
                   .replace("\\", "\\\\")
                   .replace("%", "\\%")
                   .replace("_", "\\_");
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Perform secure query execution with logging
     */
    public static ResultSet executeSecureQuery(Connection conn, String sql) throws SQLException {
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            String validatedSql = validateSqlQuery(sql);
            stmt = conn.createStatement();
            stmt.setQueryTimeout(30);
            rs = stmt.executeQuery(validatedSql);
            
            // Log query execution time if it takes too long (potential performance issue)
            long executionTime = System.currentTimeMillis() - startTime;
            if (executionTime > 1000) { // More than 1 second
                AuditLogger.logActivity("SYSTEM", 
                    "Slow query execution: " + executionTime + "ms for query: " + 
                    sql.substring(0, Math.min(50, sql.length())), 
                    "Performance");
            }
            
            return rs;
        } catch (SQLException e) {
            closeResources(null, stmt, rs);
            ErrorHandler.handleException(e, "Error executing secure query");
            throw e;
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Check for sensitive data before logging
     */
    public static String maskSensitiveData(String data) {
        if (data == null) {
            return null;
        }
        
        // Mask potential account numbers
        data = data.replaceAll("\\b(\\d{6})(\\d{4,10})\\b", "$1****");
        
        // Mask potential passwords in logs
        data = data.replaceAll("(?i)password\\s*=\\s*['\"](.*?)['\"]", "password='****'");
        data = data.replaceAll("(?i)passwd\\s*=\\s*['\"](.*?)['\"]", "passwd='****'");
        data = data.replaceAll("(?i)pin\\s*=\\s*['\"](.*?)['\"]", "pin='****'");
        
        return data;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get database server version (for compatibility checks)
     */
    public static String getDatabaseServerInfo(Connection conn) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            return metaData.getDatabaseProductName() + " " + 
                   metaData.getDatabaseProductVersion();
        } catch (SQLException e) {
            ErrorHandler.handleException(e, "Error getting database server info");
            return "Unknown";
        }
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-DB-003, TC-DB-004, TC-SEC-010
 * Requirement IDs: US-1 (Database security)
 * Agent Run: AGENT-20251127-01
 */