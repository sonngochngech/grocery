package com.grocery.app.entities;

import com.grocery.app.config.constant.StatusConfig;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "shopping_list")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "family_id")
    private Family family;

    private String name;
    private String description;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> taskArrayList = new ArrayList<>();

    private Date createdAt;
    private Date updatedAt;
    private String status = StatusConfig.AVAILABLE.getStatus();
}
