package com.grocery.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "fav_recipe")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động sinh giá trị ID
    private Long id;

    @ManyToOne // Một người dùng (User) có thể có nhiều FavoriteRecipe
    @JoinColumn(name = "user_id", nullable = false) // Tên cột trong bảng fav_recipe
    private User user;

    @OneToOne // Một gia đình (Family) có thể có nhiều FavoriteRecipe
    @JoinColumn(name = "family_id") // Cho phép nullable vì có thể không thuộc Family
    private Family family;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fav_recipe_id") // Tạo liên kết với bảng Recipe
    private List<Recipe> favoriteList;
}
