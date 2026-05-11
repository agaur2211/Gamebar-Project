package com.example.demo.service;

import com.example.demo.dto.PurchaseResponse;
import com.example.demo.model.Games;
import com.example.demo.model.Purchase;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PurchaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private GameRepository gameRepository;

    // ✅ BUY GAME
    public String buyGame(Long gameId, String buyerEmail) {

        Games game = gameRepository.findById(gameId).orElse(null);

        if (game == null) {
            return "Game not found";
        }

        // prevent buying own game
        if (game.getSellerEmail().equals(buyerEmail)) {
            return "You cannot buy your own game";
        }

        // ✅ UPDATED CHECK (uses object, not id)
        if (purchaseRepository.existsByBuyerEmailAndGame(buyerEmail, game)) {
            return "You already own this game";
        }

        Purchase purchase = new Purchase();
        purchase.setBuyerEmail(buyerEmail);

        // ✅ IMPORTANT FIX → set full object
        purchase.setGame(game);

        purchaseRepository.save(purchase);

        return "Game purchased successfully";
    }

    // ✅ GET MY PURCHASES
    public List<PurchaseResponse> getMyPurchases(String email) {

        List<Purchase> purchases = purchaseRepository.findByBuyerEmail(email);

        List<PurchaseResponse> response = new ArrayList<>();

        for (Purchase p : purchases) {

            // ✅ DIRECT ACCESS (no DB call needed)
            Games game = p.getGame();

            if (game != null) {
                response.add(new PurchaseResponse(
                        game.getTitle(),
                        game.getDescription(),
                        game.getPrice()
                ));
            }
        }

        return response;
    }
}