package com.example.demo.controller;

import java.util.List;
import com.example.demo.dto.PurchaseResponse;


import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.repository.PurchaseRepository;
import com.example.demo.service.PurchaseService;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;
	@Autowired 
	PurchaseService purchaseservices;

    PurchaseController(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }
	
	@PostMapping("/buy/{gameId}")
	public String buyGame(@PathVariable Long gameId) {
		org.springframework.security.core.@Nullable Authentication auth = 
				SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

	        return purchaseservices.buyGame(gameId, email);
	}
	
	@GetMapping("/my")
	public List<PurchaseResponse> myPurchases() {

		org.springframework.security.core.@Nullable Authentication auth = 
				SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

	    return purchaseservices.getMyPurchases(email);
	}
	

}

