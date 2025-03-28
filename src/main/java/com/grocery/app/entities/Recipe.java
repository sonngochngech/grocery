package com.grocery.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipe")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private String description;

    private String imageUrl;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "meal_recipe",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    @JsonIgnore
    private List<Meal> meals = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "recipe_food",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private List<Food> foods = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Ràng buộc công thức phải có người sở hữu
    private User user;

    @ManyToOne
    @JoinColumn(name = "favorite_recipe_id")
    private FavoriteRecipeList favRecipe;

    private Date createdAt;
    private Date updatedAt;

    private String status;
}
