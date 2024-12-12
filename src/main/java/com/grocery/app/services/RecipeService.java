package com.grocery.app.services;

import com.grocery.app.dto.FavoriteRecipeListDTO;
import com.grocery.app.dto.RecipeDTO;

import java.util.ArrayList;

public interface RecipeService {
    RecipeDTO createRecipe(RecipeDTO recipeDTO);

    RecipeDTO getRecipeById(Long userId, Long recipeId);

    ArrayList<RecipeDTO> getAllRecipe(Long userId, int from, int to);

    RecipeDTO updateRecipe(RecipeDTO recipeDTO);

    RecipeDTO deleteRecipe(Long userId, Long recipeId);

    ArrayList<RecipeDTO> getFavoriteList(Long userId, int from, int to);

    FavoriteRecipeListDTO removeFavoriteRecipe(Long userId, Long recipeId);

    FavoriteRecipeListDTO addFavoriteRecipe(Long userId, Long recipeId);
}
