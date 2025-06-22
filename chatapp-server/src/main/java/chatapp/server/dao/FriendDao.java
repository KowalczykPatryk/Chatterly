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
            conn.setAutoCommit(false);
            String UpdateQuery = "UPDATE friends SET status='accepted' WHERE ((userId=? AND friendId=?) OR (userId=? AND friendId=?)) AND status='pending';";
            String DeleteQuery = "DELETE FROM friends WHERE ((userId = ? AND friendId = ?) OR (userId=? AND friendId=?)) AND status='rejected';";
            try(PreparedStatement update = conn.prepareStatement(UpdateQuery); PreparedStatement delete = conn.prepareStatement(DeleteQuery))
            {
                update.setInt(1, userIdTo);
                update.setInt(2, userIdFrom);
                update.setInt(3, userIdFrom);
                update.setInt(4, userIdTo);
                update.executeUpdate();

                delete.setInt(1, userIdTo);
                delete.setInt(2, userIdFrom);
                delete.setInt(3, userIdFrom);
                delete.setInt(4, userIdTo);
                delete.executeUpdate();

                conn.commit();
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
        String acceptedSql =
                        "SELECT 1 "
                        + "FROM friends "
                        + "WHERE "
                        + "  ((userId = ? AND friendId = ?) OR (userId = ? AND friendId = ?)) "
                        + "  AND status = 'accepted'";

        String statusSql =
                        "SELECT status "
                        + "FROM friends "
                        + "WHERE (userId = ? AND friendId = ?)";

        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(acceptedSql)) {
                ps.setInt(1, userIdFrom);
                ps.setInt(2, userIdTo);
                ps.setInt(3, userIdTo);
                ps.setInt(4, userIdFrom);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return "accepted";
                    }
                }
            }
            try (PreparedStatement ps2 = conn.prepareStatement(statusSql)) {
                ps2.setInt(1, userIdTo);
                ps2.setInt(2, userIdFrom);

                try (ResultSet rs2 = ps2.executeQuery()) {
                    if (rs2.next()) {
                        return rs2.getString("status");
                    } else {
                        return "Not a friend";
                    }
                }
            }
        }
    }
}