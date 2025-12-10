package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Controller responsible for the Authentication flow (Landing, Sign In, Sign Up).
 * Handles user input validation, API communication for login/register,
 * and scene transitions upon successful authentication.
 */
public class AuthController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    // ==========================================
    // NAVIGATION METHODS
    // ==========================================

    /**
     * Triggered by the "Get Started" button on the Landing page.
     * Navigates the user to the Registration (Sign Up) view.
     */
    @FXML
    public void onGetStartedClick(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_up.fxml");
    }

    /**
     * Triggered by the "Sign In" link on the Landing page.
     * Navigates the user to the Login view.
     */
    @FXML
    public void onSignInClick(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_in.fxml");
    }

    /**
     * Switches the view from Login to Registration.
     */
    @FXML
    public void onGoToSignUp(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_up.fxml");
    }

    /**
     * Switches the view from Registration to Login.
     */
    @FXML
    public void onGoToSignIn(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_in.fxml");
    }

    // ==========================================
    // BUSINESS LOGIC
    // ==========================================

    /**
     * Handles the login process.
     * 1. Validates that email and password fields are not empty.
     * 2. Calls {@link ApiService#loginUser} asynchronously.
     * 3. On success: Updates {@link UserSession} and transitions to Main Application.
     * 4. On failure: Shows an error alert.
     */
    @FXML
    public void onLoginAttempt(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Simple validation
        if (email.isEmpty() || password.isEmpty()) {
            AlertService.showError("Error", "Please fill fields");
            return;
        }

        ApiService.getInstance().loginUser(email, password)
                .thenAccept(user -> {
                    if (user != null) {
                        // Must run UI updates on the JavaFX Application Thread
                        Platform.runLater(() -> {
                            UserSession.getInstance().login(user);
                            openMainApplication(event);
                        });
                    } else {
                        AlertService.showError("Login Failed", "Invalid credentials or server error");
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    // Handling connection errors (e.g., 404, 500, or Network down)
                    AlertService.showError("Connection Error", "Could not connect to server");
                    return null;
                });
    }

    /**
     * Handles the registration process.
     * 1. Validates all inputs.
     * 2. Calls {@link ApiService#registerUser} asynchronously.
     * 3. On success: Automatically logs the user in and opens the Main Application.
     */
    @FXML
    public void onRegisterAttempt(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            AlertService.showError("Error", "Please fill all fields");
            return;
        }

        ApiService.getInstance().registerUser(username, email, password)
                .thenAccept(registeredUser -> {
                    if (registeredUser != null) {
                        Platform.runLater(() -> {
                            UserSession.getInstance().login(registeredUser);
                            openMainApplication(event);
                        });
                    } else {
                        AlertService.showError("Registration Error", "Server returned empty response");
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    AlertService.showError("Registration Failed", "Email might be taken or server error");
                    return null;
                });
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Helper method to switch between different FXML views within the Auth stage
     * (e.g., swapping Login form with Sign Up form).
     *
     * @param event The ActionEvent to retrieve the current Stage.
     * @param fxmlPath The resource path to the new FXML file.
     */
    private void loadAuthView(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
        }
    }

    /**
     * Transitions the user from the Auth environment to the Main Application environment.
     * Loads the main layout, applies global CSS, and initializes the MainController.
     *
     * @param event The ActionEvent to retrieve the current Stage.
     */
    private void openMainApplication(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/MainView.fxml"));
            Parent root = loader.load();

            // Notify MainController that login was successful (triggers data loading)
            MainController mainController = loader.getController();
            mainController.onSuccessfulLogin();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set up the main scene with larger dimensions
            Scene scene = new Scene(root, 1200, 800);
            URL cssUrl = getClass().getResource("/com/synapse/client/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("style.css not found! Check file name.");
            }

            stage.setTitle("Synapse");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}