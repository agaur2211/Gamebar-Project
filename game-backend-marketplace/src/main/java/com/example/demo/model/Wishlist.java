package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    private String userEmail;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Games game;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Games getGame() {
		return game;
	}

	public void setGame(Games game) {
		this.game = game;
	}
    
    
}
