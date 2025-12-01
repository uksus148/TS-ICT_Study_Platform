package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName(value = "user_id")
    private Long user_id;
    @SerializedName(value = "name")
    private String username;
    @SerializedName(value = "email")
    private String email;

    public User() {}

    public User(Long user_id, String username, String email) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
    }

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return username;
    }
}