package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.CategoryDTO;
import com.grocery.app.dto.FoodDTO;
import com.grocery.app.dto.UnitDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.entities.Category;
import com.grocery.app.entities.Food;
import com.grocery.app.entities.Unit;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.CategoryRepo;
import com.grocery.app.repositories.FoodRepo;
import com.grocery.app.repositories.UnitRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.FoodService;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class FoodServiceImpl implements FoodService {
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private UnitRepo unitRepo;
    @Autowired
    private UserRepo userRepo;

    @Override
    public FoodDTO createFood(Long userId, FoodDTO foodDTO) {
        // Kiểm tra owner
        if (!Objects.equals(userId, foodDTO.getUser().getId())) {
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        // Map sang entity
        Food food = convertToFood(foodDTO);
        if (food == null) {
            throw new ServiceException(
                    ResCode.FOOD_CREATION_FAILED.getMessage(),
                    ResCode.FOOD_CREATION_FAILED.getCode()
            );
        }

        Food createdFood = foodRepo.save(food);

        return convertToFoodDTO(createdFood);
    }

    @Override
    public FoodDTO getFoodById(Long userId, Long id) {
        Food food = foodRepo.findById(id).orElse(null);

        // Kiểm tra tồn tại
        if (food == null) {
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra chủ sở hữu
        if (!Objects.equals(food.getUser().getId(), userId)) {
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        return convertToFoodDTO(food);
    }

    @Override
    public ArrayList<FoodDTO> getAllFood(Long userId, int from, int to) {
        ArrayList<Food> foods = foodRepo.findAllByUserId(userId);

        int maxSize = foods.size();

        // Nếu là rỗng
        if (maxSize == 0) return new ArrayList<>();

        // clamp
        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize)); // to trong khoảng [from + 1, maxSize]

        // Chuyển đổi phần tử trong range từ Food sang FoodDTO
        ArrayList<FoodDTO> results = new ArrayList<>();
        for (int i = from; i < to; i++) {
            results.add(convertToFoodDTO(foods.get(i)));
        }

        return results;
    }

    @Override
    public FoodDTO updateFood(FoodDTO foodDTO) {
        Food food = convertToFood(foodDTO);

        Food updatedFood = foodRepo.save(food);

        return convertToFoodDTO(updatedFood);
    }

    @Override
    public FoodDTO deleteFood(Long userId, Long id) {
        // Tìm food
        Food food = foodRepo.findById(id).orElse(null);

        if (food == null || Objects.equals(food.getStatus(), StatusConfig.DELETED.getStatus())) {
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu
        boolean isOwner = Objects.equals(userId, food.getUser().getId());

        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        // xóa food
        food.setStatus(StatusConfig.DELETED.getStatus());
        Food deletedFood = foodRepo.save(food);

        return convertToFoodDTO(deletedFood);
    }

    @Override
    public FoodDTO convertToFoodDTO(Food food) {
        CategoryDTO categoryDTO = modelMapper.map(food.getCategory(), CategoryDTO.class);
        UnitDTO unitDTO = modelMapper.map(food.getMeasureUnit(), UnitDTO.class);
        UserDTO userDTO = modelMapper.map(food.getUser(), UserDTO.class);
        return FoodDTO.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .user(userDTO)
                .categoryDTO(categoryDTO)
                .measureUnitDTO(unitDTO)
                .createdAt(food.getCreatedAt())
                .updatedAt(food.getUpdatedAt())
                .status(food.getStatus())
                .build();
    }

    @Override
    public Food convertToFood(FoodDTO foodDTO) {
        User user = userRepo.findById(foodDTO.getUser().getId()).orElse(null);
        Category category = categoryRepo.findById(foodDTO.getCategoryDTO().getId()).orElse(null);
        Unit unit = unitRepo.findById(foodDTO.getMeasureUnitDTO().getId()) .orElse(null);

        Food food = Food.builder()
                .user(user)
                .category(category)
                .measureUnit(unit)
                .name(foodDTO.getName())
                .description(foodDTO.getDescription())
                .createdAt(foodDTO.getCreatedAt())
                .updatedAt(foodDTO.getUpdatedAt())
                .status(foodDTO.getStatus())
                .build();

        if(foodDTO.getId() != null){
            food.setId(foodDTO.getId());
        }

        return food;
    }
}
