package ru.mirea.nosenkov.dbapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import ru.mirea.nosenkov.dbapp.impl.DisplayContextImpl;
import ru.mirea.nosenkov.dbapp.logic.DisplayContext;
import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;
import ru.mirea.nosenkov.dbapp.service.TableDataService;
import ru.mirea.nosenkov.dbapp.service.TableRow;
import ru.mirea.nosenkov.dbapp.service.PDFExporter;
import ru.mirea.nosenkov.dbapp.ui.ElementsManager;
import ru.mirea.nosenkov.dbapp.ui.StageCreator;
import ru.mirea.nosenkov.dbapp.ui.TableViewBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button exportButton;

    private DisplayContext displayContext;
    private TableDataService tableDataService;
    private TableViewBuilder tableViewBuilder;
    private ElementsManager elementsManager;

    @FXML
    public void initialize() {
        this.elementsManager = new ElementsManager(connectItem, disconnectItem, tableComboBox, refreshButton, addButton, updateButton, deleteButton, exportButton, dataTableView);
        this.tableDataService = new TableDataService(ConnectionManager.getInstance());
        this.tableViewBuilder = new TableViewBuilder();
        this.displayContext = new DisplayContextImpl();
        setupContextMenu();
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

    @FXML
    public void onUpdateButtonClick() {
        TableRow row = dataTableView.getSelectionModel().getSelectedItem();
        String table = tableComboBox.getValue();

        if (row == null) {
            displayContext.showError("Ошибка", "Не выбрана запись для изменения");
            return;
        }

        try {
            StageCreator.StageWithController<UpdateFormController> result = StageCreator.createUpdateFormStage(table, updateButton.getScene().getWindow());
            var columns = ConnectionManager.getInstance().getJdbcService().getEditableColumns(ConnectionManager.getInstance().getConnection(), table);
            result.controller().setTableData(table, columns, row.getDataAsStrings(), this);
            result.stage().showAndWait();
        } catch (Exception e) {
            displayContext.showError("Ошибка", e.getMessage());
        }
    }

    @FXML
    public void onDeleteButtonClick() {
        TableRow row = dataTableView.getSelectionModel().getSelectedItem();
        String table = tableComboBox.getValue();

        if (row == null) {
            displayContext.showError("Ошибка", "Не выбрана запись для удаления");
            return;
        }

        Optional<ButtonType> result = displayContext.showConfirmation("Подтверждение удаления", "Вы уверены, что хотите удалить выбранную запись?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ConnectionManager.getInstance().getJdbcService().deleteFromDB(ConnectionManager.getInstance().getConnection(), table, row.getDataAsStrings());
                displayContext.showInfo("Успех", "Запись успешно удалена");
                refreshCurrentTable();
            } catch (SQLException e) {
                displayContext.showError("Ошибка", "Не удалось удалить запись: " + e.getMessage());
            }
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

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(_ -> onDeleteButtonClick());

        MenuItem updateItem = new MenuItem("Изменить");
        updateItem.setOnAction(_ -> onUpdateButtonClick());

        contextMenu.getItems().addAll(updateItem, deleteItem);

        dataTableView.setOnMouseClicked(event -> {
            TableRow row = dataTableView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 1 && event.getButton() == MouseButton.SECONDARY && row != null) {
                contextMenu.show(dataTableView, event.getScreenX(), event.getScreenY());
                event.consume();
            } else { contextMenu.hide(); }
        });
        contextMenu.setAutoHide(true);
    }

    @FXML
    public void onExportButtonClick() {
        String table = tableComboBox.getValue();
        if (table == null) {
            displayContext.showError("Ошибка", "Не выбрана таблица");
            return;
        }

        try {
            List<String> columns = ConnectionManager.getInstance().getJdbcService().getTableColumns(ConnectionManager.getInstance().getConnection(), table);

            List<Map<String, String>> rows = new ArrayList<>();
            for (TableRow row : dataTableView.getItems()) {
                rows.add(row.getDataAsStrings());
            }

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выберите папку для сохранения PDF");
            String userHome = System.getProperty("user.home");
            File documentsDir = new File(userHome, "Documents");
            if (documentsDir.exists()) {
                directoryChooser.setInitialDirectory(documentsDir);
            }

            File selectedDirectory = directoryChooser.showDialog(
                    exportButton.getScene().getWindow()
            );

            if (selectedDirectory != null) {
                String fileName = table + ".pdf";
                String filePath = selectedDirectory.getAbsolutePath() + File.separator + fileName;
                PDFExporter.exportToPDF(filePath, table, columns, rows);

                displayContext.showInfo("Успех", "Таблица экспортирована:\n" + filePath);
            }

        } catch (Exception e) {
            displayContext.showError("Ошибка", e.getMessage());
        }
    }
}