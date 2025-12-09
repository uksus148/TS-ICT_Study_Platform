package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.User;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
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
        // Переключение с Входа на Регистрацию
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
            showAlert("Error", "Please fill fields");
            return;
        }

        ApiService.getInstance().loginUser(email, password)
                .thenAccept(user -> {
                    if (user != null) {
                        Platform.runLater(() -> {
                            System.out.println("Logged in as ID: " + user.getUser_id());
                            UserSession.getInstance().login(user);
                            openMainApplication(event);
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Login Failed", "Invalid email or password"));
                    }
                });
    }
    @FXML
    public void onRegisterAttempt(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill all fields");
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);

        ApiService.getInstance().registerUser(newUser)
                .thenAccept(savedUser -> {
                    if (savedUser != null) {
                        Platform.runLater(() -> {
                            System.out.println("Registered User ID: " + savedUser.getUser_id());
                            UserSession.getInstance().login(savedUser);
                            openMainApplication(event);
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Registration failed (Email might be taken)"));
                    }
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> showAlert("Connection Error", "Could not connect to server"));
                    return null;
                });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadAuthView(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMainApplication(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/main-view.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.onSuccessfulLogin();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setTitle("Synapse");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}