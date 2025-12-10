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

/**
 * The Root Controller of the application's main interface.
 * <p>
 * This class serves as the central hub for the application after a successful login.
 * It is responsible for:
 * <ul>
 * <li><b>Layout Management:</b> Swapping views in the center area (Navigation) and managing side panels (Editors).</li>
 * <li><b>Global Event Handling:</b> Subscribing to WebSocket topics for all user groups.</li>
 * <li><b>Notification System:</b> Displaying toast notifications for real-time updates.</li>
 * <li><b>Session Management:</b> Handling logout and data refresh.</li>
 * </ul>
 */
public class MainController {

    // Tracks which groups we are already listening to via WebSocket to avoid duplicate subscriptions.
    private final Set<Long> subscribedGroupIds = new HashSet<>();

    @FXML private BorderPane mainBorderPane;

    // Sub-controllers
    @FXML private SidebarController sidebarController;
    @FXML private TaskEditorController taskEditorController;
    @FXML private GroupEditorController groupEditorController;
    @FXML private TodayController todayViewController;

    // Cached Parent views for performance
    @FXML private Parent taskEditor;
    @FXML private Parent groupEditor;
    private Parent resourceEditor;
    private ResourceEditorController resourceEditorController;

    /**
     * Initializes the controller. Called automatically by JavaFX.
     * Hides the editor panels initially and links sub-controllers to this MainController.
     */
    @FXML
    public void initialize() {
        if (taskEditor != null) {
            taskEditor.setVisible(false);
            taskEditor.setManaged(false);
        }

        // Dependency Injection: Pass reference of 'this' to sub-controllers
        if (todayViewController != null) {
            todayViewController.setMainController(this);
        }
        if (sidebarController != null) {
            sidebarController.setMainController(this);
        }
    }

    /**
     * Entry point called immediately after the user logs in.
     * <p>
     * 1. Sets up global WebSocket listeners for all groups.
     * 2. Subscribes to personal user alerts.
     * 3. Fetches initial data from the server.
     * 4. Navigates to the default view (Groups).
     */
    public void onSuccessfulLogin() {
        setupGlobalGroupSubscriptions();
        subscribeToPersonalUpdates();
        refreshAllData();
        showGroupsView();
    }

    /**
     * Triggers a fresh fetch of Groups and Tasks from the backend API.
     * Updates the local Stores, which automatically updates the UI via bindings.
     */
    public void refreshAllData() {
        GroupsStore.getInstance().fetchGroupsFromServer();
        TaskStore.getInstance().fetchTasksFromServer();
    }

    // ==========================================
    // NAVIGATION METHODS (Center View)
    // ==========================================

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

    /**
     * Dynamically loads an FXML file and sets it as the center content of the BorderPane.
     * Also injects the MainController instance into the new view's controller.
     *
     * @param fxmlFileName The relative path to the FXML file within 'views/'.
     * @return The controller of the loaded view, or null if loading failed.
     */
    public Object loadView(String fxmlFileName) {
        try {
            String fxmlPath = "/com/synapse/client/views/" + fxmlFileName;

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            mainBorderPane.setCenter(view);

            Object controller = loader.getController();

            // Inject MainController into the newly loaded controller
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

    // ==========================================
    // EDITOR PANELS (Right View)
    // ==========================================

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

    /**
     * Loads or displays the Group Editor panel.
     * Used for creating new groups or editing existing ones.
     */
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

    // ==========================================
    // AUTH & SYSTEM
    // ==========================================

    /**
     * Logs the user out.
     * 1. Sends logout request to API.
     * 2. Clears local session and stores.
     * 3. Redirects to the Auth View.
     */
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
            stage.setTitle("Synapse");
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

    /**
     * Helper to retrieve the current JavaFX Stage.
     * Used for positioning notifications or dialogs.
     */
    public Stage getStage() {
        if (mainBorderPane != null && mainBorderPane.getScene() != null) {
            return (Stage) mainBorderPane.getScene().getWindow();
        }
        return null;
    }

    // ==========================================
    // REAL-TIME UPDATES (WebSocket)
    // ==========================================

    /**
     * Initializes global subscriptions for all groups the user is a member of.
     * Uses a Listener on the GroupsStore to automatically subscribe to any new groups
     * that are loaded or added later.
     */
    private void setupGlobalGroupSubscriptions() {
        ObservableList<Group> groups = GroupsStore.getInstance().getGroups();

        // Listener for future groups (dynamically added)
        groups.addListener((ListChangeListener<Group>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Group group : change.getAddedSubList()) {
                        subscribeToGroupTopic(group.getGroup_id());
                    }
                }
            }
        });

        // Subscribe to existing groups immediately
        for (Group group : groups) {
            subscribeToGroupTopic(group.getGroup_id());
        }
    }

    /**
     * Subscribes to a specific group's WebSocket topic.
     * Routes incoming messages (Tasks, Resources, Members) to the appropriate Store
     * and triggers a popup notification.
     *
     * @param groupId The ID of the group to listen to.
     */
    private void subscribeToGroupTopic(Long groupId) {
        if (subscribedGroupIds.contains(groupId)) {
            return; // Avoid duplicate subscriptions
        }
        String topic = "/topic/group/" + groupId;

        StompClient.getInstance().subscribe(topic, message -> {
            Platform.runLater(() -> {
                // Determine the type of update based on the message content
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

    /**
     * Displays a non-blocking toast notification in the bottom-right corner.
     * Uses the ControlsFX library.
     */
    private void showPopupNotification(String title, String message) {
        Platform.runLater(() -> {
            try {
                Notifications notification = Notifications.create()
                        .title(title)
                        .text(message)
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.BOTTOM_RIGHT);

                // Attach to main stage if available
                if (this.getStage() != null) {
                    notification.owner(this.getStage());
                }
                notification.showInformation();
            } catch (Exception e) {
                System.err.println("Notification failed: " + e.getMessage());
            }
        });
    }

    /**
     * Subscribes to a personal user queue.
     * Used for events specific to the user, such as being kicked from a group.
     */
    private void subscribeToPersonalUpdates() {
        Long userId = UserSession.getInstance().getUserId();
        if (userId == null) return;

        String topic = "/queue/user/" + userId;

        StompClient.getInstance().subscribe(topic, message -> {
            Platform.runLater(() -> {
                if (message.contains("You have been removed from group")) {
                    RequestStore.getInstance().fetchRequests(); // Refresh requests or groups
                    showPopupNotification("Remove from group", "You have been removed from group.");
                }
            });
        });
    }
}