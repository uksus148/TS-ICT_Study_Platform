package com.synapse.client.model;

import com.google.gson.annotations.SerializedName;
import com.synapse.client.MembershipRole;

public class User {
    @SerializedName(value = "user_id", alternate = {"id", "userId"})
    private Long user_id;

    @SerializedName(value = "name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;
    @SerializedName("role")
    private MembershipRole role;

    public User() {}

    public Long getUser_id() { return user_id; }
    public void setUser_id(Long user_id) { this.user_id = user_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public  MembershipRole getRole() { return role; }

    @Override
    public String toString() {
        return name;
    }
}