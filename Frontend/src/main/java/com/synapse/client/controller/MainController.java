package com.synapse.client.controller;

import com.synapse.client.Task;
import com.synapse.client.store.TaskStore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private SidebarController sidebarController;
    @FXML
    private TaskEditorController taskEditorController;
    @FXML
    private Parent taskEditor;
    @FXML
    private TodayController todayViewController;

    @FXML
    public void initialize() {
        TaskStore.getInstance().fetchTasksFromServer();
        if (taskEditor != null) {
            taskEditor.setVisible(false);
        }
        if (todayViewController != null) {
            todayViewController.setMainController(this);
        }
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        } else {
            System.out.println("SidebarController is null");
        }
    }

    public void requestEditTaskEditor(Task task) {
        if (taskEditorController != null) {
            taskEditorController.loadTask(task);
        }

        if (taskEditor != null) {
            taskEditor.setVisible(true);
            taskEditor.setManaged(true);
        }
    }

    public void requestOpenNewTaskEditor() {
        if (taskEditorController != null) {
            taskEditorController.loadTask(null);
        } else {
            System.err.println("ERROR: Main Controller don't find TaskEditorController");
        }

        if (taskEditor != null) {
            taskEditor.setVisible(true);
            taskEditor.setManaged(true);
        }
    }

    public void loadView(String fxmlFileName) {
        try {
            String fxmlPath = "/com/synapse/client/views/" + fxmlFileName;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            mainBorderPane.setCenter(view);

            Object controller = loader.getController();

            if (controller instanceof TodayController) {
                ((TodayController) controller).setMainController(this);
            }

        } catch (IOException e) {
            e.printStackTrace();
            mainBorderPane.setCenter(new Label("Error while loading view: " + fxmlFileName));
        }
    }
}