package com.synapse.client.store;

import com.synapse.client.model.Group;
import com.synapse.client.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class GroupsStore {
    private static GroupsStore instance;
    private final ObservableList<Group> groups;
    private GroupsStore() {
        groups = FXCollections.observableArrayList();
    }
    public static GroupsStore getInstance() {
        if (instance == null) {
            instance = new GroupsStore();
        }
        return instance;
    }
    public ObservableList<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    public void updateGroup(Group group) {
        int idToUpdate = group.getGroup_id();
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getGroup_id() == idToUpdate) {
                groups.set(i, group);
                return;
            }
        }
    }

    public void deleteGroup(Group group) {
        this.groups.remove(group);
    }

    public void fetchGroupsFromServer() {
        groups.clear();
        LocalDate today = LocalDate.now();
        groups.addAll(
                new Group(
                        1,
                        "IKT",
                        "Information Technologes",
                        "Me",
                        LocalDate.now()
                ),
                new Group(
                        2,
                        "IKT2",
                        "Information Technologes",
                        "Me",
                        LocalDate.now()
                ),
                new Group(
                        3,
                        "IKT3",
                        "Information Technologes",
                        "Me",
                        LocalDate.now()
                ),
                new Group(
                        4,
                        "IKT4",
                        "Information Technologes",
                        "Me",
                        LocalDate.now()
                ),
                new Group(
                        5,
                        "IKT5",
                        "Information Technologes",
                        "Me",
                        LocalDate.now()
                )
        );
    }
}
