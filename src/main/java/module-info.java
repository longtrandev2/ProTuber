    module org.example.protuber {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires java.desktop;

    opens org.example.protuber.controller to javafx.fxml;
    opens org.example.protuber to javafx.fxml;
    opens org.example.protuber.model to com.fasterxml.jackson.databind, javafx.base;

    exports org.example.protuber;
    exports org.example.protuber.controller;
    opens org.example.protuber.service to com.fasterxml.jackson.databind;
    opens org.example.protuber.storage to com.fasterxml.jackson.databind, javafx.fxml;
}