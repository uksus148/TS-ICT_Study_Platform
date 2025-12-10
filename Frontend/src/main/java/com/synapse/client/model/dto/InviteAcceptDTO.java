package com.synapse.client.model.dto;

import com.synapse.client.enums.AcceptStatus;

/**
 * Immutable data carrier representing the server's response to an invitation acceptance request.
 * <p>
 * This DTO is received from the API after the client attempts to use an invitation token.
 * It tells the client whether the operation was successful and which group was involved.
 *
 * @param groupId The unique identifier of the study group the user attempted to join.
 * Used to redirect the user to the specific group page upon success.
 * @param status  The result of the operation (e.g., {@link AcceptStatus#ACTIVE} for success,
 * {@link AcceptStatus#EXPIRED} or {@link AcceptStatus#USED} for failure).
 */
public record InviteAcceptDTO(Long groupId, AcceptStatus status) {}