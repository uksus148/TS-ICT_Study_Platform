package com.synapse.client.store;

import com.synapse.client.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;

public class MembersStore {
    private static MembersStore instance;
    private Map<Integer, ObservableList<User>> groupMembers;

    private MembersStore() {
        groupMembers = new HashMap<>();

        ObservableList<User> usersGroup1 = FXCollections.observableArrayList();
        usersGroup1.add(new User(1, "Ivan Ivanov", "ivan@test.com"));
        usersGroup1.add(new User(2, "Petr Petrov", "petr@test.com"));
        groupMembers.put(1, usersGroup1);
    }

    public static synchronized MembersStore getInstance() {
        if (instance == null) {
            instance = new MembersStore();
        }
        return instance;
    }

    public ObservableList<User> getMembersByGroupId(int groupId) {
        return groupMembers.computeIfAbsent(groupId, k -> FXCollections.observableArrayList());
    }

    public void removeMember(int groupId, User user) {
        ObservableList<User> members = getMembersByGroupId(groupId);
        if (members != null) {
            members.remove(user);
            // Тут будет запрос к серверу: server.kickUser(groupId, user.getId());
            System.out.println("User " + user.getUsername() + " kicked from group " + groupId);
        }
    }

    public void addMember(int groupId, User user) {
        getMembersByGroupId(groupId).add(user);
    }
}