package com.synapse.client.store;

import com.synapse.client.model.GroupRequest;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Centralized data store for managing Group Invitations and Join Requests.
 * <p>
 * This class handles the state of the "Notifications" screen. It holds a list of
 * pending requests ({@link GroupRequest}) that the user needs to respond to.
 * <p>
 * Implements the Singleton pattern to ensure data consistency across different views.
 */
public class RequestStore {

    private static RequestStore instance;

    // The live list of pending requests. The Notification View binds to this.
    private final ObservableList<GroupRequest> requests = FXCollections.observableArrayList();

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private RequestStore() {}

    /**
     * Returns the global singleton instance of the RequestStore.
     *
     * @return The active RequestStore instance.
     */
    public static synchronized RequestStore getInstance() {
        if (instance == null) instance = new RequestStore();
        return instance;
    }

    /**
     * Provides access to the observable list of pending requests.
     * Controllers can bind their UI lists to this object to automatically
     * reflect changes (e.g., when a request is accepted and removed).
     *
     * @return The observable list of group requests.
     */
    public ObservableList<GroupRequest> getRequests() {
        return requests;
    }

    /**
     * Fetches the latest pending requests from the backend API.
     * <p>
     * Updates the local {@code requests} list on the JavaFX Application Thread.
     * If the API call fails, errors are logged to the console but do not crash the UI.
     */
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

    /**
     * Accepts a specific group invitation or join request.
     * <p>
     * Workflow:
     * 1. Calls the API to mark the request as ACCEPTED.
     * 2. On success, removes the request from the local list.
     * 3. Shows a success notification.
     * 4. <b>Crucial Step:</b> Triggers a refresh in {@link GroupsStore}, because accepting
     * an invite means the user is now part of a new group, and it should appear in the main list.
     *
     * @param req The request object to accept.
     */
    public void acceptRequest(GroupRequest req) {
        ApiService.getInstance().respondToRequest(req.getId(), true)
                .thenAccept(v -> {
                    Platform.runLater(() -> {
                        // Remove from the notification list
                        requests.remove(req);

                        AlertService.showInfo("Success", "You joined the group!");

                        // Update the main group list to show the new group immediately
                        GroupsStore.getInstance().fetchGroupsFromServer();
                    });
                })
                .exceptionally(e -> {
                    AlertService.showError("Error", "Failed to accept request");
                    return null;
                });
    }

    /**
     * Rejects (declines) a group invitation.
     * <p>
     * Workflow:
     * 1. Calls the API to mark the request as REJECTED.
     * 2. On success, simply removes the request from the local list.
     *
     * @param req The request object to reject.
     */
    public void rejectRequest(GroupRequest req) {
        ApiService.getInstance().respondToRequest(req.getId(), false)
                .thenAccept(v -> {
                    Platform.runLater(() -> requests.remove(req));
                });
    }
}