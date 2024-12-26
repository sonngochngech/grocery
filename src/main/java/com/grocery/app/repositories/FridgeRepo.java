package com.grocery.app.repositories;

import com.grocery.app.dto.UserFridgeDTO;
import com.grocery.app.entities.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FridgeRepo extends JpaRepository<Fridge,Long> {

    @Query(value = "SELECT * FROM fridges f WHERE f.family_id = :family",nativeQuery = true)
    Optional<Fridge> findByFamilyId(Long family);


    @Query(value = "SELECT fo.name AS foodName, u.email AS email, f.name AS fridgeName, i.id AS fridgeItemId " +
            "FROM fridges f " +
            "JOIN fridge_items i ON f.id = i.fridge_id " +
            "JOIN families fa ON f.family_id = fa.id " +
            "JOIN food fo ON fo.id = i.food_id " +
            "JOIN users u ON fa.owner_id = u.id " +
            "WHERE i.is_notified = 0 AND i.created_at < DATE_SUB(NOW(), INTERVAL (i.duration+3) DAY)",
            nativeQuery = true)
    Optional<List<Object[]>> findExpriedFridgeItems();
}
