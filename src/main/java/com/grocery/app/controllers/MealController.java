package com.grocery.app.controllers;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.constant.TermConfig;
import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;
import com.grocery.app.dto.request.RecommendedMealDTO;
import com.grocery.app.dto.request.createRequest.CreateMealRequest;
import com.grocery.app.dto.request.updateRequest.UpdateMealRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("api/meal")
@Slf4j
public class MealController {
    private MealService mealService;
    private AuthenticationService authenticationService;
    private FamilyService familyService;
    private RecipeService recipeService;
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<MealDTO> createMeal(CreateMealRequest createMealRequest){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Kiểm tra người dùng
        UserDetailDTO user = userService.getUser(currentUser.getId());

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

        // Tạo bữa ăn tạm thời từ yêu cầu
        MealDTO newMeal = MealDTO
                .builder()
                .userDetailDTO(user)
                .familyDetailDTO(family)
                .name(createMealRequest.getName())
                .term(TermConfig.valueOf(createMealRequest.getTerm()).getTerm())
                .date(createMealRequest.getDate())
                .recipeDTOS(recipesList)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
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
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
    }

    @GetMapping("/get/{mealId}")
    public ResponseEntity<MealDTO> getMealById(@PathVariable Long mealId){
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
        return ResponseEntity.ok(meal);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<MealDTO>> getAllMeal(@RequestParam int from, @RequestParam int to){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy tất cả bữa ăn trong khoảng từ `from` đến `to`
        ArrayList<MealDTO> meals = mealService.getAllMeal(
                currentUser.getId(),
                from,
                to
        );

        // Trả về danh sách bữa ăn
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/recommend/{term}")
    public ResponseEntity<RecommendedMealDTO> recommendMeal(@PathVariable String term){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Kiểm tra và chuyển đổi giá trị `term`
        String validatedTerm;
        try {
            validatedTerm = TermConfig.valueOf(term).getTerm();
        } catch (IllegalArgumentException e) {
            throw new ServiceException(
                    ResCode.INVALID_TERM.getMessage(),
                    ResCode.INVALID_TERM.getCode()
            );
        }

        // Lấy danh sách bữa ăn gợi ý
        RecommendedMealDTO recommendedMeals = mealService.recommendMeal(
                currentUser.getId(),
                validatedTerm
        );

        return ResponseEntity.ok(recommendedMeals);
    }

    @PostMapping("/update")
    public ResponseEntity<MealDTO> updateMeal(UpdateMealRequest updateMealRequest){
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
        currentMeal.setTerm(TermConfig.valueOf(updateMealRequest.getTerm()).getTerm());
        currentMeal.setRecipeDTOS(newRecipes);
        currentMeal.setUpdatedAt(LocalDate.now());

        // Thực hiện cập nhật bữa ăn
        MealDTO updatedMeal = mealService.updateMeal(currentMeal);

        if(updatedMeal == null){
            throw new ServiceException(
                    ResCode.MEAL_UPDATE_FAILED.getMessage(),
                    ResCode.MEAL_UPDATE_FAILED.getCode()
            );
        }

        return ResponseEntity.ok(updatedMeal);
    }

    @DeleteMapping("/delete/{mealId}")
    public ResponseEntity<MealDTO> deleteMeal(@PathVariable Long mealId){
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

        return ResponseEntity.ok(deletedMeal);
    }
}
