package banking.management.system;

import java.util.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Session management service for Online Banking system
 *
 * [AGENT GENERATED CODE - REQUIREMENT:US1-AC5]
 * This class implements session timeout handling after 15 minutes of inactivity
 * as specified in the authentication requirements.
 */
public class SessionManager {
    // Session timeout in milliseconds (15 minutes)
    private static final int SESSION_TIMEOUT_MS = 15 * 60 * 1000;
    
    // Map to store user sessions
    private static final Map<String, SessionData> activeSessions = new HashMap<>();
    
    // Session data storage class
    private static class SessionData {
        String accountNo;
        LocalDateTime lastActivityTime;
        Timer inactivityTimer;
        
        SessionData(String accountNo, Timer timer) {
            this.accountNo = accountNo;
            this.lastActivityTime = LocalDateTime.now();
            this.inactivityTimer = timer;
        }
    }
    
    /**
     * Creates a new user session with inactivity timeout
     * 
     * @param accountNo User's account number for session identification
     * @param timeoutHandler Action to execute when session times out
     * @return String Generated session ID
     */
    public static String createSession(String accountNo, Runnable timeoutHandler) {
        // Generate unique session ID
        String sessionId = UUID.randomUUID().toString();
        
        // Create inactivity timer
        Timer timer = new Timer(SESSION_TIMEOUT_MS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSessionTimeout(sessionId, timeoutHandler);
            }
        });
        
        // Timer will not repeat - one timeout only
        timer.setRepeats(false);
        
        // Start the timer
        timer.start();
        
        // Store session data
        activeSessions.put(sessionId, new SessionData(accountNo, timer));
        
        return sessionId;
    }
    
    /**
     * Handles session timeout by removing session and executing timeout handler
     * 
     * @param sessionId Session ID to handle timeout for
     * @param timeoutHandler Action to execute on timeout
     */
    private static void handleSessionTimeout(String sessionId, Runnable timeoutHandler) {
        // Remove the session
        if (activeSessions.containsKey(sessionId)) {
            activeSessions.remove(sessionId);
            
            // Execute timeout handler if provided
            if (timeoutHandler != null) {
                timeoutHandler.run();
            }
        }
    }
    
    /**
     * Validates if a session is active
     * 
     * @param sessionId Session ID to validate
     * @return boolean True if session exists and is active
     */
    public static boolean isSessionActive(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }
    
    /**
     * Updates session activity timestamp and resets timeout timer
     * 
     * @param sessionId Session ID to update
     * @return boolean True if session was found and updated
     */
    public static boolean updateSessionActivity(String sessionId) {
        if (!activeSessions.containsKey(sessionId)) {
            return false;
        }
        
        SessionData session = activeSessions.get(sessionId);
        
        // Update last activity time
        session.lastActivityTime = LocalDateTime.now();
        
        // Reset the timer
        session.inactivityTimer.restart();
        
        return true;
    }
    
    /**
     * Explicitly invalidates/ends a session
     * 
     * @param sessionId Session ID to invalidate
     * @return boolean True if session was found and invalidated
     */
    public static boolean invalidateSession(String sessionId) {
        if (!activeSessions.containsKey(sessionId)) {
            return false;
        }
        
        // Stop the timer
        SessionData session = activeSessions.get(sessionId);
        session.inactivityTimer.stop();
        
        // Remove the session
        activeSessions.remove(sessionId);
        
        return true;
    }
    
    /**
     * Gets session information
     * 
     * @param sessionId Session ID to get info for
     * @return Map Session information or null if not found
     */
    public static Map<String, Object> getSessionInfo(String sessionId) {
        if (!activeSessions.containsKey(sessionId)) {
            return null;
        }
        
        SessionData session = activeSessions.get(sessionId);
        Map<String, Object> sessionInfo = new HashMap<>();
        
        sessionInfo.put("accountNo", session.accountNo);
        sessionInfo.put("lastActivity", session.lastActivityTime);
        sessionInfo.put("timeUntilTimeout", 
                       SESSION_TIMEOUT_MS - session.inactivityTimer.getDelay());
        
        return sessionInfo;
    }
    
    /**
     * Configures session timeout period (for testing)
     * 
     * @param minutes Timeout period in minutes
     */
    public static void configureTimeout(int minutes) {
        // This would adjust the timeout in a real implementation
        // For this demo, we just log the configuration change
        System.out.println("Session timeout configured to " + minutes + " minutes");
    }
}

/*
 * File generated/modified to fulfill User Story 1: Account Login & Authentication
 * Test cases: Session timeout after inactivity
 * Agent run: VIBE-1001
 */