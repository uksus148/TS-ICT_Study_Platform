package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;
import com.synapse.client.enums.GroupRequestStatus;

/**
 * Represents a pending action related to group membership.
 * <p>
 * This model serves as a unified Data Transfer Object (DTO) for two scenarios:
 * <ol>
 * <li><b>Invitations:</b> A group admin invites the current user to join.</li>
 * <li><b>Join Requests:</b> A user asks to join a private group managed by the current user.</li>
 * </ol>
 * The {@code type} field distinguishes between these scenarios to render the correct UI.
 */
public class GroupRequest {

    /**
     * The unique identifier of the request in the database.
     * <p>
     * Annotated with {@link SerializedName} to handle multiple JSON field variations
     * ("id", "requestId", "request_id"), ensuring robustness against backend API changes.
     */
    @SerializedName(value = "id", alternate = {"requestId", "request_id"})
    private Long id;

    /**
     * The display name of the person initiating the action.
     * <ul>
     * <li>If type is INVITE: Name of the person who sent the invite.</li>
     * <li>If type is REQUEST: Name of the user wanting to join.</li>
     * </ul>
     */
    @SerializedName(value = "sender_name", alternate = {"senderName", "sender"})
    private String senderName;

    /**
     * The name of the Study Group involved in this request.
     * Used for display purposes in the notification list.
     */
    @SerializedName(value = "group_name", alternate = {"groupName", "group"})
    private String groupName;

    /**
     * The technical ID of the related group.
     * Required to perform API actions (like accepting the request) which target a specific group.
     */
    @SerializedName(value = "group_id", alternate = {"groupId"})
    private Long groupId;

    /**
     * Discriminator field indicating the nature of the request.
     * <p>
     * Expected values:
     * <ul>
     * <li><b>"INVITE"</b>: Renders an envelope icon. User decides to join or not.</li>
     * <li><b>"REQUEST"</b>: Renders a user icon. Admin decides to admit or reject.</li>
     * </ul>
     */
    @SerializedName(value = "type")
    private String type;

    /**
     * The current lifecycle state of the request (e.g., PENDING, ACCEPTED).
     */
    @SerializedName(value = "status")
    private GroupRequestStatus status;

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

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