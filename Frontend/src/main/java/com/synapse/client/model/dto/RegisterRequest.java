package com.synapse.client.model.dto;

/**
 * Immutable data carrier used for new User Registration.
 * <p>
 * This DTO collects the necessary information to create a new account in the system.
 * It is serialized into JSON and sent to the {@code /auth/register} API endpoint.
 *
 * @param name     The full name or display name chosen by the user.
 * @param email    The user's email address (must be unique within the system).
 * @param password The secret password for the new account (sent in plain text over HTTPS).
 */
public record RegisterRequest(String name, String email, String password) {}