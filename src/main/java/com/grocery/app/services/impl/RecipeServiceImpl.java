package com.grocery.app.services.impl;

import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.services.RecipeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class RecipeServiceImpl implements RecipeService {
    @Override
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        return null;
    }

    @Override
    public RecipeDTO getRecipeById(Long userId, Long recipeId) {
        return null;
    }

    @Override
    public ArrayList<RecipeDTO> getAllRecipe(Long userId, int from, int to) {
        return null;
    }

    @Override
    public RecipeDTO updateRecipe(RecipeDTO recipeDTO) {
        return null;
    }

    @Override
    public RecipeDTO deleteRecipe(Long userId, Long recipeId) {
        return null;
    }
}
