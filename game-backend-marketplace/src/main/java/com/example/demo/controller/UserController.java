package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.MyGameResponse;
import com.example.demo.dto.userProfileUpdateRequest;
import com.example.demo.dto.userprofileResponse;
import com.example.demo.model.User;
import com.example.demo.service.UserService;

@RestController
@RequestMapping ("/users")
@CrossOrigin
public class UserController {
	@Autowired 
	private UserService userservice;
	
	@PostMapping("/register")
	public String register(@RequestBody User user) {
	    return userservice.register(user);
	}
	@PostMapping("/login")
    public String login(@RequestBody User user) {
        return userservice.loginUser(user);
    }
	
	// 👤 GET PROFILE
	@GetMapping("/profile")
	public userprofileResponse getProfile() {

	    String email = SecurityContextHolder
	                        .getContext()
	                        .getAuthentication()
	                        .getName();

	    return userservice.getProfile(email);
	}

    // ✏️ UPDATE PROFILE
	@PutMapping("/profile")
	public String updateProfile(@RequestBody userProfileUpdateRequest  request) {

	    String email = SecurityContextHolder
	                        .getContext()
	                        .getAuthentication()
	                        .getName();

	    return userservice.updateProfile(email, request);
	}
	
	@PutMapping("/update")
	public String updateProfile(@RequestBody User updatedUser) {

	    String email = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    return userservice.updateProfile(email, updatedUser);
	}
	
	@GetMapping("/my-games")
	public List<MyGameResponse> getMyGames() {

	    String email = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    return userservice.getMyGames(email);
	}
}
