package ru.mirea.nosenkov.dbapp.logic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseService {

    List<String> getTableNames(Connection connection) throws SQLException;

    List<List<Object>> getTableData(Connection connection, String table) throws SQLException;

    List<String> getTableColumns(Connection connection, String table) throws SQLException;
}
