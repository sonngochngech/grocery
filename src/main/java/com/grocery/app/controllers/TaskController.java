package com.grocery.app.controllers;

import com.grocery.app.config.StatusConfig;
import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.*;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.request.createRequest.CreateTaskRequest;
import com.grocery.app.dto.request.updateRequest.UpdateTaskRequest;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.services.*;
import com.grocery.app.services.impl.TaskServiceImpl;
import com.grocery.app.services.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("api/task")
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private FoodService foodService;

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private FamilyService familyService;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        // Get the currently authenticated user who is assigning the task
        UserInfoConfig assigner = authenticationService.getCurrentUser();

        // Validate the existence of the assignee (user)
        UserDetailDTO assignee = userService.getUser(createTaskRequest.getUserId());
        if (assignee == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // Validate food existence
        FoodDTO foodDTO = foodService.getFoodById(createTaskRequest.getUserId(), createTaskRequest.getFoodId())
                .orElseThrow(() -> new ServiceException(
                        ResCode.FOOD_NOT_FOUND.getMessage(),
                        ResCode.FOOD_NOT_FOUND.getCode()
                ));

        // Validate shopping list existence
        ShoppingListDTO shoppingListDTO = shoppingListService.getShoppingListById(createTaskRequest.getUserId(), createTaskRequest.getShoppingListId())
                .orElseThrow(() -> new ServiceException(
                        ResCode.SHOPPING_LIST_NOT_FOUND.getMessage(),
                        ResCode.SHOPPING_LIST_NOT_FOUND.getCode()
                ));

        // Validate assigner's ownership of the family
        boolean isOwner = familyService.verifyOwner(shoppingListDTO.getFamilyId(), assigner.getId());
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Validate assignee's membership in the family
        boolean isMember = familyService.verifyMember(shoppingListDTO.getFamilyId(), assignee.getId());
        if (!isMember) {
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }

        // Build the TaskDTO with validated entities and other details
        TaskDTO taskDTO = TaskDTO.builder()
                .user(assignee)
                .foodDTO(foodDTO)
                .shoppingListDTO(shoppingListDTO)
                .quantity(createTaskRequest.getQuantity())
                .status(StatusConfig.AVAILABLE.getStatus())  // Set a default status if necessary
                .build();

        // Persist the task and return the response with the created task
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }



    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable long taskId) {
        // Get the currently authorized user
        UserInfoConfig user = authenticationService.getCurrentUser();

        // Retrieve the task based on the user and taskId
        TaskDTO taskDTO = taskService.getTaskById(user.getId(), taskId)
                .orElseThrow(() -> new ServiceException(
                        ResCode.TASK_NOT_FOUND.getMessage(),
                        ResCode.TASK_NOT_FOUND.getCode()
                ));

        // Return the found task
        return ResponseEntity.ok(taskDTO);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ArrayList<TaskDTO>> getAllTask(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int to) {

        ArrayList<TaskDTO> tasks = taskService.getAllTask(userId, from, to);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping
    public ResponseEntity<TaskDTO> updateTask(@RequestBody UpdateTaskRequest updateTaskRequest) {
        Optional<TaskDTO> updatedTask = Optional.ofNullable(taskService.updateTask(
                updateTaskRequest.getUserId(), updateTaskRequest.toTaskDTO()));

        return updatedTask.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/{userId}/{taskId}")
    public ResponseEntity<TaskDTO> deleteTask(@PathVariable long userId, @PathVariable long taskId) {
        Optional<TaskDTO> deletedTask = Optional.ofNullable(taskService.deleteTask(userId, taskId));

        return deletedTask.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
