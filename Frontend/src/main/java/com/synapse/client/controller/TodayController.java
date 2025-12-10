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
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;

import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CALENDAR2_X_FILL;
import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CHEVRON_RIGHT;

/**
 * Controller class responsible for the "Today" view.
 * <p>
 * This view displays tasks that are due today or overdue. It serves as the user's
 * immediate action list.
 * Key responsibilities include:
 * <ul>
 * <li>Displaying a filtered list of tasks for the current day.</li>
 * <li>Showing a real-time count of pending tasks.</li>
 * <li>Providing quick access to edit tasks or mark them as complete.</li>
 * </ul>
 */
public class TodayController {

    public MainController mainController;

    @FXML
    private Label labelCount;

    @FXML
    private ListView<Task> taskListView;

    /**
     * Injects the MainController to allow navigation to the Task Editor side panel.
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
     * 1. Binds the task count label to the {@link TaskStore} property for automatic updates.
     * 2. Retrieves the filtered 'Today' list from the store.
     * 3. Sets up the custom cell factory for rendering tasks.
     */
    @FXML
    public void initialize() {
        // Bind label text to the dynamic property in the store
        labelCount.textProperty().bind(
                TaskStore.getInstance().getTodayTaskCountProperty().asString()
        );

        TaskStore taskStore = TaskStore.getInstance();
        ObservableList<Task> todayTasks = taskStore.getTodayTasks();
        taskListView.setItems(todayTasks);

        setupTaskListView();
    }

    /**
     * Configures the visual appearance of the task list items.
     * Defines a custom {@link ListCell} that includes:
     * <ul>
     * <li>A checkbox for completion status.</li>
     * <li>The task title and deadline.</li>
     * <li>Navigation icons.</li>
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
                    // Row Container
                    HBox rowLayout = new HBox(10);
                    rowLayout.setAlignment(Pos.TOP_LEFT);
                    rowLayout.setPadding(new Insets(2, 10, 2, 10));

                    // Checkbox
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(task.getStatus() == TaskStatus.COMPLETED);
                    checkBox.getStyleClass().add("list-checkbox");
                    // Future improvement: Add listener to checkBox.selectedProperty() to update TaskStatus

                    // Content Layout
                    BorderPane centerLayout = new BorderPane();

                    // Title
                    Label titleLabel = new Label(task.getTitle());
                    titleLabel.getStyleClass().add("list-title");
                    centerLayout.setTop(titleLabel);

                    // Deadline with Icon
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

                    // Interaction: Open Task Editor on click
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