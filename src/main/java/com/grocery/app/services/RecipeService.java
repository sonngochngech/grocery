package com.grocery.app.services;

import com.grocery.app.dto.RecipeDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

public interface RecipeService {
    RecipeDTO createRecipe(RecipeDTO recipeDTO);

    RecipeDTO getRecipeById(Long userId, Long recipeId);

    ArrayList<RecipeDTO> getAllRecipe(Long userId, int from, int to);

    RecipeDTO updateRecipe(RecipeDTO recipeDTO);

    RecipeDTO deleteRecipe(Long userId, Long recipeId);
}
