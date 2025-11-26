package com.synapse.client.store;

import com.synapse.client.model.Task;
import com.synapse.client.TaskStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.LocalDate;

public class TaskStore {
    private static TaskStore instance;
    private final ObservableList<Task> tasks;
    private final FilteredList<Task> todayTasks;
    private final FilteredList<Task> upcomingTasks;
    private final ReadOnlyIntegerProperty todayTaskCount;
    private final ReadOnlyIntegerProperty upcomingTaskCount;
    private TaskStore() {
        tasks = FXCollections.observableArrayList();
        this.todayTasks = new FilteredList<>(this.tasks);
        this.upcomingTasks = new FilteredList<>(this.tasks);
        this.todayTasks.setPredicate(this::isTaskForToday);
        this.upcomingTasks.setPredicate(this::isTaskForUpcoming);

        SimpleIntegerProperty todayCountProp = new SimpleIntegerProperty();
        SimpleIntegerProperty upcomingCountProp = new SimpleIntegerProperty();
        todayCountProp.bind(Bindings.size(todayTasks));
        upcomingCountProp.bind(Bindings.size(upcomingTasks));
        this.todayTaskCount = todayCountProp;
        this.upcomingTaskCount = upcomingCountProp;
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

    public ObservableList<Task> getTasksByGroupId(int groupId) {
        FilteredList<Task> filteredData = new FilteredList<>(this.tasks, task -> {
            return task.getGroup_id() != null && task.getGroup_id().equals(groupId);
        });

        return filteredData;
    }

    private boolean isTaskForToday(Task task) {
        if (task.getDeadline() == null) return false;
        LocalDate today = LocalDate.now();
        LocalDate deadline = task.getDeadline();

        boolean isDueToday = deadline.isEqual(today);
        boolean isOverdue = deadline.isBefore(today) && (task.getStatus() != TaskStatus.COMPLETED);

        return isDueToday || isOverdue;
    }

    private boolean isTaskForUpcoming(Task task) {
        if (task.getDeadline() == null) return false;
        LocalDate today = LocalDate.now();
        return task.getDeadline().isAfter(today);
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getTask_id().equals(task.getTask_id())) {
                tasks.set(i, task);
                break;
            }
        }
    }

    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }

    public ObservableList<Task> getTodayTasks() {
        return this.todayTasks;
    }

    public ObservableList<Task> getUpcomingTasks() {
        return this.upcomingTasks;
    }

    public ReadOnlyIntegerProperty getTodayTaskCountProperty() {
        // Возвращаем "живое" свойство самого списка
        return this.todayTaskCount;
    }

    public ReadOnlyIntegerProperty getUpcomingTaskCountProperty() {
        // То же самое здесь
        return this.upcomingTaskCount;
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
