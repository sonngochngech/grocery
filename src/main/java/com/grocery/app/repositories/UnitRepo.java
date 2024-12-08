package com.grocery.app.repositories;

import com.grocery.app.entities.Category;
import com.grocery.app.entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepo extends JpaRepository<Unit,Long> {

    @Override
    List<Unit> findAll();


    Optional<Unit> findById(Long id);

    Optional<Unit> findByName(String name);
}
