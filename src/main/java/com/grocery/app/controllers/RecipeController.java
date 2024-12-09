package com.grocery.app.controllers;

import com.grocery.app.dto.FavoriteRecipeDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.request.createRequest.CreateRecipeRequest;
import com.grocery.app.dto.request.updateRequest.UpdateRecipeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/recipe")
public class RecipeController {

    // CRUD Recipe

    @PostMapping("/create")
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody CreateRecipeRequest createRecipeRequest) {
        // Implement create recipe logic here
    }

    @GetMapping("get/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        // Implement get recipe by ID logic here
    }

    @PostMapping("update")
    public ResponseEntity<RecipeDTO> updateRecipe(@RequestBody UpdateRecipeRequest updateRecipeRequest) {
        // Implement update recipe logic here
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<RecipeDTO> deleteRecipe(@PathVariable Long id) {
        // Implement delete recipe logic here
    }

    // Get All Recipes

    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<RecipeDTO>> getAllRecipes(@RequestParam int from, @RequestParam int to) {
        // Implement get all recipes logic here
    }

    // Add or Remove Recipe from Favorites

    @GetMapping("/favorite/get")
    public ResponseEntity<FavoriteRecipeDTO> getFavoriteRecipe(@RequestParam int from, @RequestParam int to){

    }

    @PostMapping("/favorites/add")
    public ResponseEntity<FavoriteRecipeDTO> addRecipeToFavorites(@RequestBody Long recipeId) {
        // Implement add recipe to favorites logic here
    }

    @PostMapping("/favorites/remove")
    public ResponseEntity<FavoriteRecipeDTO> removeRecipeFromFavorites(@RequestBody Long recipeId) {
        // Implement remove recipe from favorites logic here
    }
}
