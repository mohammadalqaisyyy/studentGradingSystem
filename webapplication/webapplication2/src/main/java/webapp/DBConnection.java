package webapp;

import java.sql.*;

public class DBConnection {
    private static Connection connection;

    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        makeConnection();
    }

    private static void makeConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3306/education",
                            "root", "MkAt4321@");
        }
    }

    public static Connection getConnection() throws SQLException {
        makeConnection();
        return connection;
    }

    public static ResultSet getQuery(String query) throws SQLException {
        makeConnection();
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public static void closeConnection() throws SQLException {
        if (!connection.isClosed())
            connection.close();
    }
}
