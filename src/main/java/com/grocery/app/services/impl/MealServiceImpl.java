package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.constant.TermConfig;
import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.request.RecommendedMealDTO;
import com.grocery.app.entities.Family;
import com.grocery.app.entities.Meal;
import com.grocery.app.entities.Recipe;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.MealRepo;
import com.grocery.app.repositories.RecipeRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.MealService;
import com.grocery.app.services.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MealServiceImpl implements MealService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MealRepo mealRepository;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RecipeRepo recipeRepo;
    @Autowired
    private RecipeService recipeService;

    @Override
    public MealDTO createMeal(MealDTO mealDTO) {
        // Chuyển đổi đối tượng MealDTO thành Meal
        Meal meal = convertToEntity(mealDTO);
        // Lưu Meal mới vào cơ sở dữ liệu
        Meal createdMeal = mealRepository.save(meal);
        // Chuyển đổi đối tượng Meal đã tạo thành MealDTO và trả về
        return convertToDTO(createdMeal);
    }

    @Override
    public MealDTO getMealById(Long userId, Long mealId) {
        System.out.println("userId " + userId + " meal Id " + mealId);
        // Tìm kiếm Meal theo mealId
        Meal meal = mealRepository.findById(mealId).orElse(null);

        System.out.println("get from repo meal");
//        for (Field field : Meal.class.getDeclaredFields()){
//            field.setAccessible(true);
//            try {
//                // In tên và giá trị của field
//                System.out.println(field.getName() + ": " + field.get(meal));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
        assert meal != null;
        System.out.println("meal info");
        System.out.println(meal.getUser().getId());
        System.out.println(meal.getFamily().getId());
        System.out.println(meal.getRecipes().size());
        // Kiểm tra xem Meal có tồn tại không
        if(meal == null || Objects.equals(meal.getStatus(), StatusConfig.DELETED.getStatus())){
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

        return convertToDTO(meal);
    }

    @Override
    public ArrayList<MealDTO> getAllMeal(Long userId, int from, int to) {
        // Lấy tất cả các Meal của người dùng dựa vào userId
        List<Meal> meals = mealRepository.findAllByUserId(userId);

        // Kiểm tra và điều chỉnh chỉ số "to" để không vượt ngoài phạm vi danh sách
        int maxSize = meals.size();

        if(maxSize == 0) return new ArrayList<>();

        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize));

        // Lấy danh sách con của các phần tử trong phạm vi từ "from" đến "to"
        List<Meal> paginatedMeals = meals.subList(from, to);
        ArrayList<MealDTO> paginatedMealDTOs = new ArrayList<>();

        for(Meal meal : paginatedMeals){
            paginatedMealDTOs.add(convertToDTO(meal));
        }

        return paginatedMealDTOs;
    }

    @Override
    public ArrayList<RecommendedMealDTO> recommendMeal(Long userId) {
        ArrayList<RecommendedMealDTO> recommendations = new ArrayList<>();

        // Lặp qua tất cả các term trong TermConfig
        for (TermConfig termConfig : TermConfig.values()) {
            String term = termConfig.getTerm();
            System.out.println("Processing term: " + term);

            // Lấy tất cả các món ăn của người dùng dựa trên term
            ArrayList<Meal> meals = mealRepository.findAllByTerm(userId, term);
            System.out.println("Number of meals found for term " + term + ": " + meals.size());

            // Bỏ qua nếu danh sách bữa ăn rỗng
            if (meals.isEmpty()) continue;

            // Tính trung bình số lượng công thức (recipes) của các bữa ăn
            int avgRecipes = meals.stream()
                    .mapToInt(meal -> meal.getRecipes().size())
                    .sum() / meals.size();

            // Tạo một tập hợp các công thức không trùng lặp
            HashSet<Recipe> uniqueRecipesSet = new HashSet<>();
            for (Meal meal : meals) {
                uniqueRecipesSet.addAll(meal.getRecipes());
            }

            // Chuyển HashSet thành ArrayList để dễ xử lý
            ArrayList<Recipe> uniqueRecipes = new ArrayList<>(uniqueRecipesSet);

            // Trộn danh sách để tạo tính ngẫu nhiên
            Collections.shuffle(uniqueRecipes);

            // Khởi tạo danh sách chứa các công thức được chọn
            ArrayList<RecipeDTO> selectedRecipes = new ArrayList<>();

            // Chọn ngẫu nhiên avgRecipes công thức hoặc tất cả nếu ít hơn
            for (int i = 0; i < Math.min(avgRecipes, uniqueRecipes.size()); i++) {
                Recipe recipe = uniqueRecipes.get(i);
                RecipeDTO recipeDTO = recipeService.convertToRecipeDTO(recipe);
                selectedRecipes.add(recipeDTO);
            }

            // Chỉ thêm vào danh sách kết quả nếu có công thức được chọn
            if (!selectedRecipes.isEmpty()) {
                recommendations.add(RecommendedMealDTO.builder()
                        .term(term)
                        .recommendedRecipes(selectedRecipes)
                        .build());
            }
        }

        return recommendations;
    }



    @Override
    public MealDTO updateMeal(MealDTO mealDTO) {

        Meal meal = convertToEntity(mealDTO);
        Meal updatedMeal = mealRepository.save(meal);

        return convertToDTO(updatedMeal);
    }

    @Override
    public MealDTO deleteMeal(Long userId, Long mealId) {
        // Tìm kiếm Meal theo mealId
        Meal meal = mealRepository.findById(mealId).orElse(null);

        // Kiểm tra xem Meal có tồn tại không
        if(meal == null || Objects.equals(meal.getStatus(), StatusConfig.DELETED.getStatus())){
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

        return convertToDTO(meal);
    }

    private MealDTO convertToDTO(Meal meal){
        System.out.println("convert to meal dto");
        MealDTO mealDTO = MealDTO.builder()
                .mealId(meal.getMealId())
                .name(meal.getName())
                .term(meal.getTerm())
                .date(meal.getDate())
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .status(meal.getStatus())
                .build();

        ArrayList<RecipeDTO> recipeDTOS = new ArrayList<>();
        for(Recipe recipe : meal.getRecipes()){
            System.out.println(recipe.getId());
            recipeDTOS.add(recipeService.convertToRecipeDTO(recipe));
        }

        mealDTO.setRecipeDTOS(recipeDTOS);
        mealDTO.setUserDetailDTO(modelMapper.map(meal.getUser(), UserDTO.class));
        mealDTO.setFamilyDetailDTO(modelMapper.map(meal.getFamily(), FamilyDTO.class));

        return mealDTO;
    }

    Meal convertToEntity(MealDTO mealDTO){
        System.out.println("convert to meal entity");
        Meal meal = Meal.builder()
                .name(mealDTO.getName())
                .term(mealDTO.getTerm())
                .date(mealDTO.getDate())
                .createdAt(mealDTO.getCreatedAt())
                .updatedAt(mealDTO.getUpdatedAt())
                .status(mealDTO.getStatus())
                .build();

        ArrayList<Recipe> recipes = new ArrayList<>();
        for(RecipeDTO recipeDTO : mealDTO.getRecipeDTOS()){
            Recipe recipe = recipeRepo.findById(recipeDTO.getId()).orElse(null);
            recipes.add(recipe);
        }

        meal.setRecipes(recipes);
        meal.setUser(modelMapper.map(mealDTO.getUserDetailDTO(), User.class));
        meal.setFamily(modelMapper.map(mealDTO.getFamilyDetailDTO(), Family.class));

        if(mealDTO.getMealId() != null){
            meal.setMealId(mealDTO.getMealId());
        }

        return meal;
    }
}
