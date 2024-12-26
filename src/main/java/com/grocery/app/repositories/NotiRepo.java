package com.grocery.app.repositories;

import com.grocery.app.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotiRepo  extends JpaRepository<Notification,Long> {


}
