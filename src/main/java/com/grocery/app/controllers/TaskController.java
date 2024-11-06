package com.grocery.app.controllers;

import com.grocery.app.config.StatusConfig;
import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.*;
import com.grocery.app.dto.request.createRequest.CreateTaskRequest;
import com.grocery.app.dto.request.updateRequest.UpdateTaskRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Objects;
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

    @PostMapping("/add")
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

    @GetMapping("/get/{taskId}")
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

    @GetMapping("/getAll")
    public ResponseEntity<ArrayList<TaskDTO>> getAllTask(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int to) {

        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        ArrayList<TaskDTO> tasks = taskService.getAllTask(userInfoConfig.getId(), from, to);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/update")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody UpdateTaskRequest updateTaskRequest) {
        UserInfoConfig assigner = authenticationService.getCurrentUser();

        // Retrieve the task by ID and validate existence
        TaskDTO taskDTO = taskService.getTaskById(
                updateTaskRequest.getUserId(),
                updateTaskRequest.getTaskId()
        ).orElseThrow(() -> new ServiceException(
                ResCode.TASK_NOT_FOUND.getMessage(),
                ResCode.TASK_NOT_FOUND.getCode()
        ));

        // Validate shopping list association
        if (!Objects.equals(taskDTO.getShoppingListDTO().getId(), updateTaskRequest.getShoppingListId())) {
            throw new ServiceException(
                    ResCode.TASK_NOT_IN_SHOPPING_LIST.getMessage(),
                    ResCode.TASK_NOT_IN_SHOPPING_LIST.getCode()
            );
        }

        // Update food if it's changed
        if (updateTaskRequest.getFoodId() != taskDTO.getFoodDTO().getId()) {
            FoodDTO foodDTO = foodService.getFoodById(assigner.getId(), updateTaskRequest.getFoodId())
                    .orElseThrow(() -> new ServiceException(
                            ResCode.FOOD_NOT_FOUND.getMessage(),
                            ResCode.FOOD_NOT_FOUND.getCode()
                    ));
            taskDTO.setFoodDTO(foodDTO);
        }

        // Update assignee if it's changed
        if (!Objects.equals(updateTaskRequest.getUserId(), taskDTO.getUser().getId())) {
            UserDetailDTO newAssignee = userService.getUser(updateTaskRequest.getUserId());
            if (newAssignee == null) {
                throw new ServiceException(
                        ResCode.USER_NOT_FOUND.getMessage(),
                        ResCode.USER_NOT_FOUND.getCode()
                );
            }
            taskDTO.setUser(newAssignee);
        }

        // Update quantity if it's changed
        if (updateTaskRequest.getQuantity() != taskDTO.getQuantity()) {
            taskDTO.setQuantity(updateTaskRequest.getQuantity());
        }

        // Ensure the assigner is the owner of the family in the shopping list
        boolean isOwner = familyService.verifyOwner(
                taskDTO.getShoppingListDTO().getFamilyId(),
                assigner.getId()
        );
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Validate if the assignee belongs to the family
        boolean isMember = familyService.verifyMember(
                taskDTO.getShoppingListDTO().getFamilyId(),
                updateTaskRequest.getUserId()
        );
        if (!isMember) {
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }

        // Persist the updated task and return the response
        TaskDTO updatedTaskDTO = taskService.updateTask(taskDTO);

        // Return the updated task
        return ResponseEntity.ok(updatedTaskDTO);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<TaskDTO> deleteTask(@PathVariable long taskId) {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        TaskDTO deletedTask = taskService.deleteTask(userInfoConfig.getId(), taskId);

        // Validate if task was deleted
        if (deletedTask == null) {
            throw new ServiceException(
                    ResCode.TASK_NOT_DELETED.getMessage(),
                    ResCode.TASK_NOT_DELETED.getCode()
            );
        }

        return ResponseEntity.ok(deletedTask);
    }
}
