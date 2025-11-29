package ru.mirea.nosenkov.dbapp.logic;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseService {

    Connection createConnection(String address, String name, String login, String password) throws SQLException;
}
