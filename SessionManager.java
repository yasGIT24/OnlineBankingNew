package banking.management.system;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/* [AGENT GENERATED CODE - REQUIREMENT:US-1]
 * Created SessionManager.java to implement:
 * 1. Session timeout after 15 minutes of inactivity (US-1 requirement)
 * 2. Session tracking and management
 * 3. Session security controls
 */
public class SessionManager {
    private Timer sessionTimer;
    private int sessionTimeout; // in seconds
    private long sessionStartTime;
    private long lastActivityTime;
    private String sessionId;
    private boolean sessionActive;
    private SessionTimeoutListener timeoutListener;
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Interface for timeout notification
     */
    public interface SessionTimeoutListener {
        void onSessionTimeout();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Constructor with default 15-minute timeout (as per US-1 requirement)
     */
    public SessionManager() {
        this(900); // 15 minutes in seconds
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Constructor with custom timeout
     */
    public SessionManager(int timeoutInSeconds) {
        this.sessionTimeout = timeoutInSeconds;
        this.sessionTimer = new Timer(true); // Run as daemon
        this.sessionId = generateSessionId();
        this.sessionActive = false;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Generate a secure session ID
     */
    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Start a new session
     */
    public void startSession() {
        if (sessionActive) {
            endSession(); // End any existing session first
        }
        
        sessionStartTime = System.currentTimeMillis();
        lastActivityTime = sessionStartTime;
        sessionActive = true;
        
        // Schedule session timeout
        scheduleSessionTimeout();
        
        // Log session start
        try {
            AuditLogger.logActivity(sessionId, "Session started", "Security");
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Failed to log session start");
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * End the current session
     */
    public void endSession() {
        if (sessionActive) {
            sessionActive = false;
            
            // Cancel any pending timeout
            if (sessionTimer != null) {
                sessionTimer.cancel();
                sessionTimer = new Timer(true); // Create new timer for future use
            }
            
            // Log session end
            try {
                long sessionDuration = System.currentTimeMillis() - sessionStartTime;
                AuditLogger.logActivity(sessionId, 
                    "Session ended (duration: " + (sessionDuration / 1000) + " seconds)", "Security");
            } catch (Exception e) {
                ErrorHandler.handleException(e, "Failed to log session end");
            }
            
            // Generate new session ID for next session
            sessionId = generateSessionId();
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Reset the session timer due to user activity
     */
    public void resetTimer() {
        if (sessionActive) {
            lastActivityTime = System.currentTimeMillis();
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Schedule the session timeout task
     */
    private void scheduleSessionTimeout() {
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (sessionActive) {
                    long currentTime = System.currentTimeMillis();
                    long inactiveTime = (currentTime - lastActivityTime) / 1000; // in seconds
                    
                    if (inactiveTime >= sessionTimeout) {
                        // Session has timed out
                        handleSessionTimeout();
                    }
                }
            }
        }, 1000, 1000); // Check every second
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Handle session timeout
     */
    private void handleSessionTimeout() {
        sessionActive = false;
        
        // Log session timeout
        try {
            AuditLogger.logActivity(sessionId, "Session timed out due to inactivity", "Security");
        } catch (Exception e) {
            ErrorHandler.handleException(e, "Failed to log session timeout");
        }
        
        // Notify listener if registered
        if (timeoutListener != null) {
            timeoutListener.onSessionTimeout();
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Set a listener for session timeout events
     */
    public void setTimeoutListener(SessionTimeoutListener listener) {
        this.timeoutListener = listener;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Check if the session is active
     */
    public boolean isSessionActive() {
        return sessionActive;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get the session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get the session start time
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get the session duration in seconds
     */
    public long getSessionDuration() {
        if (!sessionActive) {
            return 0;
        }
        return (System.currentTimeMillis() - sessionStartTime) / 1000;
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US-1]
     * Get remaining time before timeout in seconds
     */
    public int getRemainingTime() {
        if (!sessionActive) {
            return 0;
        }
        
        long elapsedInactive = (System.currentTimeMillis() - lastActivityTime) / 1000;
        int remaining = sessionTimeout - (int)elapsedInactive;
        
        return Math.max(0, remaining);
    }
}

/* [AGENT GENERATED CODE]
 * Test Case IDs: TC-SM-001, TC-SM-002, TC-SEC-005
 * Requirement IDs: US-1 (Session management)
 * Agent Run: AGENT-20251127-01
 */