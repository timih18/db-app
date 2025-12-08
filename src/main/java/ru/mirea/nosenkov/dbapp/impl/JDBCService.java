package ru.mirea.nosenkov.dbapp.impl;

import ru.mirea.nosenkov.dbapp.logic.DatabaseService;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

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

    @Override
    public int updateInDB(Connection connection, String table, Map<String, String> originalValues, Map<String, String> newValues) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        List<String> primaryKeyColumns = getPrimaryKeyColumns(connection, table);
        List<String> editableColumns = getEditableColumns(connection, table);
        StringBuilder setClause = new StringBuilder();
        List<String> columnsToUpdate = new ArrayList<>();
        Set<String> set = new HashSet<>(primaryKeyColumns);

        for (String column : editableColumns) {
            if (!set.contains(column) && newValues.containsKey(column)) {
                if (!setClause.isEmpty()) {
                    setClause.append(", ");
                }
                setClause.append("[").append(column).append("]").append(" = ?");
                columnsToUpdate.add(column);
            }
        }

        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < primaryKeyColumns.size(); ++i) {
            if (i != 0) {
                whereClause.append(" AND ");
            }
            whereClause.append("[").append(primaryKeyColumns.get(i)).append("]").append(" = ?");
        }

        String query = "UPDATE " + "[" + table + "]" + " SET " + setClause + " WHERE " + whereClause;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            Map<String, Integer> columnTypes = getColumnTypes(connection, table);
            int ind = 1;
            for (String column : columnsToUpdate) {
                String value = newValues.get(column);
                Integer type = columnTypes.get(column);

                if (value == null || "null".equalsIgnoreCase(value)) {
                    statement.setNull(ind++, type != null ? type : Types.VARCHAR);
                } else {
                    setParameterByType(statement, ind++, value, type);
                }
            }

            for (String primaryKeyColumn : primaryKeyColumns) {
                String value = newValues.get(primaryKeyColumn);
                Integer type = columnTypes.get(primaryKeyColumn);

                if (value == null || "null".equalsIgnoreCase(value)) {
                    statement.setNull(ind++, type != null ? type : Types.VARCHAR);
                } else {
                    setParameterByType(statement, ind++, value, type);
                }
            }

            return statement.executeUpdate();
        }
    }

    @Override
    public void deleteFromDB(Connection connection, String table, Map<String, String> primaryKeyValues) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        List<String> primaryKeyColumns = getPrimaryKeyColumns(connection,  table);

        StringBuilder clause = new StringBuilder();
        for (int i = 0; i < primaryKeyColumns.size(); ++i) {
            clause.append("[").append(primaryKeyColumns.get(i)).append("]").append(" = ?");
            if (i > 0) { clause.append(" AND "); }
        }

        String query = "DELETE FROM " + "[" + table + "]" + "WHERE " + clause;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            Map<String, Integer> columnTypes = getColumnTypes(connection, table);
            int ind = 1;
            for (String primaryKeyColumn : primaryKeyColumns) {
                String value = primaryKeyValues.get(primaryKeyColumn);
                Integer type = columnTypes.get(primaryKeyColumn);

                if (value == null || "null".equalsIgnoreCase(value)) {
                    statement.setNull(ind++, type != null ? type : Types.VARCHAR);
                } else {
                    setParameterByType(statement, ind++, value, type);
                }
            }
            statement.executeUpdate();
        }
    }

    private List<String> getPrimaryKeyColumns(Connection connection, String table) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Нет подключения к БД");
        }

        List<String> primaryKeyColumns = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet resultSet = metaData.getPrimaryKeys(null, null, table)) {
            while (resultSet.next()) {
                primaryKeyColumns.add(resultSet.getString("COLUMN_NAME"));
            }
        }
        return primaryKeyColumns;
    }

    private Map<String, Integer> getColumnTypes(Connection connection, String table) throws SQLException {
        Map<String, Integer> types = new HashMap<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet rs = metaData.getColumns(null, null, table, null)) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                int dataType = rs.getInt("DATA_TYPE");
                types.put(columnName, dataType);
            }
        }
        return types;
    }

    private void setParameterByType(PreparedStatement statement, int ind, String value, Integer type) throws SQLException {
        if (type == null) {
            statement.setString(ind, value);
            return;
        }

        try {
            switch (type) {
                case Types.INTEGER, Types.SMALLINT, Types.TINYINT:
                    statement.setInt(ind, Integer.parseInt(value));
                    break;
                case Types.BIGINT:
                    statement.setLong(ind, Long.parseLong(value));
                    break;
                case Types.DECIMAL, Types.NUMERIC:
                    statement.setBigDecimal(ind, new BigDecimal(value));
                    break;
                case Types.FLOAT, Types.REAL:
                    statement.setFloat(ind, Float.parseFloat(value));
                    break;
                case Types.DOUBLE:
                    statement.setDouble(ind, Double.parseDouble(value));
                    break;
                case Types.BOOLEAN, Types.BIT:
                    statement.setBoolean(ind, Boolean.parseBoolean(value));
                    break;
                case Types.DATE:
                    statement.setDate(ind, Date.valueOf(value));
                    break;
                case Types.TIMESTAMP:
                    statement.setTimestamp(ind, Timestamp.valueOf(value));
                    break;
                default:
                    statement.setString(ind, value);
                    break;
            }
        } catch (IllegalArgumentException e) {
            statement.setString(ind, value);
        }
    }
}
