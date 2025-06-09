package chatapp.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {
    // will get this info from render when creating
    // render will probably set env System.getenv("DATABASE_URL")
    private static final String URL = "jdbc:postgresql://localhost:5432/yourdb";
    private static final String USER = "youruser";
    private static final String PASSWORD = "yourpassword";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
