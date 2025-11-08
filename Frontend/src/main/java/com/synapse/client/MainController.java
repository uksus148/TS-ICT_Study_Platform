package com.synapse.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private SidebarController sidebarController;

    @FXML
    public void initialize() {
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        } else {
            System.out.println("SidebarController is null");
        }
    }

    public void loadView(String fxmlFileName) {
        try {
            String fxmlPath = "/com/synapse/client/views/" + fxmlFileName;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            mainBorderPane.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            mainBorderPane.setCenter(new Label("Error while loading view: " + fxmlFileName));
        }
    }
}