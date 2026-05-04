package com.weatherapp.models;

public class Admin {

    private String id;
    private String name;
    private String email;
    private String password;
    private String role;

    // Constructor
    public Admin(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "admin";
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}