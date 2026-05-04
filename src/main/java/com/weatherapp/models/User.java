package com.weatherapp.models;

public class User {

    private String id;
    private String name;
    private String city;
    private String address;

    // Constructor
    public User(String name, String city, String address) {
        this.name = name;
        this.city = city;
        this.address = address;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getAddress() { return address; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setAddress(String address) { this.address = address; }
}