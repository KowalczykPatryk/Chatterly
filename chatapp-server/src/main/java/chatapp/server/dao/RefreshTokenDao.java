package chatapp.server.dao;

import java.sql.*;

import chatapp.server.db.Database;
import java.time.Instant;

public class RefreshTokenDao {
    public void saveToken(String token, String username, Instant expiresAt) throws SQLException {
        String sql = "INSERT INTO refreshTokens(token, userId, expiresAt, revoked) " +
                "VALUES(?, (SELECT id FROM users WHERE username=?), ?, FALSE)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setString(2, username);
            stmt.setTimestamp(3, Timestamp.from(expiresAt));
            stmt.executeUpdate();
        }
    }

    public boolean isValid(String token) throws SQLException {
        String sql = "SELECT 1 FROM refreshTokens " +
                "WHERE token=? AND expiresAt>NOW() AND revoked=FALSE";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet result = stmt.executeQuery()) {
                return result.next();
            }
        }
    }

    public void revoke(String token) throws SQLException {
        String sql = "UPDATE refreshTokens SET revoked=TRUE WHERE token=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        }
    }
}
