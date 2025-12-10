package com.synapse.client.store;

import com.synapse.client.UserSession;
import com.synapse.client.model.Task;
import com.synapse.client.service.AlertService;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Centralized data store for managing the state of Tasks within the application.
 * <p>
 * This class implements the <b>Singleton</b> pattern and acts as the "Single Source of Truth"
 * for all task-related data.
 * <p>
 * <b>Key Architectural Feature: Reactive Filtering.</b>
 * Instead of maintaining separate lists for "Today" and "Upcoming" tasks manually,
 * this store maintains one master list ({@code tasks}). The specific views are created using
 * {@link FilteredList}, which automatically updates whenever the master list changes.
 */
public class TaskStore {

    private static TaskStore instance;

    // The Master List containing ALL tasks loaded into the application
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    // Live views (Read-only wrappers around the master list)
    private final FilteredList<Task> todayTasks;
    private final FilteredList<Task> upcomingTasks;

    /**
     * Private constructor to enforce Singleton pattern.
     * <p>
     * Initializes the reactive filters:
     * <ul>
     * <li><b>Today Tasks:</b> Filters tasks where deadline is exactly {@link LocalDate#now()}.</li>
     * <li><b>Upcoming Tasks:</b> Filters tasks where deadline is strictly after today.</li>
     * </ul>
     */
    private TaskStore() {
        this.todayTasks = new FilteredList<>(this.tasks, task -> {
            if (task.getDeadline() == null) return false;
            return task.getDeadline().toLocalDate().equals(LocalDate.now());
        });

        this.upcomingTasks = new FilteredList<>(this.tasks, task -> {
            if (task.getDeadline() == null) return false;
            return task.getDeadline().toLocalDate().isAfter(LocalDate.now());
        });
    }

    /**
     * Returns the global singleton instance of the TaskStore.
     *
     * @return The active TaskStore instance.
     */
    public static synchronized TaskStore getInstance() {
        if (instance == null) instance = new TaskStore();
        return instance;
    }

    /**
     * Creates a dynamic filtered view for a specific group.
     * <p>
     * This is used by the {@code GroupDetailsController} to show only tasks relevant
     * to the currently opened group.
     *
     * @param groupId The ID of the group to filter by.
     * @return A FilteredList containing only tasks for the specified group.
     */
    public ObservableList<Task> getTasksByGroupId(Long groupId) {
        return new FilteredList<>(this.tasks, task ->
                task.getGroup_id() != null && task.getGroup_id().equals(groupId)
        );
    }

    public ObservableList<Task> getTodayTasks() {
        return todayTasks;
    }

    public ObservableList<Task> getUpcomingTasks() {
        return upcomingTasks;
    }

    /**
     * Returns a reactive property for the count of today's tasks.
     * Bound to the Sidebar counter UI. Updates automatically.
     */
    public IntegerBinding getTodayTaskCountProperty() {
        return Bindings.size(todayTasks);
    }

    /**
     * Returns a reactive property for the count of upcoming tasks.
     * Bound to the Sidebar counter UI. Updates automatically.
     */
    public IntegerBinding getUpcomingTaskCountProperty() {
        return Bindings.size(upcomingTasks);
    }

    /**
     * Fetches all tasks for the current user from the backend.
     * Replaces the entire content of the master list.
     */
    public void fetchTasksFromServer() {
        ApiService.getInstance().getAllTasks()
                .thenAccept(loadedTasks -> {
                    if (loadedTasks != null) {
                        Platform.runLater(() -> tasks.setAll(loadedTasks));
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to load tasks: " + e.getMessage());
                    return null;
                });
    }

    /**
     * Refreshes tasks for a specific group.
     * <p>
     * Logic:
     * 1. Fetches data from the server.
     * 2. Removes old tasks belonging to this group from the master list.
     * 3. Adds the fresh tasks for this group.
     * This ensures we don't duplicate tasks while keeping other groups' data intact.
     *
     * @param groupId The ID of the group to refresh.
     */
    public void fetchTasksByGroupId(Long groupId) {
        ApiService.getInstance().getAllTasks()
                .thenAccept(downloadedTasks -> {
                    if (downloadedTasks != null) {
                        Platform.runLater(() -> {
                            // Filter only the relevant tasks from the response
                            List<Task> newGroupTasks = Arrays.stream(downloadedTasks)
                                    .filter(t -> groupId.equals(t.getGroup_id()))
                                    .toList();

                            // Clean up old local data for this group
                            this.tasks.removeIf(t -> groupId.equals(t.getGroup_id()));

                            // Add new data
                            this.tasks.addAll(newGroupTasks);
                        });
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to fetch tasks: " + e.getMessage());
                    return null;
                });
    }

    /**
     * Sends a request to create a new task.
     * On success, adds it to the master list (which auto-updates Today/Upcoming views).
     */
    public void addTask(Task task) {
        if (task.getCreated_by() == null) {
            Long userId = UserSession.getInstance().getUserId();
            task.setCreated_by(userId != null ? userId : 1L);
        }

        ApiService.getInstance().createTask(task)
                .thenAccept(savedTask -> {
                    if (savedTask != null) {
                        Platform.runLater(() -> tasks.add(savedTask));
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to create task: " + e.getMessage());
                    Platform.runLater(() -> AlertService.showError("Failed to create task", e.getMessage()));
                    return null;
                });
    }

    /**
     * Sends a request to update an existing task.
     * Finds the task in the local list by ID and replaces it to reflect changes immediately.
     */
    public void updateTask(Task task) {
        ApiService.getInstance().updateTask(task)
                .thenAccept(updatedTask -> {
                    if (updatedTask != null) {
                        Platform.runLater(() -> {
                            for (int i = 0; i < tasks.size(); i++) {
                                if (tasks.get(i).getTask_id().equals(updatedTask.getTask_id())) {
                                    tasks.set(i, updatedTask);
                                    break;
                                }
                            }
                        });
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to update task: " + e.getMessage());
                    return null;
                });
    }

    /**
     * Sends a request to delete a task.
     * On success, removes it from the master list.
     */
    public void deleteTask(Task task) {
        ApiService.getInstance().deleteTask(task.getTask_id())
                .thenAccept(voidResponse -> Platform.runLater(() -> tasks.remove(task)))
                .exceptionally(e -> {
                    System.err.println("Failed to delete task: " + e.getMessage());
                    return null;
                });
    }

    public void clear() {
        tasks.clear();
    }
}