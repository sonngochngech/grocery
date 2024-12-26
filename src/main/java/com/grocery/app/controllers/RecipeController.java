package com.grocery.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.*;
import com.grocery.app.dto.request.FavRequest;
import com.grocery.app.dto.request.createRequest.CreateRecipeRequest;
import com.grocery.app.dto.request.updateRequest.UpdateRecipeRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.*;
import com.grocery.app.utils.ImagePathUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import static com.grocery.app.config.constant.AppConstants.AVATAR_PATH;

@RestController
@RequestMapping("api/recipe")
@SecurityRequirement(name = "bearerAuth")
public class RecipeController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserService userService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private FileService fileService;

    // CRUD Recipe

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<RecipeDTO>> createRecipe(
            @RequestParam("request") String requestJson,
            @RequestParam("image") MultipartFile image) throws IOException {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Kiểm tra người dùng
        UserDTO userDetailDTO = userService.getUserById(currentUser.getId());
        if (userDetailDTO == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // Chuyển đổi requestJson thành đối tượng CreateRecipeRequest
        CreateRecipeRequest createRecipeRequest = new ObjectMapper().readValue(requestJson, CreateRecipeRequest.class);

        ArrayList<FoodDTO> foodDTOArrayList = new ArrayList<>();
        for (Long id : createRecipeRequest.getFoods()) {
            FoodDTO foodDTO = foodService.getFoodById(currentUser.getId(), id);
            foodDTOArrayList.add(foodDTO);
        }

        // Xử lý ảnh
        String fileName = ImagePathUtil.setImagePath(AVATAR_PATH, image.getOriginalFilename(), System.currentTimeMillis());
        String imageUrl = fileService.uploadImage(fileName, image.getBytes());

        // Tạo đối tượng RecipeDTO
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .user(userDetailDTO.getId())
                .name(createRecipeRequest.getName())
                .meals(new ArrayList<>())
                .description(createRecipeRequest.getDescription())
                .imageUrl(imageUrl)
                .foods(foodDTOArrayList)
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        // Tạo món ăn
        RecipeDTO createdRecipe = recipeService.createRecipe(recipeDTO);

        if (createdRecipe == null) {
            throw new ServiceException(
                    ResCode.RECIPE_CREATION_FAILED.getMessage(),
                    ResCode.RECIPE_CREATION_FAILED.getCode()
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseFactory.createResponse(
                                createdRecipe,
                                ResCode.CREATE_RECIPE_SUCCESSFULLY.getMessage(),
                                ResCode.CREATE_RECIPE_SUCCESSFULLY.getCode()
                        )
                );
    }


    @GetMapping("get/{id}")
    public ResponseEntity<BaseResponse<RecipeDTO>> getRecipeById(@PathVariable Long id) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy recipe theo ID và người dùng hiện tại
        RecipeDTO recipeDTO = recipeService.getRecipeById(
                currentUser.getId(),
                id
        );

        // Trả về thông tin food
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                recipeDTO,
                                ResCode.GET_RECIPE_SUCCESSFULLY.getMessage(),
                                ResCode.GET_RECIPE_SUCCESSFULLY.getCode()
                        )
                );
    }

    @PostMapping("update")
    public ResponseEntity<BaseResponse<RecipeDTO>> updateRecipe(@RequestParam("image") MultipartFile image,
                                                                @RequestParam("request") String updateRecipeRequestJson) throws IOException {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        // Chuyển đổi từ JSON String thành đối tượng UpdateRecipeRequest
        ObjectMapper objectMapper = new ObjectMapper();
        UpdateRecipeRequest updateRecipeRequest = objectMapper.readValue(updateRecipeRequestJson, UpdateRecipeRequest.class);

        // Lấy recipe
        RecipeDTO recipeDTO = recipeService.getRecipeById(userInfoConfig.getId(), updateRecipeRequest.getId());

        // Lấy Foods mới
        ArrayList<FoodDTO> foodDTOArrayList = new ArrayList<>();
        for (Long id : updateRecipeRequest.getFoods()) {
            // Kiểm tra quyền sở hữu
            FoodDTO foodDTO = foodService.getFoodById(userInfoConfig.getId(), id);
            foodDTOArrayList.add(foodDTO);
        }

        // Xử lý tệp hình ảnh
        String fileName = ImagePathUtil.setImagePath(AVATAR_PATH, image.getOriginalFilename(), System.currentTimeMillis());
        String imageUrl = fileService.uploadImage(fileName, image.getBytes());

        // Cập nhật thông tin recipe
        recipeDTO.setDescription(updateRecipeRequest.getDescription());
        recipeDTO.setName(updateRecipeRequest.getName());
        recipeDTO.setFoods(foodDTOArrayList);
        recipeDTO.setImageUrl(imageUrl);
        recipeDTO.setUpdatedAt(Date.valueOf(LocalDate.now()));

        // Cập nhật recipe
        RecipeDTO updatedRecipeDTO = recipeService.updateRecipe(recipeDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                updatedRecipeDTO,
                                ResCode.UPDATE_RECIPE_SUCCESSFULLY.getMessage(),
                                ResCode.UPDATE_RECIPE_SUCCESSFULLY.getCode()
                        )
                );
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<BaseResponse<RecipeDTO>> deleteRecipe(@PathVariable Long id) {
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

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                deletedRecipe,
                                ResCode.DELETE_RECIPE_SUCCESSFULLY.getMessage(),
                                ResCode.DELETE_RECIPE_SUCCESSFULLY.getCode()
                        )
                );
    }

    // get all

    @GetMapping("/getAll")
    public ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> getAllRecipes(@RequestParam(defaultValue = "0") int from,
                                                                            @RequestParam(defaultValue = "10") int to) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy tất cả recipe trong khoảng từ `from` đến `to`
        ArrayList<RecipeDTO> recipeDTOS = recipeService.getAllRecipe(
                currentUser.getId(),
                from,
                to
        );

        // Trả về danh sách recipe
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                recipeDTOS,
                                ResCode.GET_RECIPES_SUCCESSFULLY.getMessage(),
                                ResCode.GET_RECIPES_SUCCESSFULLY.getCode()
                        )
                );
    }

    // chỉnh sửa danh sách ưa thích

    @GetMapping("/favorite/get")
    public ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> getFavoriteRecipe(@RequestParam(defaultValue = "0") int from,
                                                                                @RequestParam(defaultValue = "10") int to){
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Lấy danh sách
        ArrayList<RecipeDTO> response = recipeService.getFavoriteList(
                currentUser.getId(),
                from,
                to
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                response,
                                ResCode.GET_FAVORITE_RECIPES_SUCCESSFULLY.getMessage(),
                                ResCode.GET_FAVORITE_RECIPES_SUCCESSFULLY.getCode()
                        )
                );
    }

    @PostMapping("/favorites/add")
    public ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> addRecipeToFavorites(@RequestBody FavRequest favRequest) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // Thêm vào danh sách
        FavoriteRecipeListDTO favoriteRecipeListDTO = recipeService.addFavoriteRecipe(
                currentUser.getId(),
                favRequest.getRecipeId()
        );

        // Lấy danh sách trả về
        ArrayList<RecipeDTO> recipeDTOArrayList = recipeService.getFavoriteList(
                currentUser.getId(),
                favRequest.getFrom(),
                favRequest.getTo()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                recipeDTOArrayList,
                                ResCode.ADD_FAVORITE_RECIPE_SUCCESSFULLY.getMessage(),
                                ResCode.ADD_FAVORITE_RECIPE_SUCCESSFULLY.getCode()
                        )
                );
    }

    @PostMapping("/favorites/remove")
    public ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> removeRecipeFromFavorites(@RequestBody FavRequest favRequest) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        // xóa khỏi danh sách
        FavoriteRecipeListDTO favoriteRecipeListDTO = recipeService.removeFavoriteRecipe(
                currentUser.getId(),
                favRequest.getRecipeId()
        );

        // Lấy danh sách trả về
        ArrayList<RecipeDTO> recipeDTOArrayList = recipeService.getFavoriteList(
                currentUser.getId(),
                favRequest.getFrom(),
                favRequest.getTo()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                recipeDTOArrayList,
                                ResCode.REMOVE_FAVORITE_RECIPE_SUCCESSFULLY.getMessage(),
                                ResCode.REMOVE_FAVORITE_RECIPE_SUCCESSFULLY.getCode()
                        )
                );
    }
}
