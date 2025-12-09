package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

public class Group {
    @SerializedName(value = "groupId", alternate = {"id", "group_id"})
    private Long group_id;
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "description")
    private String description;
    @SerializedName(value = "created_by")
    private Long created_by;
    public Group() {
    }
    public Group(Long group_id, String name, String description, Long created_by) {
        this.group_id = group_id;
        this.name = name;
        this.description = description;
        this.created_by = created_by;
    }
    public Long getGroup_id() {
        return group_id;
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
    public Long getCreated_by() {
        return created_by;
    }
    public void setCreated_by(Long created_by) {
        this.created_by = created_by;
    }
    @Override
    public String toString() {
        return this.name;
    }
}
