package farmsweeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/farmsweeper";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Juliana"; // Replace with your actual password

    // Static method to get a connection to the database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
