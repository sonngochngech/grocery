package com.grocery.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDTO {
    private Long id;

    private Long user;

    //
    private String name;

    //
    private String description;

    //
    private String imageUrl;

    //
    private ArrayList<FoodDTO> foods;

    private ArrayList<Long> meals;

    private Long favoriteList;

    //
    private Date createdAt;

    //
    private Date updatedAt;

    //
    private String status;
}
