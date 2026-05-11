package com.example.demo.dto;

public class userprofileResponse{

    private String name;
    private String email;

    public userprofileResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // getters
    public String getName() { return name; }
    public String getEmail() { return email; }
}