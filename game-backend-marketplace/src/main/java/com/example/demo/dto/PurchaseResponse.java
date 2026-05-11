package com.example.demo.dto;

public class PurchaseResponse {

    private String title;
    private String description;
    private double price;

    public PurchaseResponse(String title, String description, double d) {
        this.title = title;
        this.description = description;
        this.price = d;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}