package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/college_carpool_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Rohith@2005";

    private DBConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        try {
            // Create a new connection each time
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rethrow to allow specific handling
        }
    }
}
