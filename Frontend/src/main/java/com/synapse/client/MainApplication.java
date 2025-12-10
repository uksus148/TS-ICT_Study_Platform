package com.synapse.client;

import com.synapse.client.service.StompClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * The entry point class for the Synapse JavaFX application.
 * <p>
 * This class extends {@link Application} and manages the initial startup lifecycle.
 * Its primary responsibilities are:
 * <ul>
 * <li>Initializing the global WebSocket connection via {@link StompClient}.</li>
 * <li>Loading the initial Authentication View (Login/Register).</li>
 * <li>Applying global CSS styles to ensure consistent theming.</li>
 * <li>Configuring and displaying the primary Stage (Application Window).</li>
 * </ul>
 */
public class MainApplication extends Application {

    /**
     * The main entry method for the JavaFX application.
     * <p>
     * Called by the JavaFX runtime after the system is ready for the application to begin running.
     *
     * @param stage The primary stage for this application, onto which the application scene is set.
     * @throws IOException If the FXML resource for the authentication view cannot be found or loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Initialize WebSocket Connection early.
        // This ensures the client performs the handshake immediately, so the connection
        // is ready ('warm') by the time the user logs in.
        StompClient.getInstance().connect();

        // 2. Load the initial View (Authentication / Landing Page)
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("views/auth/auth_view.fxml"));
        Parent root = fxmlLoader.load();

        // 3. Set up the Scene (Content container) with default dimensions
        Scene scene = new Scene(root, 1200, 800);

        // 4. Apply Global Styles
        URL cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Warning: style.css not found. Application will run without custom styles.");
        }

        // 5. Configure and Show the Primary Window
        stage.setTitle("Synapse"); // Application name displayed in the title bar
        stage.setScene(scene);
        stage.show();
    }
}