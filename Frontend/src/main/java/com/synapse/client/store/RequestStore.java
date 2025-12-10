package com.synapse.client.store;

import com.synapse.client.model.GroupRequest;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RequestStore {
    private static RequestStore instance;
    private final ObservableList<GroupRequest> requests = FXCollections.observableArrayList();

    private RequestStore() {}

    public static synchronized RequestStore getInstance() {
        if (instance == null) instance = new RequestStore();
        return instance;
    }

    public ObservableList<GroupRequest> getRequests() {
        return requests;
    }

    public void fetchRequests() {
        ApiService.getInstance().getMyRequests()
                .thenAccept(loaded -> {
                    if (loaded != null) {
                        Platform.runLater(() -> requests.setAll(loaded));
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to load requests");
                    return null;
                });
    }

    public void acceptRequest(GroupRequest req) {
        ApiService.getInstance().respondToRequest(req.getId(), true)
                .thenAccept(v -> {
                    Platform.runLater(() -> {
                        requests.remove(req);
                        AlertService.showInfo("Success", "You joined the group!");
                        GroupsStore.getInstance().fetchGroupsFromServer();
                    });
                })
                .exceptionally(e -> {
                    AlertService.showError("Error", "Failed to accept request");
                    return null;
                });
    }

    public void rejectRequest(GroupRequest req) {
        ApiService.getInstance().respondToRequest(req.getId(), false)
                .thenAccept(v -> {
                    Platform.runLater(() -> requests.remove(req));
                });
    }
}