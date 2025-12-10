package com.synapse.client.store;

import com.synapse.client.UserSession;
import com.synapse.client.model.Group;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Centralized data store for managing the state of Study Groups in the application.
 * <p>
 * This class implements the <b>Singleton</b> pattern and acts as the "Single Source of Truth"
 * for the UI. It holds an {@link ObservableList} of groups, which allows JavaFX components
 * (like Lists or Tables) to automatically update whenever data is added, removed, or modified,
 * without requiring manual refresh calls from the controllers.
 */
public class GroupsStore {

    private static GroupsStore instance;

    // The core data list. UI components bind directly to this.
    private final ObservableList<Group> groups;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes the empty observable list.
     */
    private GroupsStore() {
        groups = FXCollections.observableArrayList();
    }

    /**
     * Returns the global singleton instance of the GroupsStore.
     *
     * @return The active GroupsStore instance.
     */
    public static synchronized GroupsStore getInstance() {
        if (instance == null) {
            instance = new GroupsStore();
        }
        return instance;
    }

    /**
     * Provides access to the live list of groups.
     * <p>
     * Controllers should bind their UI elements (e.g., {@code listView.setItems()})
     * to this list to ensure they always display the latest data.
     *
     * @return The observable list of groups.
     */
    public ObservableList<Group> getGroups() {
        return groups;
    }

    /**
     * Fetches the latest list of groups from the backend API.
     * <p>
     * This operation is asynchronous. Upon a successful response, the local {@code groups} list
     * is completely replaced with the new data. This update happens on the JavaFX Application Thread
     * via {@link Platform#runLater} to ensure thread safety.
     */
    public void fetchGroupsFromServer() {
        ApiService.getInstance().getAllGroups()
                .thenAccept(loadedGroups -> {
                    if (loadedGroups != null) {
                        Platform.runLater(() -> groups.setAll(loadedGroups));
                    } else {
                        System.out.println("DEBUG: LoadedGroups is NULL");
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    AlertService.showError("Connection Error", "Failed to load groups.");
                    return null;
                });
    }

    /**
     * Returns a reactive property representing the total number of groups.
     * <p>
     * Used by the Sidebar to display the counter (e.g., "Groups: 5").
     * The value updates automatically whenever items are added to or removed from the list.
     *
     * @return An IntegerBinding representing the list size.
     */
    public IntegerBinding getGroupsCountProperty() {
        return Bindings.size(groups);
    }

    /**
     * Sends a request to create a new group.
     * <p>
     * Logic:
     * 1. Ensures the 'created_by' field is set to the current user.
     * 2. Calls the API to create the group.
     * 3. On success, adds the returned Group object (with the generated ID) to the local list.
     *
     * @param group The new group object to create.
     */
    public void addGroup(Group group) {
        if (group.getCreated_by() == null) {
            Long userId = UserSession.getInstance().getUserId();
            group.setCreated_by(userId != null ? userId : 1L);
        }

        ApiService.getInstance().createGroup(group)
                .thenAccept(savedGroup -> {
                    if (savedGroup != null) {
                        Platform.runLater(() -> this.groups.add(savedGroup));
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    AlertService.showError("Error", "Failed to create group.");
                    return null;
                });
    }

    /**
     * Sends a request to update an existing group.
     * <p>
     * On success, finds the group in the local list by ID and replaces it
     * with the updated version from the server to reflect changes immediately.
     *
     * @param group The group object with updated data.
     */
    public void updateGroup(Group group) {
        ApiService.getInstance().updateGroup(group)
                .thenAccept(updatedGroup -> {
                    if (updatedGroup != null) {
                        Platform.runLater(() -> {
                            for (int i = 0; i < groups.size(); i++) {
                                if (groups.get(i).getGroup_id().equals(updatedGroup.getGroup_id())) {
                                    groups.set(i, updatedGroup);
                                    return;
                                }
                            }
                        });
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    AlertService.showError("Error", "Failed to update group.");
                    return null;
                });
    }

    /**
     * Sends a request to delete a group.
     * <p>
     * On success, removes the group from the local list, causing it to disappear from the UI.
     *
     * @param group The group object to delete.
     */
    public void deleteGroup(Group group) {
        if (group == null || group.getGroup_id() == null) return;

        ApiService.getInstance().deleteGroup(group.getGroup_id())
                .thenAccept(response -> Platform.runLater(() -> this.groups.remove(group)))
                .exceptionally(e -> {
                    e.printStackTrace();
                    AlertService.showError("Error", "Failed to delete group.");
                    return null;
                });
    }

    /**
     * Clears all data from the store.
     * Typically called upon user logout to ensure no sensitive data remains in memory.
     */
    public void clear() {
        groups.clear();
    }
}