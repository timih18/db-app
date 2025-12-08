package ru.mirea.nosenkov.dbapp.logic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseService {

    List<String> getTableNames(Connection connection) throws SQLException;

    List<List<Object>> getTableData(Connection connection, String table) throws SQLException;

    List<String> getTableColumns(Connection connection, String table) throws SQLException;

    List<String> getEditableColumns(Connection connection, String table) throws SQLException;

    int addToDB(Connection connection, String table, Map<String, String> values) throws SQLException;

    int updateInDB(Connection connection, String table, Map<String, String> originalValues, Map<String, String> newValues) throws SQLException;

    void deleteFromDB(Connection connection, String table, Map<String, String> primaryKeyValues) throws SQLException;
}
