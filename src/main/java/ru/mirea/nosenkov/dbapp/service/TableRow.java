package ru.mirea.nosenkov.dbapp.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class TableRow {
    private final Map<String, StringProperty> data = new HashMap<>();

    public void set(String column, String value) {
        data.put(column, new SimpleStringProperty(value));
    }

    public StringProperty getProperty(String column) {
        return data.getOrDefault(column, new SimpleStringProperty("null"));
    }
}
