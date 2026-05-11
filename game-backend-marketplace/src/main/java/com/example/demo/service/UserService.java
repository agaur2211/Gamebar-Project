package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.MyGameResponse;
import com.example.demo.dto.userProfileUpdateRequest;
import com.example.demo.dto.userprofileResponse;
import com.example.demo.model.Games;
import com.example.demo.model.Purchase;
import com.example.demo.model.User;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PurchaseRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.jwtUtil;

@Service
public class UserService {

@Autowired 
private UserRepository userrepository;
@Autowired
private GameRepository gamerepository;
@Autowired
private PurchaseRepository purchaserepository;
@Autowired
private BCryptPasswordEncoder passwordEncoder;
@Autowired
private jwtUtil jwtutil;

public String register(User user) {

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    userrepository.save(user);

    return "User registered successfully";
}
	
public String loginUser(User user) {

    User existingUser = userrepository.findByEmail(user.getEmail());

    if (existingUser == null) {
        return "User not found";
    }
    
    if (user.isBanned()) {
        throw new RuntimeException("User is banned");
    }

    if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {

        String token = jwtutil.generateToken(user.getEmail(), "USER");

        return token;
    } else {
        return "Invalid password";
    }
}

// 🔍 GET PROFILE
public userprofileResponse getProfile(String email) {

    User user = userrepository.findByEmail(email);

    if (user == null) {
        throw new RuntimeException("User not found");
    }

    return new userprofileResponse(
            user.getName(),
            user.getEmail()
    );
}

// ✏️ UPDATE PROFILE
public String updateProfile(String email, userProfileUpdateRequest request) {

    User user = userrepository.findByEmail(email);

    if (user == null) {
        throw new RuntimeException("User not found");
    }

    user.setName(request.getName());

    userrepository.save(user); 

    return "Profile updated successfully";
}
public String updateProfile(String email, User updatedUser) {

    User existingUser = userrepository.findByEmail(email);

    if (existingUser == null) {
        return "User not found";
    }

    existingUser.setName(updatedUser.getName());
    existingUser.setWhatsappNumber(updatedUser.getWhatsappNumber());

    userrepository.save(existingUser);

    return "Profile updated successfully";
}

public List<MyGameResponse> getMyGames(String email) {

	// 1. Get all purchases of user
	List<Purchase> purchases = purchaserepository.findByBuyerEmail(email);

	// 2. Extract game IDs (NO STREAM ISSUE)
	List<Long> gameIds = new ArrayList<>();

	for (Purchase p : purchases) {
		gameIds.add(p.getGame().getId());   // make sure this method exists
	}
    // 3. Fetch all games in ONE query
    List<Games> games = gamerepository.findByIdIn(gameIds);

    // 4. Convert to response DTO
    return games.stream()
            .map(g -> new MyGameResponse(
                    g.getTitle(),
                    g.getDescription(),
                    g.getPrice(),
                    g.getImageUrl()   // ✅ image included
            ))
            .toList();
}
}
