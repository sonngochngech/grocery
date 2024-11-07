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

        // Validate user
        UserDetailDTO user = userService.getUser(currentUser.getId());

        if(user == null){
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // Validate ownership of family
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

        // Validate recipes in meal
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

        // Build temporary meal from request
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

        // Validate created meal
        MealDTO createdMeal = mealService.createMeal(newMeal);

        if(createdMeal == null){
            throw new ServiceException(
                    ResCode.MEAL_CREATION_FAILED.getMessage(),
                    ResCode.MEAL_CREATION_FAILED.getCode()
            );
        }

        // Create response of recently created meal
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
    }
    @GetMapping("/get/{mealId}")
    public ResponseEntity<MealDTO> getMealById(@PathVariable Long mealId){
        return null;
    }
    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<MealDTO>> getAllMeal(@RequestParam int from, @RequestParam int to){
        return null;
    }

    @GetMapping("/recommend/{term}")
    public ResponseEntity<ArrayList<RecommendedMealDTO>> recommendMeal(@PathVariable String term){
        return null;
    }
    @PostMapping("/update")
    public ResponseEntity<MealDTO> updateMeal(UpdateMealRequest updateMealRequest){
        return null;
    }
    @DeleteMapping("/delete/{mealId}")
    public ResponseEntity<MealDTO> deleteMeal(@PathVariable Long mealId){
        return null;
    }
}
