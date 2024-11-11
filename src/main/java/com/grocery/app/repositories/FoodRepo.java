package com.grocery.app.repositories;

import com.grocery.app.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepo extends JpaRepository<Food, Long> {
}
