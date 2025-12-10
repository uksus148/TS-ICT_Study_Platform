package com.synapse.client.controller;

import com.synapse.client.enums.TaskStatus;
import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.model.Resource;
import com.synapse.client.model.Task;
import com.synapse.client.model.User;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import com.synapse.client.store.MembersStore;
import com.synapse.client.store.ResourceStore;
import com.synapse.client.store.TaskStore;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;

import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CALENDAR2_X_FILL;
import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.CHEVRON_RIGHT;

/**
 * Controller class responsible for the Group Details View.
 * <p>
 * This controller acts as the central hub for a specific Study Group. It manages:
 * <ul>
 * <li>Displaying the list of Tasks, Resources, and Members.</li>
 * <li>Visualizing task progress via a PieChart.</li>
 * <li>Handling user actions such as creating tasks, uploading files, and inviting members.</li>
 * <li> enforcing permissions (e.g., only the owner can kick members).</li>
 * </ul>
 */
public class GroupDetailsController {

    private MainController mainController;
    private Group currentGroup;

    @FXML private Label groupNameLabel;
    @FXML private ListView<Task> tasksListView;
    @FXML private ListView<Resource> resourcesListView;
    @FXML private ListView<User> membersListView;
    @FXML private PieChart tasksPieChart;

    /**
     * Injects the MainController to allow navigation back to other views
     * or to request opening side panels (Task Editor, Resource Editor).
     *
     * @param mainController The primary application controller.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the view with data for a specific group.
     * This is the entry point when opening a group. It triggers data fetching
     * from the Stores and configures the UI components.
     *
     * @param group The group entity to display.
     */
    public void setGroup(Group group) {
        this.currentGroup = group;
        if (group != null) {
            groupNameLabel.setText(group.getName());
            loadData();
        }

        setupTaskListView();
        setupResourceListView();
        setupMembersListView();
    }

    /**
     * Fetches data from the Stores (TaskStore, MembersStore, ResourceStore)
     * and binds the ObservableLists to the UI ListViews.
     * <p>
     * Also sets up a listener on the Task list to automatically update
     * the PieChart whenever tasks change (added, removed, or status updated).
     */
    private void loadData() {
        if (currentGroup == null || currentGroup.getGroup_id() == null) return;
        Long groupId = currentGroup.getGroup_id();

        // Load Tasks
        ObservableList<Task> tasks = TaskStore.getInstance().getTasksByGroupId(groupId);
        tasksListView.setItems(tasks);
        // Add listener to update chart dynamically
        tasks.addListener((ListChangeListener<Task>) change -> updateChart(tasks));
        updateChart(tasks);

        // Load Members
        MembersStore.getInstance().fetchMembersForGroup(groupId);
        ObservableList<User> members = MembersStore.getInstance().getMembersByGroupId(groupId);
        membersListView.setItems(members);

        // Load Resources
        ResourceStore.getInstance().fetchResourcesForGroup(groupId);
        ObservableList<Resource> resources = ResourceStore.getInstance().getResourcesByGroupId(groupId);
        resourcesListView.setItems(resources);
    }

    // ==========================================
    // UI SETUP & CELL FACTORIES
    // ==========================================

    /**
     * Configures the visual representation of Tasks in the ListView.
     * Defines a custom CellFactory to show checkboxes, titles, deadlines, and styling.
     */
    private void setupTaskListView() {
        tasksListView.setCellFactory(param -> new ListCell<>() {
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
                    checkBox.getStyleClass().add("list-checkbox");

                    BorderPane centerLayout = new BorderPane();
                    Label titleLabel = new Label(task.getTitle());
                    titleLabel.getStyleClass().add("list-title");
                    centerLayout.setTop(titleLabel);

                    if (task.getDeadline() != null) {
                        Label deadlineLabel = new Label(formatter.format(task.getDeadline()));
                        deadlineLabel.getStyleClass().add("list-deadline");
                        centerLayout.setLeft(deadlineLabel);

                        FontIcon deadlineIcon = new FontIcon(CALENDAR2_X_FILL);
                        deadlineIcon.setIconSize(11);
                        deadlineIcon.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                        deadlineLabel.setGraphic(deadlineIcon);
                    }

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    FontIcon arrowRight = new FontIcon(CHEVRON_RIGHT);
                    arrowRight.getStyleClass().add("list-arrow-right");
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

    /**
     * Configures the visual representation of Resources (Files/Links).
     * Adds buttons for "Open Link" or "Download File" based on resource type.
     */
    private void setupResourceListView() {
        resourcesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Resource resource, boolean empty) {
                super.updateItem(resource, empty);

                if (empty || resource == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);

                    String iconLiteral = "FILE".equals(resource.getType()) ? "bi-file-earmark-text" : "bi-link-45deg";
                    FontIcon icon = new FontIcon(iconLiteral);
                    icon.setIconSize(18);

                    VBox textContainer = new VBox();
                    Label nameLabel = new Label(resource.getName());
                    nameLabel.setStyle("-fx-font-weight: bold;");

                    Long creatorId = resource.getCreated_by();
                    String creatorName = MembersStore.getInstance().getNameById(creatorId);
                    Label pathLabel = new Label("Uploaded by: " + creatorName);
                    pathLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 10px;");

                    textContainer.getChildren().addAll(nameLabel, pathLabel);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button actionBtn = new Button();
                    actionBtn.getStyleClass().add("btn-secondary");

                    if ("LINK".equals(resource.getType())) {
                        actionBtn.setGraphic(new FontIcon("bi-box-arrow-up-right"));
                        actionBtn.setOnAction(e -> openLink(resource.getPath()));
                    } else {
                        actionBtn.setGraphic(new FontIcon("bi-download"));
                        actionBtn.setOnAction(e -> downloadFile(resource));
                    }

                    row.getChildren().addAll(icon, textContainer, spacer, actionBtn);
                    setGraphic(row);
                }
            }
        });
    }

    /**
     * Helper to open a URL in the system's default browser.
     */
    private void openLink(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper to copy a file from the stored path to a user-selected location.
     * Opens a FileChooser dialog.
     */
    private void downloadFile(Resource resource) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Resource");
        fileChooser.setInitialFileName(resource.getName());

        File destFile = fileChooser.showSaveDialog(resourcesListView.getScene().getWindow());

        if (destFile != null) {
            try {
                File sourceFile = new File(resource.getPath());
                if (sourceFile.exists()) {
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    System.err.println("Source file not found: " + resource.getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Configures the Member list.
     * Includes logic to show a "Kick" button only if the current user is the owner
     * of the group, and they are not trying to kick themselves.
     */
    private void setupMembersListView() {
        membersListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(10);
                    row.setAlignment(Pos.CENTER_LEFT);

                    VBox infoBox = new VBox();
                    Label nameLabel = new Label(user.getName());
                    nameLabel.setStyle("-fx-font-weight: bold;");
                    Label emailLabel = new Label(user.getEmail());
                    emailLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                    infoBox.getChildren().addAll(nameLabel, emailLabel);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    row.getChildren().addAll(infoBox, spacer);
                    Long currentLoggedInUserId = UserSession.getInstance().getUserId();

                    boolean amIOwner = currentGroup.getCreated_by() != null
                            && currentGroup.getCreated_by().equals(currentLoggedInUserId);

                    boolean isTargetSelf = user.getUser_id().equals(currentLoggedInUserId);

                    // Show Kick button ONLY if I am owner AND target is not me
                    if (amIOwner && !isTargetSelf) {
                        Button kickButton = new Button();
                        kickButton.setGraphic(new FontIcon("bi-person-dash-fill"));
                        kickButton.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #dc3545;");
                        kickButton.setOnAction(event -> kickUser(user));

                        row.getChildren().add(kickButton);
                    }

                    setGraphic(row);
                }
            }
        });
    }

    /**
     * Shows a confirmation dialog and triggers the removal of a member via MembersStore.
     */
    private void kickUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Kick Member");
        alert.setHeaderText("Remove " + user.getName() + "?");
        alert.setContentText("Are you sure you want to remove this user from the group?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            MembersStore.getInstance().removeMember(currentGroup.getGroup_id(), user);
        }
    }

    /**
     * Calculates task statistics (Completed vs In Progress vs Canceled)
     * and updates the PieChart data.
     *
     * @param tasks The list of tasks to analyze.
     */
    private void updateChart(ObservableList<Task> tasks) {
        int canceledCount = 0;
        int inProgressCount = 0;
        int completedCount = 0;

        for (Task task : tasks) {
            if (task.getStatus() == null) continue;
            switch (task.getStatus()) {
                case CANCELED -> canceledCount++;
                case IN_PROGRESS -> inProgressCount++;
                case COMPLETED -> completedCount++;
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        if (canceledCount > 0) pieChartData.add(new PieChart.Data("Canceled", canceledCount));
        if (inProgressCount > 0) pieChartData.add(new PieChart.Data("In Progress", inProgressCount));
        if (completedCount > 0) pieChartData.add(new PieChart.Data("Completed", completedCount));

        tasksPieChart.setData(pieChartData);
        tasksPieChart.setTitle("Status Overview");
        tasksPieChart.setLegendVisible(false);
    }

    // ==========================================
    // USER ACTIONS (@FXML)
    // ==========================================

    @FXML
    public void onBack() {
        if (mainController != null) {
            mainController.showGroupsView();
        }
    }

    @FXML
    public void onAddTask() {
        if (mainController != null) {
            mainController.requestOpenNewTaskEditor(this.currentGroup.getGroup_id());
        }
    }

    /**
     * Calls the API to generate an invitation token, then displays it in a dialog.
     */
    @FXML
    public void onInviteMember() {
        if (currentGroup == null) return;
        ApiService.getInstance().createInvitation(currentGroup.getGroup_id())
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response != null && response.token() != null) {
                        showInviteDialog(response.token());
                    } else {
                        AlertService.showError("Error", "Failed to generate invitation.");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> AlertService.showError("Error", "Connection error: " + e.getMessage()));
                    return null;
                });
    }

    private void showInviteDialog(String token) {
        TextInputDialog dialog = new TextInputDialog(token);
        dialog.setTitle("Invitation Code");
        dialog.setHeaderText("Share this code with others to join:");
        dialog.setContentText("Code:");
        dialog.setGraphic(null);
        dialog.showAndWait();
    }

    @FXML
    public void onUploadFile() {
        if (mainController != null && currentGroup != null) {
            mainController.requestOpenResourceEditor(currentGroup);
        }
    }
}