package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/ivan?serverTimezone=UTC";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "Jenya_Pidor_123";

    public Connection createConnection() {
        Connection connection;
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            connection = DriverManager.getConnection(CONNECTION_URL, USER_NAME, PASSWORD);
            if (!connection.isClosed()) {
                System.out.println("--------------------------------");
                System.out.println("|    Connected successfully!   |");
                System.out.println("--------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return connection;
    }

    public void closeConnection(Connection connection) {
        try {
            connection.close();
            if (connection.isClosed()) {
                System.out.println("--------------------------------");
                System.out.println("|     Connection is closed!    |");
                System.out.println("--------------------------------");
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
