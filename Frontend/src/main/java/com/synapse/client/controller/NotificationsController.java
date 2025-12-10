package com.synapse.client.controller;

import com.synapse.client.model.GroupRequest;
import com.synapse.client.store.RequestStore;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class NotificationsController {

    private MainController mainController;

    @FXML
    private ListView<GroupRequest> requestsListView;
    @FXML
    private Label emptyLabel;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        requestsListView.setItems(RequestStore.getInstance().getRequests());
        setupListView();
        RequestStore.getInstance().fetchRequests();
        RequestStore.getInstance().getRequests().addListener((javafx.beans.Observable o) -> {
            updateEmptyState();
        });
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = RequestStore.getInstance().getRequests().isEmpty();
        emptyLabel.setVisible(isEmpty);
        requestsListView.setVisible(!isEmpty);
    }

    private void setupListView() {
        requestsListView.setCellFactory(param -> new ListCell<GroupRequest>() {
            @Override
            protected void updateItem(GroupRequest req, boolean empty) {
                super.updateItem(req, empty);

                if (empty || req == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(15);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 5;");

                    String iconCode = "INVITE".equals(req.getType()) ? "bi-envelope" : "bi-person-plus";
                    FontIcon typeIcon = new FontIcon(iconCode);
                    typeIcon.setIconSize(24);
                    typeIcon.setIconColor(javafx.scene.paint.Color.web("#6c757d"));

                    VBox textBox = new VBox(2);
                    Label mainText = new Label();
                    Label subText = new Label();

                    if ("INVITE".equals(req.getType())) {
                        mainText.setText("Invitation to group: " + req.getGroupName());
                        subText.setText("From: " + req.getSenderName());
                    } else {
                        mainText.setText("Request to join: " + req.getGroupName());
                        subText.setText("User: " + req.getSenderName());
                    }

                    mainText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    subText.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");
                    textBox.getChildren().addAll(mainText, subText);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button acceptBtn = new Button("Accept");
                    acceptBtn.getStyleClass().add("btn-success");
                    acceptBtn.setGraphic(new FontIcon("bi-check-lg"));
                    acceptBtn.setOnAction(e -> RequestStore.getInstance().acceptRequest(req));

                    Button rejectBtn = new Button("Decline");
                    rejectBtn.getStyleClass().add("btn-danger");
                    rejectBtn.setGraphic(new FontIcon("bi-x-lg"));
                    rejectBtn.setOnAction(e -> RequestStore.getInstance().rejectRequest(req));

                    row.getChildren().addAll(typeIcon, textBox, spacer, acceptBtn, rejectBtn);
                    setGraphic(row);
                }
            }
        });
    }

    @FXML
    public void onBack() {
        if (mainController != null) {
            mainController.showGroupsView();
        }
    }
}