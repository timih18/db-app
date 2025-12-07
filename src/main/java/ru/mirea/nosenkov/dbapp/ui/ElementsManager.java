package ru.mirea.nosenkov.dbapp.ui;

import javafx.scene.control.*;
import ru.mirea.nosenkov.dbapp.service.TableRow;

public class ElementsManager {
    private final MenuItem connectItem;
    private final MenuItem disconnectItem;
    private final ComboBox<String> tableComboBox;
    private final Button refreshButton;
    private final Button addButton;
    private final TableView<TableRow> dataTableView;

    public ElementsManager(MenuItem connectItem, MenuItem disconnectItem, ComboBox<String> tableComboBox, Button updateButton, Button addButton, TableView<TableRow> dataTableView) {
        this.connectItem = connectItem;
        this.disconnectItem = disconnectItem;
        this.tableComboBox = tableComboBox;
        this.refreshButton = updateButton;
        this.addButton = addButton;
        this.dataTableView = dataTableView;
    }

    public void setConnectedState() {
        disconnectItem.setDisable(false);
        connectItem.setDisable(true);
        tableComboBox.setDisable(false);
        refreshButton.setDisable(false);
        addButton.setDisable(false);
    }

    public void setDisconnectedState() {
        disconnectItem.setDisable(true);
        connectItem.setDisable(false);
        tableComboBox.setDisable(true);
        refreshButton.setDisable(true);
        addButton.setDisable(true);
    }

    public void clearData() {
        tableComboBox.getItems().clear();
        dataTableView.getColumns().clear();
    }
}
