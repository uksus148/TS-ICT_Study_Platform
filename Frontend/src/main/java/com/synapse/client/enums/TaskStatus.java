package com.synapse.client.enums;

/**
 * Represents the execution state of a {@link com.synapse.client.model.Task}.
 * <p>
 * This enum is used throughout the application to:
 * <ul>
 * <li>Filter tasks in lists (e.g., active vs. history).</li>
 * <li>Visualize progress in the Pie Chart (Status Overview).</li>
 * <li>Determine the visual style of the task card (e.g., crossed out or highlighted).</li>
 * </ul>
 */
public enum TaskStatus {

    /**
     * The task is currently active and pending completion.
     * This is typically the default state for newly created tasks.
     */
    IN_PROGRESS,

    /**
     * The task was abandoned or stopped before completion.
     * These tasks are usually excluded from progress calculations but kept for history.
     */
    CANCELED,

    /**
     * The task has been successfully finished.
     * In the UI, this corresponds to a checked checkbox.
     */
    COMPLETED
}