package com.grocery.app.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grocery.app.mapper.LocalDateConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "task")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shopping_list_id", foreignKey = @ForeignKey(name = "fk_shopping_list"))
    private ShoppingList shoppingList;

    @ManyToOne
    @JoinColumn(name = "buyer_id", foreignKey = @ForeignKey(name = "fk_buyer"))
    private User user;

    private String name;

    @ManyToOne
    @JoinColumn(name = "food_id", foreignKey = @ForeignKey(name = "fk_food"))
    private Food food;

    private float quantity;

    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Convert(converter = LocalDateConverter.class)
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Convert(converter = LocalDateConverter.class)
    private Date updatedAt;
}
