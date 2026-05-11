package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.config.SecurityConfig;
import com.example.demo.model.Games;
import com.example.demo.service.GamesService;

@RestController
@RequestMapping("/games")
public class GamesController {

    private final SecurityConfig securityConfig;
@Autowired
private GamesService gameservice;

    GamesController(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

@PostMapping("/add")
public Games addGame(@RequestBody Games game) {

	org.springframework.security.core.@Nullable Authentication auth = 
			SecurityContextHolder.getContext().getAuthentication();

    String email = auth.getName();

    System.out.println("Controller Email: " + email);

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

    System.out.println("File received: " + file);
    return gameservice.uploadImage(id, file);
}

@GetMapping("/search")
public List<Games> searchGames(@RequestParam String title) {
    return gameservice.searchGames(title);
}

@GetMapping("/filter")
public List<Games> filterGames(
        @RequestParam double minPrice,
        @RequestParam double maxPrice) {

    return gameservice.filterGames(minPrice, maxPrice);
}

@PutMapping("/update/{gameId}")
public String updateGame(@PathVariable Long gameId,
                         @RequestBody Games updatedGame) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return gameservice.updateGame(gameId, updatedGame, email);
}

@DeleteMapping("/delete/{gameId}")
public String deleteGame(@PathVariable Long gameId) {

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    return gameservice.deleteGame(gameId, email);
}

@GetMapping
public List<Games> getSortedGames(
        @RequestParam(defaultValue = "price") String sort,
        @RequestParam(defaultValue = "asc") String direction) {

    return gameservice.sortGames(sort, direction);
}
}
