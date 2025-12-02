package ru.mirea.nosenkov.dbapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.mirea.nosenkov.dbapp.impl.DisplayContextImpl;
import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;
import ru.mirea.nosenkov.dbapp.logic.DisplayContext;

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

    private MainFormController mainFormController;

    public void setMainFormController(MainFormController mainFormController) {
        this.mainFormController = mainFormController;
    }

    @FXML
    protected void onConnectButtonClick() {
        DisplayContext displayContext = new DisplayContextImpl();
        String address = addressField.getText().trim();
        String dbName = nameField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (address.isEmpty() || dbName.isEmpty() || login.isEmpty() || password.isEmpty()) {
            displayContext.showError("Ошибка", "Недостаточно данных");
            return;
        }

        try {
            ConnectionManager.getInstance().createConnection(address, dbName, login, password);
            displayContext.showInfo("Успех", "БД подключена");

            if (mainFormController != null) {
                mainFormController.onConnectionEstablished();
            }

            Stage stage = (Stage) addressField.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            displayContext.showError("Ошибка подключения", e.getMessage());
        }
    }
}
