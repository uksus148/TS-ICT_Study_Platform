package com.synapse.client.store;

import com.synapse.client.model.Group;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

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
        ApiService.getInstance().getAllGroups().thenAccept(loadedGroups -> {
            if (loadedGroups != null) {
                Platform.runLater(() -> {
                    groups.clear();
                    groups.addAll(loadedGroups);
                    System.out.println("DEBUG: Loaded " + groups.size() + " groups from server.");
                    for(Group g : groups) {
                        System.out.println(" - Group: " + g.getName() + " (ID: " + g.getGroup_id() + ")");
                    }
                });
            } else {
                System.out.println("DEBUG: LoadedGroups is NULL");
            }
        });
    }

    public void addGroup(Group group) {
        if (group.getCreated_by() == null) {
            group.setCreated_by(1L);
        }

        ApiService.getInstance().createGroup(group).thenAccept(savedGroup -> {
            if (savedGroup != null) {
                Platform.runLater(() -> {
                    this.groups.add(savedGroup);
                    System.out.println("Group created: " + savedGroup.getName());
                });
            }
        });
    }

    public void updateGroup(Group group) {
        ApiService.getInstance().updateGroup(group).thenAccept(updatedGroup -> {
            if (updatedGroup != null) {
                Platform.runLater(() -> {
                    for (int i = 0; i < groups.size(); i++) {
                        if (groups.get(i).getGroup_id().equals(updatedGroup.getGroup_id())) {
                            groups.set(i, updatedGroup);
                            System.out.println("Group updated: " + updatedGroup.getName());
                            return;
                        }
                    }
                });
            }
        });
    }

    public void deleteGroup(Group group) {
        if (group == null || group.getGroup_id() == null) return;
        ApiService.getInstance().deleteGroup(group.getGroup_id()).thenAccept(response -> {
            Platform.runLater(() -> {
                this.groups.remove(group);
                System.out.println("Group deleted: " + group.getName());
            });
        });
    }
}