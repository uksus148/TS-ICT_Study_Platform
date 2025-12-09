package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.model.Task;
import com.synapse.client.TaskStatus;
import com.synapse.client.service.AlertService;
import com.synapse.client.store.GroupsStore;
import com.synapse.client.store.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class TaskEditorController {
    private Task task;

    @FXML public DatePicker taskDeadline;
    @FXML public VBox taskEditor;
    @FXML public Button closeEditor;
    @FXML public TextField taskTitle;
    @FXML public TextArea taskDescription;
    @FXML public ChoiceBox<TaskStatus> taskStatus;
    @FXML public ChoiceBox<Group> taskGroup;

    @FXML public Button taskSave;
    @FXML public Button taskCancel;
    @FXML public Button taskDelete;
    @FXML public Button taskCreate;

    @FXML
    public void initialize(){
        taskStatus.getItems().addAll(TaskStatus.values());
        taskGroup.setItems(GroupsStore.getInstance().getGroups());
        taskStatus.setValue(TaskStatus.IN_PROGRESS);
    }

    public void loadTask(Task task) {
        boolean isEditing = (task != null && task.getTask_id() != null && task.getTask_id() > 0L);

        if (isEditing) {
            this.task = task;
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskStatus.setValue(task.getStatus());

            if (task.getDeadline() != null) {
                taskDeadline.setValue(task.getDeadline().toLocalDate());
            } else {
                taskDeadline.setValue(null);
            }

            selectGroupInChoiceBox(task.getGroup_id());
            setupButtons(true);

        } else {
            this.task = task;

            taskTitle.clear();
            taskDescription.clear();
            taskStatus.setValue(TaskStatus.IN_PROGRESS);
            taskDeadline.setValue(null);

            if (this.task != null && this.task.getGroup_id() != null) {
                selectGroupInChoiceBox(this.task.getGroup_id());
            } else {
                taskGroup.setValue(null);
            }

            setupButtons(false);
        }
    }

    public void loadTask() {
        loadTask((Task) null);
    }

    public void loadTask(Long groupId) {
        Task newTaskWithGroup = new Task();
        newTaskWithGroup.setGroup_id(groupId);
        loadTask(newTaskWithGroup);
    }

    private void selectGroupInChoiceBox(Long groupId) {
        if (groupId == null) return;
        for (Group group : taskGroup.getItems()) {
            if (group.getGroup_id() != null && group.getGroup_id().equals(groupId)) {
                taskGroup.setValue(group);
                return;
            }
        }
    }

    private void updateTaskFromFields() {
        this.task.setTitle(taskTitle.getText());
        this.task.setDescription(taskDescription.getText());
        this.task.setStatus(taskStatus.getValue());
        if (taskDeadline.getValue() != null) {
            this.task.setDeadline(taskDeadline.getValue().atStartOfDay());
        } else {
            this.task.setDeadline(null);
        }

        Group selectedGroup = taskGroup.getValue();
        if (selectedGroup != null) {
            this.task.setGroup_id(selectedGroup.getGroup_id());
        }
    }

    private boolean isFormValid() {
        if (taskTitle.getText().isEmpty()) {
            AlertService.showError("Validation Error", "Title cannot be empty");
            return false;
        }
        if (taskGroup.getValue() == null) {
            AlertService.showError("Validation Error", "Please select a group");
            return false;
        }
        return true;
    }

    public void setupButtons(boolean isEditing) {
        taskDelete.setVisible(isEditing);
        taskDelete.setManaged(isEditing);
        taskSave.setVisible(isEditing);
        taskSave.setManaged(isEditing);

        taskCancel.setVisible(!isEditing);
        taskCancel.setManaged(!isEditing);
        taskCreate.setVisible(!isEditing);
        taskCreate.setManaged(!isEditing);
    }

    @FXML
    public void onDeleteTask() {
        if (task != null) {
            TaskStore.getInstance().deleteTask(task);
        }
        closeEditor();
    }

    @FXML
    public void onCreateTask() {
        if (!isFormValid()) return;

        if (this.task == null) {
            this.task = new Task();
        }

        updateTaskFromFields();
        this.task.setCreated_at(LocalDateTime.now());

        Long currentUserId = UserSession.getInstance().getUserId();
        this.task.setCreated_by(currentUserId != null ? currentUserId : 1L);

        TaskStore.getInstance().addTask(this.task);
        closeEditor();
    }

    @FXML
    public void onSaveTask() {
        if (!isFormValid()) return;

        if (this.task != null) {
            updateTaskFromFields();
            TaskStore.getInstance().updateTask(task);
        }
        closeEditor();
    }

    public void closeEditor(){
        taskEditor.setVisible(false);
        taskEditor.setManaged(false);
    }
}