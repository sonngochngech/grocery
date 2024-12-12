package com.grocery.app.repositories;

import com.grocery.app.entities.FavoriteRecipeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRecipeRepo extends JpaRepository<FavoriteRecipeList, Long> {
    @Query("SELECT s FROM FavoriteRecipeList s WHERE s.user.id = :userId")
    FavoriteRecipeList findByUser(@Param("userId") Long userId);
}
