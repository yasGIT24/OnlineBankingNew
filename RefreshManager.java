package banking.management.system;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
 * Created RefreshManager to implement fourth acceptance criteria:
 * "Data refreshes automatically or on manual refresh"
 */
public class RefreshManager {
    private Object targetComponent;
    private int refreshInterval; // in seconds
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> refreshTask;
    private boolean autoRefreshEnabled;
    
    /**
     * Constructor for RefreshManager
     * @param targetComponent The component that needs to be refreshed (must have a public updateBalance() or loadLinkedAccounts() method)
     * @param defaultRefreshInterval Default refresh interval in seconds
     */
    public RefreshManager(Object targetComponent, int defaultRefreshInterval) {
        this.targetComponent = targetComponent;
        this.refreshInterval = defaultRefreshInterval;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.autoRefreshEnabled = true;
        
        // Try to load user-specific refresh settings from database
        loadRefreshSettings();
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Load user's refresh settings from database if available
     */
    private void loadRefreshSettings() {
        try {
            String accountNumber = null;
            
            // Determine which component we're dealing with to extract account number
            if (targetComponent instanceof BalanceEnquiry) {
                accountNumber = ((BalanceEnquiry) targetComponent).Accountno;
            } else if (targetComponent instanceof Transactions) {
                accountNumber = ((Transactions) targetComponent).Accountno;
            } else if (targetComponent instanceof LinkedAccountsView) {
                // Use reflection to get the account number
                // This is a workaround since we don't have direct field access
                java.lang.reflect.Field field = targetComponent.getClass().getDeclaredField("currentAccountNo");
                field.setAccessible(true);
                accountNumber = (String) field.get(targetComponent);
            }
            
            if (accountNumber != null && !accountNumber.isEmpty()) {
                ConnectionSql c = new ConnectionSql();
                String query = "SELECT auto_refresh, refresh_interval FROM refresh_settings WHERE Account_No = '" + accountNumber + "'";
                ResultSet rs = c.s.executeQuery(query);
                
                if (rs.next()) {
                    this.autoRefreshEnabled = rs.getBoolean("auto_refresh");
                    this.refreshInterval = rs.getInt("refresh_interval");
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading refresh settings: " + e);
            // Use defaults if settings can't be loaded
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Start automatic refresh at the specified interval
     */
    public void startAutoRefresh() {
        if (!autoRefreshEnabled) {
            return;
        }
        
        stopAutoRefresh(); // Stop any existing refresh task
        
        refreshTask = scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                performRefresh();
            });
        }, refreshInterval, refreshInterval, TimeUnit.SECONDS);
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Stop automatic refresh
     */
    public void stopAutoRefresh() {
        if (refreshTask != null && !refreshTask.isCancelled()) {
            refreshTask.cancel(true);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Perform one manual refresh immediately
     */
    public void manualRefresh() {
        SwingUtilities.invokeLater(() -> {
            performRefresh();
        });
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Set new refresh interval (in seconds)
     */
    public void setRefreshInterval(int seconds) {
        this.refreshInterval = seconds;
        if (autoRefreshEnabled && refreshTask != null && !refreshTask.isCancelled()) {
            // Restart with new interval
            startAutoRefresh();
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Enable or disable auto-refresh
     */
    public void setAutoRefreshEnabled(boolean enabled) {
        this.autoRefreshEnabled = enabled;
        if (enabled) {
            startAutoRefresh();
        } else {
            stopAutoRefresh();
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Save user's refresh preferences to database
     */
    public void saveRefreshSettings(String accountNumber) {
        try {
            ConnectionSql c = new ConnectionSql();
            String query = "INSERT INTO refresh_settings (Account_No, auto_refresh, refresh_interval) " +
                          "VALUES ('" + accountNumber + "', " + (autoRefreshEnabled ? "TRUE" : "FALSE") + ", " + refreshInterval + ") " +
                          "ON DUPLICATE KEY UPDATE auto_refresh = " + (autoRefreshEnabled ? "TRUE" : "FALSE") + ", " +
                          "refresh_interval = " + refreshInterval;
            c.s.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Error saving refresh settings: " + e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Perform the actual refresh by calling appropriate method on target component
     */
    private void performRefresh() {
        try {
            if (targetComponent instanceof BalanceEnquiry) {
                ((BalanceEnquiry) targetComponent).updateBalance();
            } else if (targetComponent instanceof Transactions) {
                ((Transactions) targetComponent).updateBalanceDisplay();
            } else if (targetComponent instanceof LinkedAccountsView) {
                java.lang.reflect.Method method = targetComponent.getClass().getMethod("loadLinkedAccounts");
                method.invoke(targetComponent);
            }
        } catch (Exception e) {
            System.out.println("Error performing refresh: " + e);
        }
    }
    
    /* [AGENT GENERATED CODE - REQUIREMENT:US1-AC4] 
     * Clean up resources on shutdown
     */
    public void shutdown() {
        stopAutoRefresh();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}

/* 
 * Test Cases: TC-US1-04, TC-US1-08
 * Agent Run ID: AR-2025-11-27-001
 */