package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AdminLoginRequest;
import com.example.demo.dto.AdminRegisterRequest;
import com.example.demo.model.Admin;
import com.example.demo.model.Games;
import com.example.demo.model.Purchase;
import com.example.demo.model.User;
import com.example.demo.service.AdminServices;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminServices adminservices;

    // ============================================================
    // 🔐 ADMIN AUTHENTICATION & MANAGEMENT
    // ============================================================

    /**
     * Create a new admin account (initially not approved)
     * This endpoint is used to register admins in the system
     */
    
    /**
     * Admin login endpoint
     * Returns JWT token if credentials are valid
     */
    @PostMapping("/login")
    public String loginAdmin(@RequestBody AdminLoginRequest request) {
        return adminservices.loginAdmin(
                request.getEmail(),
                request.getPassword()
        );
    }

    // ============================================================
    // 👥 USER MANAGEMENT
    // ============================================================

    /**
     * Get all registered users
     * (Admin-only access)
     */
    @GetMapping("/users")
    public List<User> getUsers() {
        return adminservices.getAllUsers();
    }

    /**
     * Delete a user by ID
     * Useful for removing unwanted or abusive users
     */
    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminservices.deleteUser(id);
        return "User deleted successfully";
    }

    // ============================================================
    // 🎮 GAME MANAGEMENT
    // ============================================================

    /**
     * Get all games available on the platform
     */
    @GetMapping("/games")
    public List<Games> getGames() {
        return adminservices.getAllGames();
    }

    /**
     * Delete a game by ID
     * Used for moderation or removing invalid listings
     */
    @DeleteMapping("/game/{id}")
    public String deleteGame(@PathVariable Long id) {
        adminservices.deleteGame(id);
        return "Game deleted successfully";
    }

    // ============================================================
    // 💰 PURCHASE MANAGEMENT
    // ============================================================

    /**
     * Get all purchases made on the platform
     * Helps admin track transactions and activity
     */
    @GetMapping("/purchases")
    public List<Purchase> getPurchases() {
        return adminservices.getAllPurchases();
    }

    // ============================================================
    // 📊 ADMIN DASHBOARD
    // ============================================================

    /**
     * Get platform statistics
     * Includes:
     * - Total users
     * - Total games
     * - Total purchases
     * - Total revenue
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return adminservices.getStats();
    }
    
    @PutMapping("/user/ban/{id}")
    public String banUser(@PathVariable Long id) {
        return adminservices.banUser(id);
    }

    @PutMapping("/user/unban/{id}")
    public String unbanUser(@PathVariable Long id) {
        return adminservices.unbanUser(id);
    }
    
    @PutMapping("/make-admin/{userId}")
    public String makeAdmin(@PathVariable Long userId) {
        return adminservices.makeAdmin(userId);
    }
    
    @PutMapping("/remove-admin/{userId}")
    public String removeAdmin(@PathVariable Long userId) {
        return adminservices.removeAdmin(userId);
    }
    
    @GetMapping("/earnings")
    public Map<String, Double> getEarnings() {
        return adminservices.getSellerEarnings();
    }
    
    @GetMapping("/top-games")
    public Map<String, Integer> getTopGames() {
        return adminservices.getTopGames();
    }
}