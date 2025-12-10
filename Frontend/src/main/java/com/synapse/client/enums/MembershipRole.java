package com.synapse.client.enums;

/**
 * Defines the authorization levels for a user within a specific Study Group.
 * <p>
 * These roles determine the set of privileges available to the user in the UI,
 * such as the ability to manage other members or delete the group.
 */
public enum MembershipRole {

    /**
     * The creator or administrator of the group.
     * <p>
     * Has full access rights, including:
     * <ul>
     * <li>Removing (kicking) other members.</li>
     * <li>Editing group details (Name, Description).</li>
     * <li>Deleting the group entirely.</li>
     * </ul>
     */
    OWNER,

    /**
     * A standard participant of the group.
     * <p>
     * Can contribute content (create tasks, upload resources) and view group data,
     * but does not have administrative privileges (cannot kick users or delete the group).
     */
    MEMBER
}