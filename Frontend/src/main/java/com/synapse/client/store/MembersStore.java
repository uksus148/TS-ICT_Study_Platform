package com.synapse.client.store;

import com.synapse.client.model.User;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class MembersStore {
    private static MembersStore instance;
    private final Map<Long, ObservableList<User>> groupMembers;

    private MembersStore() {
        groupMembers = new HashMap<>();
    }

    public static synchronized MembersStore getInstance() {
        if (instance == null) {
            instance = new MembersStore();
        }
        return instance;
    }

    public ObservableList<User> getMembersByGroupId(Long groupId) {
        return groupMembers.computeIfAbsent(groupId, k -> FXCollections.observableArrayList());
    }

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

    public void clear() {
        groupMembers.clear();
    }
}