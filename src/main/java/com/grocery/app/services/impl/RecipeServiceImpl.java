package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.FavoriteRecipeDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.entities.Family;
import com.grocery.app.entities.FavoriteRecipe;
import com.grocery.app.entities.Recipe;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.FavoriteRecipeRepo;
import com.grocery.app.repositories.RecipeRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        // Chuyển đổi đối tượng RecipeDTO thành Meal
        Recipe recipe = modelMapper.map(recipeDTO, Recipe.class);

        // Lưu Recipe mới vào cơ sở dữ liệu
        Recipe createdRecipe = recipeRepo.save(recipe);

        // Chuyển đổi đối tượng Recipe đã tạo thành RecipeDTO và trả về
        return modelMapper.map(createdRecipe, RecipeDTO.class);
    }

    @Override
    public RecipeDTO getRecipeById(Long userId, Long recipeId) {
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);

        // Kiểm tra sự tồn tại
        if(recipe == null || Objects.equals(recipe.getStatus(), StatusConfig.DELETED.getStatus())){
            throw new ServiceException(
                    ResCode.RECIPE_NOT_FOUND.getMessage(),
                    ResCode.RECIPE_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu
        if(!Objects.equals(userId, recipe.getUser().getId())){
            throw new ServiceException(
                    ResCode.NOT_RECIPE_OWNER.getMessage(),
                    ResCode.NOT_RECIPE_OWNER.getCode()
            );
        }

        return modelMapper.map(recipe, RecipeDTO.class);
    }

    @Override
    public ArrayList<RecipeDTO> getAllRecipe(Long userId, int from, int to) {
        ArrayList<Recipe> recipes = recipeRepo.findAllByUserId(userId);

        int maxSize = recipes.size();

        // Nếu không có recipe thì trả về mảng rỗng
        if(maxSize == 0) return new ArrayList<>();

        // clamp
        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize)); // to trong khoảng [from + 1, maxSize]

        // Chuyển đổi phần tử trong range từ Recipe sang RecipeDTO
        ArrayList<RecipeDTO> result = new ArrayList<>();
        for (int i = from; i < to; i++) {
            result.add(modelMapper.map(recipes.get(i), RecipeDTO.class));
        }

        return result;
    }

    @Override
    public RecipeDTO updateRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = modelMapper.map(recipeDTO, Recipe.class);

        if(recipe == null){
            throw new ServiceException(
                    ResCode.RECIPE_UPDATE_FAILED.getMessage(),
                    ResCode.RECIPE_UPDATE_FAILED.getCode()
            );
        }

        Recipe updatedRecipe = recipeRepo.save(recipe);

        return modelMapper.map(updatedRecipe, RecipeDTO.class);
    }

    @Override
    public RecipeDTO deleteRecipe(Long userId, Long recipeId) {
        // Tìm food
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);

        if(recipe == null || Objects.equals(recipe.getStatus(), StatusConfig.DELETED.getStatus())){
            throw new ServiceException(
                    ResCode.RECIPE_NOT_FOUND.getMessage(),
                    ResCode.RECIPE_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu
        boolean isOwner = Objects.equals(userId, recipe.getUser().getId());

        if(!isOwner){
            throw new ServiceException(
                    ResCode.NOT_RECIPE_OWNER.getMessage(),
                    ResCode.NOT_RECIPE_OWNER.getCode()
            );
        }

        // xóa food
        recipe.setStatus(StatusConfig.DELETED.getStatus());
        Recipe deletedRecipe = recipeRepo.save(recipe);

        return modelMapper.map(deletedRecipe, RecipeDTO.class);
    }

    public FavoriteRecipe createFavoriteList(Long userId, Long familyId) {
        FavoriteRecipe favoriteRecipe = favoriteRecipeRepo.findByUser(userId, familyId);
        if(favoriteRecipe != null){
            throw new ServiceException(
                    ResCode.FAVORITE_RECIPE_LIST_EXISTED.getMessage(),
                    ResCode.FAVORITE_RECIPE_LIST_EXISTED.getCode()
            );
        }

        // check user
        User user = userRepo.findById(userId).orElse(null);
        if(user == null){
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // check family
        Family selectedFamily = familyRepo.findById(familyId).orElse(null);
        if(selectedFamily == null){
            throw new ServiceException(
                    ResCode.FAMILY_NOT_FOUND.getCode(),
                    ResCode.FAMILY_NOT_FOUND.getCode()
            );
        }

        // check quyền sở hữu family
        if(!Objects.equals(selectedFamily.getOwner().getId(), userId)){
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        favoriteRecipe = FavoriteRecipe.builder()
                .user(user)
                .favoriteList(new ArrayList<>())
                .family(selectedFamily)
                .build();

        return favoriteRecipe;
    }

    @Override
    public FavoriteRecipeDTO getFavoriteList(Long userId, Long familyId) {
        FavoriteRecipe favoriteRecipe = favoriteRecipeRepo.findByUser(userId, familyId);
        if(favoriteRecipe == null){
            favoriteRecipe = createFavoriteList(userId, familyId);
        }

        return modelMapper.map(favoriteRecipe, FavoriteRecipeDTO.class);
    }

    @Override
    public FavoriteRecipeDTO removeFavoriteRecipe(Long userId, Long familyId, Long recipeId) {
        FavoriteRecipe favoriteRecipe = favoriteRecipeRepo.findByUser(userId, familyId);
        if(favoriteRecipe == null){
            favoriteRecipe = createFavoriteList(userId, familyId);
        }

        // Tìm kiếm recipe
        Iterator<Recipe> iterator = favoriteRecipe.getFavoriteList().iterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (Objects.equals(recipe.getId(), recipeId)) {
                iterator.remove(); // Xóa nếu có cùng ID
                break;
            }
        }

        // lưu lại
        favoriteRecipeRepo.save(favoriteRecipe);

        return modelMapper.map(favoriteRecipe, FavoriteRecipeDTO.class);
    }

    @Override
    public FavoriteRecipeDTO addFavoriteRecipe(Long userId, Long familyId, Long recipeId) {
        FavoriteRecipe favoriteRecipe = favoriteRecipeRepo.findByUser(userId, familyId);
        if(favoriteRecipe == null){
            favoriteRecipe = createFavoriteList(userId, familyId);
        }

        // Kiểm tra đã ở trong list chưa
        boolean alreadyExists = favoriteRecipe.getFavoriteList().stream()
                .anyMatch(recipe -> Objects.equals(recipe.getId(), recipeId));

        if(alreadyExists){
            return modelMapper.map(favoriteRecipe, FavoriteRecipeDTO.class);
        }

        // Kiểm tra tồn tại recipe
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if(recipe == null){
            throw new ServiceException(
                    ResCode.RECIPE_NOT_FOUND.getMessage(),
                    ResCode.RECIPE_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra chủ sở hữu
        if(!Objects.equals(recipe.getUser().getId(), userId)){
            throw new ServiceException(
                    ResCode.NOT_FOOD_OWNER.getMessage(),
                    ResCode.NOT_FOOD_OWNER.getCode()
            );
        }

        favoriteRecipe.getFavoriteList().add(recipe);
        favoriteRecipeRepo.save(favoriteRecipe);

        return  modelMapper.map(favoriteRecipe, FavoriteRecipeDTO.class);
    }
}
