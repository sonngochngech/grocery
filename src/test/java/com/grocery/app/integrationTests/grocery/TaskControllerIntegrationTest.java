package com.grocery.app.integrationTests.grocery;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.TaskDTO;
import com.grocery.app.dto.request.createRequest.CreateTaskRequest;
import com.grocery.app.dto.request.updateRequest.UpdateMealRequest;
import com.grocery.app.dto.request.updateRequest.UpdateTaskRequest;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskControllerIntegrationTest extends ServicesTestSupport {

    private User otherUser;
    private ShoppingList shoppingList, otherShoppingList;
    private Family family, otherFamily;
    private FamilyMember fm1;
    private Food food, otherFood;
    private Unit unit;
    private Category category;
    private Task task, otherTask;

    @Autowired
    private ShoppingListRepo shoppingListRepo;
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
    private TaskRepo taskRepo;

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

        // tạo unit
        unit = Unit.builder()
                .name("KG")
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .build();

        unit = unitRepo.save(unit);

        // Tạo food
        food = Food.builder()
                .user(user)
                .name("Food")
                .description("Food but not food")
                .category(category)
                .measureUnit(unit)
                .build();

        foodRepo.save(food);

        otherFood = Food.builder()
                .user(user)
                .name("Food 1")
                .description("Food 1 not Food")
                .category(category)
                .measureUnit(unit)
                .build();

        foodRepo.save(otherFood);

        // user là chủ shopping list
        shoppingList = createShoppingList(user, family);
        otherShoppingList = createShoppingList(otherUser, otherFamily);

        task = Task.builder()
                .shoppingList(shoppingList)
                .assignee(otherUser)
                .food(food)
                .quantity(1.2f)
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();
        task = taskRepo.save(task);

        otherTask = Task.builder()
                .shoppingList(otherShoppingList)
                .assignee(user)
                .food(food)
                .quantity(1.2f)
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();
        otherTask = taskRepo.save(otherTask);
    }

    // DONE
    @Test
    void testCreateTask() throws Exception{
        System.out.println("-----test create task-----");

        System.out.println("request");
        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
                .assignee(otherUser.getId())
                .shoppingListId(shoppingList.getId())
                .foodId(food.getId())
                .quantity(1.2f)
                .build();
        System.out.println(createTaskRequest);

        ResponseEntity<BaseResponse<TaskDTO>> response = testRestTemplate.exchange(
                "/api/task/add",
                HttpMethod.POST,
                new HttpEntity<>(createTaskRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<TaskDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        TaskDTO taskDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(taskDTO.getAssignee().getId(), is(otherUser.getId()));
        assertThat(taskDTO.getShoppingListId(), is(shoppingList.getId()));
        assertThat(taskDTO.getFoodDTO().getId(), is(food.getId()));
        assertThat(taskDTO.getQuantity(), is(1.2f));
    }

    // Done
    @Test
    void getTaskWithAssignerView() throws Exception{
        // user là chủ shopping list và là người giao task
        ResponseEntity<BaseResponse<TaskDTO>> response = testRestTemplate.exchange(
                "/api/task/get/" + task.getId(),
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<TaskDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        TaskDTO taskDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(taskDTO.getAssignee().getId(), is(otherUser.getId()));
        assertThat(taskDTO.getShoppingListId(), is(shoppingList.getId()));
        assertThat(taskDTO.getFoodDTO().getId(), is(food.getId()));
        assertThat(taskDTO.getQuantity(), is(1.2f));
    }

    // Done
    @Test
    void getTaskWithAssigneeView() throws Exception{
        // user được giao task
        ResponseEntity<BaseResponse<TaskDTO>> response = testRestTemplate.exchange(
                "/api/task/get/" + otherTask.getId(),
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<TaskDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        TaskDTO taskDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(taskDTO.getAssignee().getId(), is(user.getId()));
        assertThat(taskDTO.getShoppingListId(), is(otherShoppingList.getId()));
        assertThat(taskDTO.getFoodDTO().getId(), is(food.getId()));
        assertThat(taskDTO.getQuantity(), is(1.2f));
    }

    // Done
    @Test
    void getAllTasks() throws Exception{
        // user là người được giao task
        ResponseEntity<BaseResponse<ArrayList<TaskDTO>>> response = testRestTemplate.exchange(
                "/api/task/getAll",
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<TaskDTO>>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<TaskDTO> taskDTOs = Objects.requireNonNull(response.getBody()).getData();

        assertThat(taskDTOs.size(), is(1));

        TaskDTO taskDTO = taskDTOs.get(0);

        assertThat(taskDTO.getAssignee().getId(), is(user.getId()));
        assertThat(taskDTO.getShoppingListId(), is(otherShoppingList.getId()));
        assertThat(taskDTO.getFoodDTO().getId(), is(food.getId()));
        assertThat(taskDTO.getQuantity(), is(1.2f));
    }

    // Done
    @Test
    void updateTask() throws Exception{
        // user là chủ shopping list
        UpdateTaskRequest updateTaskRequest = UpdateTaskRequest.builder()
                .taskId(task.getId())
                .foodId(otherFood.getId())
                .userId(otherUser.getId())
                .quantity(1.3f)
                .shoppingListId(shoppingList.getId())
                .build();

        ResponseEntity<BaseResponse<TaskDTO>> response = testRestTemplate.exchange(
                "/api/task/update",
                HttpMethod.POST,
                new HttpEntity<>(updateTaskRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<TaskDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        TaskDTO taskDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(taskDTO.getAssignee().getId(), is(updateTaskRequest.getUserId()));
        assertThat(taskDTO.getShoppingListId(), is(updateTaskRequest.getShoppingListId()));
        assertThat(taskDTO.getFoodDTO().getId(), is(updateTaskRequest.getFoodId()));
        assertThat(taskDTO.getQuantity(), is(updateTaskRequest.getQuantity()));
    }

    // Done
    @Test
    void deleteTask() throws Exception{
        // user là chủ shopping list
        ResponseEntity<BaseResponse<TaskDTO>> response = testRestTemplate.exchange(
                "/api/task/delete/" + task.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<TaskDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        TaskDTO taskDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(taskDTO.getAssignee().getId(), is(otherUser.getId()));
        assertThat(taskDTO.getShoppingListId(), is(shoppingList.getId()));
        assertThat(taskDTO.getFoodDTO().getId(), is(food.getId()));
        assertThat(taskDTO.getQuantity(), is(1.2f));
        assertThat(taskDTO.getStatus(), is(StatusConfig.DELETED.getStatus()));
    }

    ShoppingList createShoppingList(User owner, Family family){
        ShoppingList sp = ShoppingList.builder()
                .owner(owner)
                .family(family)
                .name("sp")
                .description("sp detail")
                .taskArrayList(new ArrayList<>())
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        return shoppingListRepo.save(sp);
    }
}
