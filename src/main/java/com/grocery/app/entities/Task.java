package com.grocery.app.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grocery.app.mapper.LocalDateConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "task")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shopping_list_id", foreignKey = @ForeignKey(name = "fk_shopping_list"))
    private ShoppingList shoppingList;

    @ManyToOne
    @JoinColumn(name = "buyer_id", foreignKey = @ForeignKey(name = "fk_buyer"))
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "food_id", foreignKey = @ForeignKey(name = "fk_food"))
    private Food food;

    private float quantity;

    private Timestamp timestamp;

    private String status;

    private Date createdAt;

    private Date updatedAt;
}
