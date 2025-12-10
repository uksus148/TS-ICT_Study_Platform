package com.synapse.client.controller;

import com.synapse.client.model.Group;
import com.synapse.client.store.GroupsStore;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import static org.kordamp.ikonli.bootstrapicons.BootstrapIcons.*;

public class GroupsController {
    public MainController mainController;
    @FXML
    private ListView<Group> groupsListView;
    @FXML
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void onGroupList() {

    }
    @FXML
    public void onGroupGrid() {

    }
    @FXML
    private void onAddGroupClicked() {
        if (mainController != null) {
            mainController.requestOpenNewGroupEditor();
        } else {
            System.err.println("ERROR: Main Controller is null");
        }
    }
    @FXML
    public void initialize() {
        GroupsStore groupStore = GroupsStore.getInstance();
        ObservableList<Group> groups = groupStore.getGroups();
        groupsListView.setItems(groups);
        setupGroupListView();
    }
    private void setupGroupListView() {

        groupsListView.setCellFactory(param -> new ListCell<Group>() {

            @Override
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);

                if (empty || group == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox rowLayout = new HBox(10);
                    rowLayout.setAlignment(Pos.TOP_LEFT);
                    rowLayout.setPadding(new Insets(2, 10, 2, 10));

                    BorderPane centerLayout = new BorderPane();
                    Label titleLabel = new Label(group.getName());
                    titleLabel.getStyleClass().add("list-title");
                    centerLayout.setTop(titleLabel);

                    if (group.getDescription() != null) {
                        Label descriptionLabel = new Label(group.getDescription());
                        descriptionLabel.getStyleClass().add("list-description");
                        centerLayout.setLeft(descriptionLabel);
                    }

                    if (group.getCreated_by() != null) {
                        Label createdByLabel = new Label("Created by: "+group.getCreated_by());
                        createdByLabel.getStyleClass().add("list-created-by");
                        FontIcon personFill = new FontIcon(PERSON_FILL);
                        personFill.setIconSize(11);
                        personFill.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                        createdByLabel.setGraphic(personFill);
                        centerLayout.setRight(createdByLabel);
                    }

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    FontIcon arrowRight = new FontIcon(CHEVRON_RIGHT);
                    arrowRight.getStyleClass().add("list-arrow-right");
                    arrowRight.setIconSize(11);
                    arrowRight.setIconColor(Paint.valueOf("rgb(124, 124, 124)"));
                    arrowRight.setTranslateY(3);

                    rowLayout.getChildren().addAll(centerLayout, spacer, arrowRight);

                    setGraphic(rowLayout);

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
}
