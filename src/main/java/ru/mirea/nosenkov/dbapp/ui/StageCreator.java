package ru.mirea.nosenkov.dbapp.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.mirea.nosenkov.dbapp.Launcher;
import ru.mirea.nosenkov.dbapp.controller.ConnectionFormController;

import java.io.IOException;
import java.util.Objects;

public class StageCreator {

    private static final String iconPath = "icon.png";

    public static StageWithController<ConnectionFormController> createConnectionFormStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("ConnectionForm.fxml"));
        Scene scene = new Scene(loader.load());

        ConnectionFormController controller = loader.getController();

        Stage stage =  createStage("DBApp", scene);
        return new StageWithController<>(stage, controller);
    }

    public static <T> StageWithController<T> createAddFromStage(String table, Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("AddForm.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = createStage("Добавить запись - " + table, scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        T controller = loader.getController();
        return new StageWithController<>(stage, controller);
    }

    public static <T> StageWithController<T> createUpdateFormStage(String table, Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("UpdateForm.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = createStage("Изменить запись - " + table, scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        T controller = loader.getController();
        return new StageWithController<>(stage, controller);
    }

    private static Stage createStage(String title, Scene scene) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.setResizable(false);

        Image icon = new Image(Objects.requireNonNull(Launcher.class.getResourceAsStream(iconPath)));
        stage.getIcons().add(icon);

        return stage;
    }

    public record StageWithController<T>(Stage stage, T controller) {}
}
