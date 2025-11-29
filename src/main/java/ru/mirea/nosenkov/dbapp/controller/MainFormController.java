package ru.mirea.nosenkov.dbapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.mirea.nosenkov.dbapp.Launcher;
import ru.mirea.nosenkov.dbapp.impl.DisplayContextImpl;
import ru.mirea.nosenkov.dbapp.logic.DisplayContext;
import ru.mirea.nosenkov.dbapp.logic.ConnectionManager;

import java.io.IOException;
import java.sql.SQLException;

public class MainFormController {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu connectionMenu;
    @FXML
    private MenuItem connectItem;
    @FXML
    private MenuItem disconnectItem;

    DisplayContext displayContext = new DisplayContextImpl();


    @FXML
    protected void onConnectMenuAction(ActionEvent event) throws IOException {
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
    protected void onDisconnectMenuAction(ActionEvent event) {
        try {
            ConnectionManager.getInstance().closeConnection();
            disconnectItem.setDisable(true);
            connectItem.setDisable(false);
            displayContext.showInfo("Успех", "Соединение разорвано");
        } catch (SQLException e) {
            e.printStackTrace();
            displayContext.showError("Ошибка", "Не удалось разорвать соединение: " + e.getMessage());
        }
    }

    public void onConnectionEstablished() {
        disconnectItem.setDisable(false);
        connectItem.setDisable(true);
    }
}