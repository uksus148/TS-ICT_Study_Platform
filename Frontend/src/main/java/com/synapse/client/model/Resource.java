package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Resource {
    @SerializedName(value = "resource_id")
    private Long resource_id;
    @SerializedName(value = "group_id")
    private Long group_id;
    @SerializedName(value = "title")
    private String name;
    @SerializedName(value = "type")
    private String type;
    @SerializedName(value = "path_or_url")
    private String path;
    @SerializedName(value = "uploaded_by")
    private Long created_by;
    @SerializedName(value = "uploaded_at")
    private LocalDateTime created_at;

    public Resource() {
    }

    public Resource(Long group_id, String name, String type, String path, Long created_by) {
        this.group_id = group_id;
        this.name = name;
        this.type = type;
        this.path = path;
        this.created_by = created_by;
        this.created_at = LocalDateTime.now();
    }

    public Long getResource_id() { return resource_id; }
    public void setResource_id(Long resource_id) { this.resource_id = resource_id; }

    public Long getGroup_id() { return group_id; }
    public void setGroup_id(Long group_id) { this.group_id = group_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Long getCreated_by() { return created_by; }
    public void setCreated_by(Long created_by) { this.created_by = created_by; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    @Override
    public String toString() { return name; }
}