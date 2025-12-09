package com.synapse.client.store;

import com.synapse.client.model.User;
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
                    System.out.println("Loaded " + list.size() + " members for group " + groupId);
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
        ObservableList<User> members = getMembersByGroupId(groupId);
        if (members != null) {
            members.remove(user);
            System.out.println("User " + user.getName() + " removed from list locally.");
        }
    }

    public void addMember(Long groupId, User user) {
        getMembersByGroupId(groupId).add(user);
    }

    public void clear() {
        groupMembers.clear();
    }
}