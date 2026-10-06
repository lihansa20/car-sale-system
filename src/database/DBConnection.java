package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {

    private static DBConnection instance;

    private Connection connection;
    private final String url;
    private final String username;
    private final String password;

    private DBConnection() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot read db.properties file: " + e.getMessage(), e);
        }
        url = props.getProperty("db.url");
        username = props.getProperty("db.username");
        password = props.getProperty("db.password");
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }
}