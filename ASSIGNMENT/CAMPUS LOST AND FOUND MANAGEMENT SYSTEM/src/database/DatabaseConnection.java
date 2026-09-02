package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * DatabaseConnection manages JDBC connectivity to the MySQL database.
 * Host, port, database name, username, and password can be customized here.
 */
public class DatabaseConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "campus_lost_found";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin"; // Change as per your MySQL root password

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE 
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Load JDBC Driver once upon class initialization
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Ensure mysql-connector-j is included in classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new java.sql.Connection instance.
     * @return Connection object to campus_lost_found MySQL DB.
     * @throws SQLException if connection fails.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Tests whether the database connection is active and reachable.
     * Displays a JOptionPane notification if the database connection fails.
     * @return true if connected successfully, false otherwise.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to show standard connection error dialog to user.
     */
    public static void showConnectionErrorDialog(java.awt.Component parent) {
        String msg = "Could not connect to MySQL Database!\n\n"
                + "Expected Details:\n"
                + " • URL: " + URL + "\n"
                + " • Username: " + USERNAME + "\n\n"
                + "Please ensure:\n"
                + " 1. MySQL Server is running on port " + PORT + ".\n"
                + " 2. Database 'campus_lost_found' exists (run schema.sql).\n"
                + " 3. Username and password in DatabaseConnection.java match your setup.";
        JOptionPane.showMessageDialog(parent, msg, "Database Connection Error", JOptionPane.ERROR_MESSAGE);
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getDatabaseName() {
        return DATABASE;
    }
}
