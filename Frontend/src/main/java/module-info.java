module com.synapse.client { // Желательно переименовать модуль так, чтобы он совпадал с пакетом
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.bootstrapicons;
    requires eu.hansolo.tilesfx;

    requires javafx.base;
    requires javafx.graphics;
    requires java.desktop;
    opens com.synapse.client to javafx.fxml;
    exports com.synapse.client;

    exports com.synapse.client.controller;
    opens com.synapse.client.controller to javafx.fxml;

    exports com.synapse.client.store;
    opens com.synapse.client.store to javafx.fxml;

    exports com.synapse.client.model;
    opens com.synapse.client.model to javafx.fxml;
}