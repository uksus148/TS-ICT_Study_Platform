package com.synapse.client.store;

import com.synapse.client.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;

public class MembersStore {
    private static MembersStore instance;
    private Map<Long, ObservableList<User>> groupMembers;

    private MembersStore() {
        groupMembers = new HashMap<>();

        ObservableList<User> usersGroup1 = FXCollections.observableArrayList();
        usersGroup1.add(new User(1L, "Ivan Ivanov", "ivan@test.com"));
        usersGroup1.add(new User(2L, "Petr Petrov", "petr@test.com"));
        groupMembers.put(1L, usersGroup1);
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

    public void removeMember(Long groupId, User user) {
        ObservableList<User> members = getMembersByGroupId(groupId);
        if (members != null) {
            members.remove(user);
            System.out.println("User " + user.getUsername() + " kicked from group " + groupId);
        }
    }

    public void addMember(Long groupId, User user) {
        getMembersByGroupId(groupId).add(user);
    }
}