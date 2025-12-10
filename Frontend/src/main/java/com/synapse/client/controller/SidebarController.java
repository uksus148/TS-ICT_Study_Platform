package com.synapse.client.controller;

import com.synapse.client.store.TaskStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

public class SidebarController {
    private MainController mainController;
    private final double SIDEBAR_WIDTH = 280.0;
    @FXML
    private BorderPane sidebarRootPane;
    @FXML
    private VBox sidebar;
    @FXML
    private Button sidebarOn;
    @FXML
    private Label upcomingCount;
    @FXML
    private Label todayCount;
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
        } else {
            System.out.println("MainController is null");
        }
    }
    @FXML
    private void onUpcomingClicked() {
        if (mainController != null) {
            mainController.loadView("UpcomingView.fxml");
        } else {
            System.out.println("MainController is null");
        }
    }

    @FXML
    private void onCalendarClicked() {
        System.out.println("Click on 'Calendar'");
    }

    @FXML
    private void onDashboardClicked() {
        System.out.println("Click on 'Dashboard'");
    }

    @FXML
    private void onGroupsClicked() {
        if (mainController != null) {
            mainController.loadView("groups/GroupsView.fxml");
        } else {
            System.out.println("MainController is null");
        }
    }

    @FXML
    private void onSettingsClicked() {
        System.out.println("Click on 'Settings'");
    }

    @FXML
    private void onSignOutClicked() {
        if (mainController != null) {
            mainController.logout();
        } else {
            System.out.println("MainController is null");
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
}
