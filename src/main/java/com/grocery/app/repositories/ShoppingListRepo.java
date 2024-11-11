package com.grocery.app.repositories;

import com.grocery.app.entities.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ShoppingListRepo extends JpaRepository<ShoppingList, Long> {

    @Query("SELECT s FROM ShoppingList s WHERE s.owner.id = :userId ORDER BY s.createdAt DESC")
    ArrayList<ShoppingList> findAllByUserId(@Param("userId") Long userId);
}

