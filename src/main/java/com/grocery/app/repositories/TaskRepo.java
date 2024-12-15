package com.grocery.app.repositories;

import com.grocery.app.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    @Query("SELECT s FROM Task s WHERE s.assignee.id = :buyerId AND s.status <> 'deleted' ORDER BY s.createdAt DESC")
    ArrayList<Task> findAllByUserId(@Param("buyerId") Long buyerId);
}
