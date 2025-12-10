package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a shared learning artifact within a Study Group.
 * <p>
 * This model serves as a unified container for different types of materials:
 * <ul>
 * <li><b>Files:</b> Documents, PDFs, or images uploaded by users.</li>
 * <li><b>Links:</b> URLs to external websites or resources.</li>
 * </ul>
 * The UI uses the {@code type} field to determine whether to offer a "Download"
 * or "Open Link" action.
 */
public class Resource {

    /**
     * The unique identifier of the resource.
     * <p>
     * Annotated with {@link SerializedName} to handle various JSON naming conventions
     * from the backend ("id", "resource_id").
     */
    @SerializedName(value = "resource_id", alternate = {"id"})
    private Long resource_id;

    /**
     * The display name or title of the resource.
     * Shown in the list view to identify the content.
     */
    @SerializedName(value = "title", alternate = {"name", "resourceName"})
    private String name;

    /**
     * Discriminator field indicating the nature of the resource.
     * <p>
     * Expected values:
     * <ul>
     * <li><b>"FILE"</b>: Treats {@code path} as a file location for downloading.</li>
     * <li><b>"LINK"</b>: Treats {@code path} as a URL for web browsing.</li>
     * </ul>
     */
    @SerializedName(value = "type", alternate = {"resourceType"})
    private String type;

    /**
     * The location of the resource.
     * <p>
     * Depending on the {@code type}, this string contains either:
     * <ul>
     * <li>A remote URL (e.g., "https://example.com").</li>
     * <li>A file path/URI for retrieval from the file server.</li>
     * </ul>
     */
    @SerializedName(value = "path", alternate = {"pathOrUrl", "url", "filePath"})
    private String path;

    /**
     * The ID of the study group to which this resource belongs.
     */
    @SerializedName(value = "group_id", alternate = {"studyGroup", "groupId"})
    private Long group_id;

    /**
     * The ID of the user who uploaded or created this resource.
     * Used to display "Uploaded by: [Name]" in the UI.
     */
    @SerializedName(value = "created_by", alternate = {"uploadedBy", "userId", "creatorId"})
    private Long created_by;

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

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