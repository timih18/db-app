package ru.mirea.nosenkov.dbapp.logic;

import ru.mirea.nosenkov.dbapp.impl.JDBCService;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {
    private static ConnectionManager instance;
    private final DatabaseService jdbcService;
    private Connection connection;

    private ConnectionManager() {
        jdbcService = new JDBCService();
    };

    public static ConnectionManager getInstance() {
        if (instance == null) { instance = new ConnectionManager(); }
        return instance;
    }

    public void setConnection(Connection connection) { this.connection = connection; }
    public Connection getConnection() { return this.connection; }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    public DatabaseService getJdbcService() { return jdbcService; }
}
