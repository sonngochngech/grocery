package com.grocery.app.repositories;

import com.grocery.app.entities.Food;
import com.grocery.app.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface FoodRepo extends JpaRepository<Food, Long> {
    @Query("SELECT s FROM Food s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    ArrayList<Food> findAllByUserId(@Param("userId") Long userId);
}
