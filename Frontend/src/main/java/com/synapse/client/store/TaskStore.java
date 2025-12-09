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
import java.time.LocalDateTime;

public class TaskStore {
    private static TaskStore instance;
    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    private FilteredList<Task> todayTasks;
    private FilteredList<Task> upcomingTasks;

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

    public static synchronized TaskStore getInstance() {
        if (instance == null) instance = new TaskStore();
        return instance;
    }
    public ObservableList<Task> getTasks() {
        return tasks;
    }

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

    public IntegerBinding getTodayTaskCountProperty() {
        return Bindings.size(todayTasks);
    }

    public IntegerBinding getUpcomingTaskCountProperty() {
        return Bindings.size(upcomingTasks);
    }

    public void fetchTasksFromServer() {
        ApiService.getInstance().getAllTasks()
                .thenAccept(loadedTasks -> {
                    if (loadedTasks != null) {
                        Platform.runLater(() -> {
                            tasks.setAll(loadedTasks);
                            System.out.println("Tasks loaded: " + tasks.size());
                        });
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to load tasks: " + e.getMessage());
                    return null;
                });
    }

    public void addTask(Task task) {
        if (task.getCreated_by() == null) {
            Long userId = UserSession.getInstance().getUserId();
            task.setCreated_by(userId != null ? userId : 1L);
        }

        ApiService.getInstance().createTask(task)
                .thenAccept(savedTask -> {
                    if (savedTask != null) {
                        Platform.runLater(() -> {
                            tasks.add(savedTask);
                            System.out.println("Task created: " + savedTask.getTitle());
                        });
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to create task: " + e.getMessage());
                    Platform.runLater(() -> AlertService.showError("Failed to create task", e.getMessage()));
                    return null;
                });
    }

    public void updateTask(Task task) {
        ApiService.getInstance().updateTask(task)
                .thenAccept(updatedTask -> {
                    if (updatedTask != null) {
                        Platform.runLater(() -> {
                            for (int i = 0; i < tasks.size(); i++) {
                                if (tasks.get(i).getTask_id().equals(updatedTask.getTask_id())) {
                                    tasks.set(i, updatedTask); // Это автоматически обновит и фильтрованные списки
                                    break;
                                }
                            }
                            System.out.println("Task updated: " + updatedTask.getTitle());
                        });
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Failed to update task: " + e.getMessage());
                    return null;
                });
    }

    public void deleteTask(Task task) {
        ApiService.getInstance().deleteTask(task.getTask_id())
                .thenAccept(voidResponse -> {
                    Platform.runLater(() -> {
                        tasks.remove(task);
                        System.out.println("Task deleted");
                    });
                })
                .exceptionally(e -> {
                    System.err.println("Failed to delete task: " + e.getMessage());
                    return null;
                });
    }

    public void clear() {
        tasks.clear();
    }
}