package com.grocery.app.services.impl;

import com.grocery.app.dto.FoodDTO;
import com.grocery.app.services.FoodService;

import java.util.ArrayList;
import java.util.Optional;

public class FoodServiceImpl implements FoodService {

    @Override
    public FoodDTO createFood(long userId, FoodDTO foodDTO) {
        return null;
    }

    @Override
    public Optional<FoodDTO> getFoodById(long userId, long id) {
        return Optional.empty();
    }

    @Override
    public ArrayList<FoodDTO> getAllFood(long userId, int from, int to) {
        return null;
    }

    @Override
    public FoodDTO updateFood(long userId, FoodDTO foodDTO) {
        return null;
    }

    @Override
    public FoodDTO deleteFood(long userId, long id) {
        return null;
    }
}
