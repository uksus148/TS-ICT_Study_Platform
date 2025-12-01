package com.synapse.client.store;

import com.synapse.client.model.Resource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class ResourceStore {
    private static ResourceStore instance;
    private ObservableList<Resource> resources;

    private ResourceStore() {
        resources = FXCollections.observableArrayList();
        resources.add(new Resource(1L, "Java Textbook", "FILE", "C:/Books/java.pdf", "Admin"));
    }

    public static synchronized ResourceStore getInstance() {
        if (instance == null) {
            instance = new ResourceStore();
        }
        return instance;
    }

    public void addResource(Resource resource) {
        // Эмуляция ID от сервера
        if (resource.getResource_id() == null) {
            resource.setResource_id((long) (System.currentTimeMillis() % 10000));
        }
        resources.add(resource);
        System.out.println("Resource added: " + resource.getName());
    }

    public ObservableList<Resource> getResourcesByGroupId(Long groupId) {
        return new FilteredList<>(this.resources, res ->
                res.getGroup_id() != null && res.getGroup_id().equals(groupId)
        );
    }
}