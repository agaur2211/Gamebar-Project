package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Games;
import com.example.demo.model.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
	boolean existsByBuyerEmailAndGame(String buyerEmail, Games game);
	List<Purchase> findByBuyerEmail(String buyerEmail);
	
}
