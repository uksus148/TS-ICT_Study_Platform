package com.synapse.client;

import com.synapse.client.controller.AuthController;
import com.synapse.client.controller.MainController;
import com.synapse.client.service.StompClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        StompClient.getInstance().connect();
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("views/auth/auth_view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1200, 800);

        URL cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("style.css not found");
        }

        stage.setTitle("Synapse"); // Application name
        stage.setScene(scene);
        stage.show();
    }
}
