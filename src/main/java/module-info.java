module ru.mirea.dbapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires itextpdf;

    opens ru.mirea.nosenkov.dbapp to javafx.fxml;
    exports ru.mirea.nosenkov.dbapp;
    exports ru.mirea.nosenkov.dbapp.controller;
    opens ru.mirea.nosenkov.dbapp.controller to javafx.fxml;
}