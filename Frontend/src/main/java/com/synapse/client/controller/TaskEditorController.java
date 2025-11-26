package com.synapse.client.controller;

import com.synapse.client.model.Group;
import com.synapse.client.model.Task;
import com.synapse.client.TaskStatus;
import com.synapse.client.store.GroupsStore;
import com.synapse.client.store.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class TaskEditorController {
    private Task task;
    @FXML
    public DatePicker taskDeadline;
    @FXML
    public VBox taskEditor;
    @FXML
    public Button closeEditor;
    @FXML
    public TextField taskTitle;
    @FXML
    public TextArea taskDescription;
    @FXML
    public ChoiceBox<TaskStatus> taskStatus;
    @FXML
    public ChoiceBox<Group> taskGroup;
    @FXML
    public Button taskSave;
    @FXML
    public Button taskCancel;
    @FXML
    public Button taskDelete;
    @FXML
    public Button taskCreate;

    @FXML
    public void initialize(){
        taskStatus.getItems().addAll(TaskStatus.values());
        taskGroup.setItems(GroupsStore.getInstance().getGroups());
        taskStatus.setValue(TaskStatus.IN_PROGRESS);
    }

    public void loadTask(Task task) {
        if (task != null) {
            System.out.println("Opening task: " + task.getTitle());
            System.out.println("Task ID: " + task.getTask_id());
        } else {
            System.out.println("Opening NEW task (task is null)");
        }
        boolean isEditing = (task != null && task.getTask_id() != null && task.getTask_id() > 0);
        System.out.println("Is Editing Mode: " + isEditing);
        if (isEditing) {
            this.task = task;
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskStatus.setValue(task.getStatus());

            if (task.getDeadline() != null) {
                taskDeadline.setValue(task.getDeadline());
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
        loadTask(null);
    }

    public void loadTask(int groupId) {
        Task newTaskWithGroup = new Task();
        newTaskWithGroup.setGroup_id(groupId);

        loadTask(newTaskWithGroup);
    }

    private void selectGroupInChoiceBox(Integer groupId) {
        if (groupId == null) return;

        // Проходим по всем группам в выпадающем списке
        for (Group group : taskGroup.getItems()) {
            // Используем getGroup_id(), так как именно так называется метод в твоем классе Group
            if (group.getGroup_id() != null && group.getGroup_id().equals(groupId)) {
                taskGroup.setValue(group);
                return;
            }
        }
    }

    private void updateTaskFromFields() {
        this.task.setTitle(taskTitle.getText());
        this.task.setDescription(taskDescription.getText());
        this.task.setStatus((TaskStatus) taskStatus.getValue());
        this.task.setDeadline(taskDeadline.getValue());

        Group selectedGroup = taskGroup.getValue();
        if (selectedGroup != null) {
            this.task.setGroup_id(selectedGroup.getGroup_id());
        }
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
        if (this.task == null) {
            this.task = new Task();
        }

        updateTaskFromFields();
        this.task.setCreated_at(LocalDate.now());
        this.task.setCreated_by("User");

        TaskStore.getInstance().addTask(this.task);
        closeEditor();
    }
    @FXML
    public void onSaveTask() {
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
