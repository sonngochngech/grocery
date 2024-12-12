package com.grocery.app.controllers;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.*;
import com.grocery.app.dto.request.createRequest.CreateFoodRequest;
import com.grocery.app.dto.request.updateRequest.UpdateFoodRequest;
import com.grocery.app.entities.Category;
import com.grocery.app.entities.Unit;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.repositories.CategoryRepo;
import com.grocery.app.repositories.UnitRepo;
import com.grocery.app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private UnitRepo unitRepo;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<FoodDTO>> createFood(@RequestBody CreateFoodRequest createFoodRequest) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        UserDTO userDetailDTO = userService.getUserById(currentUser.getId());
        if (userDetailDTO == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // Không có service nên phải lấy thẳng từ repo
        Category category = categoryRepo.findById(createFoodRequest.getCategoryId()).orElse(null);
        if(category == null){
            throw new ServiceException(
                    ResCode.CATEGORY_NOT_FOUND.getMessage(),
                    ResCode.CATEGORY_NOT_FOUND.getCode()
            );
        }

        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

        Unit unit = unitRepo.findById(createFoodRequest.getUnitId()).orElse(null);
        if(unit == null){
            throw new ServiceException(
                    ResCode.UNIT_NOT_FOUND.getMessage(),
                    ResCode.UNIT_NOT_FOUND.getCode()
            );
        }
        UnitDTO unitDTO = modelMapper.map(unit, UnitDTO.class);

        FoodDTO foodDTO = FoodDTO.builder()
                .user(userDetailDTO)
                .categoryDTO(categoryDTO)
                .measureUnitDTO(unitDTO)
                .description(createFoodRequest.getDescription())
                .name(createFoodRequest.getName())
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();

        FoodDTO createFood = foodService.createFood(currentUser.getId(), foodDTO);
        if (createFood == null) {
            throw new ServiceException(
                    ResCode.FOOD_CREATION_FAILED.getMessage(),
                    ResCode.FOOD_CREATION_FAILED.getCode()
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseFactory.createResponse(
                                createFood,
                                ResCode.CREATE_FOOD_SUCCESSFULLY.getMessage(),
                                ResCode.CREATE_FAMILY_SUCCESSFULLY.getCode()
                        )
                );
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BaseResponse<FoodDTO>> getFoodById(@PathVariable Long id) {
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
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                foodDTO,
                                ResCode.GET_FOOD_SUCCESSFULLY.getMessage(),
                                ResCode.GET_FOOD_SUCCESSFULLY.getCode()
                        )
                );
    }

    @GetMapping("/getAll")
    public ResponseEntity<BaseResponse<ArrayList<FoodDTO>>> getAllFood(@RequestParam(defaultValue = "0") int from,
                                                                       @RequestParam(defaultValue = "10") int to){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy tất cả food trong khoảng từ `from` đến `to`
        ArrayList<FoodDTO> foodDTOS = foodService.getAllFood(
                currentUser.getId(),
                from,
                to
        );

        // Trả về danh sách food
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                foodDTOS,
                                ResCode.GET_FOOD_LIST_SUCCESSFULLY.getMessage(),
                                ResCode.GET_FOOD_LIST_SUCCESSFULLY.getCode()
                        )
                );
    }

    @PostMapping("/update")
    public ResponseEntity<BaseResponse<FoodDTO>> updateFood(@RequestBody UpdateFoodRequest updateFoodRequest){
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
        if(!Objects.equals(currentUser.getId(), foodDTO.getUser().getId())){
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }


        // Không có service nên phải lấy thẳng từ repo
        Category category = categoryRepo.findById(updateFoodRequest.getCategoryId()).orElse(null);
        if(category == null){
            throw new ServiceException(
                    ResCode.CATEGORY_NOT_FOUND.getMessage(),
                    ResCode.CATEGORY_NOT_FOUND.getCode()
            );
        }

        CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

        Unit unit = unitRepo.findById(updateFoodRequest.getUnitId()).orElse(null);
        if(unit == null){
            throw new ServiceException(
                    ResCode.UNIT_NOT_FOUND.getMessage(),
                    ResCode.UNIT_NOT_FOUND.getCode()
            );
        }
        UnitDTO unitDTO = modelMapper.map(unit, UnitDTO.class);

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

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                updatedFood,
                                ResCode.UPDATE_FOOD_SUCCESSFULLY.getMessage(),
                                ResCode.UPDATE_FOOD_SUCCESSFULLY.getCode()
                        )
                );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BaseResponse<FoodDTO>> deleteMeal(@PathVariable Long id){
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

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                deletedFood,
                                ResCode.DELETE_FOOD_SUCCESSFULLY.getMessage(),
                                ResCode.DELETE_FOOD_SUCCESSFULLY.getCode()
                        )
                );
    }
}
