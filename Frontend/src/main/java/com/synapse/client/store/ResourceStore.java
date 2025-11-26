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
        // Добавим пару тестовых данных
        resources.add(new Resource(1, "Java Textbook", "FILE", "C:/Books/java.pdf", "Admin"));
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
            resource.setResource_id((int) (System.currentTimeMillis() % 10000));
        }
        resources.add(resource);
        System.out.println("Resource added: " + resource.getName());
    }

    // Используем FilteredList, чтобы список обновлялся автоматически!
    public ObservableList<Resource> getResourcesByGroupId(int groupId) {
        return new FilteredList<>(this.resources, res ->
                res.getGroup_id() != null && res.getGroup_id().equals(groupId)
        );
    }
}