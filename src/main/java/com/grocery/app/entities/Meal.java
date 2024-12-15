package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "meal")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mealId; // Khóa chính của bảng meal

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Quan hệ nhiều đến một với User (một người dùng có nhiều bữa ăn)

    @ManyToOne
    @JoinColumn(name = "family_id", nullable = false)
    private Family family; // Quan hệ nhiều đến một với Family (một gia đình có nhiều bữa ăn)

    private String name;
    private String term;
    private String date;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "meal_recipe",
            joinColumns = @JoinColumn(name = "meal_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> recipes = new ArrayList<>();
    // Danh sách các công thức nấu ăn (một bữa ăn có thể có nhiều công thức)

    private Date createdAt;

    private Date updatedAt;
    private String status;
}
