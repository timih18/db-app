package ru.mirea.nosenkov.dbapp.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;
import ru.mirea.nosenkov.dbapp.logic.DatabaseService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TableDataService {
    private final ConnectionManager connectionManager;
    private final DatabaseService jdbcService;

    public TableDataService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.jdbcService = connectionManager.getJdbcService();
    }

    public ObservableList<String> loadTablesList() throws SQLException {
        Connection connection = connectionManager.getConnection();
        return FXCollections.observableArrayList(jdbcService.getTableNames(connection));
    }

    public TableData loadTableData(String table) throws SQLException {
        Connection connection = connectionManager.getConnection();
        List<String> columns = jdbcService.getTableColumns(connection, table);
        List<List<Object>> rows = jdbcService.getTableData(connection, table);

        ObservableList<TableRow> tableRows = FXCollections.observableArrayList();
        for (List<Object> rowData : rows) {
            TableRow row = new TableRow();
            for (int i = 0; i < columns.size(); i++) {
                row.set(columns.get(i), rowData.get(i).toString());
            }
            tableRows.add(row);
        }

        return new TableData(columns, tableRows);
    }

    public record TableData(List<String> columns, ObservableList<TableRow> rows) {}
}
