package com.synapse.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        setupTaskListView();
        loadDummyTasks();
    }
    private void loadDummyTasks() {
        LocalDate today = LocalDate.now();
        ObservableList<Task> taskList = FXCollections.observableArrayList(
                new Task(
                        101,
                        1,
                        "maria",
                        "Finish UI Mockup",
                        "Design all screens for the app",
                        today.plusDays(1),
                        today.minusDays(3)
                ),
                new Task(
                        201,
                        2,
                        "alex",
                        "Setup Backend API",
                        "Create Spring Boot endpoints for tasks",
                        today,
                        today.minusDays(2)
                ),
                new Task(
                        202,
                        2,
                        "alex",
                        "Connect to Database",
                        "Configure JPA and PostgreSQL",
                        null,
                        today.minusDays(2)
                ),
                new Task(
                        203,
                        2,
                        "alex",
                        "Implement Login",
                        "Add JWT authentication",
                        today.plusDays(3),
                        today.minusDays(1)
                ),
                new Task(
                        102,
                        1,
                        "maria",
                        "Test Task Cell",
                        "Make sure the UI looks good",
                        today,
                        today
                ),
                new Task(
                        103,
                        1,
                        "maria",
                        "Implement Dashboard View",
                        "Use JavaFX Charts to show statistics",
                        today.plusWeeks(1),
                        today.plusDays(1)
                ),
                new Task(
                        204,
                        2,
                        "alex",
                        "Write API Documentation",
                        "Use Swagger or SpringDoc",
                        today.minusDays(1),
                        today.minusDays(5)
                )
        );

        taskListView.setItems(taskList);
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
                    checkBox.setSelected(task.getStatus());
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
