package com.synapse.client.controller;

import com.synapse.client.MembershipRole;
import com.synapse.client.TaskStatus;
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

public class GroupDetailsController {

    private MainController mainController;
    private Group currentGroup;

    @FXML private Label groupNameLabel;
    @FXML private ListView<Task> tasksListView;
    @FXML private ListView<Resource> resourcesListView;
    @FXML private ListView<User> membersListView;
    @FXML private PieChart tasksPieChart;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

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

    private void loadData() {
        if (currentGroup == null || currentGroup.getGroup_id() == null) return;
        Long groupId = currentGroup.getGroup_id();

        ObservableList<Task> tasks = TaskStore.getInstance().getTasksByGroupId(groupId);
        tasksListView.setItems(tasks);
        tasks.addListener((ListChangeListener<Task>) change -> updateChart(tasks));
        updateChart(tasks);

        MembersStore.getInstance().fetchMembersForGroup(groupId);
        ObservableList<User> members = MembersStore.getInstance().getMembersByGroupId(groupId);
        membersListView.setItems(members);

        ResourceStore.getInstance().fetchResourcesForGroup(groupId);
        ObservableList<Resource> resources = ResourceStore.getInstance().getResourcesByGroupId(groupId);
        resourcesListView.setItems(resources);
    }

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

    private void openLink(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                    System.out.println("File saved to: " + destFile.getAbsolutePath());
                } else {
                    System.err.println("Source file not found: " + resource.getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
                    Long currentLoggedInUser = UserSession.getInstance().getUserId();

                    boolean amIAdmin = user.getRole().equals(MembershipRole.OWNER);
                    boolean isTargetSelf = user.getUser_id().equals(currentLoggedInUser);

                    if (amIAdmin && !isTargetSelf) {
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

    private void kickUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Kick Member");
        alert.setHeaderText("Remove " + user.getName() + "?");
        alert.setContentText("Are you sure you want to remove this user from the group?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            MembersStore.getInstance().removeMember(currentGroup.getGroup_id(), user);
        }
    }

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

    @FXML
    public void onInviteMember() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Invite Member");
        dialog.setHeaderText("Invite a new member to " + currentGroup.getName());
        dialog.setContentText("Please enter user email:");
        dialog.showAndWait().ifPresent(email -> {
            if (email.trim().isEmpty()) return;
            ApiService.getInstance().inviteUserToGroup(currentGroup.getGroup_id(), email.trim())
                    .thenAccept(v -> {
                        Platform.runLater(() ->
                                AlertService.showInfo("Success", "Invitation sent to " + email));
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        Platform.runLater(() ->
                                AlertService.showError("Error", "User not found or already in group"));
                        return null;
                    });
        });
    }

    @FXML
    public void onUploadFile() {
        if (mainController != null && currentGroup != null) {
            mainController.requestOpenResourceEditor(currentGroup);
        }
    }
}