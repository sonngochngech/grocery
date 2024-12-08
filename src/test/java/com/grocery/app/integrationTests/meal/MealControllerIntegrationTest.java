package com.grocery.app.integrationTests.meal;

import com.grocery.app.config.constant.TermConfig;
import com.grocery.app.dto.MealDTO;
import com.grocery.app.dto.RecipeDTO;
import com.grocery.app.dto.request.createRequest.CreateMealRequest;
import com.grocery.app.entities.*;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.repositories.FamilyRepo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.CREATED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class MealControllerIntegrationTest extends ServicesTestSupport {
    Recipe r1, r2, r3, r4;
    Meal m1, m2;

    User tmp;

    private Family tmpFamily;

    private Family family;
    @Autowired
    RecipeRepo recipeRepo;
    @Autowired
    private FamilyRepo familyRepo;
    @BeforeEach
    public void setUp() {
        // Một user khác
        tmp = User.builder()
                .firstName("test1")
                .lastName("test1")
                .username("test" + Math.floor(Math.random() * 10000000))
                .password(passwordEncoder.encode("123456789"))
                .email("test@example.com")
                .role(Role.builder().id(102L).name("USER").build())
                .build();
        userRepo.save(tmp);

        // Tạo 2 gia đình
        FamilyMember fm1 = FamilyMember.builder().user(tmp).build();
        FamilyMember fm2 = FamilyMember.builder().user(user).build();

        tmpFamily = Family.builder().name("test").owner(user).familyMembers(List.of(fm1)).build();
        family = Family.builder().name("test").owner(tmp).familyMembers(List.of(fm2)).build();

        fm1.setFamily(tmpFamily);
        fm2.setFamily(family);

        tmpFamily =familyRepo.save(tmpFamily);
        family =familyRepo.save(family);

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
                .user(tmp)
                .build();

        // Lưu các recipe vào database
        recipeRepo.save(r1);
        recipeRepo.save(r2);
        recipeRepo.save(r3);
        recipeRepo.save(r4);
    }

    @Order(1)
    @Test
    void testCreateMeal() throws Exception {
        // Chuẩn bị danh sách recipe IDs
        ArrayList<Long> recipes = new ArrayList<>();
        recipes.add(r1.getId());
        recipes.add(r2.getId());

        System.out.println("r1");
        System.out.println(r1.getId());
        System.out.println("r2");
        System.out.println(r2.getId());

        // Tạo đối tượng CreateMealRequest
        CreateMealRequest createMealRequest = CreateMealRequest.builder()
                .familyId(tmpFamily.getId())    // ID của Family
                .date("2024-11-24")         // Ngày (YYYY-MM-DD)
                .term(TermConfig.BREAKFAST.getTerm()) // Loại bữa ăn (BREAKFAST)
                .name("m1")                 // Tên meal
                .userId(user.getId())       // ID của User
                .recipeList(recipes)        // Danh sách recipe IDs
                .build();

        System.out.println("create meal request");
        System.out.println(createMealRequest);

        int a = 1;

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
                new ParameterizedTypeReference<BaseResponse<MealDTO>>() {}
        );

        // Kiểm tra trạng thái HTTP trả về
        System.out.println("response");
        System.out.println(response);

        // Kiểm tra nội dung của MealDTO trả về
        MealDTO responseBody = Objects.requireNonNull(response.getBody()).getData();
        assertThat(responseBody.getName(), is("m1"));
        assertThat(responseBody.getFamilyDetailDTO().getBasicInfo().getId(), is(family.getId()));

        // Kiểm tra danh sách recipe IDs
        List<Long> responseRecipeIds = responseBody.getRecipeDTOS().stream()
                .map(RecipeDTO::getId)
                .collect(Collectors.toList());
        assertThat(responseRecipeIds, is(recipes));
    }


}
