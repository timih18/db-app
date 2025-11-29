package ru.mirea.nosenkov.dbapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import ru.mirea.nosenkov.dbapp.impl.JDBCService;
import ru.mirea.nosenkov.dbapp.logic.DatabaseService;

import java.sql.*;

public class ConnectionFormController {
    @FXML
    private TextField addressField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;

    private final DatabaseService jdbcService = new JDBCService();

    @FXML
    protected void onConnectButtonClick() {
        String address = addressField.getText().trim();
        String dbName = nameField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (address.isEmpty() || dbName.isEmpty() || login.isEmpty() || password.isEmpty()) {
            showError("Ошибка", "Недостаточно данных");
            return;
        }

        try (Connection connection = jdbcService.createConnection(address, dbName, login, password)) {
            showError("Успех", "БД подключена");
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Ошибка подключения", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
