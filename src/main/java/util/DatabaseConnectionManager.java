package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {
    private static final String PATH_TO_PROPERTIES = "src/main/resources/properties/config.properties";
    private static final String CONNECTION_URL = "jdbc:mysql://" + getProp("db.url") + getProp("db.port") + "/" + getProp("db.base") + "?serverTimezone=UTC";
    private static final String USER_NAME = getProp("db.user");
    private static final String PASSWORD = getProp("db.password");

    private static String getProp(String propName) {
        String propStr = null;
        FileInputStream fileInputStream;
        Properties properties = new Properties();
        try {
            if (new File(PATH_TO_PROPERTIES).isFile()) {
                fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
                properties.load(fileInputStream);
                propStr = properties.get(propName).toString();
            } else {
                propStr = getSysProperty(propName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propStr == null ? "" : propStr;
    }

    private static String getSysProperty(String propName) {
        return System.getProperty(propName);
    }

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