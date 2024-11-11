package com.grocery.app.repositories;

import com.grocery.app.entities.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface MealRepo extends JpaRepository<Meal, Long> {
    @Query("SELECT s FROM Meal s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    ArrayList<Meal> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT s FROM Meal s WHERE s.user.id = :userId AND s.term = :term ORDER BY s.createdAt DESC")
    ArrayList<Meal> findAllByTerm(@Param("userId") Long userId, @Param("term") String term);
}
