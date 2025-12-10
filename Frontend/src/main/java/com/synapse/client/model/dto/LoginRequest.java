package com.synapse.client.model.dto;

/**
 * Immutable data carrier used for User Authentication.
 * <p>
 * This DTO encapsulates the credentials required to establish a user session.
 * It is serialized into JSON and sent to the {@code /auth/login} endpoint.
 *
 * @param email    The user's unique email address used as the principal identifier.
 * @param password The user's secret password (sent in plain text over HTTPS,
 * typically hashed on the server side).
 */
public record LoginRequest(String email, String password) {}