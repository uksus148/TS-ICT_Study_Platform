package com.synapse.client.store;

import com.synapse.client.model.Resource;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

/**
 * Centralized data store for managing Resources (Files and Links) associated with a group.
 * <p>
 * This class implements the <b>Singleton</b> pattern.
 * <p>
 * <b>Architecture Note:</b> Unlike {@link GroupsStore} which loads all groups at once,
 * {@code ResourceStore} is designed to act as a <i>context-aware buffer</i>.
 * It typically holds only the resources for the <b>currently active/viewed group</b>
 * to minimize memory usage and network overhead.
 */
public class ResourceStore {

    private static ResourceStore instance;

    // The live list of resources currently loaded in memory
    private final ObservableList<Resource> resources;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private ResourceStore() {
        resources = FXCollections.observableArrayList();
    }

    /**
     * Returns the global singleton instance of the ResourceStore.
     *
     * @return The active ResourceStore instance.
     */
    public static synchronized ResourceStore getInstance() {
        if (instance == null) {
            instance = new ResourceStore();
        }
        return instance;
    }

    /**
     * Fetches resources for a specific group from the backend API.
     * <p>
     * <b>Important Behavior:</b> This method clears the <i>entire</i> current list
     * of resources before adding the new ones. This confirms that the store
     * represents the state of the single group currently being viewed by the user.
     *
     * @param groupId The ID of the group to load resources for.
     */
    public void fetchResourcesForGroup(Long groupId) {
        if (groupId == null) return;

        ApiService.getInstance().getGroupResources(groupId).thenAccept(loadedResources -> {
            if (loadedResources != null) {
                Platform.runLater(() -> {
                    resources.clear();
                    resources.addAll(loadedResources);
                });
            } else {
                System.out.println("No resources loaded (null response)");
            }
        });
    }

    /**
     * Sends a request to upload a new resource (File or Link).
     * <p>
     * On success, the new resource is added to the local list immediately,
     * allowing the UI to update without waiting for a full refresh.
     *
     * @param resource The resource object containing path, name, and type.
     */
    public void addResource(Resource resource) {
        if (resource.getCreated_by() == null) {
            // Fallback if creator ID is missing (should ideally be handled by controller)
            resource.setCreated_by(1L);
        }

        ApiService.getInstance().createResource(resource).thenAccept(savedResource -> {
            if (savedResource != null) {
                Platform.runLater(() -> resources.add(savedResource));
            }
        });
    }

    /**
     * Returns a FilteredList view of the resources for a specific group.
     * <p>
     * Even though {@link #fetchResourcesForGroup(Long)} usually ensures only
     * relevant data is present, this filter provides an extra layer of safety guarantees,
     * ensuring no data leakage from one group view to another.
     *
     * @param groupId The ID of the group to filter by.
     * @return A live view of resources belonging to the specified group.
     */
    public ObservableList<Resource> getResourcesByGroupId(Long groupId) {
        return new FilteredList<>(this.resources, r ->
                r.getGroup_id() != null && r.getGroup_id().equals(groupId)
        );
    }

    /**
     * Clears all loaded resources.
     * Should be called on logout or when clearing application state.
     */
    public void clear() {
        resources.clear();
    }
}