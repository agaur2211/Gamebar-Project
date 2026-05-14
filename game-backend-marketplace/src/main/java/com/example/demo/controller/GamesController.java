package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Games;
import com.example.demo.service.GamesService;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = "*")
public class GamesController {

    @Autowired
    private GamesService gameservice;

    @GetMapping("/test")
    public String test() {
        return "Games controller is working";
    }

    @PostMapping("/add")
    public Games addGame(@RequestBody Games game) {

        String email = getLoggedInEmail();

        game.setSellerEmail(email);

        return gameservice.addGame(game);
    }

    @GetMapping("/all")
    public List<Map<String, Object>> getAllGames() {
        return gameservice.getAllGames();
    }

    @PostMapping(value = "/upload/{id}", consumes = "multipart/form-data")
    public String uploadImage(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file) {

        return gameservice.uploadImage(id, file);
    }

    @GetMapping("/search")
    public List<Games> searchGames(@RequestParam String title) {
        return gameservice.searchGames(title);
    }

    @GetMapping("/filter")
    public List<Games> filterGames(@RequestParam double minPrice,
                                   @RequestParam double maxPrice) {

        return gameservice.filterGames(minPrice, maxPrice);
    }

    @PutMapping("/update/{gameId}")
    public String updateGame(@PathVariable Long gameId,
                             @RequestBody Games updatedGame) {

        String email = getLoggedInEmail();

        return gameservice.updateGame(gameId, updatedGame, email);
    }

    @DeleteMapping("/delete/{gameId}")
    public String deleteGame(@PathVariable Long gameId) {

        String email = getLoggedInEmail();

        return gameservice.deleteGame(gameId, email);
    }

    @GetMapping
    public List<Games> getSortedGames(@RequestParam(defaultValue = "price") String sort,
                                      @RequestParam(defaultValue = "asc") String direction) {

        return gameservice.sortGames(sort, direction);
    }

    private String getLoggedInEmail() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }

        return auth.getName();
    }
}
