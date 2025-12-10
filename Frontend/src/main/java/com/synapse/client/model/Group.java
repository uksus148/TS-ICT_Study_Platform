package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Study Group entity within the application.
 * <p>
 * This model acts as a Data Transfer Object (DTO) that maps JSON responses from the backend
 * to a Java object. It contains the fundamental details of a group, such as its identity,
 * descriptive metadata, and ownership information.
 */
public class Group {

    /**
     * The unique identifier for the study group.
     * <p>
     * Annotated with {@link SerializedName} to handle variations in JSON field names
     * from the backend (e.g., accepts "groupId", "id", or "group_id").
     */
    @SerializedName(value = "groupId", alternate = {"id", "group_id"})
    private Long group_id;

    /**
     * The display name of the group (e.g., "Advanced Calculus").
     */
    @SerializedName(value = "name")
    private String name;

    /**
     * A brief explanation of the group's purpose or topic.
     */
    @SerializedName(value = "description")
    private String description;

    /**
     * The unique identifier (User ID) of the group's creator/administrator.
     * Used to determine permission levels (e.g., showing the "Delete Group" button).
     */
    @SerializedName(value = "created_by", alternate = {"createdBy"})
    private Long created_by;

    /**
     * The display name of the group's creator (e.g., "John Doe").
     * This field is typically populated by the backend DTO for UI display purposes,
     * avoiding the need for an extra API call to fetch user details.
     */
    @SerializedName(value = "groupOwner")
    private String groupOwner;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Group() {
    }

    /**
     * Parameterized constructor for creating new instances manually.
     *
     * @param group_id    The group ID (null for new groups).
     * @param name        The group name.
     * @param description The group description.
     * @param created_by  The ID of the creating user.
     */
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

    public String getGroupOwner() {
        return groupOwner;
    }

    /**
     * Returns the string representation of the group.
     * <p>
     * Overridden to return the {@code name} property. This is particularly useful
     * when binding `Group` objects directly to JavaFX controls (like ComboBox or ListView)
     * without a custom cell factory.
     *
     * @return The name of the group.
     */
    @Override
    public String toString() {
        return this.name;
    }
}