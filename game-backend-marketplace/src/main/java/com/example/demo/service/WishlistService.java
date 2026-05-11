package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.wishlistResponse;
import com.example.demo.model.Games;
import com.example.demo.model.Wishlist;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.WishlistRepository;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private GameRepository gameRepository;

    // ➕ ADD TO WISHLIST
    public String addToWishlist(Long gameId, String email) {

        Games game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return "Game not found";
        }

        if (wishlistRepository.existsByUserEmailAndGame(email, game)) {
            return "Already in wishlist";
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserEmail(email);
        wishlist.setGame(game);

        wishlistRepository.save(wishlist);

        return "Added to wishlist";
    }

    // 📄 GET MY WISHLIST
    public List<wishlistResponse> getMyWishlist(String email) {

        List<Wishlist> list = wishlistRepository.findByUserEmail(email);

        List<wishlistResponse> response = new ArrayList<>();

        for (Wishlist w : list) {

            Games game = w.getGame();

            if (game != null) {
                response.add(new wishlistResponse(
                        game.getId(),
                        game.getTitle(),
                        game.getDescription(),
                        game.getPrice(),
                        game.getImageUrl()
                ));
            }
        }

        return response;
    }

    // ❌ REMOVE FROM WISHLIST
    @Transactional
    public String removeFromWishlist(Long gameId, String email) {

        Games game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return "Game not found";
        }

        if (!wishlistRepository.existsByUserEmailAndGame(email, game)) {
            return "Game not found in wishlist";
        }

        wishlistRepository.deleteByUserEmailAndGame(email, game);

        return "Removed from wishlist";
    }
}