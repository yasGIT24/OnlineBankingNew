package banking.management.system;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Authentication filter for secure access control.
 * 
 * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
 * This class manages session authentication, timeout, and access control
 * for the banking application.
 */
public class AuthenticationFilter {
    
    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());
    private static final Map<String, SessionInfo> activeSessions = new HashMap<>();
    private static final long SESSION_TIMEOUT = 300000; // 5 minutes in milliseconds
    private final SecurityService securityService;
    
    /**
     * Constructor initializes security service.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    public AuthenticationFilter() {
        this.securityService = new SecurityService();
    }
    
    /**
     * Authenticates user and creates a new session.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param accountNo User account number
     * @param password User password
     * @return Session token if successful, null otherwise
     */
    public String authenticate(String accountNo, String password) {
        if (securityService.authenticateUser(accountNo, password)) {
            String sessionToken = generateSessionToken(accountNo);
            
            // Create and store session info
            SessionInfo sessionInfo = new SessionInfo(accountNo, sessionToken, System.currentTimeMillis());
            activeSessions.put(sessionToken, sessionInfo);
            
            // Log successful authentication
            logger.info("User authenticated successfully: " + maskAccountNumber(accountNo));
            
            return sessionToken;
        } else {
            // Log failed authentication attempt
            logger.warning("Failed authentication attempt for account: " + maskAccountNumber(accountNo));
            return null;
        }
    }
    
    /**
     * Validates a session token.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param sessionToken Session token to validate
     * @return true if session is valid, false otherwise
     */
    public boolean validateSession(String sessionToken) {
        SessionInfo session = activeSessions.get(sessionToken);
        
        if (session == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Check if session has timed out
        if (currentTime - session.getLastActivityTime() > SESSION_TIMEOUT) {
            // Session timed out, remove it
            activeSessions.remove(sessionToken);
            logger.info("Session timed out for account: " + maskAccountNumber(session.getAccountNo()));
            return false;
        }
        
        // Update last activity time
        session.updateLastActivityTime();
        return true;
    }
    
    /**
     * Applies security filter to a frame.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param frame JFrame to apply security to
     * @param sessionToken Current session token
     */
    public void applySecurityFilter(JFrame frame, String sessionToken) {
        // Set up session timeout monitoring
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!validateSession(sessionToken)) {
                    // Session expired, close window and show message
                    frame.dispose();
                    JOptionPane.showMessageDialog(null, 
                            "Your session has expired due to inactivity.\nPlease login again.", 
                            "Session Expired", JOptionPane.WARNING_MESSAGE);
                    
                    // Redirect to login
                    new Login().setVisible(true);
                    timer.cancel();
                }
            }
        }, SESSION_TIMEOUT, 60000); // Check every minute
        
        // Add window listener to handle window close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.cancel();
                logoutSession(sessionToken);
            }
        });
    }
    
    /**
     * Logs out a user session.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param sessionToken Session token to invalidate
     * @return true if logged out successfully
     */
    public boolean logoutSession(String sessionToken) {
        SessionInfo session = activeSessions.remove(sessionToken);
        
        if (session != null) {
            logger.info("User logged out: " + maskAccountNumber(session.getAccountNo()));
            return true;
        }
        
        return false;
    }
    
    /**
     * Validates access permissions for a resource.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param sessionToken Session token
     * @param resourceType Resource being accessed
     * @return true if access is allowed
     */
    public boolean validateAccess(String sessionToken, String resourceType) {
        // First validate session is active
        if (!validateSession(sessionToken)) {
            return false;
        }
        
        // For enhanced security, additional role-based checks could be implemented here
        SessionInfo session = activeSessions.get(sessionToken);
        
        // Log access attempt
        logger.info("Resource access: " + resourceType + " for account: " + 
                maskAccountNumber(session.getAccountNo()));
        
        return true; // Basic implementation grants access to all authenticated users
    }
    
    /**
     * Generates a unique session token.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param accountNo Account number
     * @return Unique session token
     */
    private String generateSessionToken(String accountNo) {
        EncryptionUtils encryptionUtils = new EncryptionUtils();
        String tokenBase = accountNo + ":" + System.currentTimeMillis();
        return encryptionUtils.encrypt(tokenBase);
    }
    
    /**
     * Masks account number for logging (shows only last 4 digits).
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     * 
     * @param accountNo Full account number
     * @return Masked account number
     */
    private String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() <= 4) {
            return "****";
        }
        
        int length = accountNo.length();
        return "****" + accountNo.substring(length - 4, length);
    }
    
    /**
     * Inner class to store session information.
     * [AGENT GENERATED CODE - REQUIREMENT:SECURITY_ENHANCEMENT]
     */
    private static class SessionInfo {
        private final String accountNo;
        private final String token;
        private long lastActivityTime;
        
        public SessionInfo(String accountNo, String token, long creationTime) {
            this.accountNo = accountNo;
            this.token = token;
            this.lastActivityTime = creationTime;
        }
        
        public String getAccountNo() {
            return accountNo;
        }
        
        public String getToken() {
            return token;
        }
        
        public long getLastActivityTime() {
            return lastActivityTime;
        }
        
        public void updateLastActivityTime() {
            this.lastActivityTime = System.currentTimeMillis();
        }
    }
}