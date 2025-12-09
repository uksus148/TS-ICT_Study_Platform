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

public class AuthController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void onGetStartedClick(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_up.fxml");
    }

    @FXML
    public void onSignInClick(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_in.fxml");
    }

    @FXML
    public void onGoToSignUp(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_up.fxml");
    }

    @FXML
    public void onGoToSignIn(ActionEvent event) {
        loadAuthView(event, "/com/synapse/client/views/auth/sign_in.fxml");
    }

    @FXML
    public void onLoginAttempt(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertService.showError("Error", "Please fill fields");
            return;
        }

        ApiService.getInstance().loginUser(email, password)
                .thenAccept(user -> {
                    if (user != null) {
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
                    AlertService.showError("Connection Error", "Could not connect to server");
                    return null;
                });
    }

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

    private void openMainApplication(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/MainView.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.onSuccessfulLogin();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

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