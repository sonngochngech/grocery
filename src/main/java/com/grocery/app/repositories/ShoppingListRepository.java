package com.grocery.app.repositories;

import com.grocery.app.entities.ShoppingList;
import com.grocery.app.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    @Query("SELECT s FROM ShoppingList s WHERE s.userId = :userId ORDER BY s.createdAt DESC")
    ArrayList<ShoppingList> findAllByUserId(@Param("userId") long userId);
}

