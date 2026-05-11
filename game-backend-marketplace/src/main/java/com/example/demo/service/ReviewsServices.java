package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ReviewRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.model.Games;
import com.example.demo.model.Reviews;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.ReviewsRepository;

@Service
public class ReviewsServices {

    @Autowired
    private ReviewsRepository reviewRepository;

    @Autowired
    private GameRepository gameRepository;

    public String addReview(Long gameId, String userEmail, ReviewRequest request) {

        Games game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return "Game not found";
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            return "Rating must be between 1 and 5";
        }

        if (reviewRepository.existsByUserEmailAndGame(userEmail, game)) {
            return "You already reviewed this game";
        }

        Reviews review = new Reviews();
        review.setUserEmail(userEmail);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setGame(game);

        reviewRepository.save(review);

        return "Review added successfully";
    }

    public List<ReviewResponse> getReviews(Long gameId) {

        Games game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return new ArrayList<>();
        }

        List<Reviews> reviews = reviewRepository.findByGame(game);
        List<ReviewResponse> response = new ArrayList<>();

        for (Reviews review : reviews) {
            response.add(new ReviewResponse(
                    review.getUserEmail(),
                    review.getRating(),
                    review.getComment(),
                    review.getCreatedAt()
            ));
        }

        return response;
    }

    public double getAverageRating(Long gameId) {

        Games game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return 0;
        }

        List<Reviews> reviews = reviewRepository.findByGame(game);

        if (reviews.isEmpty()) {
            return 0;
        }

        double total = 0;

        for (Reviews review : reviews) {
            total += review.getRating();
        }

        return total / reviews.size();
    }
}