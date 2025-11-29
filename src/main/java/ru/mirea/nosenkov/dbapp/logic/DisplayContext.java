package ru.mirea.nosenkov.dbapp.logic;

public interface DisplayContext {
    public void showError(String title, String message);
    public void showInfo(String title, String message);
}
