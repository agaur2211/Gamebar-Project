package com.example.demo.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email of the user who bought the game
    private String buyerEmail;
    private LocalDateTime purchaseDate;

    // 🔥 RELATION WITH GAME (IMPORTANT)
    @ManyToOne
    @JoinColumn(name = "game_id")
    private Games game;

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Games getGame() {
        return game;
    }

    public void setGame(Games game) {
        this.game = game;
    }
    
    
    @PrePersist
    public void setPurchaseDate() {
        this.purchaseDate = LocalDateTime.now();
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
}