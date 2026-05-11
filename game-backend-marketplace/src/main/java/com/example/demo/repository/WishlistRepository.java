package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Games;
import com.example.demo.model.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long>{
	List<Wishlist> findByUserEmail(String email);

    boolean existsByUserEmailAndGame(String email, Games game);

    void deleteByUserEmailAndGame(String email, Games game);
}
