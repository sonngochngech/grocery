package com.grocery.app.repositories;

import com.grocery.app.entities.FridgeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FridgeItemRepo  extends JpaRepository<FridgeItem,Long> {


}
