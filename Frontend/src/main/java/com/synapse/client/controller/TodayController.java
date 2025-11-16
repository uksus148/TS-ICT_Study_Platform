package com.synapse.client.controller;

import com.synapse.client.Task;
import com.synapse.client.TaskStatus;
import com.synapse.client.store.TaskStore;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CALENDAR2_X_FILL;
import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CHEVRON_RIGHT;

public class TodayController {
    public MainController mainController;
    @FXML
    private ListView<Task> taskListView;

    @FXML
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void initialize() {
        TaskStore taskStore = TaskStore.getInstance();
        ObservableList<Task> todayTasks = taskStore.getTodayTasks();
        taskListView.setItems(todayTasks);
        setupTaskListView();
    }

    private void setupTaskListView() {

        taskListView.setCellFactory(param -> new ListCell<Task>() {

            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox rowLayout = new HBox(10);
                    rowLayout.setAlignment(Pos.TOP_LEFT);
                    rowLayout.setPadding(new Insets(2, 10, 2, 10));

                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(task.getStatus() == TaskStatus.COMPLETED);
                    checkBox.getStyleClass().add("task-checkbox");
                    // TODO: Add listener to checkBox.selectedProperty()

                    BorderPane centerLayout = new BorderPane();
                    Label titleLabel = new Label(task.getTitle());
                    titleLabel.getStyleClass().add("task-title");
                    centerLayout.setTop(titleLabel);

                    if (task.getDeadline() != null) {
                        Label deadlineLabel = new Label(formatter.format(task.getDeadline()));
                        deadlineLabel.getStyleClass().add("task-deadline");
                        centerLayout.setLeft(deadlineLabel);
                        FontIcon deadlineIcon = new FontIcon(CALENDAR2_X_FILL);
                        deadlineIcon.setIconSize(11);
                        deadlineIcon.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                        deadlineLabel.setGraphic(deadlineIcon);
                    }

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    FontIcon arrowRight = new FontIcon(CHEVRON_RIGHT);
                    arrowRight.getStyleClass().add("task-arrow-right");
                    arrowRight.setIconSize(11);
                    arrowRight.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                    arrowRight.setTranslateY(3);

                    rowLayout.getChildren().addAll(checkBox, centerLayout, spacer, arrowRight);

                    setGraphic(rowLayout);

                    setOnMouseClicked(event -> {
                        if (mainController != null) {
                            mainController.requestEditTaskEditor(task);
                        }
                    });
                }
            }
        });
    }
    @FXML
    private void onAddTaskClicked() {
        if (mainController != null) {
            mainController.requestOpenNewTaskEditor();
        } else {
            System.err.println("ERROR: Main Controller is null");
        }
    }
}
