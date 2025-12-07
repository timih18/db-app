package ru.mirea.nosenkov.dbapp.ui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.mirea.nosenkov.dbapp.service.TableDataService;
import ru.mirea.nosenkov.dbapp.service.TableRow;

import java.util.List;

public class TableViewBuilder {
    private static final double columnWidth = 150.0;

    public void buildTableView(TableView<TableRow> tableView, TableDataService.TableData tableData) {
        tableView.getColumns().clear();

        List<String> columns = tableData.columns();
        for (String columnName : columns) {
            TableColumn<TableRow, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(cellData -> cellData.getValue().getProperty(columnName));
            column.setPrefWidth(columnWidth);
            tableView.getColumns().add(column);
        }
        tableView.getItems().setAll((tableData.rows()));
    }
}
