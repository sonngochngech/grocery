package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.dto.*;
import com.grocery.app.entities.Food;
import com.grocery.app.entities.ShoppingList;
import com.grocery.app.entities.Task;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.FoodRepo;
import com.grocery.app.repositories.ShoppingListRepo;
import com.grocery.app.repositories.TaskRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskRepo taskRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ShoppingListRepo shoppingListRepo;

    @Autowired
    private FoodRepo foodRepo;

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        // Map DTO to entity
        Task task = convertToTask(taskDTO);

        // Save the entity
        Task savedTask = taskRepository.save(task);

        // Map entity back to DTO and return
        return convertToTaskDTO(savedTask);
    }

    @Override
    public TaskDTO getTaskById(long userId, long id) {
        Task task = taskRepository.findById(id).orElse(null);
        if(task == null){
            throw new ServiceException(
                    ResCode.TASK_NOT_FOUND.getMessage(),
                    ResCode.TASK_NOT_FOUND.getCode()
            );
        }

        if(task.getAssignee().getId() == userId || task.getShoppingList().getOwner().getId() == userId) {
            // Retrieve task and check user ownership
            return convertToTaskDTO(task);
        }
        else throw new ServiceException(
            ResCode.NOT_SHOPPING_LIST_OWNER.getMessage(),
            ResCode.NOT_SHOPPING_LIST_OWNER.getCode()
        );
    }

    @Override
    public ArrayList<TaskDTO> getAllTask(long userId, int from, int to) {
        List<Task> tasks = taskRepository.findAllByUserId(userId);

        // Clamp "to" parameter
        int maxSize = tasks.size();

        if (maxSize == 0) return new ArrayList<>();

        from = Math.max(0, Math.min(from, maxSize - 1)); // from trong khoảng [0, maxSize - 1]
        to = Math.max(from + 1, Math.min(to, maxSize));

        // Paginate and map tasks to DTOs
        List<Task> paginatedTasks = tasks.subList(from, to);

        return paginatedTasks.stream()
                .map(this::convertToTaskDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public TaskDTO updateTask(TaskDTO taskDTO) {
        Task existingTask = convertToTask(taskDTO);

        Task updatedTask = taskRepository.save(existingTask);
        return convertToTaskDTO(updatedTask);
    }

    @Override
    public TaskDTO deleteTask(long userId, long id) {
        Task task = taskRepository.findById(id).orElse(null);

        // Kiểm tra sự tồn tại
        if (task == null || Objects.equals(task.getStatus(), StatusConfig.DELETED.getStatus())) {
            throw new ServiceException(
                    ResCode.TASK_NOT_FOUND.getMessage(),
                    ResCode.TASK_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra quyền sở hữu cả danh sách mua sắm
        if (task.getShoppingList().getOwner().getId() != userId) {
            throw new ServiceException(
                    ResCode.NOT_SHOPPING_LIST_OWNER.getMessage(),
                    ResCode.NOT_SHOPPING_LIST_OWNER.getCode()
            );
        }

        task.setStatus(StatusConfig.DELETED.getStatus()); // Assume DELETED is the status to mark as deleted
        Task deletedTask = taskRepository.save(task);
        return convertToTaskDTO(deletedTask);
    }

    @Override
    public TaskDTO convertToTaskDTO(Task task) {
        UserDTO userDTO = modelMapper.map(task.getAssignee(), UserDTO.class);
        Food food = task.getFood();
        FoodDTO foodDTO = FoodDTO.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .user(modelMapper.map(food.getUser(), UserDTO.class))
                .categoryDTO(modelMapper.map(food.getCategory(), CategoryDTO.class))
                .measureUnitDTO(modelMapper.map(food.getMeasureUnit(), UnitDTO.class))
                .createdAt(food.getCreatedAt())
                .updatedAt(food.getUpdatedAt())
                .status(food.getStatus())
                .build();

        return TaskDTO.builder()
                .id(task.getId())
                .assignee(userDTO)
                .shoppingListId(task.getShoppingList().getId())
                .foodDTO(foodDTO)
                .quantity(task.getQuantity())
                .dueDateTime(task.getDueDateTime())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .status(task.getStatus())
                .build();
    }

    @Override
    public Task convertToTask(TaskDTO taskDTO) {
        ShoppingList shoppingList = shoppingListRepo.findById(taskDTO.getShoppingListId()).orElse(null);
        User assignee = userRepository.findById(taskDTO.getAssignee().getId()).orElse(null);
        Food food = foodRepo.findById(taskDTO.getFoodDTO().getId()).orElse(null);

        Task task = Task.builder()
                .assignee(assignee)
                .shoppingList(shoppingList)
                .food(food)
                .quantity(taskDTO.getQuantity())
                .dueDateTime(taskDTO.getDueDateTime())
                .createdAt(taskDTO.getCreatedAt())
                .updatedAt(taskDTO.getUpdatedAt())
                .status(taskDTO.getStatus())
                .build();

        System.out.println(taskDTO.getId());

        if (taskDTO.getId() != null) {
            task.setId(taskDTO.getId());
        }

        return task;
    }
}
