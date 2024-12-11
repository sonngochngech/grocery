package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "favorite_recipe")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FavoriteRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động sinh giá trị ID
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "favRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> favoriteList = new ArrayList<>();
}
