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
    @Query("SELECT s FROM fav_recipe s WHERE s.user.id = :userId AND s.family.id = :familyId ORDER BY s.createdAt DESC")
    FavoriteRecipe findByUser(@Param("userId") Long userId, @Param("familyId") Long familyId);
}
