package com.synapse.client.controller;

import com.synapse.client.Task;
import com.synapse.client.TaskStatus;
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
    public ChoiceBox taskStatus;
    @FXML
    public ChoiceBox taskGroup;
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
        taskStatus.setValue(TaskStatus.IN_PROGRESS);
    }

    public void loadTask(Task task){
        if (task != null) {
            taskDelete.setVisible(true);
            taskDelete.setManaged(true);
            taskSave.setVisible(true);
            taskSave.setManaged(true);
            taskCancel.setVisible(false);
            taskCancel.setManaged(false);
            taskCreate.setVisible(false);
            taskCreate.setManaged(false);
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskStatus.setValue(task.getStatus());
            this.task = task;
        } else {
            this.task = null;

            taskTitle.clear();
            taskDescription.clear();
            taskStatus.setValue(TaskStatus.IN_PROGRESS);
            taskDelete.setVisible(false);
            taskDelete.setManaged(false);
            taskSave.setVisible(false);
            taskSave.setManaged(false);
            taskCancel.setVisible(true);
            taskCancel.setManaged(true);
            taskCreate.setVisible(true);
            taskCreate.setManaged(true);
        }
    }
    @FXML
    public void onDeleteTask() {
        TaskStore.getInstance().deleteTask(task);
        closeEditor();
    }
    @FXML
    public void onCreateTask() {
        String title = taskTitle.getText();
        String description = taskDescription.getText();
        TaskStatus status = (TaskStatus) taskStatus.getValue();
        LocalDate deadline = taskDeadline.getValue();
        Task task = new Task(title, description, deadline, status);
        TaskStore.getInstance().addTask(task);
        closeEditor();
    }
    @FXML
    public void onSaveTask() {
        String title = taskTitle.getText();
        String description = taskDescription.getText();
        TaskStatus status = (TaskStatus) taskStatus.getValue();
        LocalDate deadline = taskDeadline.getValue();
        this.task.setTitle(title);
        this.task.setDescription(description);
        this.task.setStatus(status);
        this.task.setDeadline(deadline);
        TaskStore.getInstance().updateTask(task);
        closeEditor();
    }

    public void closeEditor(){
        taskEditor.setVisible(false);
        taskEditor.setManaged(false);
    }
}
