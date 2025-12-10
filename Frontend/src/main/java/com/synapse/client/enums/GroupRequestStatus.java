package com.synapse.client.enums;

/**
 * Represents the lifecycle state of a request to join a study group.
 * <p>
 * This status tracks the decision process for:
 * <ul>
 * <li><b>Join Requests:</b> A user asking to join a private group.</li>
 * <li><b>Invitations:</b> A group admin inviting a user to join.</li>
 * </ul>
 */
public enum GroupRequestStatus {

    /**
     * The request has been sent but not yet processed.
     * No decision has been made by the receiving party (Admin or User).
     */
    PENDING,

    /**
     * The request was approved.
     * The user has been successfully added as a member of the group.
     */
    ACCEPTED,

    /**
     * The request was denied or declined.
     * No membership was granted, and the request is considered closed.
     */
    REJECTED
}