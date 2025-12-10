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

public class GroupsStore {
    private static GroupsStore instance;
    private final ObservableList<Group> groups;

    private GroupsStore() {
        groups = FXCollections.observableArrayList();
    }

    public static synchronized GroupsStore getInstance() {
        if (instance == null) {
            instance = new GroupsStore();
        }
        return instance;
    }

    public ObservableList<Group> getGroups() {
        return groups;
    }

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

    public IntegerBinding getGroupsCountProperty() {
        return Bindings.size(groups);
    }

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

    public void clear() {
        groups.clear();
    }
}