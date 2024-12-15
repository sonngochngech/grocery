package com.grocery.app.integrationTests.Food;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.constant.TermConfig;
import com.grocery.app.dto.FoodDTO;
import com.grocery.app.dto.TaskDTO;
import com.grocery.app.dto.request.createRequest.CreateFoodRequest;
import com.grocery.app.dto.request.updateRequest.UpdateFoodRequest;
import com.grocery.app.entities.*;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class FoodControllerIntegrationTest extends ServicesTestSupport {

    private User otherUser;
    private Family family, otherFamily;
    private FamilyMember fm1;
    private Food food, otherFood;
    private Unit unit, otherUnit;
    private Category category, otherCategory;

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
    }

    @Test
    void testCreateFood() throws Exception{
        System.out.println("-----test create food-----");
        CreateFoodRequest createFoodRequest = CreateFoodRequest.builder()
                .name("food test")
                .description("food for me not for test")
                .categoryId(category.getId())
                .unitId(unit.getId())
                .build();

        ResponseEntity<BaseResponse<FoodDTO>> response = testRestTemplate.exchange(
                "/api/food/add",
                HttpMethod.POST,
                new HttpEntity<>(createFoodRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<FoodDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        FoodDTO foodDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(foodDTO.getUser().getId(), is(user.getId()));
        assertThat(foodDTO.getName(), is(createFoodRequest.getName()));
        assertThat(foodDTO.getDescription(), is(createFoodRequest.getDescription()));
        assertThat(foodDTO.getCategoryDTO().getId(), is(createFoodRequest.getCategoryId()));
        assertThat(foodDTO.getMeasureUnitDTO().getId(), is(createFoodRequest.getUnitId()));
    }

    @Test
    void testGetFood() throws Exception{
        System.out.println("-----test get food-----");
        ResponseEntity<BaseResponse<FoodDTO>> response = testRestTemplate.exchange(
                "/api/food/get/" + food.getId(),
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<FoodDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        FoodDTO foodDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(foodDTO.getUser().getId(), is(food.getUser().getId()));
        assertThat(foodDTO.getName(), is(food.getName()));
        assertThat(foodDTO.getDescription(), is(food.getDescription()));
        assertThat(foodDTO.getCategoryDTO().getId(), is(food.getCategory().getId()));
        assertThat(foodDTO.getMeasureUnitDTO().getId(), is(food.getMeasureUnit().getId()));
    }

    @Test
    void testGetFoods() throws Exception{
        System.out.println("-----test get foods-----");
        ResponseEntity<BaseResponse<ArrayList<FoodDTO>>> response = testRestTemplate.exchange(
                "/api/food/getAll",
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<FoodDTO>>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<FoodDTO> foodDTOs = Objects.requireNonNull(response.getBody()).getData();

        assertThat(foodDTOs.size(), is(2));

        FoodDTO foodDTO = foodDTOs.get(0);

        assertThat(foodDTO.getUser().getId(), is(food.getUser().getId()));
        assertThat(foodDTO.getName(), is(food.getName()));
        assertThat(foodDTO.getDescription(), is(food.getDescription()));
        assertThat(foodDTO.getCategoryDTO().getId(), is(food.getCategory().getId()));
        assertThat(foodDTO.getMeasureUnitDTO().getId(), is(food.getMeasureUnit().getId()));

        FoodDTO otherFoodDTO = foodDTOs.get(1);

        assertThat(otherFoodDTO.getUser().getId(), is(otherFood.getUser().getId()));
        assertThat(otherFoodDTO.getName(), is(otherFood.getName()));
        assertThat(otherFoodDTO.getDescription(), is(otherFood.getDescription()));
        assertThat(otherFoodDTO.getCategoryDTO().getId(), is(otherFood.getCategory().getId()));
        assertThat(otherFoodDTO.getMeasureUnitDTO().getId(), is(otherFood.getMeasureUnit().getId()));
    }

    @Test
    void testUpdate() throws Exception{
        System.out.println("-----test update food-----");
        UpdateFoodRequest updateFoodRequest = UpdateFoodRequest.builder()
                .id(food.getId())
                .name("food test 1")
                .description("food 1 for me not for test")
                .categoryId(otherCategory.getId())
                .unitId(otherUnit.getId())
                .build();

        ResponseEntity<BaseResponse<FoodDTO>> response = testRestTemplate.exchange(
                "/api/food/update",
                HttpMethod.POST,
                new HttpEntity<>(updateFoodRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<FoodDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        FoodDTO foodDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(foodDTO.getUser().getId(), is(user.getId()));
        assertThat(foodDTO.getName(), is(updateFoodRequest.getName()));
        assertThat(foodDTO.getDescription(), is(updateFoodRequest.getDescription()));
        assertThat(foodDTO.getCategoryDTO().getId(), is(updateFoodRequest.getCategoryId()));
        assertThat(foodDTO.getMeasureUnitDTO().getId(), is(updateFoodRequest.getUnitId()));
    }

    @Test
    void testDeleteFood() throws Exception{
        System.out.println("-----test delete food-----");
        ResponseEntity<BaseResponse<FoodDTO>> response = testRestTemplate.exchange(
                "/api/food/delete/" + food.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<FoodDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        FoodDTO foodDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(foodDTO.getUser().getId(), is(food.getUser().getId()));
        assertThat(foodDTO.getName(), is(food.getName()));
        assertThat(foodDTO.getDescription(), is(food.getDescription()));
        assertThat(foodDTO.getCategoryDTO().getId(), is(food.getCategory().getId()));
        assertThat(foodDTO.getMeasureUnitDTO().getId(), is(food.getMeasureUnit().getId()));
        assertThat(foodDTO.getStatus(), is(StatusConfig.DELETED.getStatus()));
    }
}
