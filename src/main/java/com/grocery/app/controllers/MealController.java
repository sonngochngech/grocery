package com.grocery.app.controllers;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.constant.TermConfig;
import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;
import com.grocery.app.dto.request.RecommendedMealDTO;
import com.grocery.app.dto.request.createRequest.CreateMealRequest;
import com.grocery.app.dto.request.updateRequest.UpdateMealRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("api/meal")
@Slf4j
public class MealController {
    @Autowired
    private MealService mealService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private FamilyService familyService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<MealDTO>> createMeal(@RequestBody CreateMealRequest createMealRequest){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Kiểm tra người dùng
        UserDTO user = userService.getUserById(currentUser.getId());

        if(user == null){
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu của gia đình
        boolean isOwner = familyService.verifyOwner(
                createMealRequest.getFamilyId(),
                currentUser.getId()
        );

        if(!isOwner){
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        FamilyDetailDTO family = familyService.getFamilyInformation(createMealRequest.getFamilyId());
        // Kiểm tra danh sách công thức trong bữa ăn
        ArrayList<RecipeDTO> recipesList = new ArrayList<>();
        for (Long recipeId : createMealRequest.getRecipeList()) {
            RecipeDTO recipeDTO = recipeService.getRecipeById(
                    currentUser.getId(),
                    recipeId
            );

            if(recipeDTO == null){
                throw new ServiceException(
                        ResCode.RECIPE_NOT_FOUND.getMessage(),
                        ResCode.RESOURCE_NOT_FOUND.getCode()
                );
            }

            recipesList.add(recipeDTO);
        }

        MealDTO newMeal = MealDTO
                .builder()
                .userDetailDTO(user)
                .familyDetailDTO(family.getBasicInfo())
                .name(createMealRequest.getName())
                .term(TermConfig.valueOf(createMealRequest.getTerm().toUpperCase()).getTerm())
                .date(createMealRequest.getDate())
                .recipeDTOS(recipesList)
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();
        // Tạo bữa ăn mới
        MealDTO createdMeal = mealService.createMeal(newMeal);

        if(createdMeal == null){
            throw new ServiceException(
                    ResCode.MEAL_CREATION_FAILED.getMessage(),
                    ResCode.MEAL_CREATION_FAILED.getCode()
            );
        }

        // Trả về bữa ăn mới tạo
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseFactory.createResponse(
                        createdMeal,
                        ResCode.CREATE_FOOD_SUCCESSFULLY.getMessage(),
                        ResCode.CREATE_FOOD_SUCCESSFULLY.getCode()
                        )
                );
    }

    @GetMapping("/get/{mealId}")
    public ResponseEntity<BaseResponse<MealDTO>> getMealById(@PathVariable Long mealId){
        System.out.println("meal id");
        System.out.println(mealId);
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy bữa ăn theo ID và người dùng hiện tại
        MealDTO meal = mealService.getMealById(
                currentUser.getId(),
                mealId
        );

        // Kiểm tra tồn tại của bữa ăn
        if(meal == null){
            throw new ServiceException(
                    ResCode.MEAL_NOT_FOUND.getMessage(),
                    ResCode.MEAL_NOT_FOUND.getCode()
            );
        }

        // Trả về thông tin bữa ăn
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseFactory.createResponse(
                        meal,
                        ResCode.GET_MEAL_SUCCESSFULLY.getMessage(),
                        ResCode.GET_MEAL_SUCCESSFULLY.getCode()
                ));
    }

    @GetMapping("/getAll")
    public ResponseEntity<BaseResponse<ArrayList<MealDTO>>> getAllMeal(@RequestParam int from, @RequestParam int to){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy tất cả bữa ăn trong khoảng từ `from` đến `to`
        ArrayList<MealDTO> meals = mealService.getAllMeal(
                currentUser.getId(),
                from,
                to
        );

        // Trả về danh sách bữa ăn
        return ResponseEntity.status(HttpStatus.OK).body(
                ResponseFactory.createResponse(
                        meals,
                        ResCode.GET_MEALS_SUCCESSFULLY.getMessage(),
                        ResCode.GET_MEALS_SUCCESSFULLY.getCode()
                )
        );
    }

    @GetMapping("/recommend/{term}")
    public ResponseEntity<BaseResponse<RecommendedMealDTO>> recommendMeal(@PathVariable String term){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();
        System.out.println("request term" + term);

        // Kiểm tra người dùng
        UserDTO user = userService.getUserById(currentUser.getId());

        if(user == null){
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        System.out.println("user id");
        System.out.println(user.getId());

        // Lấy danh sách bữa ăn gợi ý
        RecommendedMealDTO recommendedMeals = mealService.recommendMeal(
                currentUser.getId(),
                term
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                recommendedMeals,
                                ResCode.GET_RECOMMENDED_MEALS_SUCCESSFULLY.getMessage(),
                                ResCode.GET_RECOMMENDED_MEALS_SUCCESSFULLY.getCode()
                        )
                );
    }

    @PostMapping("/update")
    public ResponseEntity<BaseResponse<MealDTO>> updateMeal(@RequestBody UpdateMealRequest updateMealRequest){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Kiểm tra quyền sở hữu
        boolean isOwner = familyService.verifyOwner(
                currentUser.getId(),
                updateMealRequest.getFamilyId()
        );

        if(!isOwner){
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Kiểm tra sự tồn tại của bữa ăn
        MealDTO currentMeal = mealService.getMealById(
                currentUser.getId(),
                updateMealRequest.getMealId()
        );

        if(currentMeal == null){
            throw new ServiceException(
                    ResCode.MEAL_NOT_FOUND.getMessage(),
                    ResCode.MEAL_NOT_FOUND.getCode()
            );
        }

        // Lấy danh sách công thức mới
        ArrayList<RecipeDTO> newRecipes = new ArrayList<>();
        System.out.println("New recipe list");
        System.out.println(updateMealRequest.getRecipeList());
        for (Long recipeId : updateMealRequest.getRecipeList()) {
            RecipeDTO newRecipe = recipeService.getRecipeById(
                    currentUser.getId(),
                    recipeId
            );

            if(newRecipe == null){
                throw new ServiceException(
                        ResCode.RECIPE_NOT_FOUND.getMessage(),
                        ResCode.RECIPE_NOT_FOUND.getCode()
                );
            }

            newRecipes.add(newRecipe);
        }

        // Cập nhật thông tin bữa ăn
        currentMeal.setName(updateMealRequest.getName());
        currentMeal.setDate(updateMealRequest.getDate());
        currentMeal.setTerm(TermConfig.valueOf(updateMealRequest.getTerm().toUpperCase()).getTerm());
        currentMeal.setRecipeDTOS(newRecipes);
        currentMeal.setUpdatedAt(Date.valueOf(LocalDate.now()));

        // Thực hiện cập nhật bữa ăn
        MealDTO updatedMeal = mealService.updateMeal(currentMeal);

        if(updatedMeal == null){
            throw new ServiceException(
                    ResCode.MEAL_UPDATE_FAILED.getMessage(),
                    ResCode.MEAL_UPDATE_FAILED.getCode()
            );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                updatedMeal,
                                ResCode.UPDATE_MEAL_SUCCESSFULLY.getMessage(),
                                ResCode.UPDATE_MEAL_SUCCESSFULLY.getCode()
                        )
                );
    }

    @DeleteMapping("/delete/{mealId}")
    public ResponseEntity<BaseResponse<MealDTO>> deleteMeal(@PathVariable Long mealId){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Thực hiện xóa bữa ăn
        MealDTO deletedMeal = mealService.deleteMeal(
                currentUser.getId(),
                mealId
        );

        if(deletedMeal == null){
            throw new ServiceException(
                    ResCode.MEAL_DELETE_FAILED.getMessage(),
                    ResCode.MEAL_DELETE_FAILED.getCode()
            );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                deletedMeal,
                                ResCode.DELETE_MEAL_SUCCESSFULLY.getMessage(),
                                ResCode.DELETE_MEAL_SUCCESSFULLY.getCode()
                        )
                );
    }
}
