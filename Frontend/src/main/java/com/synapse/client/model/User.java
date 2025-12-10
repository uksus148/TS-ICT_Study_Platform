package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;
import com.synapse.client.enums.MembershipRole;

/**
 * Represents a registered user of the application.
 * <p>
 * This model serves as the primary Data Transfer Object (DTO) for:
 * <ul>
 * <li><b>Authentication:</b> Carrying credentials during Login/Register.</li>
 * <li><b>Profile Management:</b> storing user details like Name and Email.</li>
 * <li><b>Membership Display:</b> showing lists of group members.</li>
 * </ul>
 */
public class User {

    /**
     * The unique identifier for the user in the database.
     * <p>
     * Annotated with {@link SerializedName} to handle inconsistent JSON field names
     * from the backend (accepts "user_id", "id", or "userId").
     */
    @SerializedName(value = "user_id", alternate = {"id", "userId"})
    private Long user_id;

    /**
     * The display name of the user (e.g., "Jane Doe").
     */
    @SerializedName(value = "name")
    private String name;

    /**
     * The user's email address.
     * Serves as the unique principal for login authentication.
     */
    @SerializedName("email")
    private String email;

    /**
     * The user's password.
     * <p>
     * <b>Security Note:</b> This field is primarily populated when sending data TO the server
     * (Registration, Password Change). When receiving User objects FROM the server,
     * this field should ideally be null or empty for security reasons.
     */
    @SerializedName("password")
    private String password;

    /**
     * The role of the user within a specific context (e.g., Group Owner vs Member).
     */
    @SerializedName("role")
    private MembershipRole role;

    /**
     * Default constructor required for Gson deserialization.
     */
    public User() {}

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public MembershipRole getRole() { return role; }
    public void setRole(MembershipRole role) { this.role = role; }

    /**
     * Returns the string representation of the user.
     * <p>
     * Overridden to return the {@code name} property. This is crucial for JavaFX controls
     * (like ComboBox or ListView) which use the {@code toString()} method to determine
     * what text to display if no custom CellFactory is provided.
     *
     * @return The user's name.
     */
    @Override
    public String toString() {
        return name;
    }
}