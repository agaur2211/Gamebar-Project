package com.example.demo.controller;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.wishlistResponse;
import com.example.demo.service.WishlistService;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    private String getEmail() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    @PostMapping("/add/{gameId}")
    public String add(@PathVariable Long gameId) {
        return wishlistService.addToWishlist(gameId, getEmail());
    }

    @GetMapping("/my")
    public List<wishlistResponse> myWishlist() {
        return wishlistService.getMyWishlist(getEmail());
    }

    @DeleteMapping("/remove/{gameId}")
    public String remove(@PathVariable Long gameId) {
        return wishlistService.removeFromWishlist(gameId, getEmail());
    }
}