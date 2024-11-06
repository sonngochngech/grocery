package com.grocery.app.entities;

import com.grocery.app.config.StatusConfig;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "shoppingList")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingList {
    private long id;
    private User owner;
    private Family family;
    private String name;
    private String description;
    private ArrayList<Task> taskArrayList = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    private String status;
}
