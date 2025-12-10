package com.synapse.client.controller;

import com.synapse.client.store.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SidebarController {

    private MainController mainController;
    private final double SIDEBAR_WIDTH = 280.0;

    @FXML private BorderPane sidebarRootPane;
    @FXML private VBox sidebar;
    @FXML private Button sidebarOn;
    @FXML private Label upcomingCount;
    @FXML private Label todayCount;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

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

    @FXML
    private void onSidebarToggle() {
        boolean isSidebarVisible = sidebar.isVisible();

        sidebar.setVisible(!isSidebarVisible);
        sidebar.setManaged(!isSidebarVisible);

        sidebarOn.setVisible(isSidebarVisible);
        sidebarOn.setManaged(isSidebarVisible);

        if (isSidebarVisible) {
            sidebarRootPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        } else {
            sidebarRootPane.setPrefWidth(SIDEBAR_WIDTH);
        }
    }
    @FXML
    public void onNotificationsClick() {
        if (mainController != null) {
            mainController.showNotificationsView();
        }
    }
}