package com.grocery.app.services;

import com.grocery.app.dto.FavoriteRecipeDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.entities.FavoriteRecipe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

public interface RecipeService {
    RecipeDTO createRecipe(RecipeDTO recipeDTO);

    RecipeDTO getRecipeById(Long userId, Long recipeId);

    ArrayList<RecipeDTO> getAllRecipe(Long userId, int from, int to);

    RecipeDTO updateRecipe(RecipeDTO recipeDTO);

    RecipeDTO deleteRecipe(Long userId, Long recipeId);

    FavoriteRecipeDTO getFavoriteList(Long userId, Long familyId);

    FavoriteRecipeDTO removeFavoriteRecipe(Long userId, Long familyId, Long recipeId);

    FavoriteRecipeDTO addFavoriteRecipe(Long userId, Long familyId, Long recipeId);
}
