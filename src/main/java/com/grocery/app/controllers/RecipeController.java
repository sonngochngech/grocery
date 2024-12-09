package com.grocery.app.controllers;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.FavoriteRecipeDTO;
import com.grocery.app.dto.FoodDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.dto.request.FavRequest;
import com.grocery.app.dto.request.createRequest.CreateRecipeRequest;
import com.grocery.app.dto.request.updateRequest.UpdateRecipeRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.services.AuthenticationService;
import com.grocery.app.services.FoodService;
import com.grocery.app.services.RecipeService;
import com.grocery.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;

@RestController
@RequestMapping("api/recipe")
public class RecipeController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserService userService;
    @Autowired
    private FoodService foodService;

    // CRUD Recipe

    @PostMapping("/create")
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody CreateRecipeRequest createRecipeRequest) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Kiểm tra người dùng
        UserDetailDTO userDetailDTO = userService.getUser(currentUser.getId());
        if(userDetailDTO == null){
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        ArrayList<FoodDTO> foodDTOArrayList = new ArrayList<>();
        for(Long id : createRecipeRequest.getFoods()){
            FoodDTO foodDTO = foodService.getFoodById(currentUser.getId(), id);

            // check food
            if(foodDTO == null){
                throw new ServiceException(
                        ResCode.FOOD_NOT_FOUND.getMessage(),
                        ResCode.FOOD_NOT_FOUND.getCode()
                );
            }

            foodDTOArrayList.add(foodDTO);
        }

        RecipeDTO recipeDTO = RecipeDTO.builder()
                .userDetailDTO(userDetailDTO)
                .name(createRecipeRequest.getName())
                .description(createRecipeRequest.getDescription())
                .imageUrl(createRecipeRequest.getImageUrl())
                .foodDTOs(foodDTOArrayList)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        RecipeDTO createdRecipe = recipeService.createRecipe(recipeDTO);

        if(createdRecipe == null){
            throw new ServiceException(
                    ResCode.RECIPE_CREATION_FAILED.getMessage(),
                    ResCode.RECIPE_CREATION_FAILED.getCode()
            );
        }

        return ResponseEntity.ok(createdRecipe);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy recipe theo ID và người dùng hiện tại
        RecipeDTO recipeDTO = recipeService.getRecipeById(
                currentUser.getId(),
                id
        );

        // Trả về thông tin food
        return ResponseEntity.ok(recipeDTO);
    }

    @PostMapping("update")
    public ResponseEntity<RecipeDTO> updateRecipe(@RequestBody UpdateRecipeRequest updateRecipeRequest) {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        // Lấy recipe
        RecipeDTO recipeDTO = recipeService.getRecipeById(userInfoConfig.getId(), updateRecipeRequest.getId());

        // Lấy Foods mới
        ArrayList<FoodDTO> foodDTOArrayList = new ArrayList<>();
        for(Long id : updateRecipeRequest.getFoods()){
            FoodDTO foodDTO = foodService.getFoodById(userInfoConfig.getId(), id);
            foodDTOArrayList.add(foodDTO);
        }

        recipeDTO.setDescription(updateRecipeRequest.getDescription());
        recipeDTO.setName(updateRecipeRequest.getName());
        recipeDTO.setFoodDTOs(foodDTOArrayList);
        recipeDTO.setUpdatedAt(LocalDate.now());

        RecipeDTO updatedRecipeDTO = recipeService.updateRecipe(recipeDTO);

        return ResponseEntity.ok(updatedRecipeDTO);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<RecipeDTO> deleteRecipe(@PathVariable Long id) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Thực hiện xóa bữa ăn
        RecipeDTO deletedRecipe = recipeService.deleteRecipe(
                currentUser.getId(),
                id
        );

        if(deletedRecipe == null){
            throw new ServiceException(
                    ResCode.RECIPE_DELETE_FAILED.getMessage(),
                    ResCode.RECIPE_DELETE_FAILED.getCode()
            );
        }

        return ResponseEntity.ok(deletedRecipe);
    }

    // get all

    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<RecipeDTO>> getAllRecipes(@RequestParam int from, @RequestParam int to) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy tất cả recipe trong khoảng từ `from` đến `to`
        ArrayList<RecipeDTO> recipeDTOS = recipeService.getAllRecipe(
                currentUser.getId(),
                from,
                to
        );

        // Trả về danh sách recipe
        return ResponseEntity.ok(recipeDTOS);
    }

    // chỉnh sửa danh sách ưa thích

    @GetMapping("/favorite/get")
    public ResponseEntity<ArrayList<RecipeDTO>> getFavoriteRecipe(@RequestParam int from, @RequestParam int to){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy danh sách
        ArrayList<RecipeDTO> response = recipeService.getFavoriteList(
                currentUser.getId(),
                from,
                to
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/favorites/add")
    public ResponseEntity<ArrayList<RecipeDTO>> addRecipeToFavorites(@RequestBody FavRequest favRequest) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Thêm vào danh sách
        FavoriteRecipeDTO favoriteRecipeDTO = recipeService.addFavoriteRecipe(
                currentUser.getId(),
                favRequest.getRecipeId()
        );

        // Lấy danh sách trả về
        ArrayList<RecipeDTO> recipeDTOArrayList = recipeService.getFavoriteList(
                currentUser.getId(),
                favRequest.getFrom(),
                favRequest.getTo()
        );

        return ResponseEntity.ok(recipeDTOArrayList);
    }

    @PostMapping("/favorites/remove")
    public ResponseEntity<ArrayList<RecipeDTO>> removeRecipeFromFavorites(@RequestBody FavRequest favRequest) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // xóa khỏi danh sách
        FavoriteRecipeDTO favoriteRecipeDTO = recipeService.removeFavoriteRecipe(
                currentUser.getId(),
                favRequest.getRecipeId()
        );

        // Lấy danh sách trả về
        ArrayList<RecipeDTO> recipeDTOArrayList = recipeService.getFavoriteList(
                currentUser.getId(),
                favRequest.getFrom(),
                favRequest.getTo()
        );

        return ResponseEntity.ok(recipeDTOArrayList);
    }
}
