package com.grocery.app.services;

import com.grocery.app.dto.FoodDTO;
import com.grocery.app.dto.TaskDTO;
import com.grocery.app.entities.Food;

import java.util.ArrayList;
import java.util.Optional;

public interface FoodService {
    FoodDTO createFood(Long userId, FoodDTO foodDTO);

    FoodDTO getFoodById(Long userId, Long id);

    ArrayList<FoodDTO> getAllFood(Long userId, int from, int to);

    FoodDTO updateFood(FoodDTO foodDTO);

    FoodDTO deleteFood(Long userId, Long id);

    Food convertToFood(FoodDTO foodDTO);

    FoodDTO convertToFoodDTO(Food food);
}
