package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.FoodDTO;
import com.grocery.app.entities.Food;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FoodRepo;
import com.grocery.app.services.FoodService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
@Service
public class FoodServiceImpl implements FoodService {
    private FoodRepo foodRepo;
    private ModelMapper modelMapper;

    @Override
    public FoodDTO createFood(long userId, FoodDTO foodDTO) {
        // Kiểm tra owner
        if(userId != foodDTO.getOwnerId()){
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        // Map sang entity
        Food food = modelMapper.map(foodDTO, Food.class);
        if(food == null){
            throw new ServiceException(
                ResCode.FOOD_CREATION_FAILED.getMessage(),
                ResCode.FOOD_CREATION_FAILED.getCode()
            );
        }

        Food createdFood = foodRepo.save(food);

        return modelMapper.map(createdFood, FoodDTO.class);
    }

    @Override
    public Optional<FoodDTO> getFoodById(long userId, long id) {
        Food food = foodRepo.findById(id).orElse(null);

        // Kiểm tra tồn tại
        if(food == null){
            throw new ServiceException(
                ResCode.FOOD_NOT_FOUND.getMessage(),
                ResCode.FOOD_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra chủ sở hữu
        if(food.getOwnerId() != userId){
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        FoodDTO foodDTO = modelMapper.map(food, FoodDTO.class);

        return Optional.ofNullable(foodDTO);
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
