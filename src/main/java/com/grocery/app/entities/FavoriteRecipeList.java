package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "favorite_recipe")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FavoriteRecipeList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động sinh giá trị ID
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "favRecipe", cascade = CascadeType.MERGE, orphanRemoval = true)
    private List<Recipe> favoriteList = new ArrayList<>();
}
