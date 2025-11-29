package ru.mirea.nosenkov.dbapp.impl;

import ru.mirea.nosenkov.dbapp.logic.DatabaseService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCService implements DatabaseService {

    @Override
    public Connection createConnection(String address, String name, String login, String password) throws SQLException {
        String url = "jdbc:sqlserver://" + address + ";databaseName=" + name + ";encrypt=false;";
        return DriverManager.getConnection(url, login, password);
    }
}
