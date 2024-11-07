package com.grocery.app.dto.request;

import com.grocery.app.dto.RecipeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RecommendedMealDTO {
    String term;
    ArrayList<RecipeDTO> recommendedRecipes;
}
