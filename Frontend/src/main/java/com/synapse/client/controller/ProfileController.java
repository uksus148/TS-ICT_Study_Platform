package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.User;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
        }
    }

    @FXML
    public void onSave() {
        String newName = nameField.getText();
        String newEmail = emailField.getText();
        String newPass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            AlertService.showError("Validation Error", "Name and Email cannot be empty.");
            return;
        }

        if (!newPass.isEmpty()) {
            if (!newPass.equals(confirmPass)) {
                AlertService.showError("Validation Error", "Passwords do not match.");
                return;
            }
        }
        User currentUser = UserSession.getInstance().getCurrentUser();

        User updatedUser = new User();
        updatedUser.setUser_id(currentUser.getUser_id());
        updatedUser.setName(newName);
        updatedUser.setEmail(newEmail);
        if (!newPass.isEmpty()) {
             updatedUser.setPassword(newPass);
        }

        ApiService.getInstance().updateUser(updatedUser)
                .thenAccept(savedUser -> {
                    if (savedUser != null) {
                        Platform.runLater(() -> {
                            UserSession.getInstance().login(savedUser);
                            AlertService.showInfo("Success", "Profile updated successfully!");
                            passwordField.clear();
                            confirmPasswordField.clear();
                        });
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    AlertService.showError("Error", "Failed to update profile.");
                    return null;
                });
    }

    @FXML
    public void onLogout() {
        ApiService.getInstance().logout().thenRun(() -> Platform.runLater(() -> {
            UserSession.getInstance().logout();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/auth/sign_in.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) nameField.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}