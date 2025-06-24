package chatapp.client.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import chatapp.client.model.Tokens;
import chatapp.client.model.Friend;

public class SQLiteManager {
    private static final String DB_URL = "jdbc:sqlite:chatterly.db";
    private static SQLiteManager instance;
    private Connection connection;

    private SQLiteManager() throws SQLException {
        connect();
        initTables();
    }

    public static SQLiteManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new SQLiteManager();
        }
        return instance;
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
    }

    private void initTables() throws SQLException {
        String createTokensTable = "CREATE TABLE IF NOT EXISTS tokens ("
                + "owner TEXT NOT NULL PRIMARY KEY,"
                + "access_token TEXT NOT NULL,"
                + "refresh_token TEXT NOT NULL" + ")";

        String createFriendsTable = "CREATE TABLE IF NOT EXISTS friends ("
                + "owner TEXT NOT NULL,"
                + "username TEXT NOT NULL, "
                + "publicKey TEXT NOT NULL, "
                + "PRIMARY KEY(owner, username)"
                + ")";

        String createLastMessagesTable = "CREATE TABLE IF NOT EXISTS lastmessages ("
                + "id integer PRIMARY KEY AUTOINCREMENT,"
                + "owner TEXT NOT NULL, "
                + "username TEXT NOT NULL, "
                + "message TEXT NOT NULL, "
                + "mine BOOLEAN DEFAULT 0" + ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTokensTable);
            stmt.execute(createFriendsTable);
            stmt.execute(createLastMessagesTable);
        }
    }

    public void saveTokens(String owner, String accessToken, String refreshToken) throws SQLException {
        String upsert = "INSERT INTO tokens (owner, access_token, refresh_token) VALUES (?, ?, ?) "
                + "ON CONFLICT(owner) DO UPDATE SET access_token = excluded.access_token, refresh_token = excluded.refresh_token";

        try (PreparedStatement ps = connection.prepareStatement(upsert)) {
            ps.setString(1, owner);
            ps.setString(2, accessToken);
            ps.setString(3, refreshToken);
            ps.executeUpdate();
        }
    }

    public Tokens loadTokens(String owner) throws SQLException {
        String query = "SELECT access_token, refresh_token FROM tokens WHERE owner = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, owner);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tokens(rs.getString("access_token"), rs.getString("refresh_token"));
                }
            }
        }
        return null;
    }
    public String getAccessToken(String owner) throws SQLException {
        String query = "SELECT access_token FROM tokens WHERE owner = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, owner);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("access_token");
                }
            }
        }
        return null;
    }
    public String getRefreshToken(String owner) throws SQLException {
        String query = "SELECT refresh_token FROM tokens WHERE owner = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, owner);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("refresh_token");
                }
            }
        }
        return null;
    }

    public void upsertFriend(String owner, String username, String publicKey) throws SQLException {
        String upsert = "INSERT INTO friends (owner, username, publicKey) VALUES (?, ?, ?) "
                + "ON CONFLICT(owner, username) DO NOTHING";
        try (PreparedStatement ps = connection.prepareStatement(upsert)) {
            ps.setString(1, owner);
            ps.setString(2, username);
            ps.setString(3, publicKey);
            ps.executeUpdate();
        }
    }

    public String getPublicKey(String owner, String username) throws SQLException {
        String query = "SELECT publicKey FROM friends WHERE owner = ? AND username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, owner);
            ps.setString(2, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("publicKey");
                }
            }
        }
        return null;
    }

    public List<Friend> loadFriends(String owner) throws SQLException {
        List<Friend> friends = new ArrayList<>();
        String query = "SELECT * FROM friends WHERE owner = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, owner);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    friends.add(new Friend(rs.getString("username"), rs.getString("publicKey")));
                }
            }
        }
        return friends;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
