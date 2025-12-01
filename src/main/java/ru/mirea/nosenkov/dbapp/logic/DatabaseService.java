package ru.mirea.nosenkov.dbapp.logic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseService {

    Connection createConnection(String address, String name, String login, String password) throws SQLException;

    List<String> getTableNames() throws SQLException;

    List<List<Object>> getTableData(String table) throws SQLException;

    List<String> getTableColumns(String table) throws SQLException;
}
