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
import com.grocery.app.repositories.MealRepo;
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
    private MealRepo mealRepository;

    @Override
    public MealDTO createMeal(MealDTO mealDTO) {
        // Chuyển đổi đối tượng MealDTO thành Meal
        Meal meal = modelMapper.map(mealDTO, Meal.class);

        // Lưu Meal mới vào cơ sở dữ liệu
        Meal createdMeal = mealRepository.save(meal);

        // Chuyển đổi đối tượng Meal đã tạo thành MealDTO và trả về
        return modelMapper.map(createdMeal, MealDTO.class);
    }

    @Override
    public MealDTO getMealById(Long userId, Long mealId) {
        // Tìm kiếm Meal theo mealId
        Meal meal = mealRepository.findById(mealId).orElse(null);

        // Kiểm tra xem Meal có tồn tại không
        if(meal == null){
            throw new ServiceException(
                    ResCode.MEAL_NOT_FOUND.getMessage(),
                    ResCode.MEAL_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra xem người dùng hiện tại có phải là chủ sở hữu của Meal không
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

        // Kiểm tra nếu "from" nhỏ hơn 0, ném ra lỗi
        if (from < 0) {
            throw new IndexOutOfBoundsException("Phạm vi phân trang không hợp lệ.");
        }

        // Lấy danh sách con của các phần tử trong phạm vi từ "from" đến "to"
        List<Meal> paginatedMeals = meals.subList(from, to);

        // Chuyển đổi danh sách Meal thành danh sách MealDTO và trả về
        return paginatedMeals.stream()
                .map(meal -> modelMapper.map(meal, MealDTO.class))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public RecommendedMealDTO recommendMeal(Long userId, String term) {
        // Validate term
        if (!TermConfig.contains(term)) {
            throw new ServiceException(
                    ResCode.INVALID_TERM.getMessage(),
                    ResCode.INVALID_TERM.getCode()
            );
        }
        // Lấy tất cả các món ăn của người dùng dựa trên term
        ArrayList<Meal> meals = mealRepository.findAllByTerm(userId, term);

        // Kiểm tra nếu danh sách rỗng, trả về null
        if(meals.size() == 0) return null;

        // Tính trung bình số lượng công thức (recipes) của các bữa ăn
        int avgRecipes = 0;
        for (Meal meal: meals) {
            avgRecipes += meal.getRecipes().size();
        }
        avgRecipes /= meals.size();

        // Khởi tạo danh sách chứa các ID của công thức đã chọn và đối tượng RecommendedMealDTO
        ArrayList<Long> recipeIds = new ArrayList<>();
        RecommendedMealDTO recommendedMealDTO = RecommendedMealDTO
                .builder()
                .term(TermConfig.valueOf(term).getTerm())
                .build();

        int index = 0;
        // Chọn ngẫu nhiên các công thức cho đến khi đạt đến số lượng trung bình
        while(recommendedMealDTO.getRecommendedRecipes().size() < avgRecipes){
            // Lấy một công thức ngẫu nhiên từ một món ăn trong danh sách
            ArrayList<Recipe> recipes = (ArrayList<Recipe>) meals.get(index).getRecipes();
            Recipe recipe = recipes.get((int) Math.floor(Math.random() * recipes.size()));

            // Kiểm tra xem công thức đã được thêm vào hay chưa
            if(!recipeIds.contains(recipe.getId())){
                recipeIds.add(recipe.getId());
                RecipeDTO recipeDTO = modelMapper.map(recipe, RecipeDTO.class);

                // Thêm công thức vào danh sách món ăn được đề xuất nếu chưa có trong danh sách
                recommendedMealDTO.getRecommendedRecipes().add(recipeDTO);
            }
        }

        return recommendedMealDTO;
    }

    @Override
    public MealDTO updateMeal(MealDTO mealDTO) {
        Meal meal = mealRepository.findById(mealDTO.getMealId())
                .orElseThrow(() -> new ServiceException(
                        ResCode.MEAL_NOT_FOUND.getMessage(),
                        ResCode.MEAL_NOT_FOUND.getCode())
                );

        modelMapper.map(mealDTO, meal); // Ghi đè các thuộc tính từ mealDTO sang meal
        Meal updatedMeal = mealRepository.save(meal);

        return modelMapper.map(updatedMeal, MealDTO.class);
    }

    @Override
    public MealDTO deleteMeal(Long userId, Long mealId) {
        // Tìm kiếm Meal theo mealId
        Meal meal = mealRepository.findById(mealId).orElse(null);

        // Kiểm tra xem Meal có tồn tại không
        if(meal == null){
            throw new ServiceException(
                    ResCode.MEAL_NOT_FOUND.getMessage(),
                    ResCode.MEAL_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra xem người dùng hiện tại có phải là chủ sở hữu của Meal không
        boolean isOwner = Objects.equals(meal.getUser().getId(), userId);
        if(!isOwner){
            throw new ServiceException(
                    ResCode.NOT_MEAL_OWNER.getMessage(),
                    ResCode.NOT_MEAL_OWNER.getCode()
            );
        }

        // Đặt trạng thái của Meal thành "DELETED"
        meal.setStatus(StatusConfig.DELETED.getStatus());

        // Lưu Meal đã cập nhật trạng thái vào cơ sở dữ liệu
        meal = mealRepository.save(meal);

        return modelMapper.map(
                meal,
                MealDTO.class
        );
    }
}
