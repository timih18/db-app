package ru.mirea.nosenkov.dbapp.impl;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.mirea.nosenkov.dbapp.Launcher;
import ru.mirea.nosenkov.dbapp.logic.DisplayContext;

public class DisplayContextImpl implements DisplayContext {
    @Override
    public void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Image icon = new Image(Launcher.class.getResourceAsStream("icon.png"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
        alert.showAndWait();
    }

    @Override
    public void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Image icon = new Image(Launcher.class.getResourceAsStream("icon.png"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
        alert.showAndWait();
    }
}
