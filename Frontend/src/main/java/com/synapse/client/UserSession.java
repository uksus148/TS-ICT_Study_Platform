package com.synapse.client;

import com.synapse.client.model.User;

/**
 * Manages the client-side authentication state of the application.
 * <p>
 * This class implements the <b>Singleton</b> pattern to provide a global access point
 * to the currently logged-in user's information. It allows different parts of the
 * application (Controllers, Stores) to:
 * <ul>
 * <li>Check if a user is currently logged in.</li>
 * <li>Retrieve the current user's ID for API requests (creating tasks, groups, etc.).</li>
 * <li>Access profile details (Name, Email) for display.</li>
 * </ul>
 */
public class UserSession {

    private static UserSession instance;
    private User currentUser;

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private UserSession() {}

    /**
     * Returns the global singleton instance of the UserSession.
     *
     * @return The active session instance.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Establishes a new session for a specific user.
     * <p>
     * Typically called immediately after a successful response from the Login or Register API.
     *
     * @param user The user object received from the backend.
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Invalidates the current session.
     * <p>
     * Clears the stored user data, effectively logging the user out on the client side.
     * Should be called in conjunction with the API logout request.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Retrieves the full details of the currently logged-in user.
     *
     * @return The User object, or {@code null} if no user is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * A convenience helper method to safely retrieve the ID of the current user.
     * <p>
     * Used frequently by Stores when constructing new objects (Tasks, Groups)
     * that need to be linked to the creator.
     *
     * @return The User ID, or {@code null} if no user is logged in.
     */
    public Long getUserId() {
        return currentUser != null ? currentUser.getUser_id() : null;
    }
}