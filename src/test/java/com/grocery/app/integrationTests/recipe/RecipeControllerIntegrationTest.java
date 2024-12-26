package com.grocery.app.integrationTests.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.request.FavRequest;
import com.grocery.app.dto.request.createRequest.CreateRecipeRequest;
import com.grocery.app.dto.request.updateRequest.UpdateRecipeRequest;
import com.grocery.app.entities.*;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.repositories.*;
import com.grocery.app.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RecipeControllerIntegrationTest extends ServicesTestSupport {
    private User otherUser;
    private Family family, otherFamily;
    private FamilyMember fm1;
    private Food food, otherFood;
    private Unit unit, otherUnit;
    private Category category, otherCategory;
    private Recipe recipe, otherRecipe;

    @Autowired
    private FamilyRepo familyRepo;
    @Autowired
    private FoodRepo foodRepo;
    @Autowired
    private UnitRepo unitRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private FamilyMemberRepo familyMemberRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RecipeRepo recipeRepo;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private FavoriteRecipeRepo favoriteRecipeRepo;

    @BeforeEach
    void setUp() {
        System.out.println("-----set up-----");
        // Một user khác
        otherUser = User.builder()
                .firstName("test1")
                .lastName("test1")
                .username("test" + Math.floor(Math.random() * 10000000))
                .password(passwordEncoder.encode("123456789"))
                .email("test@example.com")
                .role(Role.builder().id(102L).name("USER").build())
                .build();
        userRepo.save(otherUser);

        FamilyMember fm1 = FamilyMember.builder().user(otherUser).build();
        FamilyMember fm2 = FamilyMember.builder().user(user).build();

        family = Family.builder().name("test").owner(user).familyMembers(List.of(fm1)).build();
        otherFamily = Family.builder().name("test").owner(otherUser).familyMembers(List.of(fm2)).build();

        fm1.setFamily(family);
        fm2.setFamily(otherFamily);

        family = familyRepo.save(family);
        otherFamily = familyRepo.save(otherFamily);

        // tạo category
        category = Category.builder()
                .name("Food Category")
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();

        category = categoryRepo.save(category);

        otherCategory = Category.builder()
                .name("Food Category 1")
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();

        otherCategory = categoryRepo.save(otherCategory);

        // tạo unit
        unit = Unit.builder()
                .name("KG")
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();

        unit = unitRepo.save(unit);

        otherUnit = Unit.builder()
                .name("LB")
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();

        otherUnit = unitRepo.save(otherUnit);

        // Tạo food
        food = Food.builder()
                .user(user)
                .name("Food")
                .description("Food but not food")
                .category(category)
                .measureUnit(unit)
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        foodRepo.save(food);

        otherFood = Food.builder()
                .user(user)
                .name("Food 1")
                .description("Food 1 not Food")
                .category(category)
                .measureUnit(unit)
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        foodRepo.save(otherFood);

        recipe = Recipe.builder()
                .user(user)
                .meals(new ArrayList<>())
                .name("recipeEeEeE")
                .description("dedededeede")
                .imageUrl("url")
                .foods(List.of(food))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        recipe = recipeRepo.save(recipe);

        otherRecipe = Recipe.builder()
                .user(user)
                .meals(new ArrayList<>())
                .name("recipeEeEeE")
                .description("dedededeede")
                .imageUrl("url")
                .foods(List.of(food, otherFood))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        otherRecipe = recipeRepo.save(otherRecipe);
    }

    @Test
    void testCreateRecipe() throws Exception {
        System.out.println("-----test create recipe-----");

        // Tạo request JSON
        CreateRecipeRequest createRecipeRequest = CreateRecipeRequest.builder()
                .name("Recipe")
                .description("Recipeeeee")
                .foods(new ArrayList<>(List.of(food.getId())))
                .build();

        // Chuyển JSON request thành chuỗi
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(createRecipeRequest);

        // Tạo đối tượng MultipartFile giả
        File imageFile = new File("src/test/java/com/grocery/app/integrationTests/recipe/WIN_20240705_21_34_08_Pro.jpg"); // Cập nhật đường dẫn đúng đến tệp hình ảnh
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath()); // Đọc dữ liệu tệp thành mảng byte

        // Sử dụng ByteArrayInputStream để tạo MockMultipartFile
        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                imageFile.getName(),
                "image/jpeg",
                new ByteArrayInputStream(imageBytes) // Chuyển mảng byte thành InputStream
        );

        // Sử dụng MultiValueMap để gửi multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("request", jsonRequest); // Dữ liệu JSON dạng chuỗi
        body.add("image", mockImage.getResource()); // Sử dụng resource của MockMultipartFile

        // Định nghĩa headers
        HttpHeaders headers = getHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Tạo HttpEntity cho request
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Gửi request
        ResponseEntity<BaseResponse<RecipeDTO>> response = testRestTemplate.exchange(
                "/api/recipe/create",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<BaseResponse<RecipeDTO>>() {}
        );

        // Kiểm tra kết quả
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        RecipeDTO recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.getName(), is(createRecipeRequest.getName()));
        assertThat(recipeDTO.getDescription(), is(createRecipeRequest.getDescription()));
        assertThat(recipeDTO.getFoods().get(0).getId(), is(createRecipeRequest.getFoods().get(0)));
    }

    @Test
    void testGetRecipe() throws Exception{
        System.out.println("-----test get recipe-----");

        ResponseEntity<BaseResponse<RecipeDTO>> response = testRestTemplate.exchange(
                "/api/recipe/get/" + recipe.getId(),
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<RecipeDTO>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        RecipeDTO recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.getName(), is(recipe.getName()));
        assertThat(recipeDTO.getDescription(), is(recipe.getDescription()));
        assertThat(recipeDTO.getImageUrl(), is(recipe.getImageUrl()));
        assertThat(recipeDTO.getFoods().get(0).getId(), is(recipe.getFoods().get(0).getId()));

    }

    @Test
    void testGetRecipes() throws Exception{
        System.out.println("-----test get recipes-----");

        ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> response = testRestTemplate.exchange(
                "/api/recipe/getAll",
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<RecipeDTO>>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<RecipeDTO> recipeDTOS = Objects.requireNonNull(response.getBody()).getData();

        RecipeDTO recipeDTO = recipeDTOS.get(0);

        assertThat(recipeDTO.getName(), is(recipe.getName()));
        assertThat(recipeDTO.getDescription(), is(recipe.getDescription()));
        assertThat(recipeDTO.getImageUrl(), is(recipe.getImageUrl()));
        assertThat(recipeDTO.getFoods().get(0).getId(), is(recipe.getFoods().get(0).getId()));

    }

    @Test
    void testUpdateRecipe() throws Exception {
        System.out.println("-----test update recipe-----");

        // Tạo request JSON
        UpdateRecipeRequest updateRecipeRequest = UpdateRecipeRequest.builder()
                .id(recipe.getId())
                .name("Recipe++")
                .description("Recipeeeee++")
                .foods(new ArrayList<>(List.of(food.getId(), otherFood.getId())))
                .build();

        // Chuyển JSON request thành chuỗi
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(updateRecipeRequest);

        // Tạo đối tượng MultipartFile giả
        MockMultipartFile mockImage = new MockMultipartFile(
                "image",
                "updated-recipe-image.jpg",
                "image/jpeg",
                "Updated Image Content".getBytes()
        );

        // Sử dụng MultiValueMap để gửi multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("request", jsonRequest); // Dữ liệu JSON dạng chuỗi
        body.add("image", mockImage.getResource()); // Sử dụng resource của MockMultipartFile

        // Định nghĩa headers
        HttpHeaders headers = getHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Tạo HttpEntity cho request
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Gửi request
        ResponseEntity<BaseResponse<RecipeDTO>> response = testRestTemplate.exchange(
                "/api/recipe/update",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<BaseResponse<RecipeDTO>>() {}
        );

        // Kiểm tra kết quả
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        RecipeDTO recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.getName(), is(updateRecipeRequest.getName()));
        assertThat(recipeDTO.getDescription(), is(updateRecipeRequest.getDescription()));
        assertThat(recipeDTO.getFoods().get(0).getId(), is(updateRecipeRequest.getFoods().get(0)));
        assertThat(recipeDTO.getFoods().get(1).getId(), is(updateRecipeRequest.getFoods().get(1)));
    }



    @Test
    void testDeleteRecipes() throws Exception{
        System.out.println("-----test delete recipe-----");

        ResponseEntity<BaseResponse<RecipeDTO>> response = testRestTemplate.exchange(
                "/api/recipe/delete/" + recipe.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<RecipeDTO>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        RecipeDTO recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.getName(), is(recipe.getName()));
        assertThat(recipeDTO.getDescription(), is(recipe.getDescription()));
        assertThat(recipeDTO.getImageUrl(), is(recipe.getImageUrl()));
        assertThat(recipeDTO.getFoods().get(0).getId(), is(recipe.getFoods().get(0).getId()));

        assertThat(recipeDTO.getStatus(), is(StatusConfig.DELETED.getStatus()));

    }

    @Test
    void testGetFavRecipeList() throws Exception{
        System.out.println("-----test get favorite recipe list-----");
        ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> response = testRestTemplate.exchange(
                "/api/recipe/favorite/get",
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<RecipeDTO>>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<RecipeDTO> recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.size(), is(0));
    }

    @Test
    void testAddWithoutExistingFavList() throws Exception{
        System.out.println("-----test add recipe to favorite recipe list-----");
        FavRequest favRequest = FavRequest.builder()
                .recipeId(recipe.getId())
                .build();

        ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> response = testRestTemplate.exchange(
                "/api/recipe/favorites/add",
                HttpMethod.POST,
                new HttpEntity<>(favRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<RecipeDTO>>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<RecipeDTO> recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.size(), is(1));
        assertThat(recipeDTO.get(0).getId(), is(recipe.getId()));
    }

    @Test
    void testAddWithExistingFavList() throws Exception{
        System.out.println("-----test add recipe to favorite recipe list-----");
        FavoriteRecipeList favoriteRecipeList = recipeService.createFavoriteList(user.getId());
        favoriteRecipeList.setFavoriteList(new ArrayList<>(List.of(otherRecipe)));
        otherRecipe.setFavRecipe(favoriteRecipeList);
        favoriteRecipeRepo.save(favoriteRecipeList);
        recipeRepo.save(otherRecipe);

        FavRequest favRequest = FavRequest.builder()
                .recipeId(recipe.getId())
                .build();

        ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> response = testRestTemplate.exchange(
                "/api/recipe/favorites/add",
                HttpMethod.POST,
                new HttpEntity<>(favRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<RecipeDTO>>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<RecipeDTO> recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.size(), is(2));
        assertThat(recipeDTO.get(1).getId(), is(recipe.getId()));
        assertThat(recipeDTO.get(0).getId(), is(otherRecipe.getId()));
    }

    @Test
    void testRemove() throws Exception{
        System.out.println("-----test remove recipe from favorite recipe list-----");
        FavoriteRecipeList favoriteRecipeList = recipeService.createFavoriteList(user.getId());
        favoriteRecipeList.setFavoriteList(new ArrayList<>(List.of(recipe, otherRecipe)));
        recipe.setFavRecipe(favoriteRecipeList);
        otherRecipe.setFavRecipe(favoriteRecipeList);
        favoriteRecipeRepo.save(favoriteRecipeList);
        recipeRepo.save(recipe);
        recipeRepo.save(otherRecipe);

        FavRequest favRequest = FavRequest.builder()
                .recipeId(recipe.getId())
                .build();

        ResponseEntity<BaseResponse<ArrayList<RecipeDTO>>> response = testRestTemplate.exchange(
                "/api/recipe/favorites/remove",
                HttpMethod.POST,
                new HttpEntity<>(favRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<RecipeDTO>>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<RecipeDTO> recipeDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(recipeDTO.size(), is(1));
        assertThat(recipeDTO.get(0).getId(), is(otherRecipe.getId()));
    }
}
