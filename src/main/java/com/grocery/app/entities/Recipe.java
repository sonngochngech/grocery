package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToMany(mappedBy = "recipes")
    private List<Meal> meals = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Ràng buộc công thức phải có người sở hữu
    private User user;

}
