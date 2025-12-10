package com.synapse.client.controller;

import com.synapse.client.enums.AcceptStatus;
import com.synapse.client.model.Group;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import com.synapse.client.store.GroupsStore;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.*;

/**
 * Controller class for the Groups Overview view.
 * <p>
 * This controller manages the screen that lists all study groups the user belongs to.
 * It provides functionality to:
 * <ul>
 * <li>Display a scrollable list of groups with details (name, description, owner).</li>
 * <li>Create a new group (via {@link MainController#requestOpenNewGroupEditor()}).</li>
 * <li>Join an existing group using an invitation token.</li>
 * <li>Navigate to the detailed view of a group upon double-clicking.</li>
 * </ul>
 */
public class GroupsController {

    public MainController mainController;

    @FXML
    private Label labelCount;

    @FXML
    private ListView<Group> groupsListView;

    /**
     * Injects the MainController reference.
     * Necessary for navigating to the Group Details view or opening the Group Editor side panel.
     *
     * @param mainController The central controller of the application.
     */
    @FXML
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes the controller class. Automatically called after the FXML file has been loaded.
     * <p>
     * 1. Binds the group count label to the {@link GroupsStore} property for real-time updates.
     * 2. Sets the items of the ListView to the ObservableList from the store.
     * 3. Configures the custom cell factory for rendering group items.
     */
    @FXML
    public void initialize() {
        // Bind the label text to the integer property in the store
        labelCount.textProperty().bind(
                GroupsStore.getInstance().getGroupsCountProperty().asString()
        );

        GroupsStore groupStore = GroupsStore.getInstance();
        ObservableList<Group> groups = groupStore.getGroups();
        groupsListView.setItems(groups);

        setupGroupListView();
    }

    /**
     * Handler for the "Add Group" button.
     * Requests the MainController to open the side panel for creating a new group.
     */
    @FXML
    private void onAddGroupClicked() {
        if (mainController != null) {
            mainController.requestOpenNewGroupEditor();
        } else {
            System.err.println("ERROR: Main Controller is null");
        }
    }

    /**
     * Configures the visual appearance of the list cells.
     * Defines a custom {@link ListCell} that displays the group's name, description,
     * and creator info, along with navigation icons.
     */
    private void setupGroupListView() {
        groupsListView.setCellFactory(param -> new ListCell<>() {

            @Override
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);

                if (empty || group == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Main container for the row
                    HBox rowLayout = new HBox(10);
                    rowLayout.setAlignment(Pos.TOP_LEFT);
                    rowLayout.setPadding(new Insets(2, 10, 2, 10));

                    // Content layout
                    BorderPane centerLayout = new BorderPane();

                    // Title
                    Label titleLabel = new Label(group.getName());
                    titleLabel.getStyleClass().add("list-title");
                    centerLayout.setTop(titleLabel);

                    // Description (optional)
                    if (group.getDescription() != null) {
                        Label descriptionLabel = new Label(group.getDescription());
                        descriptionLabel.getStyleClass().add("list-description");
                        centerLayout.setLeft(descriptionLabel);
                    }

                    // Creator Info
                    if (group.getCreated_by() != null) {
                        Label createdByLabel = new Label("Created by: " + group.getGroupOwner());
                        createdByLabel.getStyleClass().add("list-created-by");
                        FontIcon personFill = new FontIcon(PERSON_FILL);
                        personFill.setIconSize(11);
                        personFill.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                        createdByLabel.setGraphic(personFill);
                        centerLayout.setRight(createdByLabel);
                    }

                    // Spacer to push the arrow to the right
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Navigation Arrow
                    FontIcon arrowRight = new FontIcon(CHEVRON_RIGHT);
                    arrowRight.getStyleClass().add("list-arrow-right");
                    arrowRight.setIconSize(11);
                    arrowRight.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                    arrowRight.setTranslateY(3);

                    rowLayout.getChildren().addAll(centerLayout, spacer, arrowRight);

                    setGraphic(rowLayout);

                    // Interaction: Double-click to open details
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && !isEmpty()) {
                            Group selectedGroup = getItem();
                            if (mainController != null) {
                                mainController.showGroupDetails(selectedGroup);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Handler for the "Join Group" button.
     * <p>
     * 1. Opens a dialog asking for an invitation token.
     * 2. Calls {@link ApiService#acceptInvitation(String)}.
     * 3. Handles server responses (Success, Expired, Error).
     * 4. Refreshes the group list upon success.
     */
    @FXML
    private void onJoinGroupClicked() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Join Group");
        dialog.setHeaderText("Enter Invitation Code");
        dialog.setContentText("Code:");

        dialog.showAndWait().ifPresent(token -> {
            String code = token.trim();
            if (code.isEmpty()) return;

            ApiService.getInstance().acceptInvitation(code)
                    .thenAccept(response -> {
                        if (response == null) {
                            AlertService.showError("Error", "Invalid code or server error.");
                            return;
                        }
                        if (response.status() == AcceptStatus.USED || response.status() == AcceptStatus.ACTIVE) {
                            // Successfully joined
                            AlertService.showInfo("Success", "You have successfully joined the group!");
                            // Refresh UI on JavaFX thread
                            Platform.runLater(this::refreshGroupsList);

                        } else if (response.status() == AcceptStatus.EXPIRED) {
                            AlertService.showError("Failed to join", "This invitation code has expired.");
                        } else {
                            AlertService.showError("Failed to join", "Unknown status: " + response.status());
                        }
                    })
                    .exceptionally(e -> {
                        Platform.runLater(() -> AlertService.showError("Error", "Connection failed: " + e.getMessage()));
                        return null;
                    });
        });
    }

    /**
     * Triggers a fetch from the server to update the local store with the latest list of groups.
     * Called after successfully joining or creating a group.
     */
    private void refreshGroupsList() {
        GroupsStore.getInstance().fetchGroupsFromServer();
    }
}