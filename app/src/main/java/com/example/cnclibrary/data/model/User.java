package com.example.cnclibrary.data.model;

public class User {
    private String email;
    private String displayName;
    private String role;

    public User(String email, String displayName,String role) {
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
