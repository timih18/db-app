package ru.mirea.nosenkov.dbapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.mirea.nosenkov.dbapp.controller.ConnectionFormController;

import java.io.IOException;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("MainForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        Image icon = new Image(Launcher.class.getResourceAsStream("icon.png"));
        stage.getIcons().add(icon);
        stage.setTitle("DBApp");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}