package com.synapse.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
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
    private Button sidebarOff;
    @FXML
    private Button sidebarOn;
    @FXML
    private TextField searchField;

    @FXML
    private HBox upcomingButton;

    @FXML
    private HBox todayButton;

    @FXML
    private HBox calendarButton;

    @FXML
    private HBox dashboardButton;

    @FXML
    private HBox groupsButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button signOutButton;
    @FXML
    private Button addTaskButton;
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void initialize() {
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
        System.out.println("Click on 'Groups'");
    }

    @FXML
    private void onSettingsClicked() {
        System.out.println("Click on 'Settings'");
    }

    @FXML
    private void onSignOutClicked() {
        System.out.println("Click on 'Sign out'");
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
