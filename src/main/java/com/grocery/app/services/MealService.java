package com.grocery.app.services;

import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.request.RecommendedMealDTO;

import java.util.ArrayList;

public interface MealService {
    MealDTO createMeal(MealDTO mealDTO);
    MealDTO getMealById(Long userId, Long mealId);
    ArrayList<MealDTO> getAllMeal(Long userId, int from, int to);
    ArrayList<RecommendedMealDTO> recommendMeal(Long userId);
    MealDTO updateMeal(MealDTO mealDTO);
    MealDTO deleteMeal(Long userId, Long mealId);
}
