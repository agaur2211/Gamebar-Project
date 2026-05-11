package com.example.demo.service;

import java.io.File;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Games;
import com.example.demo.model.User;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class GamesService {

    @Autowired
    private GameRepository gamerepo;

    @Autowired
    private UserRepository userRepository;

    // ✅ Add Game
    public Games addGame(Games game) {
        return gamerepo.save(game);
    }

    // ✅ Get All Games (WITH seller WhatsApp)
    public List<Map<String, Object>> getAllGames() {

        List<Games> games = gamerepo.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Games game : games) {

            Map<String, Object> map = new HashMap<>();

            map.put("id", game.getId());
            map.put("title", game.getTitle());
            map.put("description", game.getDescription());
            map.put("price", game.getPrice());
            map.put("imageUrl", game.getImageUrl());
            map.put("sellerEmail", game.getSellerEmail());

            // 🔥 Fetch seller WhatsApp
            User seller = userRepository.findByEmail(game.getSellerEmail());

            if (seller != null) {
                map.put("sellerWhatsapp", seller.getWhatsappNumber());
            } else {
                map.put("sellerWhatsapp", null);
            }

            response.add(map);
        }

        return response;
    }

    // ✅ Upload Image
    public String uploadImage(Long gameId, MultipartFile file) {

        try {
            Games game = gamerepo.findById(gameId).orElse(null);

            if (game == null) {
                return "Game not found";
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File destination = new File(uploadDir + fileName);
            file.transferTo(destination);

            game.setImageUrl(fileName);
            gamerepo.save(game);

            return "Image uploaded successfully";

        } catch (Exception e) {
            return "Upload failed: " + e.getMessage();
        }
    }

    // 🔍 SEARCH
    public List<Games> searchGames(String title) {
        return gamerepo.findByTitleContainingIgnoreCase(title);
    }

    // 💰 FILTER
    public List<Games> filterGames(double minPrice, double maxPrice) {
        return gamerepo.findByPriceBetween(minPrice, maxPrice);
    }

    // 🔃 SORT
    public List<Games> sortGames(String field, String direction) {

        List<String> allowedFields = Arrays.asList("price", "title", "id");

        if (!allowedFields.contains(field)) {
            field = "price";
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(Sort.Direction.DESC, field)
                : Sort.by(Sort.Direction.ASC, field);

        return gamerepo.findAll(sort);
    }
    
    public String updateGame(Long gameId, Games updatedGame, String email) {

        Games existingGame = gamerepo.findById(gameId).orElse(null);

        if (existingGame == null) {
            return "Game not found";
        }

        // Only original seller can update
        if (!existingGame.getSellerEmail().equals(email)) {
            return "You are not allowed to update this game";
        }

        existingGame.setTitle(updatedGame.getTitle());
        existingGame.setDescription(updatedGame.getDescription());
        existingGame.setPrice(updatedGame.getPrice());

        gamerepo.save(existingGame);

        return "Game updated successfully";
    }
    
    public String deleteGame(Long gameId, String email) {

        Games existingGame = gamerepo.findById(gameId).orElse(null);

        if (existingGame == null) {
            return "Game not found";
        }

        // Only original seller can delete
        if (!existingGame.getSellerEmail().equals(email)) {
            return "You are not allowed to delete this game";
        }

        gamerepo.delete(existingGame);

        return "Game deleted successfully";
    }
}