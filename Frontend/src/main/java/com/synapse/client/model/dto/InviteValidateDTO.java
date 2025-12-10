package com.synapse.client.model.dto;

import com.synapse.client.enums.AcceptStatus;

/**
 * Immutable data carrier used to convey the result of an invitation token validation check.
 * <p>
 * This DTO is returned by the API when the client performs a pre-check on an invitation code.
 * It allows the application to provide preview information (like the group name) to the user
 * before they commit to joining.
 *
 * @param groupName The display name of the Study Group associated with the token.
 * This is used to show the user exactly which group they are about to join
 * (e.g., "Do you want to join 'Advanced Physics'?").
 * @param valid     The current validity status of the token.
 * If {@link AcceptStatus#ACTIVE}, the token can be used.
 * If {@link AcceptStatus#EXPIRED} or {@link AcceptStatus#USED}, the UI should show an error.
 */
public record InviteValidateDTO(String groupName, AcceptStatus valid) {}