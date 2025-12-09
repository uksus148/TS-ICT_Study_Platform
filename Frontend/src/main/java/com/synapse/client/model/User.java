package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName(value = "id", alternate = {"user_id", "userId"})
    private Long user_id;

    @SerializedName(value = "name", alternate = {"username"})
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

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

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return username; // Чтобы в списках отображалось имя
    }
}