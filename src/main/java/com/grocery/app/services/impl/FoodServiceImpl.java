package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.FoodDTO;
import com.grocery.app.entities.Food;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FoodRepo;
import com.grocery.app.services.FoodService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
@Service
public class FoodServiceImpl implements FoodService {
    private FoodRepo foodRepo;
    private ModelMapper modelMapper;

    @Override
    public FoodDTO createFood(Long userId, FoodDTO foodDTO) {
        // Kiểm tra owner
        if(!Objects.equals(userId, foodDTO.getUser().getId())){
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
    public Optional<FoodDTO> getFoodById(Long userId, Long id) {
        Food food = foodRepo.findById(id).orElse(null);

        // Kiểm tra tồn tại
        if(food == null){
            throw new ServiceException(
                ResCode.FOOD_NOT_FOUND.getMessage(),
                ResCode.FOOD_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra chủ sở hữu
        if(!Objects.equals(food.getOwner().getId(), userId)){
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        FoodDTO foodDTO = modelMapper.map(food, FoodDTO.class);

        return Optional.ofNullable(foodDTO);
    }

    @Override
    public ArrayList<FoodDTO> getAllFood(Long userId, int from, int to) {
        ArrayList<Food> foods = foodRepo.findAllByUserId(userId);

        int maxSize = foods.size();

        // Nếu là rỗng
        if(maxSize == 0) return new ArrayList<>();

        // clamp
        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize)); // to trong khoảng [from + 1, maxSize]

        // Chuyển đổi phần tử trong range từ Food sang FoodDTO
        ArrayList<FoodDTO> result = new ArrayList<>();
        for (int i = from; i < to; i++) {
            result.add(modelMapper.map(foods.get(i), FoodDTO.class));
        }

        return result;
    }

    @Override
    public FoodDTO updateFood(FoodDTO foodDTO) {
        Food food = modelMapper.map(foodDTO, Food.class);

        if(food == null){
            throw new ServiceException(
                    ResCode.FOOD_UPDATE_FAILED.getMessage(),
                    ResCode.FOOD_UPDATE_FAILED.getCode()
            );
        }

        Food updatedFood = foodRepo.save(food);


        return modelMapper.map(updatedFood, FoodDTO.class);
    }

    @Override
    public FoodDTO deleteFood(Long userId, Long id) {
        // Tìm food
        Food food = foodRepo.findById(id).orElse(null);

        if(food == null || Objects.equals(food.getStatus(), StatusConfig.DELETED.getStatus())){
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu
        boolean isOwner = Objects.equals(userId, food.getOwner().getId());

        if(!isOwner){
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        // xóa food
        food.setStatus(StatusConfig.DELETED.getStatus());
        Food deletedFood = foodRepo.save(food);

        return modelMapper.map(deletedFood, FoodDTO.class);
    }
}
