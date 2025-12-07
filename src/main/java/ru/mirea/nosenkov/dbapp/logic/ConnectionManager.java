package ru.mirea.nosenkov.dbapp.logic;

import ru.mirea.nosenkov.dbapp.impl.JDBCService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static ConnectionManager instance;
    private final DatabaseService jdbcService;
    private Connection connection;

    private ConnectionManager() {
        jdbcService = new JDBCService();
    }

    public static ConnectionManager getInstance() {
        if (instance == null) { instance = new ConnectionManager(); }
        return instance;
    }

    public void createConnection(String address, String name, String login, String password) throws SQLException {
        String url = "jdbc:sqlserver://" + address + ";databaseName=" + name + ";encrypt=false;";
        this.connection =  DriverManager.getConnection(url, login, password);
    }

    public Connection getConnection() { return this.connection; }
    public DatabaseService getJdbcService() { return jdbcService; }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }
}
