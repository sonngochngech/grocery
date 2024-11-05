package com.grocery.app.repositories;

import com.grocery.app.entities.Family;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyRepo  extends JpaRepository<Family,Long> {

    @Query(value="SELECT f.* FROM families f WHERE f.id IN ((SELECT fm.family_id FROM family_members fm WHERE fm.user_id= :userId) UNION (SELECT f.id FROM families f WHERE f.owner_id= :userId) )",nativeQuery = true)
    Optional<List<Family>> findFamilyByUser(@Param("userId") Long userId);
}
