package chatapp.server.dao;

import chatapp.server.model.Message;
import java.util.List;
import chatapp.server.db.Database;
import java.util.ArrayList;

import java.sql.*;

public class MessageDao
{
    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages(fromUser, toUser, iv, encryptedKey, ciphertext, timestamp, delivered) " +
                "VALUES (?, ?, ?, ?, ?, false)";
        try (Connection conn = Database.getConnection()){
             try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                 stmt.setInt(1, message.getFromUser());
                 stmt.setInt(2, message.getToUser());
                 stmt.setBytes(3, message.getIv());
                 stmt.setBytes(4, message.getEncryptedKey());
                 stmt.setBytes(5, message.getCiphertext());
                 stmt.setTimestamp(6, Timestamp.from(message.getCreatedAt()));
                 stmt.executeUpdate();
             }
        }
    }

    public List<Message> getMessagesBetweenUsers(int fromUser, int toUser, int limit) throws SQLException {
        String sql = "SELECT id, fromUser, toUser, iv, encryptedKey, ciphertext, timestamp, delivered " +
                "FROM messages " +
                "WHERE (fromUser = ? AND toUser = ?) OR (fromUser = ? AND toUser = ?) " +
                "ORDER BY timestamp ASC " + "LIMIT ?";
        List<Message> messages = new ArrayList<>();
        try (Connection conn = Database.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, fromUser);
                stmt.setInt(2, toUser);
                stmt.setInt(3, toUser);
                stmt.setInt(4, fromUser);
                stmt.setInt(5, limit);
                try (ResultSet result = stmt.executeQuery()) {
                    while (result.next()) {
                        Message message = new Message();
                        message.setId(result.getLong("id"));
                        message.setFromUser(result.getInt("fromUser"));
                        message.setToUser(result.getInt("toUser"));
                        message.setIv(result.getBytes("iv"));
                        message.setEncryptedKey(result.getBytes("encryptedKey"));
                        message.setCiphertext(result.getBytes("ciphertext"));
                        message.setCreatedAt(result.getTimestamp("timestamp").toInstant());
                        message.setDelivered(result.getBoolean("delivered"));
                        messages.add(message);
                    }
                }
            }
        }
        return messages;
    }
    public void markAsDelivered(int fromUser, int toUser) throws SQLException {
        String sql = "UPDATE messages SET delivered = true WHERE fromUser = ? AND toUser = ?";
        try (Connection conn = Database.getConnection()){
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, fromUser);
                stmt.setInt(2, toUser);
                stmt.executeUpdate();
            }
        }
    }

}