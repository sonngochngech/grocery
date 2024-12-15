package com.grocery.app.integrationTests.grocery;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.ShoppingListDTO;
import com.grocery.app.dto.TaskDTO;
import com.grocery.app.dto.request.createRequest.CreateShoppingListRequest;
import com.grocery.app.dto.request.updateRequest.UpdateShoppingListRequest;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingListControllerIntegrationTest extends ServicesTestSupport {
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

        shoppingList.getTaskArrayList().add(task);

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

        otherShoppingList.getTaskArrayList().add(otherTask);
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

    // Done
    @Test
    void testCreateSP() throws Exception{
        System.out.println("-----test create sp-----");

        //tạo request
        CreateShoppingListRequest createShoppingListRequest = CreateShoppingListRequest.builder()
                .familyId(family.getId())
                .name("sp1")
                .description("sp2")
                .build();

        ResponseEntity<BaseResponse<ShoppingListDTO>> response = testRestTemplate.exchange(
                "/api/shopping/add",
                HttpMethod.POST,
                new HttpEntity<>(createShoppingListRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<ShoppingListDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        ShoppingListDTO shoppingListDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(shoppingListDTO.getOwnerDTO().getId(), is(user.getId()));
        assertThat(shoppingListDTO.getFamilyDTO().getId(), is(createShoppingListRequest.getFamilyId()));
        assertThat(shoppingListDTO.getName(), is(createShoppingListRequest.getName()));
        assertThat(shoppingListDTO.getDescription(), is(createShoppingListRequest.getDescription()));
    }

    //Done
    @Test
    void testGetShoppingList() throws Exception{
        System.out.println("-----test get sp-----");

        ResponseEntity<BaseResponse<ShoppingListDTO>> response = testRestTemplate.exchange(
                "/api/shopping/get/" + shoppingList.getId(),
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ShoppingListDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ShoppingListDTO shoppingListDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(shoppingListDTO.getOwnerDTO().getId(), is(user.getId()));
        assertThat(shoppingListDTO.getId(), is(shoppingList.getId()));
        assertThat(shoppingListDTO.getFamilyDTO().getId(), is(shoppingList.getFamily().getId()));
        assertThat(shoppingListDTO.getName(), is(shoppingList.getName()));
        assertThat(shoppingListDTO.getDescription(), is(shoppingList.getDescription()));

        List<TaskDTO> list1 = shoppingListDTO.getTaskArrayList();
        List<Task> list2 = shoppingList.getTaskArrayList();

        for (TaskDTO taskDTO : list1){
            System.out.println(taskDTO.getId());
        }

        for (Task task1 : list2){
            System.out.println(task1.getId());
        }

// Trích xuất danh sách ID
        List<Long> idsList1 = list1.stream().map(TaskDTO::getId).toList();
        List<Long> idsList2 = list2.stream().map(Task::getId).toList();

// Tạo map đếm tần suất
        Map<Long, Long> frequencyMap1 = idsList1.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Long, Long> frequencyMap2 = idsList2.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

// Tìm các id chỉ có trong list1
        frequencyMap1.keySet().stream()
                .filter(id -> !frequencyMap2.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list1 nhưng không có trong list2"));

// Tìm các id chỉ có trong list2
        frequencyMap2.keySet().stream()
                .filter(id -> !frequencyMap1.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list2 nhưng không có trong list1"));

// Tìm các id có tần suất khác nhau
        frequencyMap1.keySet().stream()
                .filter(frequencyMap2::containsKey) // Chỉ kiểm tra các id có trong cả hai map
                .filter(id -> !frequencyMap1.get(id).equals(frequencyMap2.get(id)))
                .forEach(id -> System.out.println("ID " + id + " có tần suất khác: " +
                        "list1 = " + frequencyMap1.get(id) + ", list2 = " + frequencyMap2.get(id)));

// Kiểm tra tổng thể
        boolean areListsEqual = frequencyMap1.equals(frequencyMap2);
        System.out.println("Hai danh sách có giống nhau không? " + areListsEqual);

// Assertion
        assertThat(areListsEqual, is(true));
    }

    // Done
    @Test
    void testGetSPs() throws Exception{
        System.out.println("-----test get sps-----");

        //tạo request
        CreateShoppingListRequest createShoppingListRequest = CreateShoppingListRequest.builder()
                .familyId(family.getId())
                .name("sp1")
                .description("sp2")
                .build();

        ResponseEntity<BaseResponse<ArrayList<ShoppingListDTO>>> response = testRestTemplate.exchange(
                "/api/shopping/getAll",
                HttpMethod.GET,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ArrayList<ShoppingListDTO>>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ArrayList<ShoppingListDTO> shoppingListDTOs = Objects.requireNonNull(response.getBody()).getData();

        assertThat(shoppingListDTOs.size(), is(1));

        ShoppingListDTO shoppingListDTO = shoppingListDTOs.get(0);

        assertThat(shoppingListDTO.getOwnerDTO().getId(), is(user.getId()));
        assertThat(shoppingListDTO.getId(), is(shoppingList.getId()));
        assertThat(shoppingListDTO.getFamilyDTO().getId(), is(shoppingList.getFamily().getId()));
        assertThat(shoppingListDTO.getName(), is(shoppingList.getName()));
        assertThat(shoppingListDTO.getDescription(), is(shoppingList.getDescription()));

        List<TaskDTO> list1 = shoppingListDTO.getTaskArrayList();
        List<Task> list2 = shoppingList.getTaskArrayList();

        for (TaskDTO taskDTO : list1){
            System.out.println(taskDTO.getId());
        }

        for (Task task1 : list2){
            System.out.println(task1.getId());
        }

// Trích xuất danh sách ID
        List<Long> idsList1 = list1.stream().map(TaskDTO::getId).toList();
        List<Long> idsList2 = list2.stream().map(Task::getId).toList();

// Tạo map đếm tần suất
        Map<Long, Long> frequencyMap1 = idsList1.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Long, Long> frequencyMap2 = idsList2.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

// Tìm các id chỉ có trong list1
        frequencyMap1.keySet().stream()
                .filter(id -> !frequencyMap2.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list1 nhưng không có trong list2"));

// Tìm các id chỉ có trong list2
        frequencyMap2.keySet().stream()
                .filter(id -> !frequencyMap1.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list2 nhưng không có trong list1"));

// Tìm các id có tần suất khác nhau
        frequencyMap1.keySet().stream()
                .filter(frequencyMap2::containsKey) // Chỉ kiểm tra các id có trong cả hai map
                .filter(id -> !frequencyMap1.get(id).equals(frequencyMap2.get(id)))
                .forEach(id -> System.out.println("ID " + id + " có tần suất khác: " +
                        "list1 = " + frequencyMap1.get(id) + ", list2 = " + frequencyMap2.get(id)));

// Kiểm tra tổng thể
        boolean areListsEqual = frequencyMap1.equals(frequencyMap2);
        System.out.println("Hai danh sách có giống nhau không? " + areListsEqual);

// Assertion
        assertThat(areListsEqual, is(true));
    }

    // Done
    @Test
    void testUpdateSP() throws Exception{
        System.out.println("-----test update sp-----");

        //tạo request
        UpdateShoppingListRequest updateShoppingListRequest = UpdateShoppingListRequest.builder()
                .shoppingListId(shoppingList.getId())
                .familyId(family.getId())
                .name("sp3")
                .description("sp4")
                .build();

        ResponseEntity<BaseResponse<ShoppingListDTO>> response = testRestTemplate.exchange(
                "/api/shopping/update",
                HttpMethod.POST,
                new HttpEntity<>(updateShoppingListRequest, getHeader()),
                new ParameterizedTypeReference<BaseResponse<ShoppingListDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ShoppingListDTO shoppingListDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(shoppingListDTO.getOwnerDTO().getId(), is(user.getId()));
        assertThat(shoppingListDTO.getId(), is(updateShoppingListRequest.getShoppingListId()));
        assertThat(shoppingListDTO.getFamilyDTO().getId(), is(updateShoppingListRequest.getFamilyId()));
        assertThat(shoppingListDTO.getName(), is(updateShoppingListRequest.getName()));
        assertThat(shoppingListDTO.getDescription(), is(updateShoppingListRequest.getDescription()));

        List<TaskDTO> list1 = shoppingListDTO.getTaskArrayList();
        List<Task> list2 = shoppingList.getTaskArrayList();

        for (TaskDTO taskDTO : list1){
            System.out.println(taskDTO.getId());
        }

        for (Task task1 : list2){
            System.out.println(task1.getId());
        }

// Trích xuất danh sách ID
        List<Long> idsList1 = list1.stream().map(TaskDTO::getId).toList();
        List<Long> idsList2 = list2.stream().map(Task::getId).toList();

// Tạo map đếm tần suất
        Map<Long, Long> frequencyMap1 = idsList1.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Long, Long> frequencyMap2 = idsList2.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

// Tìm các id chỉ có trong list1
        frequencyMap1.keySet().stream()
                .filter(id -> !frequencyMap2.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list1 nhưng không có trong list2"));

// Tìm các id chỉ có trong list2
        frequencyMap2.keySet().stream()
                .filter(id -> !frequencyMap1.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list2 nhưng không có trong list1"));

// Tìm các id có tần suất khác nhau
        frequencyMap1.keySet().stream()
                .filter(frequencyMap2::containsKey) // Chỉ kiểm tra các id có trong cả hai map
                .filter(id -> !frequencyMap1.get(id).equals(frequencyMap2.get(id)))
                .forEach(id -> System.out.println("ID " + id + " có tần suất khác: " +
                        "list1 = " + frequencyMap1.get(id) + ", list2 = " + frequencyMap2.get(id)));

// Kiểm tra tổng thể
        boolean areListsEqual = frequencyMap1.equals(frequencyMap2);
        System.out.println("Hai danh sách có giống nhau không? " + areListsEqual);

// Assertion
        assertThat(areListsEqual, is(true));
    }

    //Done
    @Test
    void testDeleteSP() throws Exception{
        System.out.println("-----test delete sp-----");

        ResponseEntity<BaseResponse<ShoppingListDTO>> response = testRestTemplate.exchange(
                "/api/shopping/delete/" + shoppingList.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(getHeader()),
                new ParameterizedTypeReference<BaseResponse<ShoppingListDTO>>() {
                }
        );

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ShoppingListDTO shoppingListDTO = Objects.requireNonNull(response.getBody()).getData();

        assertThat(shoppingListDTO.getOwnerDTO().getId(), is(user.getId()));
        assertThat(shoppingListDTO.getId(), is(shoppingList.getId()));
        assertThat(shoppingListDTO.getFamilyDTO().getId(), is(shoppingList.getFamily().getId()));
        assertThat(shoppingListDTO.getName(), is(shoppingList.getName()));
        assertThat(shoppingListDTO.getDescription(), is(shoppingList.getDescription()));
        assertThat(shoppingListDTO.getStatus(), is(StatusConfig.DELETED.getStatus()));

        List<TaskDTO> list1 = shoppingListDTO.getTaskArrayList();
        List<Task> list2 = shoppingList.getTaskArrayList();

        for (TaskDTO taskDTO : list1){
            System.out.println(taskDTO.getId());
        }

        for (Task task1 : list2){
            System.out.println(task1.getId());
        }

// Trích xuất danh sách ID
        List<Long> idsList1 = list1.stream().map(TaskDTO::getId).toList();
        List<Long> idsList2 = list2.stream().map(Task::getId).toList();

// Tạo map đếm tần suất
        Map<Long, Long> frequencyMap1 = idsList1.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Long, Long> frequencyMap2 = idsList2.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

// Tìm các id chỉ có trong list1
        frequencyMap1.keySet().stream()
                .filter(id -> !frequencyMap2.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list1 nhưng không có trong list2"));

// Tìm các id chỉ có trong list2
        frequencyMap2.keySet().stream()
                .filter(id -> !frequencyMap1.containsKey(id))
                .forEach(id -> System.out.println("ID " + id + " có trong list2 nhưng không có trong list1"));

// Tìm các id có tần suất khác nhau
        frequencyMap1.keySet().stream()
                .filter(frequencyMap2::containsKey) // Chỉ kiểm tra các id có trong cả hai map
                .filter(id -> !frequencyMap1.get(id).equals(frequencyMap2.get(id)))
                .forEach(id -> System.out.println("ID " + id + " có tần suất khác: " +
                        "list1 = " + frequencyMap1.get(id) + ", list2 = " + frequencyMap2.get(id)));

// Kiểm tra tổng thể
        boolean areListsEqual = frequencyMap1.equals(frequencyMap2);
        System.out.println("Hai danh sách có giống nhau không? " + areListsEqual);

// Assertion
        assertThat(areListsEqual, is(true));
    }
}
