package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.model.Resource;
import com.synapse.client.store.ResourceStore;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;

/**
 * Controller responsible for the Resource Editor side panel.
 * <p>
 * This interface allows users to share learning materials with a group.
 * Users can either:
 * <ul>
 * <li>Select a local file from their computer (Type: FILE).</li>
 * <li>Paste a URL to an external website (Type: LINK).</li>
 * </ul>
 */
public class ResourceEditorController {

    private MainController mainController;
    private Group currentGroup;

    @FXML private TextField nameField;
    @FXML private TextField pathField;

    /**
     * Injects the MainController to allow closing this side panel.
     *
     * @param mainController The primary application controller.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the editor for a specific group.
     * Clears previous input fields to ensure a fresh state.
     *
     * @param group The group to which the resource will be added.
     */
    public void setGroup(Group group) {
        this.currentGroup = group;
        nameField.clear();
        pathField.clear();
    }

    /**
     * Opens a system-native File Chooser dialog.
     * <p>
     * If a file is selected:
     * 1. The absolute path is populated in the path field.
     * 2. The file name is automatically set as the resource name (only if the name field was empty).
     */
    @FXML
    public void onChooseFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(pathField.getScene().getWindow());
        if (file != null) {
            pathField.setText(file.getAbsolutePath());
            if (nameField.getText().isEmpty()) {
                nameField.setText(file.getName());
            }
        }
    }

    /**
     * Handles the creation of the resource.
     * <p>
     * Logic:
     * 1. Validates that fields are not empty.
     * 2. Detects the resource type based on the path string:
     * - Starts with "http://" or "https://" -> Treated as a LINK.
     * - Otherwise -> Treated as a FILE.
     * 3. Creates the Resource object with the current user as the creator.
     * 4. Persists the resource via {@link ResourceStore}.
     * 5. Closes the editor panel.
     */
    @FXML
    public void onSave() {
        if (currentGroup == null) {
            System.err.println("Error: No group selected");
            return;
        }

        String name = nameField.getText();
        String path = pathField.getText();

        // Basic Validation
        if (name.isEmpty() || path.isEmpty()) {
            System.out.println("Validation Error: Name or Path is empty");
            return;
        }

        Resource resource = new Resource();
        resource.setGroup_id(currentGroup.getGroup_id());
        resource.setPath(path);
        resource.setName(name);

        // Determine Resource Type logic
        if (path.startsWith("http://") || path.startsWith("https://")) {
            resource.setType("LINK");
            // Name is already set above, but ensuring it here again
            resource.setName(name);
        } else {
            resource.setType("FILE");
        }

        resource.setCreated_by(UserSession.getInstance().getUserId());

        // Save to store
        ResourceStore.getInstance().addResource(resource);
        onClose();
    }

    /**
     * Closes the Resource Editor side panel.
     */
    @FXML
    public void onClose() {
        if (mainController != null) {
            mainController.closeRightPanel();
        }
    }
}