package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.service.AlertService;
import com.synapse.client.store.GroupsStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Controller responsible for the Group Editor side panel.
 * <p>
 * This controller handles both creating new study groups and editing existing ones.
 * It manages form validation, toggling button visibility based on the mode (Create vs Edit),
 * and communicating with the {@link GroupsStore} to persist changes.
 */
public class GroupEditorController {

    private Group group;

    @FXML public VBox groupEditor;
    @FXML public Button closeEditor;
    @FXML public TextField groupName;
    @FXML public TextArea groupDescription;

    // Buttons for different states
    @FXML public Button groupSave;    // Visible only when editing
    @FXML public Button groupCancel;  // Visible only when creating
    @FXML public Button groupDelete;  // Visible only when editing
    @FXML public Button groupCreate;  // Visible only when creating

    /**
     * Initializes the controller. Automatically called by JavaFX.
     */
    @FXML
    public void initialize() {
        // Initialization logic if needed in the future
    }

    /**
     * Prepares the editor for a specific group (Edit Mode) or resets it (Create Mode).
     *
     * @param group The group to edit, or null to create a new one.
     */
    public void loadGroup(Group group) {
        // Determine if we are editing an existing group (must have a valid ID)
        boolean isEditing = (group != null && group.getGroup_id() != null && group.getGroup_id() > 0);

        this.group = group;

        if (isEditing) {
            // Populate fields with existing data
            groupName.setText(group.getName());
            groupDescription.setText(group.getDescription());
            setupButtons(true);
        } else {
            // Clear fields for a fresh start
            groupName.clear();
            groupDescription.clear();
            setupButtons(false);
        }
    }

    /**
     * Updates the local Group model object with data from the UI text fields.
     */
    private void updateGroupFromFields() {
        this.group.setName(groupName.getText());
        this.group.setDescription(groupDescription.getText());
    }

    /**
     * Validates the form inputs before submission.
     * Ensures that essential fields (like Group Name) are not empty.
     *
     * @return true if valid, false otherwise.
     */
    private boolean isFormValid() {
        if (groupName.getText().trim().isEmpty()) {
            AlertService.showError("Validation Error", "Group name cannot be empty");
            return false;
        }
        return true;
    }

    /**
     * Toggles the visibility of buttons based on the current mode.
     *
     * @param isEditing true if editing an existing group, false if creating a new one.
     */
    public void setupButtons(boolean isEditing) {
        groupDelete.setVisible(isEditing);
        groupDelete.setManaged(isEditing);
        groupSave.setVisible(isEditing);
        groupSave.setManaged(isEditing);

        groupCancel.setVisible(!isEditing);
        groupCancel.setManaged(!isEditing);
        groupCreate.setVisible(!isEditing);
        groupCreate.setManaged(!isEditing);
    }

    // ==========================================
    // USER ACTIONS
    // ==========================================

    /**
     * Handles the deletion of the currently loaded group.
     * Calls {@link GroupsStore#deleteGroup} and closes the editor.
     */
    @FXML
    public void onDeleteGroup() {
        if (group != null) {
            GroupsStore.getInstance().deleteGroup(group);
        }
        closeEditor();
    }

    /**
     * Handles the creation of a new group.
     * 1. Validates the form.
     * 2. Creates a new Group object.
     * 3. Assigns the current user as the creator.
     * 4. Persists the group via GroupsStore.
     */
    @FXML
    public void onCreateGroup() {
        if (!isFormValid()) return;

        if (this.group == null) {
            this.group = new Group();
        }

        updateGroupFromFields();

        // Assign current logged-in user as the creator
        Long userId = UserSession.getInstance().getUserId();
        this.group.setCreated_by(userId != null ? userId : 1L);

        GroupsStore.getInstance().addGroup(this.group);
        closeEditor();
    }

    /**
     * Handles saving changes to an existing group.
     * 1. Validates the form.
     * 2. Updates the group object.
     * 3. Persists changes via GroupsStore.
     */
    @FXML
    public void onSaveGroup() {
        if (!isFormValid()) return;

        if (this.group != null) {
            updateGroupFromFields();
            GroupsStore.getInstance().updateGroup(group);
        }
        closeEditor();
    }

    /**
     * Hides and removes the editor panel from the layout.
     * Triggered by Cancel, Save, or Delete actions.
     */
    @FXML
    public void closeEditor() {
        groupEditor.setVisible(false);
        groupEditor.setManaged(false);
    }
}