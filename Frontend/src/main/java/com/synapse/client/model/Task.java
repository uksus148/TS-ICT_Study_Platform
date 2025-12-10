package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;
import com.synapse.client.enums.TaskStatus;
import com.synapse.client.UserSession;

import java.time.LocalDateTime;

/**
 * Represents a specific unit of work or assignment within a Study Group.
 * <p>
 * This model encapsulates all details required to track a task's lifecycle, including:
 * <ul>
 * <li>Basic info (Title, Description).</li>
 * <li>Time constraints (Deadline).</li>
 * <li>Progress state ({@link TaskStatus}).</li>
 * <li>Ownership and Association (Creator ID, Group ID).</li>
 * </ul>
 */
public class Task {

    /**
     * The unique identifier for the task.
     * <p>
     * Annotated with {@link SerializedName} to robustly handle inconsistent naming
     * from the backend (accepts "task_id", "id", or "taskId").
     */
    @SerializedName(value = "task_id", alternate = {"id", "taskId"})
    private Long task_id;

    /**
     * The ID of the {@link Group} this task belongs to.
     */
    @SerializedName(value = "group_id", alternate = {"groupId", "studyGroup"})
    private Long group_id;

    /**
     * The ID of the user who created the task.
     * Used to verify permissions (e.g., only the creator or admin can edit/delete).
     */
    @SerializedName(value = "created_by", alternate = {"createdBy", "userId", "owner", "ownerId"})
    private Long created_by;

    /**
     * A short summary or header of the task.
     */
    @SerializedName(value = "title")
    private String title;

    /**
     * Detailed instructions or context for the task.
     */
    @SerializedName(value = "description")
    private String description;

    /**
     * The target date and time for completion.
     */
    @SerializedName(value = "deadline")
    private LocalDateTime deadline;

    /**
     * The current progress state (e.g., IN_PROGRESS, COMPLETED).
     */
    @SerializedName(value = "status")
    private TaskStatus status;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Task() {
    }

    /**
     * Full constructor for instantiating a task when all details are known (e.g., from DB).
     */
    public Task(Long task_id, Long group_id, Long created_by, String title, String description, LocalDateTime deadline, TaskStatus status) {
        this.task_id = task_id;
        this.group_id = group_id;
        this.created_by = created_by;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }

    /**
     * Convenience constructor for creating NEW tasks from the UI.
     * <p>
     * Automatically attempts to fetch the current user's ID from the {@link UserSession}
     * to populate the {@code created_by} field.
     *
     * @param title       The task title.
     * @param description The task description.
     * @param deadline    The deadline (can be null).
     * @param status      The initial status (usually IN_PROGRESS).
     */
    public Task(String title, String description, LocalDateTime deadline, TaskStatus status) {
        this.task_id = null;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
        this.group_id = null;

        // Auto-assign creator if session is active
        if (UserSession.getInstance() != null && UserSession.getInstance().getUserId() != null) {
            this.created_by = UserSession.getInstance().getUserId();
        } else {
            this.created_by = null;
        }
    }

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getTask_id() { return task_id; }

    public Long getGroup_id() { return group_id; }
    public void setGroup_id(Long group_id) { this.group_id = group_id; }

    public Long getCreated_by() { return created_by; }
    public void setCreated_by(Long created_by) { this.created_by = created_by; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public TaskStatus getStatus() { return this.status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}