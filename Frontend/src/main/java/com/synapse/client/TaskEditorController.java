package com.synapse.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TaskEditorController {
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
    public void initialize(){
        taskStatus.getItems().addAll(TaskStatus.values());
        taskStatus.setValue(TaskStatus.OPEN);
    }

    public void loadTask(Task task){
        if (task != null) {
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
        }
    }

    public void closeEditor(){
        taskEditor.setVisible(false);
        taskEditor.setManaged(false);
    }
}
