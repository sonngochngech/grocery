package com.grocery.app.services;

import com.grocery.app.dto.FoodDTO;
import com.grocery.app.dto.TaskDTO;

import java.util.ArrayList;
import java.util.Optional;

public interface FoodService {
    FoodDTO createFood(long userId, FoodDTO foodDTO);

    Optional<FoodDTO> getFoodById(long userId, long id);

    ArrayList<FoodDTO> getAllFood(long userId, int from, int to);

    FoodDTO updateFood(long userId, FoodDTO foodDTO);

    FoodDTO deleteFood(long userId, long id);
}
