package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Admin;
import com.example.demo.model.Games;
import com.example.demo.model.Purchase;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PurchaseRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.jwtUtil;

import jakarta.transaction.Transactional;

@Service
public class AdminServices {

    // ============================================================
    // 🔗 REPOSITORIES
    // ============================================================

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PurchaseRepository purchaseRepo;

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final jwtUtil jwtUtil;

    // ============================================================
    // 🧱 CONSTRUCTOR INJECTION (BEST PRACTICE)
    // ============================================================

    public AdminServices(AdminRepository adminRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         jwtUtil jwtUtil) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ============================================================
    // 🔐 ADMIN AUTHENTICATION
    // ============================================================

    /**
     * Create a new admin (disabled by default)
     */
    public Admin createAdmin(String email, String password) {

        Admin existing = adminRepository.findByEmail(email);

        if (existing != null) {
            throw new RuntimeException("Admin already exists");
        }

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setEnabled(false); // must be approved

        return adminRepository.save(admin);
    }

    /**
     * Approve admin (enable login)
     */
    public String approveAdmin(Long adminId) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setEnabled(true);
        adminRepository.save(admin);

        return "Admin approved successfully";
    }

    /**
     * Admin login → returns JWT token
     */
    public String loginAdmin(String email, String password) {

        Admin admin = adminRepository.findByEmail(email);

        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        if (!admin.isEnabled()) {
            throw new RuntimeException("Admin not approved yet");
        }

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(admin.getEmail(), "ADMIN");
    }
    
    public String makeAdmin(Long userId) {

        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            return "User not found";
        }

        Admin existingAdmin = adminRepository.findByEmail(user.getEmail());

        if (existingAdmin != null) {
            return "Already admin";
        }

        Admin admin = new Admin();

        admin.setEmail(user.getEmail());
        admin.setPassword(user.getPassword());
        admin.setEnabled(true);

        adminRepository.save(admin);

        return "Admin created successfully";
    }
    
    @Transactional
    public String removeAdmin(Long userId) {

        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            return "User not found";
        }

        Admin admin = adminRepository.findByEmail(user.getEmail());

        if (admin == null) {
            return "Not an admin";
        }

        adminRepository.delete(admin);

        return "Admin removed successfully";
    }
    
    

    // ============================================================
    // 👥 USER MANAGEMENT
    // ============================================================

    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Delete user by ID
     */
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    // ============================================================
    // 🎮 GAME MANAGEMENT
    // ============================================================

    /**
     * Get all games
     */
    public List<Games> getAllGames() {
        return gameRepo.findAll();
    }

    /**
     * Delete game by ID
     */
    public void deleteGame(Long id) {
        gameRepo.deleteById(id);
    }

    // ============================================================
    // 💰 PURCHASE MANAGEMENT
    // ============================================================

    /**
     * Get all purchases
     */
    public List<Purchase> getAllPurchases() {
        return purchaseRepo.findAll();
    }

    // ============================================================
    // 📊 ADMIN DASHBOARD STATS
    // ============================================================

    /**
     * Get platform statistics:
     * - total users
     * - total games
     * - total purchases
     * - total revenue (calculated from game price)
     */
    public Map<String, Object> getStats() {

        long totalUsers = userRepo.count();
        long totalGames = gameRepo.count();
        long totalPurchases = purchaseRepo.count();

        // 🔥 Revenue calculation using Games price
        List<Purchase> purchases = purchaseRepo.findAll();
        double totalRevenue = 0;

        for (Purchase p : purchases) {
            if (p.getGame() != null) {
                totalRevenue += p.getGame().getPrice();
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalGames", totalGames);
        stats.put("totalPurchases", totalPurchases);
        stats.put("totalRevenue", totalRevenue);

        return stats;
    }
    
    public String banUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBanned(true);
        userRepo.save(user);

        return "User banned successfully";
    }

    public String unbanUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBanned(false);
        userRepo.save(user);

        return "User unbanned successfully";
    }
    
    public Map<String, Double> getSellerEarnings() {

        List<Purchase> purchases = purchaseRepo.findAll();
        Map<String, Double> earnings = new HashMap<>();

        for (Purchase p : purchases) {
            Games game = p.getGame();

            if (game != null) {
                String seller = game.getSellerEmail();
                double price = game.getPrice();

                earnings.put(seller,
                    earnings.getOrDefault(seller, 0.0) + price);
            }
        }

        return earnings;
    }
    
    public Map<String, Integer> getTopGames() {

        List<Purchase> purchases = purchaseRepo.findAll();
        Map<String, Integer> count = new HashMap<>();

        for (Purchase p : purchases) {
            Games game = p.getGame();

            if (game != null) {
                String title = game.getTitle();

                count.put(title,
                    count.getOrDefault(title, 0) + 1);
            }
        }

        return count;
    }
}