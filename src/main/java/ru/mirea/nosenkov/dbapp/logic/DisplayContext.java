package ru.mirea.nosenkov.dbapp.logic;

public interface DisplayContext {
    void showError(String title, String message);
    void showInfo(String title, String message);
}
