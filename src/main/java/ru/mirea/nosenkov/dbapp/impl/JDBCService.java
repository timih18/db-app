package ru.mirea.nosenkov.dbapp.impl;

import ru.mirea.nosenkov.dbapp.logic.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JDBCService implements DatabaseService {

    @Override
    public List<String> getTableNames(Connection connection) throws SQLException {
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
    public List<List<Object>> getTableData(Connection connection, String table) throws SQLException {
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
    public List<String> getTableColumns(Connection connection, String table) throws SQLException {
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

    @Override
    public void executeUpdate(Connection connection, String query) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    @Override
    public int addToDB(Connection connection, String table, Map<String, String> inputValues) throws  SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<String> columnList = new ArrayList<>(inputValues.keySet());
        List<String> editableColumns = getEditableColumns(connection, table);

        for (int i = 0; i < columnList.size(); ++i) {
            String column = columnList.get(i);
            if (editableColumns.contains(column)) {
                String value = inputValues.get(column);

                if (i > 0) {
                    columns.append(", ");
                    values.append(", ");
                }

                columns.append("[").append(column).append("]");
                values.append("'").append(value.replace("'", "''").replace("\\", "\\\\")).append("'");
            }
        }

        String query = "INSERT INTO " + "[" + table + "]" + " (" + columns + ") VALUES (" + values + ")";
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query);
        }
    }

    @Override
    public List<String> getEditableColumns(Connection connection, String table) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        List<String> columns = new ArrayList<>();
        String query = """
                SELECT COLUMN_NAME, COLUMNPROPERTY(OBJECT_ID(TABLE_SCHEMA+'.'+TABLE_NAME), COLUMN_NAME, 'IsIdentity')
                AS IS_IDENTITY FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? ORDER BY ORDINAL_POSITION
                """;
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, table);
        try (ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                String column = resultSet.getString("COLUMN_NAME");
                if (resultSet.getInt("IS_IDENTITY") == 0) { columns.add(column); }
            }
        }
        return columns;
    }
}
