package com.grocery.app.repositories;

import com.grocery.app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long> {

    @Override
    List<Category> findAll();


    Optional<Category> findById(Long id);

    Optional<Category> findByName(String name);


}
