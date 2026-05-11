package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.ReviewRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.service.ReviewsServices;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    @Autowired
    private ReviewsServices reviewService;

    @PostMapping("/add/{gameId}")
    public String addReview(@PathVariable Long gameId,
                            @RequestBody ReviewRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return reviewService.addReview(gameId, email, request);
    }

    @GetMapping("/{gameId}")
    public List<ReviewResponse> getReviews(@PathVariable Long gameId) {
        return reviewService.getReviews(gameId);
    }

    @GetMapping("/average/{gameId}")
    public double getAverageRating(@PathVariable Long gameId) {
        return reviewService.getAverageRating(gameId);
    }
}
