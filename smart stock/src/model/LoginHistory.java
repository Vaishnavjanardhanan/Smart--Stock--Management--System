package model;

import java.sql.Timestamp;

public class LoginHistory {
    private String username;
    private Timestamp loginTime;
    private Timestamp logoutTime;
    private int sessionDuration;
    
    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Timestamp getLoginTime() { return loginTime; }
    public void setLoginTime(Timestamp loginTime) { this.loginTime = loginTime; }
    
    public Timestamp getLogoutTime() { return logoutTime; }
    public void setLogoutTime(Timestamp logoutTime) { this.logoutTime = logoutTime; }
    
    public int getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(int sessionDuration) { this.sessionDuration = sessionDuration; }
}
