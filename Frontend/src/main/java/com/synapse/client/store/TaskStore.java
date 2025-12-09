package com.synapse.client.store;

import com.synapse.client.model.Task;
import com.synapse.client.service.ApiService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDate;

public class TaskStore {
    private static TaskStore instance;
    private ObservableList<Task> tasks = FXCollections.observableArrayList();

    private TaskStore() {}

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

    public void fetchTasksFromServer() {
        ApiService.getInstance().getAllTasks().thenAccept(loadedTasks -> {
            if (loadedTasks != null) {
                Platform.runLater(() -> {
                    tasks.clear();
                    tasks.addAll(loadedTasks);
                    System.out.println("Tasks loaded: " + tasks.size());
                });
            }
        });
    }

    public void addTask(Task task) {
        if (task.getCreated_by() == null) {
            // TODO: Change to auto detect id
            task.setCreated_by(1L);
        }

        ApiService.getInstance().createTask(task).thenAccept(savedTask -> {
            if (savedTask != null) {
                Platform.runLater(() -> {
                    tasks.add(savedTask);
                    System.out.println("Task created: " + savedTask.getTitle());
                });
            }
        });
    }

    public void updateTask(Task task) {
        ApiService.getInstance().updateTask(task).thenAccept(updatedTask -> {
            if (updatedTask != null) {
                Platform.runLater(() -> {
                    for (int i = 0; i < tasks.size(); i++) {
                        if (tasks.get(i).getTask_id().equals(updatedTask.getTask_id())) {
                            tasks.set(i, updatedTask);
                            break;
                        }
                    }
                    System.out.println("Task updated: " + updatedTask.getTitle());
                });
            }
        });
    }

    public void deleteTask(Task task) {
        ApiService.getInstance().deleteTask(task.getTask_id())
                .thenAccept(voidResponse -> {
                    Platform.runLater(() -> {
                        tasks.remove(task);
                        System.out.println("Task deleted");
                    });
                });
    }

    public ObservableList<Task> getTodayTasks() {
        return new FilteredList<>(this.tasks, task -> {
            if (task.getDeadline() == null) return false;
            return task.getDeadline().equals(LocalDate.now());
        });
    }

    public ObservableList<Task> getUpcomingTasks() {
        return new FilteredList<>(this.tasks, task -> {
            if (task.getDeadline() == null) return false;
            return task.getDeadline().isAfter(LocalDate.now());
        });
    }

    public IntegerBinding getTodayTaskCountProperty() {
        FilteredList<Task> todayTasks = (FilteredList<Task>) getTodayTasks();
        return Bindings.size(todayTasks);
    }

    public IntegerBinding getUpcomingTaskCountProperty() {
        FilteredList<Task> upcomingTasks = (FilteredList<Task>) getUpcomingTasks();
        return Bindings.size(upcomingTasks);
    }

    public void clear() {
        tasks.clear();
    }
}