package com.synapse.client.controller;

import com.synapse.client.model.Task;
import com.synapse.client.enums.TaskStatus;
import com.synapse.client.store.TaskStore;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;

import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CALENDAR2_X_FILL;
import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CHEVRON_RIGHT;

/**
 * Controller class responsible for the "Upcoming" view.
 * <p>
 * This view displays tasks that are scheduled for future dates (tomorrow and beyond).
 * It helps users plan ahead by providing:
 * <ul>
 * <li>A filtered list of future tasks from the {@link TaskStore}.</li>
 * <li>A real-time counter of upcoming deadlines.</li>
 * <li>Functionality to add new tasks or edit existing ones.</li>
 * </ul>
 */
public class UpcomingController {

    public MainController mainController;

    @FXML
    private Label labelCount;

    @FXML
    private ListView<Task> taskListView;

    /**
     * Injects the MainController to allow navigation and opening side panels.
     *
     * @param mainController The primary application controller.
     */
    @FXML
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the controller.
     * <p>
     * 1. Binds the count label to the {@link TaskStore} property for automatic updates.
     * 2. Retrieves the list of upcoming tasks.
     * 3. Configures the ListView cell factory.
     */
    @FXML
    public void initialize() {
        // Reactive binding: label updates automatically when store changes
        labelCount.textProperty().bind(
                TaskStore.getInstance().getUpcomingTaskCountProperty().asString()
        );

        TaskStore taskStore = TaskStore.getInstance();
        ObservableList<Task> upcomingTasks = taskStore.getUpcomingTasks();
        taskListView.setItems(upcomingTasks);

        setupTaskListView();
    }

    /**
     * Configures the visual appearance of the task list items.
     * Defines a custom {@link ListCell} that renders:
     * <ul>
     * <li>Checkbox for status.</li>
     * <li>Task Title.</li>
     * <li>Formatted Deadline (dd-MM-yy) with a calendar icon.</li>
     * </ul>
     */
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
                    // Row Layout
                    HBox rowLayout = new HBox(10);
                    rowLayout.setAlignment(Pos.TOP_LEFT);
                    rowLayout.setPadding(new Insets(2, 10, 2, 10));

                    // Checkbox
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(task.getStatus() == TaskStatus.COMPLETED);
                    checkBox.getStyleClass().add("list-checkbox");
                    // TODO: Add listener to checkBox.selectedProperty() to update status in Store

                    // Content Layout
                    BorderPane centerLayout = new BorderPane();

                    // Title
                    Label titleLabel = new Label(task.getTitle());
                    titleLabel.getStyleClass().add("list-title");
                    centerLayout.setTop(titleLabel);

                    // Deadline
                    if (task.getDeadline() != null) {
                        Label deadlineLabel = new Label(formatter.format(task.getDeadline()));
                        deadlineLabel.getStyleClass().add("list-deadline");
                        centerLayout.setLeft(deadlineLabel);

                        FontIcon deadlineIcon = new FontIcon(CALENDAR2_X_FILL);
                        deadlineIcon.setIconSize(11);
                        deadlineIcon.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                        deadlineLabel.setGraphic(deadlineIcon);
                    }

                    // Spacer
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Arrow Icon
                    FontIcon arrowRight = new FontIcon(CHEVRON_RIGHT);
                    arrowRight.getStyleClass().add("list-arrow-right");
                    arrowRight.setIconSize(11);
                    arrowRight.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                    arrowRight.setTranslateY(3);

                    rowLayout.getChildren().addAll(checkBox, centerLayout, spacer, arrowRight);

                    setGraphic(rowLayout);

                    // Interaction: Open Task Editor
                    setOnMouseClicked(event -> {
                        if (mainController != null) {
                            mainController.requestEditTaskEditor(task);
                        }
                    });
                }
            }
        });
    }

    /**
     * Handler for the "Add Task" button.
     * Requests the MainController to open the side panel for creating a new task.
     */
    @FXML
    private void onAddTaskClicked() {
        if (mainController != null) {
            mainController.requestOpenNewTaskEditor();
        } else {
            System.err.println("ERROR: Main Controller is null");
        }
    }
}