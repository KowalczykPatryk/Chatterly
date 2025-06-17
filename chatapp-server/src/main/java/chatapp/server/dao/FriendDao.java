package chatapp.server.dao;

import chatapp.server.db.Database;
import chatapp.server.model.User;
import chatapp.server.dto.FriendRequest;
import chatapp.server.dao.UserDao;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;

public class FriendDao
{
    void saveFriendRequest(int userId, int friendId) throws SQLException{
        try( Connection conn = Database.getConnection())
        {
            String sql = "INSERT INTO friends (userId, friendId, status) VALUES (?, ?, 'pending')";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userId);
                stmt.setInt(2, friendId);
                stmt.executeUpdate();
            }
        }
    }
    void saveAcceptFriendRequest(int userId, int friendId) throws SQLException{
        try( Connection conn = Database.getConnection())
        {
            String sql = "UPDATE friends SET status='accepted' WHERE userId=? AND friendId=? AND status='pending'";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userId);
                stmt.setInt(2, friendId);
                stmt.executeUpdate();
            }
        }
    }
    void removeFriend(int userId, int friendId) throws SQLException{
        try( Connection conn = Database.getConnection())
        {
            String sql = "DELETE FROM friends WHERE (userId=? AND friendId=?) OR (userId=? AND friendId=?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userId);
                stmt.setInt(2, friendId);
                stmt.setInt(3, friendId);
                stmt.setInt(4, userId);
                stmt.executeUpdate();
            }
        }
    }
    List<User> getFriends(int userId) throws SQLException{
        try( Connection conn = Database.getConnection()) {
            String sql = "SELECT u.username, u.passwordHash, u.publicKey " +
                    "FROM users u JOIN friends f " +
                    "ON ((f.userId=? AND f.friendId=u.id) OR (f.friendId=? AND f.userId=u.id)) " +
                    "WHERE f.status='accepted'";
            List<User> friends = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                try (ResultSet result = stmt.executeQuery()) {
                    while (result.next()) {
                        User user = new User();
                        user.setUsername(result.getString("username"));
                        user.setPassword(result.getString("passwordHash"));
                        user.setPublicKey(result.getString("publicKey"));
                        friends.add(user);
                    }
                }
            }
            return friends;
        }
    }
    List<FriendRequest> getPendingRequests(int userId) throws SQLException{
        try ( Connection conn = Database.getConnection()) {
            String sql = "SELECT userId, friendId, createdAt " +
                    "FROM friends WHERE friendId=? AND status='pending'";
            List<FriendRequest> requests = new ArrayList<>();
                try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet result = stmt.executeQuery()) {
                    while (result.next()) {
                        FriendRequest friendRequest = new FriendRequest();
                        UserDao userDao = new UserDao();
                        friendRequest.setUsernameFrom(userDao.getUsername(result.getInt("userId")));
                        friendRequest.setUsernameFriend(userDao.getUsername(result.getInt("friendId")));
                        friendRequest.setCreatedAt(result.getTimestamp("createdAt").toInstant());
                        requests.add(friendRequest);
                    }
                }
            }
            return requests;
        }
    }
    String getFriendshipStatus(int userId, int friendId) throws SQLException{
        String sql = "SELECT status FROM friends WHERE userId=? AND friendId=?";
        try (Connection conn = Database.getConnection())
        {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, friendId);
                try (ResultSet result = stmt.executeQuery()) {
                    if (result.next()) {
                        return result.getString("status");
                    }
                    else{
                        return "Not a friend";
                    }
                }
            }
        }
    }
}