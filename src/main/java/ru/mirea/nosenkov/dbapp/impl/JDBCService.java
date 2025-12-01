package ru.mirea.nosenkov.dbapp.impl;

import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;
import ru.mirea.nosenkov.dbapp.logic.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCService implements DatabaseService {

    private Connection connection;

    @Override
    public Connection createConnection(String address, String name, String login, String password) throws SQLException {
        String url = "jdbc:sqlserver://" + address + ";databaseName=" + name + ";encrypt=false;";
        this.connection =  DriverManager.getConnection(url, login, password);
        return connection;
    }

    @Override
    public List<String> getTableNames() throws SQLException {
        connection = ConnectionManager.getInstance().getConnection();
        List<String> tableNames = new ArrayList<>();

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                if (!name.startsWith("sys") && !name.startsWith("trace_")) {
                    tableNames.add(name);
                }
            }
        }
        return tableNames;
    }

    @Override
    public List<List<Object>> getTableData(String table) throws SQLException {
        connection = ConnectionManager.getInstance().getConnection();
        List<List<Object>> data = new ArrayList<>();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        String query = "SELECT * FROM " + "[" + table + "]";
        Statement statement = connection.createStatement();
        try (ResultSet resultSet = statement.executeQuery(query)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                data.add(row);
            }
        }

        return data;
    }

    @Override
    public List<String> getTableColumns(String table) throws SQLException {
        connection = ConnectionManager.getInstance().getConnection();
        List<String> columns = new ArrayList<>();
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table +
                "' ORDER BY ORDINAL_POSITION";
        Statement statement = connection.createStatement();
        try (ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                columns.add(resultSet.getString("COLUMN_NAME"));
            }
        }

        return columns;
    }
}
