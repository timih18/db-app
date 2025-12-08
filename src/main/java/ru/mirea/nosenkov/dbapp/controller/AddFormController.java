package ru.mirea.nosenkov.dbapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.mirea.nosenkov.dbapp.impl.DisplayContextImpl;
import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;
import ru.mirea.nosenkov.dbapp.logic.DatabaseService;
import ru.mirea.nosenkov.dbapp.logic.DisplayContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddFormController {
    @FXML
    private VBox fieldsVBox;
    @FXML
    private Button saveButton;

    private String tableName;
    private List<String> columns;
    private MainFormController mainFormController;
    private Connection connection;
    private DatabaseService jdbcService;
    private final DisplayContext displayContext = new DisplayContextImpl();

    public void setTableData(String tableName, List<String> columns, MainFormController mainFormController) {
        this.tableName = tableName;
        this.columns = columns;
        this.mainFormController = mainFormController;
        createFields();
    }

    @FXML
    public void initialize() {
        connection = ConnectionManager.getInstance().getConnection();
        jdbcService = ConnectionManager.getInstance().getJdbcService();
    }

    private void createFields() {
        fieldsVBox.getChildren().clear();
        for (String column : columns) {
            VBox fieldContainer = new VBox(5);
            fieldContainer.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-padding: 10;");

            Label label = new Label(column);
            label.setStyle("-fx-font-weight: bold;");

            TextField textField = new TextField();
            textField.setPromptText("Введите значение для " + column);
            textField.setPrefHeight(30);

            fieldContainer.getChildren().addAll(label, textField);
            fieldsVBox.getChildren().add(fieldContainer);
        }
    }

    @FXML
    protected void onSaveButtonClick() {
        try {
            Map<String, String> columnValues = new HashMap<>();

            for (int i = 0; i < columns.size(); ++i) {
                VBox fieldContainer = (VBox) fieldsVBox.getChildren().get(i);
                String columnName = columns.get(i);
                String value = ((TextField) fieldContainer.getChildren().get(1)).getText().trim();

                if (value.isEmpty()) {
                    displayContext.showError("Ошибка", "Все поля должны быть заполнены");
                    return;
                }

                columnValues.put(columnName, value);
            }

            int count = jdbcService.addToDB(connection, tableName, columnValues);
            displayContext.showInfo("Успех", "Добавлено строк: " + count);

            if (mainFormController != null) {
                mainFormController.refreshCurrentTable();
            }

            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            displayContext.showError("Ошибка", "Не удалось добавить запись: " + e.getMessage());
        }
    }
}
