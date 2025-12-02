package ru.mirea.nosenkov.dbapp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.mirea.nosenkov.dbapp.Launcher;
import ru.mirea.nosenkov.dbapp.impl.DisplayContextImpl;
import ru.mirea.nosenkov.dbapp.logic.DatabaseService;
import ru.mirea.nosenkov.dbapp.logic.DisplayContext;
import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;
import ru.mirea.nosenkov.dbapp.logic.TableRow;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MainFormController {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu connectionMenu;
    @FXML
    private MenuItem connectItem;
    @FXML
    private MenuItem disconnectItem;
    @FXML
    private ComboBox<String> tableComboBox;
    @FXML
    private Button refreshButton;
    @FXML
    private TableView<TableRow> dataTableView;
    @FXML
    private Button addButton;

    private final DisplayContext displayContext = new DisplayContextImpl();
    private ObservableList<TableRow> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        dataTableView.setItems(tableData);
    }

    @FXML
    protected void onConnectMenuAction() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("ConnectionForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        ConnectionFormController controller = fxmlLoader.getController();
        controller.setMainFormController(this);

        stage.setScene(scene);
        Image icon = new Image(Launcher.class.getResourceAsStream("icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("DBApp");
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    protected void onDisconnectMenuAction() {
        try {
            ConnectionManager.getInstance().closeConnection();
            disconnectItem.setDisable(true);
            connectItem.setDisable(false);
            tableComboBox.setDisable(true);
            refreshButton.setDisable(true);
            addButton.setDisable(true);
            tableComboBox.getItems().clear();
            dataTableView.getColumns().clear();
            tableData.clear();
            displayContext.showInfo("Успех", "Соединение разорвано");
        } catch (SQLException e) {
            e.printStackTrace();
            displayContext.showError("Ошибка", "Не удалось разорвать соединение: " + e.getMessage());
        }
    }

    public void onConnectionEstablished() {
        disconnectItem.setDisable(false);
        connectItem.setDisable(true);
        tableComboBox.setDisable(false);
        refreshButton.setDisable(false);
        addButton.setDisable(false);
        loadTablesList();
    }

    @FXML
    public void onTableSelected() {
        String table = tableComboBox.getValue();
        if (table != null && !table.isEmpty()) {
            loadTableData(table);
        }
    }

    @FXML
    public void onRefreshButtonClick() { refreshCurrentTable(); }

    private void loadTablesList() {
        try {
            Connection connection = ConnectionManager.getInstance().getConnection();
            DatabaseService jdbcService = ConnectionManager.getInstance().getJdbcService();
            ObservableList<String> tables = FXCollections.observableArrayList(jdbcService.getTableNames(connection));
            tableComboBox.setItems(tables);
        } catch (SQLException e) {
            e.printStackTrace();
            displayContext.showError("Ошибка", "Не удалось загрузить список таблиц: " + e.getMessage());
        }
    }

    private void loadTableData(String table) {
        try {
            Connection connection = ConnectionManager.getInstance().getConnection();
            DatabaseService jdbcService = ConnectionManager.getInstance().getJdbcService();
            List<String> columns = jdbcService.getTableColumns(connection, table);
            List<List<Object>> data = jdbcService.getTableData(connection, table);

            dataTableView.getColumns().clear();
            tableData.clear();

            for (String columnName : columns) {
                TableColumn<TableRow, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(cellData -> cellData.getValue().getProperty(columnName));
                column.setPrefWidth(150);
                dataTableView.getColumns().add(column);
            }

            for (List<Object> rowData : data) {
                TableRow row = new TableRow();
                for (int i = 0; i < columns.size(); i++) {
                    row.set(columns.get(i), rowData.get(i).toString());
                }
                tableData.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displayContext.showError("Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
    }

    public void refreshCurrentTable() {
        String table = tableComboBox.getValue();
        if (table != null && !table.isEmpty()) { loadTableData(table); }
        else { displayContext.showError("Ошибка", "Не удалось обновить выбранную таблицу"); }
    }

    public void onAddButtonClick() {
        if (tableComboBox.getValue() == null) {
            displayContext.showError("Ошибка", "Не выбрана таблица");
            return;
        }

        try {
            Connection connection = ConnectionManager.getInstance().getConnection();
            DatabaseService jdbcService = ConnectionManager.getInstance().getJdbcService();

            List<String> columns = jdbcService.getEditableColumns(connection, tableComboBox.getValue());
            Stage stage = new Stage();
            Image icon = new Image(Launcher.class.getResourceAsStream("icon.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Добавить запись - " + tableComboBox.getValue());
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(addButton.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("AddForm.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);

            AddFormController addFormController = fxmlLoader.getController();
            addFormController.setTableData(tableComboBox.getValue(), columns, this);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            displayContext.showError("Ошибка", e.getMessage());
        }
    }
}