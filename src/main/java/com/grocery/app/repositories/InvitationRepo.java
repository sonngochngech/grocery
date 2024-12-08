package com.grocery.app.repositories;

import com.grocery.app.entities.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepo extends JpaRepository<Invitation,Long> {

    Optional<Invitation> findById(Long id);
}
