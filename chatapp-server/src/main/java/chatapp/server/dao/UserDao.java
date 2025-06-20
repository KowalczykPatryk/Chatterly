package chatapp.server.dao;

import chatapp.server.db.Database;
import chatapp.server.model.User;
import java.util.List;
import java.util.ArrayList;
import chatapp.server.utils.BcryptPasswordHasher;

import java.sql.*;


public class UserDao {
    public void addUser(User user) throws SQLException {
        try( Connection conn = Database.getConnection())
        {
            String sql = "INSERT INTO users (username, passwordHash, publicKey) VALUES (?, ?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getPublicKey());
                stmt.executeUpdate();
            }
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        try(Connection conn = Database.getConnection())
        {
            String sql = "SELECT * FROM users where username = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setString(1, username);
                ResultSet result = stmt.executeQuery();
                if (result.next())
                {
                    return new User(result.getString("username"), result.getString("passwordHash"), result.getString("publicKey"));
                }
            }
        }
        return null;
    }
    public boolean validateCredentials(String username, String password) throws SQLException
    {
        try(Connection conn = Database.getConnection())
        {
            String sql = "SELECT * FROM users where username = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setString(1, username);
                ResultSet result = stmt.executeQuery();
                if(result.next())
                {
                    if(BcryptPasswordHasher.verifyPassword(password, result.getString("passwordHash")))
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        return false;
    }
    public List<User> searchUsers(String searchTerm) throws SQLException
    {
        List<User> users = new ArrayList<>();
        try(Connection conn = Database.getConnection())
        {
            String sql = "SELECT * FROM users where username ILIKE ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setString(1, "%"+searchTerm+"%");
                ResultSet result = stmt.executeQuery();
                while(result.next())
                {
                    User user = new User(result.getString("username"), result.getString("passwordHash"), result.getString("publicKey"));
                    users.add(user);
                }
            }
        }
        return users;
    }
    public boolean userExists(String username) throws SQLException
    {
        try(Connection conn = Database.getConnection())
        {
            String sql = "SELECT * FROM users where username = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setString(1, username);
                ResultSet result = stmt.executeQuery();
                return result.next();
            }
        }
    }
    public int getId(String username) throws SQLException
    {
        try(Connection conn = Database.getConnection())
        {
            String sql = "SELECT id FROM users where username = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setString(1, username);
                ResultSet result = stmt.executeQuery();
                if (result.next())
                {
                    return result.getInt("id");
                }
            }
        }
        return -1;
    }
    public String getUsername(int userId) throws SQLException
    {
        try(Connection conn = Database.getConnection())
        {
            String sql = "SELECT username FROM users where id = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql))
            {
                stmt.setInt(1, userId);
                ResultSet result = stmt.executeQuery();
                if (result.next())
                {
                    return result.getString("username");
                }
            }
        }
        return "";
    }

}

