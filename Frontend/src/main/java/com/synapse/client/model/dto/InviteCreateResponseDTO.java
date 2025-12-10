package com.synapse.client.model.dto;

import java.time.LocalDateTime;

/**
 * Immutable data carrier representing the created invitation details.
 * <p>
 * This DTO is received from the server API after a successful request to generate
 * a new invitation link. It contains the secure token that the user can share
 * with others and the timestamp when it will cease to function.
 *
 * @param token     The unique, secure string (code) used to join the group.
 * This is the value displayed to the user in the "Invitation Code" dialog.
 * @param expiresAt The timestamp indicating when this specific token becomes invalid.
 * Used to verify validity or display expiration info to the user.
 */
public record InviteCreateResponseDTO(String token, LocalDateTime expiresAt) {}