package chatapp.server.dao;

import chatapp.server.db.Database;
import chatapp.server.model.User;
import chatapp.server.dao.UserDao;
import chatapp.server.model.Friend;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;

public class FriendDao
{
    public void saveFriendRequest(int userId, int friendId) throws SQLException{
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
    public void saveAcceptFriendRequest(int userIdFrom, int userIdTo) throws SQLException{
        try( Connection conn = Database.getConnection())
        {
            String sql = "UPDATE friends SET status='accepted' WHERE ((userId=? AND friendId=?) OR (userId=? AND friendId=?)) AND status='pending'";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userIdTo);
                stmt.setInt(2, userIdFrom);
                stmt.setInt(3, userIdFrom);
                stmt.setInt(4, userIdTo);
                stmt.executeUpdate();
            }
        }
    }
    public void saveRejectFriendRequest(int userIdFrom, int userIdTo) throws SQLException{
        try( Connection conn = Database.getConnection())
        {
            String sql = "UPDATE friends SET status='rejected' WHERE ((userId=? AND friendId=?) OR (userId=? AND friendId=?)) AND status='pending'";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userIdTo);
                stmt.setInt(2, userIdFrom);
                stmt.setInt(3, userIdFrom);
                stmt.setInt(4, userIdTo);
                stmt.executeUpdate();
            }
        }
    }
    public void removeFriend(int userIdFrom, int userIdTo) throws SQLException{
        try( Connection conn = Database.getConnection())
        {
            String sql = "DELETE FROM friends WHERE (userId=? AND friendId=?) OR (userId=? AND friendId=?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userIdTo);
                stmt.setInt(2, userIdFrom);
                stmt.setInt(3, userIdFrom);
                stmt.setInt(4, userIdTo);
                stmt.executeUpdate();
            }
        }
    }
    public List<Friend> getFriends(int userId) throws SQLException{
        try( Connection conn = Database.getConnection()) {
            String sql = "SELECT u.username, u.publicKey " +
                    "FROM users u JOIN friends f " +
                    "ON ((f.userId=? AND f.friendId=u.id) OR (f.friendId=? AND f.userId=u.id)) " +
                    "WHERE f.status='accepted'";
            List<Friend> friends = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                try (ResultSet result = stmt.executeQuery()) {
                    while (result.next()) {
                        Friend friend = new Friend();
                        friend.setUsername(result.getString("username"));
                        friend.setPublicKey(result.getString("publicKey"));
                        friends.add(friend);
                    }
                }
            }
            return friends;
        }
    }
    public List<String> getPendingRequests(int userId) throws SQLException{
        try ( Connection conn = Database.getConnection()) {
            String sql = "SELECT userId " +
                    "FROM friends WHERE friendId=? AND status='pending'";
            List<String> requests = new ArrayList<>();
                try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet result = stmt.executeQuery()) {
                    while (result.next()) {
                        UserDao userDao = new UserDao();
                        String username = userDao.getUsername(result.getInt("userId"));
                        requests.add(username);
                    }
                }
            }
            return requests;
        }
    }
    public String getFriendshipStatus(int userIdFrom, int userIdTo) throws SQLException{
        String sql = "SELECT status FROM friends WHERE userId=? AND friendId=?";
        try (Connection conn = Database.getConnection())
        {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userIdTo);
                stmt.setInt(2, userIdFrom);
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