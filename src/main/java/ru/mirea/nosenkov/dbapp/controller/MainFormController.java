package ru.mirea.nosenkov.dbapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.mirea.nosenkov.dbapp.Launcher;

import java.io.IOException;

public class MainFormController {
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu connectionMenu;
    @FXML
    private MenuItem connectItem;


    @FXML
    protected void onConnectMenuAction(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("ConnectionForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        Image icon = new Image(Launcher.class.getResourceAsStream("icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("DBApp");
        stage.setResizable(false);
        stage.show();
    }
}