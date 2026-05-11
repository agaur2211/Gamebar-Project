package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Games;
import com.example.demo.model.Reviews;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
	 List<Reviews> findByGame(Games game);

	    boolean existsByUserEmailAndGame(String userEmail, Games game);
}
