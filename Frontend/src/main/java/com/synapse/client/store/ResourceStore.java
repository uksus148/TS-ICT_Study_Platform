package com.synapse.client.store;

import com.synapse.client.model.Resource;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.Objects;

public class ResourceStore {
    private static ResourceStore instance;
    private final ObservableList<Resource> resources;

    private ResourceStore() {
        resources = FXCollections.observableArrayList();
    }

    public static synchronized ResourceStore getInstance() {
        if (instance == null) {
            instance = new ResourceStore();
        }
        return instance;
    }

    public void fetchResourcesForGroup(Long groupId) {
        if (groupId == null) return;

        ApiService.getInstance().getGroupResources(groupId).thenAccept(loadedResources -> {
            if (loadedResources != null) {
                Platform.runLater(() -> {
                    resources.clear();
                    resources.addAll(loadedResources);
                    System.out.println("Loaded " + resources.size() + " resources for group " + groupId);
                });
            } else {
                System.out.println("No resources loaded (null response)");
            }
        });
    }

    public void addResource(Resource resource) {
        if (resource.getCreated_by() == null) {
            resource.setCreated_by(1L);
        }

        ApiService.getInstance().createResource(resource).thenAccept(savedResource -> {
            if (savedResource != null) {
                Platform.runLater(() -> {
                    resources.add(savedResource);
                    System.out.println("Resource uploaded: " + savedResource.getName());
                });
            }
        });
    }
    public ObservableList<Resource> getResources() {
        return resources;
    }
    public ObservableList<Resource> getResourcesByGroupId(Long groupId) {
        return new FilteredList<>(this.resources, r ->
                r.getGroup_id() != null && r.getGroup_id().equals(groupId)
        );
    }

    public void clear() {
        resources.clear();
    }
}