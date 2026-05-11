package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Games;

public interface GameRepository extends JpaRepository<Games, Long>{
	// 🔍 Search by title (case insensitive)
	List<Games> findByTitleContainingIgnoreCase(String title);

	// 💰 Filter by price range
	List<Games> findByPriceBetween(double minPrice, double maxPrice);
	List<Games> findByIdIn(List<Long> ids);

}
