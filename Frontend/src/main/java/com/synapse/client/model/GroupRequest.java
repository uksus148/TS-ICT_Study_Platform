package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;
import com.synapse.client.GroupRequestStatus;

public class GroupRequest {
    @SerializedName(value = "id", alternate = {"requestId", "request_id"})
    private Long id;

    @SerializedName(value = "sender_name", alternate = {"senderName", "sender"})
    private String senderName;

    @SerializedName(value = "group_name", alternate = {"groupName", "group"})
    private String groupName;

    @SerializedName(value = "group_id", alternate = {"groupId"})
    private Long groupId;

    @SerializedName(value = "type")
    private String type;

    @SerializedName(value = "status")
    private GroupRequestStatus status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public GroupRequestStatus getStatus() { return status; }
    public void setStatus(GroupRequestStatus status) { this.status = status; }
}