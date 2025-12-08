package ru.mirea.nosenkov.dbapp.logic;

import javafx.scene.control.ButtonType;

import java.util.Optional;

public interface DisplayContext {
    void showError(String title, String message);
    void showInfo(String title, String message);
    Optional<ButtonType> showConfirmation(String title, String headerText);
}
