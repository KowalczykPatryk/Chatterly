package chatapp.server.dao;

import java.sql.*;
import java.time.Instant;
import chatapp.server.db.Database;

public class UserStatusDao
{
    public void setUserOnline(int userId) throws SQLException {
        String updateSql = "UPDATE userStatus SET isOnline = TRUE, lastSeen = NOW() WHERE userId = ?";
        String insertSql = "INSERT INTO userStatus(userId, isOnline) VALUES (?, TRUE)";
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setInt(1, userId);
                int rows = updatePs.executeUpdate();
                if (rows == 0) {
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, userId);
                        insertPs.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    public void setUserOffline(int userId) throws SQLException {
        String updateSql = "UPDATE userStatus SET isOnline = FALSE, lastSeen = NOW() WHERE userId = ?";
        String insertSql = "INSERT INTO userStatus(userId, isOnline) VALUES (?, FALSE)";
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setInt(1, userId);
                int rows = updatePs.executeUpdate();
                if (rows == 0) {
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setInt(1, userId);
                        insertPs.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    public boolean isUserOnline(int userId) throws SQLException {
        String sql = "SELECT isOnline FROM userStatus WHERE userId = ?";
        try (Connection conn = Database.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet result = stmt.executeQuery()) {
                    return result.next() && result.getBoolean("isOnline");
                }
            }
        }
    }
    public Instant getLastSeen(int userId) throws SQLException {
        String sql = "SELECT lastSeen FROM userStatus WHERE userId = ?";
        try (Connection conn = Database.getConnection()){
             try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                 stmt.setInt(1, userId);
                 try (ResultSet result = stmt.executeQuery()) {
                     if (result.next()) {
                         Timestamp ts = result.getTimestamp("lastSeen");
                         return ts != null ? ts.toInstant() : null;
                     }
                 }
             }
        }
        return null;
    }
}