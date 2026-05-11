package com.example.demo.dto;

import java.time.LocalDateTime;

public class ReviewResponse {

    private String userEmail;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponse(String userEmail, int rating, String comment, LocalDateTime createdAt) {
        this.userEmail = userEmail;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}