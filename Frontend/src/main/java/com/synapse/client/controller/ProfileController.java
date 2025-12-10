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

/**
 * Controller class responsible for the User Profile View.
 * <p>
 * This controller allows the currently logged-in user to:
 * <ul>
 * <li>View their current account details (Name, Email).</li>
 * <li>Update their profile information.</li>
 * <li>Change their password (with confirmation validation).</li>
 * <li>Log out of the application safely.</li>
 * </ul>
 */
public class ProfileController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private MainController mainController;

    /**
     * Injects the MainController to allow interaction with the main application window.
     *
     * @param mainController The primary application controller.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the controller class. Automatically called after the FXML file has been loaded.
     * <p>
     * Retrieves the current user from the {@link UserSession} and pre-populates
     * the text fields with the user's existing name and email.
     */
    @FXML
    public void initialize() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
        }
    }

    /**
     * Handles the "Save Changes" action.
     * <p>
     * 1. Validates that the Name and Email fields are not empty.
     * 2. If a new password is provided, checks if it matches the confirmation field.
     * 3. Constructs a User object with updated data.
     * 4. Calls {@link ApiService#updateUser} asynchronously.
     * 5. On success, updates the local session and shows a success alert.
     */
    @FXML
    public void onSave() {
        String newName = nameField.getText();
        String newEmail = emailField.getText();
        String newPass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // Basic Validation
        if (newName.isEmpty() || newEmail.isEmpty()) {
            AlertService.showError("Validation Error", "Name and Email cannot be empty.");
            return;
        }

        // Password Validation (only if user is trying to change it)
        if (!newPass.isEmpty()) {
            if (!newPass.equals(confirmPass)) {
                AlertService.showError("Validation Error", "Passwords do not match.");
                return;
            }
        }

        User currentUser = UserSession.getInstance().getCurrentUser();

        // Prepare the update object
        User updatedUser = new User();
        updatedUser.setUser_id(currentUser.getUser_id());
        updatedUser.setName(newName);
        updatedUser.setEmail(newEmail);

        // Only set password if user entered a new one
        if (!newPass.isEmpty()) {
            updatedUser.setPassword(newPass);
        }

        // Send update request
        ApiService.getInstance().updateUser(updatedUser)
                .thenAccept(savedUser -> {
                    if (savedUser != null) {
                        Platform.runLater(() -> {
                            // Update local session with new data
                            UserSession.getInstance().login(savedUser);
                            AlertService.showInfo("Success", "Profile updated successfully!");
                            // Clear password fields for security
                            passwordField.clear();
                            confirmPasswordField.clear();
                        });
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> AlertService.showError("Error", "Failed to update profile."));
                    return null;
                });
    }

    /**
     * Handles the Logout action.
     * <p>
     * 1. Calls the API logout endpoint.
     * 2. Clears the local UserSession.
     * 3. Redirects the user back to the Sign In screen.
     */
    @FXML
    public void onLogout() {
        ApiService.getInstance().logout().thenRun(() -> Platform.runLater(() -> {
            UserSession.getInstance().logout();
            try {
                // Navigate back to Sign In View
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