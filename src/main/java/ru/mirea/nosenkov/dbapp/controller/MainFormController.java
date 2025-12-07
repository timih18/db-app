package ru.mirea.nosenkov.dbapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.mirea.nosenkov.dbapp.impl.DisplayContextImpl;
import ru.mirea.nosenkov.dbapp.logic.DisplayContext;
import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;
import ru.mirea.nosenkov.dbapp.service.TableDataService;
import ru.mirea.nosenkov.dbapp.service.TableRow;
import ru.mirea.nosenkov.dbapp.ui.ElementsManager;
import ru.mirea.nosenkov.dbapp.ui.StageCreator;
import ru.mirea.nosenkov.dbapp.ui.TableViewBuilder;

import java.io.IOException;
import java.sql.SQLException;

public class MainFormController {
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

    private DisplayContext displayContext;
    private TableDataService tableDataService;
    private TableViewBuilder tableViewBuilder;
    private ElementsManager elementsManager;

    @FXML
    public void initialize() {
        this.elementsManager = new ElementsManager(connectItem, disconnectItem, tableComboBox, refreshButton, addButton, dataTableView);
        this.tableDataService = new TableDataService(ConnectionManager.getInstance());
        this.tableViewBuilder = new TableViewBuilder();
        this.displayContext = new DisplayContextImpl();
    }

    @FXML
    protected void onConnectMenuAction() {
        try {
            StageCreator.StageWithController<ConnectionFormController> result = StageCreator.createConnectionFormStage();
            result.controller().setMainFormController(this);
            result.stage().show();
        } catch (IOException e) {
            displayContext.showError("Ошибка", e.getMessage());
        }
    }

    @FXML
    protected void onDisconnectMenuAction() {
        try {
            ConnectionManager.getInstance().closeConnection();
            elementsManager.setDisconnectedState();
            elementsManager.clearData();
            displayContext.showInfo("Успех", "Соединение разорвано");
        } catch (SQLException e) {
            displayContext.showError("Ошибка", "Не удалось разорвать соединение: " + e.getMessage());
        }
    }

    public void onConnectionEstablished() {
        elementsManager.setConnectedState();
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

    @FXML
    public void onAddButtonClick() {
        if (tableComboBox.getValue() == null) {
            displayContext.showError("Ошибка", "Не выбрана таблица");
            return;
        }

        try {
            String table = tableComboBox.getValue();
            StageCreator.StageWithController<AddFormController> result = StageCreator.createAddFromStage(table, addButton.getScene().getWindow());
            var columns = ConnectionManager.getInstance().getJdbcService().getEditableColumns(ConnectionManager.getInstance().getConnection(), table);
            result.controller().setTableData(table, columns, this);
            result.stage().showAndWait();
        } catch (Exception e) {
            displayContext.showError("Ошибка", e.getMessage());
        }
    }

    private void loadTablesList() {
        try {
            var tables = tableDataService.loadTablesList();
            tableComboBox.setItems(tables);
        } catch (SQLException e) {
            displayContext.showError("Ошибка", "Не удалось загрузить список таблиц: " + e.getMessage());
        }
    }

    private void loadTableData(String table) {
        try {
            var tableData = tableDataService.loadTableData(table);
            tableViewBuilder.buildTableView(dataTableView, tableData);
        } catch (SQLException e) {
            displayContext.showError("Ошибка", "Не удалось загрузить данные: " + e.getMessage());
        }
    }

    public void refreshCurrentTable() {
        String table = tableComboBox.getValue();
        if (table != null && !table.isEmpty()) { loadTableData(table); }
        else { displayContext.showError("Ошибка", "Не удалось обновить выбранную таблицу"); }
    }
}