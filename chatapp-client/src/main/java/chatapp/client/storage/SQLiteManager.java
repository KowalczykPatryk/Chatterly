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
                + "id INTEGER PRIMARY KEY CHECK (id = 1),"
                + "access_token TEXT NOT NULL,"
                + "refresh_token TEXT NOT NULL" + ");";

        String createFriendsTable = "CREATE TABLE IF NOT EXISTS friends ("
                + "username TEXT PRIMARY KEY, "
                + "publicKey TEXT NOT NULL"+ ");";

        String createLastMessagesTable = "CREATE TABLE IF NOT EXISTS lastmessages ("
                + "id integer PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL,"
                + "message TEXT NOT NULL, "
                + "mine BOOLEAN DEFAULT 0" + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTokensTable);
            stmt.execute(createFriendsTable);
            stmt.execute(createLastMessagesTable);
        }
    }

    public void saveTokens(String accessToken, String refreshToken) throws SQLException {
        String upsert = "INSERT INTO tokens (id, access_token, refresh_token) VALUES (1, ?, ?) "
                + "ON CONFLICT(id) DO UPDATE SET access_token = excluded.access_token, refresh_token = excluded.refresh_token;";

        try (PreparedStatement ps = connection.prepareStatement(upsert)) {
            ps.setString(1, accessToken);
            ps.setString(2, refreshToken);
            ps.executeUpdate();
        }
    }

    public Tokens loadTokens() throws SQLException {
        String query = "SELECT access_token, refresh_token FROM tokens WHERE id = 1;";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return new Tokens(rs.getString("access_token"), rs.getString("refresh_token"));
            }
        }
        return null;
    }
    public String getAccessToken() throws SQLException {
        String query = "SELECT access_token FROM tokens WHERE id = 1;";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("access_token");
            }
        }
        return null;
    }
    public String getRefreshToken() throws SQLException {
        String query = "SELECT refresh_token FROM tokens WHERE id = 1;";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("refresh_token");
            }
        }
        return null;
    }

    public void upsertFriend(String username, String publicKey) throws SQLException {
        String upsert = "INSERT INTO friends (username, publicKey) VALUES (?, ?) "
                + "ON CONFLICT(username) DO NOTHING;";
        try (PreparedStatement ps = connection.prepareStatement(upsert)) {
            ps.setString(1, username);
            ps.setString(2, publicKey);
            ps.executeUpdate();
        }
    }

    public List<Friend> loadFriends() throws SQLException {
        List<Friend> friends = new ArrayList<>();
        String query = "SELECT * FROM friends;";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                friends.add(new Friend(rs.getString("username"), rs.getString("publicKey")));
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
