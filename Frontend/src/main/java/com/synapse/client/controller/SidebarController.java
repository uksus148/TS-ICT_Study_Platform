package com.synapse.client.controller;

import com.synapse.client.store.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller responsible for the application's Sidebar (Global Navigation).
 * <p>
 * This controller manages:
 * <ul>
 * <li>Navigation between main views (Today, Upcoming, Groups, Settings).</li>
 * <li>Real-time counters for Today and Upcoming tasks.</li>
 * <li>Collapsing/Expanding the sidebar layout to maximize screen space.</li>
 * </ul>
 */
public class SidebarController {

    private MainController mainController;
    private final double SIDEBAR_WIDTH = 280.0;

    @FXML private BorderPane sidebarRootPane;
    @FXML private VBox sidebar;
    @FXML private Button sidebarOn;

    // Counters
    @FXML private Label upcomingCount;
    @FXML private Label todayCount;

    /**
     * Injects the MainController to allow navigation requests.
     *
     * @param mainController The primary application controller.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the sidebar logic.
     * <p>
     * <b>Key Feature: Reactive Data Binding.</b>
     * The text properties of {@code todayCount} and {@code upcomingCount} are bound
     * directly to the observable properties in {@link TaskStore}.
     * This means the UI updates automatically whenever the task list changes in the background,
     * without needing manual refresh calls.
     */
    @FXML
    public void initialize() {
        todayCount.textProperty().bind(
                TaskStore.getInstance().getTodayTaskCountProperty().asString()
        );

        upcomingCount.textProperty().bind(
                TaskStore.getInstance().getUpcomingTaskCountProperty().asString()
        );
        sidebarRootPane.setPrefWidth(SIDEBAR_WIDTH);
    }

    // ==========================================
    // NAVIGATION HANDLERS
    // ==========================================

    @FXML
    private void onTodayClicked() {
        if (mainController != null) {
            mainController.loadView("TodayView.fxml");
        }
    }

    @FXML
    private void onUpcomingClicked() {
        if (mainController != null) {
            mainController.loadView("UpcomingView.fxml");
        }
    }

    @FXML
    private void onGroupsClicked() {
        if (mainController != null) {
            mainController.showGroupsView();
        }
    }

    @FXML
    public void onNotificationsClick() {
        if (mainController != null) {
            mainController.showNotificationsView();
        }
    }

    @FXML
    private void onSettingsClicked() {
        if (mainController != null) {
            mainController.showProfileView();
        }
    }

    @FXML
    private void onSignOutClicked() {
        if (mainController != null) {
            mainController.logout();
        }
    }

    // ==========================================
    // LAYOUT LOGIC
    // ==========================================

    /**
     * Toggles the visibility of the sidebar.
     * <p>
     * When hidden:
     * 1. The main VBox is hidden and unmanaged (removed from layout calculations).
     * 2. The 'Show Sidebar' button becomes visible.
     * 3. The root pane width shrinks to fit only the button.
     * <p>
     * When shown:
     * 1. The main VBox is restored.
     * 2. The 'Show Sidebar' button is hidden.
     * 3. The root pane width is restored to {@code SIDEBAR_WIDTH}.
     */
    @FXML
    private void onSidebarToggle() {
        boolean isSidebarVisible = sidebar.isVisible();

        // Toggle visibility and layout management
        sidebar.setVisible(!isSidebarVisible);
        sidebar.setManaged(!isSidebarVisible);

        // Toggle the re-open button
        sidebarOn.setVisible(isSidebarVisible);
        sidebarOn.setManaged(isSidebarVisible);

        // Adjust the container width dynamically
        if (isSidebarVisible) {
            // Sidebar is now HIDDEN (isSidebarVisible was true before toggle)
            sidebarRootPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        } else {
            // Sidebar is now SHOWN
            sidebarRootPane.setPrefWidth(SIDEBAR_WIDTH);
        }
    }
}