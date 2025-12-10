package com.synapse.client.controller;

import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.model.Task;
import com.synapse.client.service.ApiService;
import com.synapse.client.service.StompClient;
import com.synapse.client.store.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class MainController {
    private final Set<Long> subscribedGroupIds = new HashSet<>();
    private MainController mainController;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private SidebarController sidebarController;
    @FXML
    private TaskEditorController taskEditorController;
    @FXML
    private GroupEditorController groupEditorController;
    @FXML
    private Parent taskEditor;
    @FXML
    private Parent groupEditor;
    @FXML
    private TodayController todayViewController;

    private Parent resourceEditor;
    private ResourceEditorController resourceEditorController;

    @FXML
    public void initialize() {
        if (taskEditor != null) {
            taskEditor.setVisible(false);
            taskEditor.setManaged(false);
        }

        if (todayViewController != null) {
            todayViewController.setMainController(this);
        }
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        } else {
        }
    }

    public void onSuccessfulLogin() {
        setupGlobalGroupSubscriptions();
        subscribeToPersonalUpdates();
        refreshAllData();
        showGroupsView();
    }

    public void refreshAllData() {
        GroupsStore.getInstance().fetchGroupsFromServer();
        TaskStore.getInstance().fetchTasksFromServer();
    }


    public void showGroupsView() {
        loadView("groups/GroupsView.fxml");
    }

    public void showGroupDetails(Group group) {
        Object controller = loadView("groups/group_details.fxml");
        if (controller instanceof GroupDetailsController) {
            ((GroupDetailsController) controller).setGroup(group);
        }
    }

    public void showProfileView() {
        loadView("ProfileView.fxml");
    }

    public Object loadView(String fxmlFileName) {
        try {
            String fxmlPath = "/com/synapse/client/views/" + fxmlFileName;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            mainBorderPane.setCenter(view);

            Object controller = loader.getController();

            if (controller instanceof TodayController) ((TodayController) controller).setMainController(this);
            if (controller instanceof UpcomingController) ((UpcomingController) controller).setMainController(this);
            if (controller instanceof GroupsController) ((GroupsController) controller).setMainController(this);
            if (controller instanceof GroupDetailsController) ((GroupDetailsController) controller).setMainController(this);
            if (controller instanceof ProfileController) ((ProfileController) controller).setMainController(this);

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
            mainBorderPane.setCenter(new Label("Error loading view: " + fxmlFileName + "\nCheck console for details."));
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            mainBorderPane.setCenter(new Label("FXML file not found: " + fxmlFileName));
            return null;
        }
    }

    public void requestEditTaskEditor(Task task) {
        restoreTaskEditorPosition();
        if (taskEditorController != null) {
            taskEditorController.loadTask(task);
        }
        showRightPanel(taskEditor);
    }

    public void requestOpenNewTaskEditor(Long group_id) {
        restoreTaskEditorPosition();
        if (taskEditorController != null) {
            taskEditorController.loadTask(group_id);
        }
        showRightPanel(taskEditor);
    }

    public void requestOpenNewTaskEditor() {
        restoreTaskEditorPosition();
        if (taskEditorController != null) {
            taskEditorController.loadTask((Task) null);
        }
        showRightPanel(taskEditor);
    }

    public void requestOpenNewGroupEditor() {
        try {
            if (groupEditor == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/groups/GroupEditor.fxml"));
                groupEditor = loader.load();
                groupEditorController = loader.getController();
            }
            if (groupEditorController != null) {
                groupEditorController.loadGroup(null);
            }
            mainBorderPane.setRight(groupEditor);
            showRightPanel(groupEditor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestOpenResourceEditor(Group group) {
        try {
            if (resourceEditor == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/ResourceEditor.fxml"));
                resourceEditor = loader.load();
                resourceEditorController = loader.getController();
                resourceEditorController.setMainController(this);
            }
            resourceEditorController.setGroup(group);
            mainBorderPane.setRight(resourceEditor);
            showRightPanel(resourceEditor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreTaskEditorPosition() {
        if (taskEditor != null && mainBorderPane.getRight() != taskEditor) {
            mainBorderPane.setRight(taskEditor);
        }
    }

    private void showRightPanel(Parent panel) {
        if (panel != null) {
            panel.setVisible(true);
            panel.setManaged(true);
        }
    }

    public void closeRightPanel() {
        if (mainBorderPane.getRight() != null) {
            mainBorderPane.getRight().setVisible(false);
            mainBorderPane.getRight().setManaged(false);
        }
    }

    public void logout() {
        ApiService.getInstance().logout().whenComplete((result, error) -> Platform.runLater(this::performLocalLogout));
    }

    private void performLocalLogout() {
        UserSession.getInstance().logout();
        TaskStore.getInstance().clear();
        GroupsStore.getInstance().clear();
        ResourceStore.getInstance().clear();
        MembersStore.getInstance().clear();
        try {
            String fxmlPath = "/com/synapse/client/views/auth/auth_view.fxml";

            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("CRITICAL: Auth View FXML not found!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            URL cssUrl = getClass().getResource("/com/synapse/client/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("style.css not found");
            }
            stage.setTitle("Synapse"); // Application name
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Auth View");
        }
    }

    public void showNotificationsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/synapse/client/views/NotificationsView.fxml"));
            Parent view = loader.load();

            NotificationsController controller = loader.getController();
            controller.setMainController(this);

            mainBorderPane.setCenter(view);

            HBox.setHgrow(view, Priority.ALWAYS);
            VBox.setVgrow(view, Priority.ALWAYS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onNotificationsClick() {
        showNotificationsView();
    }
    public Stage getStage() {
        if (mainBorderPane != null && mainBorderPane.getScene() != null) {
            return (Stage) mainBorderPane.getScene().getWindow();
        }
        return null;
    }

    private void setupGlobalGroupSubscriptions() {
        ObservableList<Group> groups = GroupsStore.getInstance().getGroups();
        groups.addListener((ListChangeListener<Group>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Group group : change.getAddedSubList()) {
                        subscribeToGroupTopic(group.getGroup_id());
                    }
                }
            }
        });

        for (Group group : groups) {
            subscribeToGroupTopic(group.getGroup_id());
        }
    }

    private void subscribeToGroupTopic(Long groupId) {
        if (subscribedGroupIds.contains(groupId)) {
            return;
        }
        String topic = "/topic/group/" + groupId;
        StompClient.getInstance().subscribe(topic, message -> {
            Platform.runLater(() -> {
                if (message.contains("New task created")) {
                    TaskStore.getInstance().fetchTasksByGroupId(groupId);
                    showPopupNotification("Tasks Created", message);
                }
                else if (message.contains("New task updated")) {
                    TaskStore.getInstance().fetchTasksByGroupId(groupId);
                    showPopupNotification("Tasks Updated", message);
                }
                else if (message.contains("Task deleted")) {
                    TaskStore.getInstance().fetchTasksByGroupId(groupId);
                    showPopupNotification("Tasks Deleted", message);
                }

                else if (message.contains("New resource created")) {
                    ResourceStore.getInstance().fetchResourcesForGroup(groupId);
                    showPopupNotification("New Resource", message);
                }
                else if (message.contains("Resource updated")) {
                    ResourceStore.getInstance().fetchResourcesForGroup(groupId);
                    showPopupNotification("Resource updated", message);
                }
                else if (message.contains("Resource deleted")) {
                    ResourceStore.getInstance().fetchResourcesForGroup(groupId);
                    showPopupNotification("Resource deleted", message);
                }

                else if (message.contains("MEMBER") || message.contains("JOIN") || message.contains("LEFT")) {
                    MembersStore.getInstance().fetchMembersForGroup(groupId);
                }
            });
        });

        subscribedGroupIds.add(groupId);
    }

    private void showPopupNotification(String title, String message) {
        Platform.runLater(() -> {
            try {
                Notifications notification = Notifications.create()
                        .title(title)
                        .text(message)
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.BOTTOM_RIGHT);
                if (mainController != null && mainController.getStage() != null) {
                    notification.owner(mainController.getStage());
                }
                notification.showInformation();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    private void subscribeToPersonalUpdates() {
        Long userId = UserSession.getInstance().getUserId();
        if (userId == null) return;

        String topic = "/queue/user/" + userId;

        StompClient.getInstance().subscribe(topic, message -> {
            Platform.runLater(() -> {
                if (message.contains("You have been removed from group")) {
                    RequestStore.getInstance().fetchRequests();
                    showPopupNotification("Remove from group", "You have been removed from group.");
                }
            });
        });
    }
}