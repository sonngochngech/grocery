package com.grocery.app.repositories;

import com.grocery.app.entities.FavoriteRecipe;
import com.grocery.app.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface FavoriteRecipeRepo extends JpaRepository<FavoriteRecipe, Long> {
    @Query("SELECT s FROM FavoriteRecipe s WHERE s.user.id = :userId")
    FavoriteRecipe findByUser(@Param("userId") Long userId);
}
