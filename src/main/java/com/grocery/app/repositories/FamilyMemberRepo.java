package com.grocery.app.repositories;

import com.grocery.app.entities.Family;
import com.grocery.app.entities.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyMemberRepo extends JpaRepository<FamilyMember,Long> {
}
