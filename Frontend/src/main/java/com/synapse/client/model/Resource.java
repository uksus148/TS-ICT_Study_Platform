package com.synapse.client.model;

import java.time.LocalDate;

public class Resource {
    private Integer resource_id;
    private Integer group_id;
    private String name;
    private String type;
    private String path;
    private String created_by;
    private LocalDate created_at;

    public Resource() {
    }

    public Resource(Integer group_id, String name, String type, String path, String created_by) {
        this.group_id = group_id;
        this.name = name;
        this.type = type;
        this.path = path;
        this.created_by = created_by;
        this.created_at = LocalDate.now();
    }

    public Integer getResource_id() { return resource_id; }
    public void setResource_id(Integer resource_id) { this.resource_id = resource_id; }

    public Integer getGroup_id() { return group_id; }
    public void setGroup_id(Integer group_id) { this.group_id = group_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getCreated_by() { return created_by; }
    public void setCreated_by(String created_by) { this.created_by = created_by; }

    public LocalDate getCreated_at() { return created_at; }
    public void setCreated_at(LocalDate created_at) { this.created_at = created_at; }

    @Override
    public String toString() { return name; }
}