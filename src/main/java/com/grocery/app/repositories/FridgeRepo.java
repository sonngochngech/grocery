package com.grocery.app.repositories;

import com.grocery.app.entities.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FridgeRepo extends JpaRepository<Fridge,Long> {

    @Query(value = "SELECT * FROM fridges f WHERE f.family_id = :family",nativeQuery = true)
    Optional<Fridge> findByFamilyId(Long family);
}
