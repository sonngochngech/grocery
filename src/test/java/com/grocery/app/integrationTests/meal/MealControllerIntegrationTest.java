package com.grocery.app.integrationTests.meal;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.constant.TermConfig;
import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.request.RecommendedMealDTO;
import com.grocery.app.dto.request.createRequest.CreateMealRequest;
import com.grocery.app.dto.request.updateRequest.UpdateMealRequest;
import com.grocery.app.entities.*;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.MealRepo;
import com.grocery.app.repositories.RecipeRepo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class MealControllerIntegrationTest extends ServicesTestSupport {
    Recipe r1, r2, r3, r4;

    User otherUser;

    private Family family;

    private Family otherFamily;

    private Meal meal;
    @Autowired
    RecipeRepo recipeRepo;
    @Autowired
    private FamilyRepo familyRepo;
    @Autowired
    private MealRepo mealRepo;


    @BeforeEach
    public void setUp() {
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

        // Tạo 2 gia đình
        FamilyMember fm1 = FamilyMember.builder().user(otherUser).build();
        FamilyMember fm2 = FamilyMember.builder().user(user).build();

        meal = Meal.builder()
                .user(user)
                .name("meal")
                .date("today")
                .recipes(new ArrayList<>())
                .term(TermConfig.BREAKFAST.getTerm())
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        family = Family.builder().name("test").owner(user).familyMembers(List.of(fm1)).build();
        otherFamily = Family.builder().name("test").owner(otherUser).familyMembers(List.of(fm2)).build();

        fm1.setFamily(family);
        fm2.setFamily(otherFamily);

        family = familyRepo.save(family);
        otherFamily = familyRepo.save(otherFamily);

        Family family1 = familyRepo.findById(family.getId()).orElse(null);

        meal.setFamily(family1);

        // Tạo các recipe và gán user cho từng recipe
        r1 = Recipe.builder()
                .name("r1")
                .user(user) // Gán user vào recipe
                .build();
        r2 = Recipe.builder()
                .name("r2")
                .user(user)
                .build();
        r3 = Recipe.builder()
                .name("r3")
                .user(user)
                .build();
        r4 = Recipe.builder()
                .name("r4")
                .user(otherUser)
                .build();

        // Lưu các recipe vào database
        recipeRepo.save(r1);
        recipeRepo.save(r2);
        recipeRepo.save(r3);
        recipeRepo.save(r4);

        meal.getRecipes().add(r1);
        System.out.println("recipe length");
        System.out.println(meal.getRecipes().size());

        mealRepo.save(meal);
    }

    //DONE
    @Disabled
    @Order(1)
    @Test
    void testCreateMeal() throws Exception {
        System.out.println("-----test create meal-----");
        // Chuẩn bị danh sách recipe IDs
        ArrayList<Long> recipes = new ArrayList<>();
        recipes.add(r1.getId());
        recipes.add(r2.getId());

        // Tạo đối tượng CreateMealRequest
        CreateMealRequest createMealRequest = CreateMealRequest.builder()
                .familyId(family.getId())    // ID của Family
                .date("2024-11-24")         // Ngày (YYYY-MM-DD)
                .term(TermConfig.BREAKFAST.getTerm()) // Loại bữa ăn (BREAKFAST)
                .name("m1")                 // Tên meal
                .userId(user.getId())       // ID của User
                .recipeList(recipes)        // Danh sách recipe IDs
                .build();

        System.out.println("create meal request");
        System.out.println(createMealRequest);

        // Tạo request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(
                objectMapper.writeValueAsString(createMealRequest), getHeader());

        System.out.println("request entity");
        System.out.println(requestEntity);
        // Gửi yêu cầu HTTP POST
        ResponseEntity<BaseResponse<MealDTO>> response = testRestTemplate.exchange(
                "/api/meal/add",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<BaseResponse<MealDTO>>() {
                }
        );

        // Kiểm tra trạng thái HTTP trả về
        System.out.println("response");
        System.out.println(response);

        // Kiểm tra nội dung của MealDTO trả về
        MealDTO responseBody = Objects.requireNonNull(response.getBody()).getData();
        assertThat(responseBody.getName(), is(createMealRequest.getName()));
        assertThat(responseBody.getFamilyDetailDTO().getId(), is(createMealRequest.getFamilyId()));

        // Kiểm tra danh sách recipe IDs
        List<Long> responseRecipeIds = responseBody.getRecipeDTOS().stream()
                .map(RecipeDTO::getId)
                .collect(Collectors.toList());
        assertThat(responseRecipeIds, is(createMealRequest.getRecipeList()));
    }

    //DONE
    @Disabled
    @Order(2)
    @Test
    void testGetMeal() throws Exception {
        System.out.println("-----test get meal-----");
        // Gửi yêu cầu HTTP GET để lấy thông tin meal
        ResponseEntity<BaseResponse<MealDTO>> response = testRestTemplate.exchange(
                "/api/meal/get/" + meal.getMealId(),
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<MealDTO>>() {}
        );

        // Kiểm tra trạng thái HTTP trả về
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Kiểm tra nội dung của MealDTO trả về
        MealDTO responseBody = Objects.requireNonNull(response.getBody()).getData();
        assertThat(responseBody.getName(), is("meal"));
        assertThat(responseBody.getFamilyDetailDTO().getId(), is(family.getId()));
        assertThat(responseBody.getTerm(), is(TermConfig.BREAKFAST.getTerm()));
        assertThat(responseBody.getDate(), is("today"));

        // Kiểm tra danh sách recipe IDs
        List<Long> responseRecipeIds = responseBody.getRecipeDTOS().stream()
                .map(RecipeDTO::getId)
                .collect(Collectors.toList());
        assertThat(responseRecipeIds, is(List.of(r1.getId(), r2.getId())));
    }

    //Done
    @Disabled
    @Order(3)
    @Test
    void getAllMeals() throws Exception {
        System.out.println("-----test get all meals-----");
        String url = String.format("/api/meal/getAll?from=%d&to=%d", 0, 10); // Thay 0 và 10 bằng giá trị cụ thể bạn muốn.

        ResponseEntity<BaseResponse<ArrayList<MealDTO>>> response = testRestTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<MealDTO>>>() {}
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Kiểm tra nội dung của MealDTO trả về
        ArrayList<MealDTO> responseBody = Objects.requireNonNull(response.getBody()).getData();
        assertThat(responseBody.size(), is(1));

        MealDTO mealDTO = responseBody.get(0);

        assertThat(mealDTO.getName(), is("meal"));
        assertThat(mealDTO.getFamilyDetailDTO().getId(), is(family.getId()));
        assertThat(mealDTO.getTerm(), is(TermConfig.BREAKFAST.getTerm()));
        assertThat(mealDTO.getDate(), is("today"));

        // Kiểm tra danh sách recipe IDs
        List<Long> responseRecipeIds = mealDTO.getRecipeDTOS().stream()
                .map(RecipeDTO::getId)
                .collect(Collectors.toList());
        assertThat(responseRecipeIds, is(List.of(r1.getId(), r2.getId())));
    }

    // Done
    @Disabled
    @Order(4)
    @Test
    void updateMeal() throws Exception{
        System.out.println("-----test update-----");
        UpdateMealRequest updateMealRequest = UpdateMealRequest.builder()
                .mealId(1L)
                .familyId(1L)
                .name("new name")
                .date("new date")
                .term("Lunch")
                .recipeList(new ArrayList<>(List.of(1L, 2L)))
                .build();

        // Gửi yêu cầu HTTP GET để lấy thông tin meal
        ResponseEntity<BaseResponse<MealDTO>> response = testRestTemplate.exchange(
                "/api/meal/update",
                HttpMethod.POST,
                new HttpEntity<>(
                        objectMapper.writeValueAsString(updateMealRequest), getHeader()),
                new ParameterizedTypeReference<BaseResponse<MealDTO>>() {}
        );

        // Kiểm tra trạng thái HTTP trả về
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Kiểm tra nội dung của MealDTO trả về
        MealDTO responseBody = Objects.requireNonNull(response.getBody()).getData();
        assertThat(responseBody.getName(), is(updateMealRequest.getName()));
        assertThat(responseBody.getFamilyDetailDTO().getId(), is(updateMealRequest.getFamilyId()));
        assertThat(responseBody.getTerm(), is(updateMealRequest.getTerm()));
        assertThat(responseBody.getDate(), is(updateMealRequest.getDate()));

        // Kiểm tra danh sách recipe IDs
        List<Long> responseRecipeIds = responseBody.getRecipeDTOS().stream()
                .map(RecipeDTO::getId)
                .collect(Collectors.toList());
        assertThat(responseRecipeIds, is(updateMealRequest.getRecipeList()));
    }

    //DONE
    @Disabled
    @Order(5)
    @Test
    void testDelete() throws Exception{
        System.out.println("-----test delete meal-----");
        // Gửi yêu cầu HTTP GET để lấy thông tin meal
        ResponseEntity<BaseResponse<MealDTO>> response = testRestTemplate.exchange(
                "/api/meal/delete/" + meal.getMealId(),
                HttpMethod.DELETE,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<MealDTO>>() {}
        );

        // Kiểm tra trạng thái HTTP trả về
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Kiểm tra nội dung của MealDTO trả về
        MealDTO responseBody = Objects.requireNonNull(response.getBody()).getData();
        assertThat(responseBody.getName(), is(meal.getName()));
        assertThat(responseBody.getFamilyDetailDTO().getId(), is(meal.getFamily().getId()));
        assertThat(responseBody.getTerm(), is(meal.getTerm()));
        assertThat(responseBody.getDate(), is(meal.getDate()));
        assertThat(responseBody.getStatus(), is(StatusConfig.DELETED.getStatus()));

        // Kiểm tra danh sách recipe IDs
        List<Long> responseRecipeIds = responseBody.getRecipeDTOS().stream()
                .map(RecipeDTO::getId)
                .collect(Collectors.toList());
        assertThat(responseRecipeIds, is(List.of(r1.getId(), r2.getId())));
    }

    // Done
    @Order(6)
    @Test
    void testRecommend() throws Exception{
        System.out.println("-----test recommend-----");
        // Gửi yêu cầu HTTP GET để lấy thông tin meal
        ResponseEntity<BaseResponse<RecommendedMealDTO>> response = testRestTemplate.exchange(
                "/api/meal/recommend/" + TermConfig.BREAKFAST.getTerm(),
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<RecommendedMealDTO>>() {}
        );

        // Kiểm tra trạng thái HTTP trả về
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Kiểm tra nội dung của MealDTO trả về
        RecommendedMealDTO responseBody = Objects.requireNonNull(response.getBody()).getData();
        assertThat(responseBody.getTerm(), is(meal.getTerm()));

        // Kiểm tra danh sách recipe IDs
        List<Long> responseRecipeIds = responseBody.getRecommendedRecipes().stream()
                .map(RecipeDTO::getId)
                .collect(Collectors.toList());
        System.out.println(responseRecipeIds);
    }
}
