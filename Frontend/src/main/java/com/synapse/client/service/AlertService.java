package com.synapse.client.service;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Utility service for displaying modal dialog boxes (Alerts) to the user.
 * <p>
 * This class provides a centralized way to show Error and Information popups.
 * <b>Key Feature: Thread Safety.</b>
 * All methods automatically wrap the UI creation logic in {@link Platform#runLater}.
 * This ensures that alerts can be safely called from background threads (e.g., inside
 * an asynchronous API callback) without causing a "Not on FX Application Thread" exception.
 */
public class AlertService {

    /**
     * Displays a modal Error dialog.
     * Used to inform the user about critical failures, validation errors, or network issues.
     *
     * @param title   The title of the dialog window.
     * @param message The detailed error message to display in the content area.
     */
    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null); // No header, just title and content
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Displays a modal Information dialog.
     * Used for success messages (e.g., "Saved successfully") or general notifications.
     *
     * @param title   The title of the dialog window.
     * @param message The information message to display.
     */
    public static void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}