package com.grocery.app.repositories;

import com.grocery.app.entities.Food;
import com.grocery.app.entities.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RecipeRepo extends JpaRepository<Recipe, Long> {
    @Query("SELECT s FROM Recipe s WHERE s.user.id = :userId AND s.status <> 'deleted' ORDER BY s.createdAt DESC")
    ArrayList<Recipe> findAllByUserId(@Param("userId") Long userId);
}
