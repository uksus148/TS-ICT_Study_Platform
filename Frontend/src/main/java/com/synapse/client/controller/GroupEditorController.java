package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.service.AlertService;
import com.synapse.client.store.GroupsStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class GroupEditorController {
    private Group group;

    @FXML public VBox groupEditor;
    @FXML public Button closeEditor;
    @FXML public TextField groupName;
    @FXML public TextArea groupDescription;

    @FXML public Button groupSave;
    @FXML public Button groupCancel;
    @FXML public Button groupDelete;
    @FXML public Button groupCreate;

    @FXML
    public void initialize(){}

    public void loadGroup(Group group) {
        boolean isEditing = (group != null && group.getGroup_id() != null && group.getGroup_id() > 0);

        this.group = group;
        if (isEditing) {
            groupName.setText(group.getName());
            groupDescription.setText(group.getDescription());
            setupButtons(true);
        } else {
            groupName.clear();
            groupDescription.clear();
            setupButtons(false);
        }
    }

    private void updateGroupFromFields() {
        this.group.setName(groupName.getText());
        this.group.setDescription(groupDescription.getText());
    }

    private boolean isFormValid() {
        if (groupName.getText().trim().isEmpty()) {
            AlertService.showError("Validation Error", "Group name cannot be empty");
            return false;
        }
        return true;
    }

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

    @FXML
    public void onDeleteGroup() {
        if (group != null) {
            GroupsStore.getInstance().deleteGroup(group);
        }
        closeEditor();
    }

    @FXML
    public void onCreateGroup() {
        if (!isFormValid()) return;

        if (this.group == null) {
            this.group = new Group();
        }

        updateGroupFromFields();

        Long userId = UserSession.getInstance().getUserId();
        this.group.setCreated_by(userId != null ? userId : 1L);

        GroupsStore.getInstance().addGroup(this.group);
        closeEditor();
    }

    @FXML
    public void onSaveGroup() {
        if (!isFormValid()) return;

        if (this.group != null) {
            updateGroupFromFields();
            GroupsStore.getInstance().updateGroup(group);
        }
        closeEditor();
    }

    public void closeEditor(){
        groupEditor.setVisible(false);
        groupEditor.setManaged(false);
    }
}