package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginHistoryDAO {

    public static void recordLogin(String username) {
        String sql = "INSERT INTO login_history (username) VALUES (?)";

        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error recording login: " + e.getMessage());
        }
    }

    public static void recordLogout(String username) {
        String sql = """
            UPDATE login_history 
            SET logout_time = CURRENT_TIMESTAMP,
                session_duration = TIMESTAMPDIFF(SECOND, login_time, CURRENT_TIMESTAMP)
            WHERE username = ? AND logout_time IS NULL
            ORDER BY login_time DESC LIMIT 1
            """;

        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error recording logout: " + e.getMessage());
        }
    }

    public static List<LoginHistory> getLoginHistory() throws SQLException {
        List<LoginHistory> history = new ArrayList<>();
        String sql = "SELECT id, username, login_time, logout_time, session_duration FROM login_history ORDER BY login_time DESC";

        try (Connection conn = main.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LoginHistory record = new LoginHistory();
                record.setUsername(rs.getString("username"));
                record.setLoginTime(rs.getTimestamp("login_time"));
                record.setLogoutTime(rs.getTimestamp("logout_time"));
                record.setSessionDuration(rs.getInt("session_duration"));
                history.add(record);
            }
        }
        return history;
    }
}