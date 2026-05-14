package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AdminLoginRequest;
import com.example.demo.model.Games;
import com.example.demo.model.Purchase;
import com.example.demo.model.User;
import com.example.demo.service.AdminServices;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminServices adminservices;

    @GetMapping("/test")
    public String test() {
        return "Admin controller is working";
    }

    @PostMapping("/login")
    public String loginAdmin(@RequestBody AdminLoginRequest request) {
        return adminservices.loginAdmin(request.getEmail(), request.getPassword());
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return adminservices.getAllUsers();
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        adminservices.deleteUser(id);
        return "User deleted successfully";
    }

    @PutMapping("/user/ban/{id}")
    public String banUser(@PathVariable Long id) {
        return adminservices.banUser(id);
    }

    @PutMapping("/user/unban/{id}")
    public String unbanUser(@PathVariable Long id) {
        return adminservices.unbanUser(id);
    }

    @GetMapping("/games")
    public List<Games> getGames() {
        return adminservices.getAllGames();
    }

    @DeleteMapping("/game/{id}")
    public String deleteGame(@PathVariable Long id) {
        adminservices.deleteGame(id);
        return "Game deleted successfully";
    }

    @GetMapping("/purchases")
    public List<Purchase> getPurchases() {
        return adminservices.getAllPurchases();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return adminservices.getStats();
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
