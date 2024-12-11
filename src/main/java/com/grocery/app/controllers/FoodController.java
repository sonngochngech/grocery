package com.grocery.app.controllers;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.*;
import com.grocery.app.dto.request.createRequest.CreateFoodRequest;
import com.grocery.app.dto.request.updateRequest.UpdateFoodRequest;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("api/food")
@Slf4j
public class FoodController {
    @Autowired
    private FoodService foodService;
    @Autowired
    private UserService userService;
    @Autowired
    private FamilyService familyService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UnitService unitService;

    @PostMapping("/add")
    public ResponseEntity<FoodDTO> createFood(@RequestBody CreateFoodRequest createFoodRequest) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Kiểm tra auth
        if (!Objects.equals(currentUser.getId(), createFoodRequest.getUserId())) {
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        UserDetailDTO userDetailDTO = userService.getUser(createFoodRequest.getUserId());
        if (userDetailDTO == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // TODO: Kiểm tra Category và Unit
        CategoryDTO categoryDTO = null;

        if (categoryDTO == null) {
            throw new ServiceException(
                    ResCode.CATEGORY_NOT_FOUND.getMessage(),
                    ResCode.CATEGORY_NOT_FOUND.getCode()
            );
        }

        UnitDTO unitDTO = null;

        if (unitDTO == null) {
            throw new ServiceException(
                    ResCode.UNIT_NOT_FOUND.getMessage(),
                    ResCode.UNIT_NOT_FOUND.getCode()
            );
        }

        FoodDTO foodDTO = FoodDTO.builder()
                .user(userDetailDTO)
                .categoryDTO(categoryDTO)
                .measureUnitDTO(unitDTO)
                .description(createFoodRequest.getDescription())
                .name(createFoodRequest.getName())
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();

        FoodDTO createFood = foodService.createFood(createFoodRequest.getUserId(), foodDTO);
        if (createFood == null) {
            throw new ServiceException(
                    ResCode.FOOD_CREATION_FAILED.getMessage(),
                    ResCode.FOOD_CREATION_FAILED.getCode()
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createFood);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<FoodDTO> getFoodById(@PathVariable Long id) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy food theo ID và người dùng hiện tại
        FoodDTO foodDTO = foodService.getFoodById(
                currentUser.getId(),
                id
        );

        // Kiểm tra tồn tại của food
        if (foodDTO == null) {
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }

        // Trả về thông tin food
        return ResponseEntity.ok(foodDTO);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<FoodDTO>> getAllFood(@RequestParam int from, @RequestParam int to){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy tất cả food trong khoảng từ `from` đến `to`
        ArrayList<FoodDTO> foodDTOS = foodService.getAllFood(
                currentUser.getId(),
                from,
                to
        );

        // Trả về danh sách food
        return ResponseEntity.ok(foodDTOS);
    }

    @PostMapping("/update")
    public ResponseEntity<FoodDTO> updateFood(@RequestBody UpdateFoodRequest updateFoodRequest){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy food theo ID và người dùng hiện tại
        FoodDTO foodDTO = foodService.getFoodById(
                currentUser.getId(),
                updateFoodRequest.getId()
        );

        // Kiểm tra tồn tại của food
        if (foodDTO == null) {
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu
        if(!Objects.equals(currentUser.getId(), updateFoodRequest.getId())
                || !Objects.equals(currentUser.getId(), foodDTO.getUser().getId())
                || !Objects.equals(foodDTO.getUser().getId(), updateFoodRequest.getUserId())
        ){
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }


        // TODO: Kiểm tra Category và Unit
        CategoryDTO categoryDTO = null;

        if (categoryDTO == null) {
            throw new ServiceException(
                    ResCode.CATEGORY_NOT_FOUND.getMessage(),
                    ResCode.CATEGORY_NOT_FOUND.getCode()
            );
        }

        UnitDTO unitDTO = null;

        if (unitDTO == null) {
            throw new ServiceException(
                    ResCode.UNIT_NOT_FOUND.getMessage(),
                    ResCode.UNIT_NOT_FOUND.getCode()
            );
        }

        foodDTO.setCategoryDTO(categoryDTO);
        foodDTO.setMeasureUnitDTO(unitDTO);
        foodDTO.setName(updateFoodRequest.getName());
        foodDTO.setDescription(updateFoodRequest.getDescription());
        foodDTO.setUpdatedAt(Date.valueOf(LocalDate.now()));

        FoodDTO updatedFood = foodService.updateFood(foodDTO);

        if(updatedFood == null){
            throw new ServiceException(
                    ResCode.FOOD_UPDATE_FAILED.getMessage(),
                    ResCode.FOOD_UPDATE_FAILED.getCode()
            );
        }

        return ResponseEntity.ok(updatedFood);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<FoodDTO> deleteMeal(@PathVariable Long id){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Thực hiện xóa bữa ăn
        FoodDTO deletedFood = foodService.deleteFood(
                currentUser.getId(),
                id
        );

        if(deletedFood == null){
            throw new ServiceException(
                    ResCode.FOOD_DELETE_FAILED.getMessage(),
                    ResCode.FOOD_DELETE_FAILED.getCode()
            );
        }

        return ResponseEntity.ok(deletedFood);
    }
}
