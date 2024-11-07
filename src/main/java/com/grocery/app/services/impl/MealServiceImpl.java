package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.constant.TermConfig;
import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.request.RecommendedMealDTO;
import com.grocery.app.entities.Meal;
import com.grocery.app.entities.Recipe;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.MealRepository;
import com.grocery.app.services.MealService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MealServiceImpl implements MealService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MealRepository mealRepository;

    @Override
    public MealDTO createMeal(MealDTO mealDTO) {
        Meal meal = modelMapper.map(
                mealDTO,
                Meal.class
        );

        Meal createdMeal = mealRepository.save(meal);
        return modelMapper.map(
                meal,
                MealDTO.class
        );
    }

    @Override
    public MealDTO getMealById(Long userId, Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElse(null);

        // Validate meal existence
        if(meal == null){
            throw new ServiceException(
                    ResCode.MEAL_NOT_FOUND.getMessage(),
                    ResCode.MEAL_NOT_FOUND.getCode()
            );
        }

        // Validate meal owner
        boolean isOwner = Objects.equals(meal.getUser().getId(), userId);
        if(!isOwner){
            throw new ServiceException(
                    ResCode.NOT_MEAL_OWNER.getMessage(),
                    ResCode.NOT_MEAL_OWNER.getCode()
            );
        }

        return modelMapper.map(
                meal,
                MealDTO.class
        );
    }

    @Override
    public ArrayList<MealDTO> getAllMeal(Long userId, int from, int to) {
        // Lấy tất cả các Meal của người dùng dựa vào userId
        List<Meal> meals = mealRepository.findAllByUserId(userId);

        // Kiểm tra và điều chỉnh chỉ số "to" để không vượt ngoài phạm vi danh sách
        to = Math.min(to, meals.size());
        from = Math.min(from, to);

        if (from < 0) {
            throw new IndexOutOfBoundsException("Phạm vi phân trang không hợp lệ.");
        }

        // Lấy danh sách con của các phần tử trong phạm vi từ "from" đến "to"
        List<Meal> paginatedMeals = meals.subList(from, to);

        // Chuyển đổi danh sách Meal thành danh sách MealDTO
        return paginatedMeals.stream()
                .map(meal -> modelMapper.map(meal, MealDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    @Override
    public RecommendedMealDTO recommendMeal(Long userId, String term) {
        ArrayList<Meal> meals = mealRepository.findAllByTerm(userId, term);

        if(meals.size() == 0) return null;
        int avgRecipes = 0;
        for (Meal meal: meals) {
            avgRecipes += meal.getRecipes().size();
        }
        avgRecipes /= meals.size();

        ArrayList<Long> recipeIds = new ArrayList<>();
        RecommendedMealDTO recommendedMealDTO = RecommendedMealDTO
                .builder()
                .term(TermConfig.valueOf(term).getTerm())
                .build();

        int index = 0;
        while(recommendedMealDTO.getRecommendedRecipes().size() < avgRecipes){
            // Get a random recipe from a meal in the list
            ArrayList<Recipe> recipes = (ArrayList<Recipe>) meals.get(index).getRecipes();
            Recipe recipe = recipes.get((int) Math.floor(Math.random() * recipes.size()));

            // Check whether recipe is added or not
            if(!recipeIds.contains(recipe.getId())){
                recipeIds.add(recipe.getId());
                RecipeDTO recipeDTO = modelMapper.map(
                        recipe,
                        RecipeDTO.class
                );

                // If recipe is not in list, add it to the list of recommend meal
                recommendedMealDTO.getRecommendedRecipes().add(recipeDTO);
            }
        }

        return recommendedMealDTO;
    }

    @Override
    public MealDTO updateMeal(MealDTO mealDTO) {
        Meal meal = modelMapper.map(
                mealDTO,
                Meal.class
        );
        return mealDTO;
    }

    @Override
    public MealDTO deleteMeal(Long userId, Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElse(null);

        // Validate meal existence
        if(meal == null){
            throw new ServiceException(
                    ResCode.MEAL_NOT_FOUND.getMessage(),
                    ResCode.MEAL_NOT_FOUND.getCode()
            );
        }

        // Validate meal owner
        boolean isOwner = Objects.equals(meal.getUser().getId(), userId);
        if(!isOwner){
            throw new ServiceException(
                    ResCode.NOT_MEAL_OWNER.getMessage(),
                    ResCode.NOT_MEAL_OWNER.getCode()
            );
        }

        meal.setStatus(StatusConfig.DELETED.getStatus());

        meal = mealRepository.save(meal);

        return modelMapper.map(
                meal,
                MealDTO.class
        );
    }
}
