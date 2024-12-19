package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.FavoriteRecipeListDTO;
import com.grocery.app.dto.FoodDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.entities.*;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.*;
import com.grocery.app.services.FoodService;
import com.grocery.app.services.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

@Service
public class RecipeServiceImpl implements RecipeService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RecipeRepo recipeRepo;
    @Autowired
    private FavoriteRecipeRepo favoriteRecipeRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FamilyRepo familyRepo;
    @Autowired
    private FoodService foodService;
    @Autowired
    private MealRepo mealRepo;
    @Autowired
    private FoodRepo foodRepo;

    @Override
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        // Chuyển đổi đối tượng RecipeDTO thành Meal
        Recipe recipe = convertToRecipe(recipeDTO);

        // Lưu Recipe mới vào cơ sở dữ liệu
        Recipe createdRecipe = recipeRepo.save(recipe);

        // Chuyển đổi đối tượng Recipe đã tạo thành RecipeDTO và trả về
        return convertToRecipeDTO(createdRecipe);
    }

    @Override
    public RecipeDTO getRecipeById(Long userId, Long recipeId) {
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);

        // Kiểm tra sự tồn tại
        if (recipe == null || Objects.equals(recipe.getStatus(), StatusConfig.DELETED.getStatus())) {
            throw new ServiceException(
                    ResCode.RECIPE_NOT_FOUND.getMessage(),
                    ResCode.RECIPE_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu
        if (!Objects.equals(userId, recipe.getUser().getId())) {
            throw new ServiceException(
                    ResCode.NOT_RECIPE_OWNER.getMessage(),
                    ResCode.NOT_RECIPE_OWNER.getCode()
            );
        }

        return convertToRecipeDTO(recipe);
    }

    @Override
    public ArrayList<RecipeDTO> getAllRecipe(Long userId, int from, int to) {
        ArrayList<Recipe> recipes = recipeRepo.findAllByUserId(userId);

        int maxSize = recipes.size();

        // Nếu không có recipe thì trả về mảng rỗng
        if (maxSize == 0) return new ArrayList<>();

        // clamp
        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize)); // to trong khoảng [from + 1, maxSize]

        // Chuyển đổi phần tử trong range từ Recipe sang RecipeDTO
        ArrayList<RecipeDTO> result = new ArrayList<>();
        for (int i = from; i < to; i++) {
            result.add(convertToRecipeDTO(recipes.get(i)));
        }

        return result;
    }

    @Override
    public RecipeDTO updateRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = convertToRecipe(recipeDTO);

        Recipe updatedRecipe = recipeRepo.save(recipe);

        return convertToRecipeDTO(updatedRecipe);
    }

    @Override
    public RecipeDTO deleteRecipe(Long userId, Long recipeId) {
        // Tìm food
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);

        if (recipe == null || Objects.equals(recipe.getStatus(), StatusConfig.DELETED.getStatus())) {
            throw new ServiceException(
                    ResCode.RECIPE_NOT_FOUND.getMessage(),
                    ResCode.RECIPE_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu
        boolean isOwner = Objects.equals(userId, recipe.getUser().getId());

        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_RECIPE_OWNER.getMessage(),
                    ResCode.NOT_RECIPE_OWNER.getCode()
            );
        }

        // xóa food
        recipe.setStatus(StatusConfig.DELETED.getStatus());
        Recipe deletedRecipe = recipeRepo.save(recipe);

        return convertToRecipeDTO(deletedRecipe);
    }

    @Override
    public FavoriteRecipeList createFavoriteList(Long userId) {
        FavoriteRecipeList favoriteRecipeList = favoriteRecipeRepo.findByUser(userId);
        if (favoriteRecipeList != null) {
            throw new ServiceException(
                    ResCode.FAVORITE_RECIPE_LIST_EXISTED.getMessage(),
                    ResCode.FAVORITE_RECIPE_LIST_EXISTED.getCode()
            );
        }

        // check user
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        favoriteRecipeList = FavoriteRecipeList.builder()
                .user(user)
                .favoriteList(new ArrayList<>())
                .build();
        System.out.println(favoriteRecipeList.getFavoriteList());

        return favoriteRecipeList;
    }

    @Override
    public ArrayList<RecipeDTO> getFavoriteList(Long userId, int from, int to) {
        FavoriteRecipeList favoriteRecipeList = favoriteRecipeRepo.findByUser(userId);
        ArrayList<RecipeDTO> recipeDTOArrayList = new ArrayList<>();
        if (favoriteRecipeList == null) {
            favoriteRecipeList = createFavoriteList(userId);
            return recipeDTOArrayList;
        }

        System.out.println(from + " " + to);

        // clamp
        int maxSize = favoriteRecipeList.getFavoriteList().size();
        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize)); // to trong khoảng [from + 1, maxSize]

        // Chuyển đổi phần tử trong range từ Food sang FoodDTO
        for (int i = from; i < to; i++) {
            recipeDTOArrayList.add(convertToRecipeDTO(favoriteRecipeList.getFavoriteList().get(i)));
        }

        return recipeDTOArrayList;
    }

    @Override
    public FavoriteRecipeListDTO removeFavoriteRecipe(Long userId, Long recipeId) {
        FavoriteRecipeList favoriteRecipeList = favoriteRecipeRepo.findByUser(userId);
        if (favoriteRecipeList == null) {
            favoriteRecipeList = createFavoriteList(userId);
        }

        // Tìm kiếm recipe
        int n = favoriteRecipeList.getFavoriteList().size();
        System.out.println("current size " + n);
        int index = -1;
        for (int i = 0; i < n; ++i) {
            if (Objects.equals(favoriteRecipeList.getFavoriteList().get(i).getId(), recipeId)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            favoriteRecipeList.getFavoriteList().get(index).setFavRecipe(null);
            recipeRepo.save(favoriteRecipeList.getFavoriteList().get(index));
            favoriteRecipeList.getFavoriteList().remove(index);
        }

        System.out.println("after " + favoriteRecipeList.getFavoriteList().size());

        // lưu lại
        favoriteRecipeRepo.save(favoriteRecipeList);

        return convertToFavoriteRecipeListDTO(favoriteRecipeList);
    }

    @Override
    public FavoriteRecipeListDTO addFavoriteRecipe(Long userId, Long recipeId) {
        FavoriteRecipeList favoriteRecipeList = favoriteRecipeRepo.findByUser(userId);
        if (favoriteRecipeList == null) {
            favoriteRecipeList = createFavoriteList(userId);
        }

        // Kiểm tra List không trống đã ở trong list chưa
        boolean alreadyExists = !favoriteRecipeList.getFavoriteList().isEmpty()
                && favoriteRecipeList.getFavoriteList().stream()
                .anyMatch(recipe -> Objects.equals(recipe.getId(), recipeId));

        if (alreadyExists) {
            return convertToFavoriteRecipeListDTO(favoriteRecipeList);
        }

        // Kiểm tra tồn tại recipe
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if (recipe == null) {
            throw new ServiceException(
                    ResCode.RECIPE_NOT_FOUND.getMessage(),
                    ResCode.RECIPE_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra chủ sở hữu
        if (!Objects.equals(recipe.getUser().getId(), userId)) {
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }
        System.out.println("size" + favoriteRecipeList.getFavoriteList().size());
        favoriteRecipeList.getFavoriteList().add(recipe);
        recipe.setFavRecipe(favoriteRecipeList);
        favoriteRecipeRepo.save(favoriteRecipeList);
        recipeRepo.save(recipe);
        System.out.println("size" + favoriteRecipeList.getFavoriteList().size());

        return convertToFavoriteRecipeListDTO(favoriteRecipeList);
    }

    @Override
    public RecipeDTO convertToRecipeDTO(Recipe recipe) {
        ArrayList<FoodDTO> foodDTOS = new ArrayList<>();
        for (Food food : recipe.getFoods()) {
            foodDTOS.add(foodService.convertToFoodDTO(food));
        }

        ArrayList<Long> meals = new ArrayList<>();
        for (Meal meal : recipe.getMeals()) {
            meals.add(meal.getMealId());
        }

        return RecipeDTO.builder()
                .id(recipe.getId())
                .user(recipe.getUser().getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .imageUrl(recipe.getImageUrl())
                .meals(meals)
                .favoriteList(recipe.getFavRecipe() != null ? recipe.getFavRecipe().getId() : null)
                .foods(foodDTOS)
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(recipe.getStatus())
                .build();
    }

    @Override
    public Recipe convertToRecipe(RecipeDTO recipeDTO) {
        ArrayList<Meal> meals = new ArrayList<>();
        for (Long id : recipeDTO.getMeals()) {
            meals.add(mealRepo.findById(id).orElse(null));
        }

        ArrayList<Food> foods = new ArrayList<>();
        for (FoodDTO foodDTO : recipeDTO.getFoods()) {
            foods.add(foodService.convertToFood(foodDTO));
        }

        User user = userRepo.findById(recipeDTO.getUser()).orElse(null);

        Recipe recipe = Recipe.builder()
                .user(user)
                .meals(meals)
                .name(recipeDTO.getName())
                .description(recipeDTO.getDescription())
                .imageUrl(recipeDTO.getImageUrl())
                .foods(foods)
                .createdAt(recipeDTO.getCreatedAt())
                .updatedAt(recipeDTO.getUpdatedAt())
                .status(recipeDTO.getStatus())
                .build();

        if (recipeDTO.getId() != null) {
            recipe.setId(recipeDTO.getId());
        }

        if (recipeDTO.getFavoriteList() != null) {
            recipe.setFavRecipe(favoriteRecipeRepo.findById(recipeDTO.getFavoriteList()).orElse(null));
        }

        return recipe;
    }

    public FavoriteRecipeList convertToFavoriteRecipeList(FavoriteRecipeListDTO favoriteRecipeListDTO) {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (RecipeDTO recipeDTO : favoriteRecipeListDTO.getFavoriteDTOList()) {
            recipes.add(convertToRecipe(recipeDTO));
        }

        User user = userRepo.findById(favoriteRecipeListDTO.getUserId()).orElse(null);

        FavoriteRecipeList favoriteRecipeList = FavoriteRecipeList.builder()
                .favoriteList(recipes)
                .user(user)
                .build();

        if (favoriteRecipeListDTO.getId() != null) {
            favoriteRecipeList.setId(favoriteRecipeListDTO.getId());
        }

        return favoriteRecipeList;
    }

    public FavoriteRecipeListDTO convertToFavoriteRecipeListDTO(FavoriteRecipeList favoriteRecipeList) {
        ArrayList<RecipeDTO> recipeDTOS = new ArrayList<>();
        for (Recipe recipe : favoriteRecipeList.getFavoriteList()) {
            recipeDTOS.add(convertToRecipeDTO(recipe));
        }

        return FavoriteRecipeListDTO.builder()
                .Id(favoriteRecipeList.getId())
                .favoriteDTOList(recipeDTOS)
                .userId(favoriteRecipeList.getUser().getId())
                .build();
    }
}
