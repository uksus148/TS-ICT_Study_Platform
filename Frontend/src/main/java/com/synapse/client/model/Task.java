package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;
import com.synapse.client.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    @SerializedName(value = "taskId", alternate = {"id", "task_id"})
    private Long task_id;
    @SerializedName(value = "groupId", alternate = {"group_id"})
    private Long group_id;
    @SerializedName(value = "created_by")
    private Long created_by;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "description")
    private String description;
    @SerializedName(value = "deadline")
    private LocalDate deadline;
    @SerializedName(value = "status")
    private TaskStatus status;
    @SerializedName(value = "created_at")
    private LocalDateTime created_at;


    public Task() {
    }


    public Task(Long task_id, Long group_id, Long created_by, String title, String description, LocalDate deadline, LocalDateTime created_at, TaskStatus status) {
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
        this.created_at = LocalDateTime.now();
        this.group_id = 1L;
        this.created_by = 1L;
    }

    public Long getTask_id() {
        return task_id;
    }

    // Добавил сеттер для ID (нужен для получения ID от сервера)
    public void setTask_id(Long task_id) {
        this.task_id = task_id;
    }

    public Long getGroup_id() {
        return group_id;
    }

    // Исправил аргумент на Integer, чтобы можно было передать null, но int тоже сработает
    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public Long getCreated_by() { return created_by; }
    public void setCreated_by(Long created_by) { this.created_by = created_by; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public TaskStatus getStatus() { return this.status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreated_at() { return this.created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }
}