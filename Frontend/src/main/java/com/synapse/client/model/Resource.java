package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;

public class Resource {
    @SerializedName(value = "resource_id", alternate = {"id"})
    private Long resource_id;

    @SerializedName(value = "title", alternate = {"name", "resourceName"})
    private String name;

    @SerializedName(value = "type", alternate = {"resourceType"})
    private String type;

    @SerializedName(value = "path", alternate = {"pathOrUrl", "url", "filePath"})
    private String path;

    @SerializedName(value = "group_id", alternate = {"studyGroup", "groupId"})
    private Long group_id;

    @SerializedName(value = "created_by", alternate = {"uploadedBy", "userId", "creatorId"})
    private Long created_by;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Long getGroup_id() { return group_id; }
    public void setGroup_id(Long group_id) { this.group_id = group_id; }

    public Long getCreated_by() { return created_by; }
    public void setCreated_by(Long created_by) { this.created_by = created_by; }
}