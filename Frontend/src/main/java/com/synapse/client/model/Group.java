package com.synapse.client.model;

import java.time.LocalDate;

public class Group {
    private Integer group_id;
    private String name;
    private String description;
    private String created_by;
    private LocalDate created_at;
    public Group() {
    }
    public Group(int group_id, String name, String description, String created_by, LocalDate created_at) {
        this.group_id = group_id;
        this.name = name;
        this.description = description;
        this.created_by = created_by;
        this.created_at = created_at;
    }
    public Integer getGroup_id() {
        return group_id;
    }
    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCreated_by() {
        return created_by;
    }
    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
    public LocalDate getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
