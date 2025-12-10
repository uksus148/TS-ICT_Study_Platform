package com.synapse.client.enums;

/**
 * Represents the status of an invitation token during the validation or acceptance process.
 * <p>
 * This enum is returned by the server to indicate whether a user's attempt
 * to join a group via a token was successful or why it failed.
 */
public enum AcceptStatus {

    /**
     * The token is valid and active.
     * The operation was successful, and the user has joined (or can join) the group.
     */
    ACTIVE,

    /**
     * The token has already been redeemed.
     * This typically happens with one-time use tokens that have already been claimed.
     */
    USED,

    /**
     * The token is no longer valid because its expiration timestamp has passed.
     * The join attempt failed.
     */
    EXPIRED
}