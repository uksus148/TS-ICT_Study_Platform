package com.synapse.client.store;

import com.synapse.client.model.User;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized data store for managing lists of Group Members.
 * <p>
 * Unlike {@link GroupsStore} which stores a flat list, this store manages
 * a collection of lists, mapped by Group ID. This allows the application to
 * cache and display members for multiple groups simultaneously without mixing data.
 */
public class MembersStore {

    private static MembersStore instance;

    // Maps GroupID -> List of Users (Members)
    private final Map<Long, ObservableList<User>> groupMembers;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private MembersStore() {
        groupMembers = new HashMap<>();
    }

    /**
     * Returns the global singleton instance of the MembersStore.
     *
     * @return The active MembersStore instance.
     */
    public static synchronized MembersStore getInstance() {
        if (instance == null) {
            instance = new MembersStore();
        }
        return instance;
    }

    /**
     * Retrieves the observable list of members for a specific group.
     * <p>
     * If the list for this group does not exist yet, it creates a new empty list
     * and returns it. This ensures UI components always have a valid list to bind to,
     * even before data is loaded from the server.
     *
     * @param groupId The ID of the group.
     * @return The ObservableList of users in that group.
     */
    public ObservableList<User> getMembersByGroupId(Long groupId) {
        return groupMembers.computeIfAbsent(groupId, k -> FXCollections.observableArrayList());
    }

    /**
     * Fetches the latest member list for a specific group from the backend API.
     * <p>
     * On success, clears the existing list for that group and repopulates it with fresh data.
     * This update happens on the JavaFX Application Thread.
     *
     * @param groupId The ID of the group to refresh.
     */
    public void fetchMembersForGroup(Long groupId) {
        ApiService.getInstance().getGroupMembers(groupId).thenAccept(users -> {
            if (users != null) {
                Platform.runLater(() -> {
                    ObservableList<User> list = getMembersByGroupId(groupId);
                    list.clear();
                    list.addAll(users);
                });
            }
        });
    }

    /**
     * Helper method to resolve a User ID to a Display Name.
     * <p>
     * Iterates through all loaded groups to find a matching user.
     * This is useful for displaying "Created by: John" instead of "Created by: 123"
     * in Task or Resource lists.
     *
     * @param userId The ID of the user to find.
     * @return The user's name if found, otherwise "User #ID" or "Unknown".
     */
    public String getNameById(Long userId) {
        if (userId == null) return "Unknown";

        for (ObservableList<User> groupList : groupMembers.values()) {
            for (User user : groupList) {
                if (user.getUser_id().equals(userId)) {
                    return user.getName();
                }
            }
        }
        return "User #" + userId;
    }

    /**
     * Removes a user from a group (Kick action).
     * <p>
     * 1. Sends a DELETE request to the API.
     * 2. If successful, removes the user from the local ObservableList to update the UI immediately.
     * 3. If failed, shows an error alert.
     *
     * @param groupId The ID of the group.
     * @param user    The user to remove.
     */
    public void removeMember(Long groupId, User user) {
        if (user == null || user.getUser_id() == null) return;

        ApiService.getInstance().removeMember(groupId, user.getUser_id())
                .thenAccept(voidResponse -> {
                    Platform.runLater(() -> {
                        ObservableList<User> members = getMembersByGroupId(groupId);
                        if (members != null) {
                            members.remove(user);
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        AlertService.showError("Error", "Could not remove member: " + e.getMessage());
                    });
                    return null;
                });
    }

    /**
     * Clears all cached member data.
     * Should be called on logout.
     */
    public void clear() {
        groupMembers.clear();
    }
}