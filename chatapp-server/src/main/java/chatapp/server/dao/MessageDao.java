package chatapp.server.dao;

import chatapp.server.model.Message;
import java.util.List;
import chatapp.server.db.Database;
import java.util.ArrayList;
import chatapp.server.dao.UserDao;

import java.sql.*;

public class MessageDao
{
    public void saveMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages(fromUser, toUser, iv, encryptedKey, ciphertext) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection()){
             try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                 UserDao userDao = new UserDao();
                 stmt.setInt(1, userDao.getId(message.getFromUser()));
                 stmt.setInt(2, userDao.getId(message.getToUser()));
                 stmt.setBytes(3, message.getIv());
                 stmt.setBytes(4, message.getEncryptedKey());
                 stmt.setBytes(5, message.getCiphertext());
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
                UserDao userDao = new UserDao();
                try (ResultSet result = stmt.executeQuery()) {
                    while (result.next()) {
                        Message message = new Message();
                        message.setFromUser(userDao.getUsername(result.getInt("fromUser")));
                        message.setToUser(userDao.getUsername(result.getInt("toUser")));
                        message.setIv(result.getBytes("iv"));
                        message.setEncryptedKey(result.getBytes("encryptedKey"));
                        message.setCiphertext(result.getBytes("ciphertext"));
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