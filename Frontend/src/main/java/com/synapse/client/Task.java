package com.synapse.client;

import java.time.LocalDate;

public class Task {
    private final int task_id;
    private int group_id;
    private String created_by;
    private String title;
    private String description;
    private LocalDate deadline;
    private boolean status;
    private LocalDate created_at;

    public Task(int task_id, int group_id, String created_by, String title, String description, LocalDate deadline, LocalDate created_at) {
        this.task_id = task_id;
        this.group_id = group_id;
        this.created_by = created_by;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.created_at = created_at;
        this.status = false;
    }

    public int getTask_id() {
        return task_id;
    }

    public int getGroup_id() {
        return group_id;
    }
    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getCreated_by() {
        return created_by;
    }
    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public boolean getStatus() { return this.status; }
    public void setStatus(boolean status) { this.status = status; }

    public LocalDate getCreated_at() { return this.created_at; }
    public void setCreated_at(LocalDate created_at) { this.created_at = created_at; }
}
