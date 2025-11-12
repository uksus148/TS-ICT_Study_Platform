package com.synapse.client.store;

import com.synapse.client.Task;
import com.synapse.client.TaskStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class TaskStore {
    private static TaskStore instance;
    private final ObservableList<Task> tasks;
    private TaskStore() {
        tasks = FXCollections.observableArrayList();
    }
    public static TaskStore getInstance() {
        if (instance == null) {
            instance = new TaskStore();
        }
        return instance;
    }
    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void updateTask(Task task) {
        tasks.set(tasks.indexOf(task), task);
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    public void fetchTasksFromServer() {
        tasks.clear();
        LocalDate today = LocalDate.now();
        tasks.addAll(
                new Task(
                        101,
                        1,
                        "maria",
                        "Finish UI Mockup",
                        "Design all screens for the app",
                        today.plusDays(1),
                        today.minusDays(3),
                        TaskStatus.COMPLETED
                ),
                new Task(
                        201,
                        2,
                        "alex",
                        "Setup Backend API",
                        "Create Spring Boot endpoints for tasks",
                        today,
                        today.minusDays(2),
                        TaskStatus.IN_PROGRESS
                ),
                new Task(
                        202,
                        2,
                        "alex",
                        "Connect to Database",
                        "Configure JPA and PostgreSQL",
                        null,
                        today.minusDays(2),
                        TaskStatus.CANCELED
                ),
                new Task(
                        203,
                        2,
                        "alex",
                        "Implement Login",
                        "Add JWT authentication",
                        today.plusDays(3),
                        today.minusDays(1),
                        TaskStatus.COMPLETED
                ),
                new Task(
                        102,
                        1,
                        "maria",
                        "Test Task Cell",
                        "Make sure the UI looks good",
                        today,
                        today,
                        TaskStatus.CANCELED
                ),
                new Task(
                        103,
                        1,
                        "maria",
                        "Implement Dashboard View",
                        "Use JavaFX Charts to show statistics",
                        today.plusWeeks(1),
                        today.plusDays(1),
                        TaskStatus.IN_PROGRESS
                ),
                new Task(
                        204,
                        2,
                        "alex",
                        "Write API Documentation",
                        "Use Swagger or SpringDoc",
                        today.minusDays(1),
                        today.minusDays(5),
                        TaskStatus.COMPLETED
                )
        );
    }
}
