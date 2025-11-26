package com.synapse.client.model;

import com.synapse.client.TaskStatus;
import java.time.LocalDate;

public class Task {
    private Integer task_id;
    private Integer group_id;
    private String created_by;
    private String title;
    private String description;
    private LocalDate deadline;
    private TaskStatus status;
    private LocalDate created_at;


    public Task() {
    }


    public Task(Integer task_id, Integer group_id, String created_by, String title, String description, LocalDate deadline, LocalDate created_at, TaskStatus status) {
        this.task_id = task_id;
        this.group_id = group_id;
        this.created_by = created_by;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.created_at = created_at;
        this.status = status;
    }

    public Task(String title, String description, LocalDate deadline, TaskStatus status) {
        this.task_id = null;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.created_at = LocalDate.now();
        this.group_id = 1;
        this.created_by = "user";
    }

    public Integer getTask_id() {
        return task_id;
    }

    // Добавил сеттер для ID (нужен для получения ID от сервера)
    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    // Исправил аргумент на Integer, чтобы можно было передать null, но int тоже сработает
    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
    }

    public String getCreated_by() { return created_by; }
    public void setCreated_by(String created_by) { this.created_by = created_by; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public TaskStatus getStatus() { return this.status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getCreated_at() { return this.created_at; }
    public void setCreated_at(LocalDate created_at) { this.created_at = created_at; }
}