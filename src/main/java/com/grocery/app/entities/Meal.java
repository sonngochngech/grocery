package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Quan hệ nhiều đến một với User (một người dùng có nhiều bữa ăn)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family; // Quan hệ nhiều đến một với Family (một gia đình có nhiều bữa ăn)

    private String name;
    private String term;
    private String date;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recipe> recipes = new ArrayList<>(); // Danh sách các công thức nấu ăn (một bữa ăn có thể có nhiều công thức)

    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String status;
}
