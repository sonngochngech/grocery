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

import java.time.LocalDate;
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
        // Lấy người dùng hiện tại đang được xác thực để gán nhiệm vụ
        UserInfoConfig assigner = authenticationService.getCurrentUser();

        // Kiểm tra sự tồn tại của người được giao (người dùng)
        UserDetailDTO assignee = userService.getUser(createTaskRequest.getUserId());
        if (assignee == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra sự tồn tại của thực phẩm
        FoodDTO foodDTO = foodService.getFoodById(createTaskRequest.getUserId(), createTaskRequest.getFoodId())
                .orElseThrow(() -> new ServiceException(
                        ResCode.FOOD_NOT_FOUND.getMessage(),
                        ResCode.FOOD_NOT_FOUND.getCode()
                ));

        // Kiểm tra sự tồn tại của danh sách mua sắm
        ShoppingListDTO shoppingListDTO = shoppingListService.getShoppingListById(createTaskRequest.getUserId(), createTaskRequest.getShoppingListId())
                .orElseThrow(() -> new ServiceException(
                        ResCode.SHOPPING_LIST_NOT_FOUND.getMessage(),
                        ResCode.SHOPPING_LIST_NOT_FOUND.getCode()
                ));

        // Kiểm tra quyền sở hữu của người giao trong gia đình
        boolean isOwner = familyService.verifyOwner(shoppingListDTO.getFamilyDTO().getBasicInfo().getId(), assigner.getId());
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Kiểm tra thành viên của người được giao trong gia đình
        boolean isMember = familyService.verifyMember(shoppingListDTO.getFamilyDTO().getBasicInfo().getId(), assignee.getId());
        if (!isMember) {
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }

        // Xây dựng TaskDTO với các thực thể đã được xác minh và các chi tiết khác
        TaskDTO taskDTO = TaskDTO.builder()
                .user(assignee)
                .foodDTO(foodDTO)
                .shoppingListDTO(shoppingListDTO)
                .quantity(createTaskRequest.getQuantity())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .status(StatusConfig.AVAILABLE.getStatus())  // Đặt trạng thái mặc định nếu cần
                .build();

        // Lưu nhiệm vụ và trả về phản hồi với nhiệm vụ đã tạo
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/get/{taskId}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable long taskId) {
        // Lấy người dùng được ủy quyền hiện tại
        UserInfoConfig user = authenticationService.getCurrentUser();

        // Lấy nhiệm vụ dựa trên người dùng và taskId
        TaskDTO taskDTO = taskService.getTaskById(user.getId(), taskId)
                .orElseThrow(() -> new ServiceException(
                        ResCode.TASK_NOT_FOUND.getMessage(),
                        ResCode.TASK_NOT_FOUND.getCode()
                ));

        // Trả về nhiệm vụ đã tìm thấy
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

        // Lấy nhiệm vụ bằng ID và xác minh sự tồn tại
        TaskDTO taskDTO = taskService.getTaskById(
                updateTaskRequest.getUserId(),
                updateTaskRequest.getTaskId()
        ).orElseThrow(() -> new ServiceException(
                ResCode.TASK_NOT_FOUND.getMessage(),
                ResCode.TASK_NOT_FOUND.getCode()
        ));

        // Kiểm tra mối liên kết của danh sách mua sắm
        if (!Objects.equals(taskDTO.getShoppingListDTO().getId(), updateTaskRequest.getShoppingListId())) {
            throw new ServiceException(
                    ResCode.TASK_NOT_IN_SHOPPING_LIST.getMessage(),
                    ResCode.TASK_NOT_IN_SHOPPING_LIST.getCode()
            );
        }

        // Cập nhật thực phẩm nếu đã thay đổi
        if (updateTaskRequest.getFoodId() != taskDTO.getFoodDTO().getId()) {
            FoodDTO foodDTO = foodService.getFoodById(assigner.getId(), updateTaskRequest.getFoodId())
                    .orElseThrow(() -> new ServiceException(
                            ResCode.FOOD_NOT_FOUND.getMessage(),
                            ResCode.FOOD_NOT_FOUND.getCode()
                    ));
            taskDTO.setFoodDTO(foodDTO);
        }

        // Cập nhật người nhận nếu đã thay đổi
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

        // Cập nhật số lượng nếu đã thay đổi
        if (updateTaskRequest.getQuantity() != taskDTO.getQuantity()) {
            taskDTO.setQuantity(updateTaskRequest.getQuantity());
        }

        // Đảm bảo người giao là chủ sở hữu của gia đình trong danh sách mua sắm
        boolean isOwner = familyService.verifyOwner(
                taskDTO.getShoppingListDTO().getFamilyDTO().getBasicInfo().getId(),
                assigner.getId()
        );
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Xác minh người nhận có thuộc về gia đình không
        boolean isMember = familyService.verifyMember(
                taskDTO.getShoppingListDTO().getFamilyDTO().getBasicInfo().getId(),
                updateTaskRequest.getUserId()
        );
        if (!isMember) {
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }

        // Cập nhật ngày sửa đổi
        taskDTO.setUpdatedAt(LocalDate.now());

        // Lưu nhiệm vụ đã cập nhật và trả về phản hồi
        TaskDTO updatedTaskDTO = taskService.updateTask(taskDTO);

        // Trả về nhiệm vụ đã cập nhật
        return ResponseEntity.ok(updatedTaskDTO);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<TaskDTO> deleteTask(@PathVariable long taskId) {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        TaskDTO deletedTask = taskService.deleteTask(userInfoConfig.getId(), taskId);

        // Kiểm tra xem nhiệm vụ đã bị xóa chưa
        if (deletedTask == null) {
            throw new ServiceException(
                    ResCode.TASK_NOT_DELETED.getMessage(),
                    ResCode.TASK_NOT_DELETED.getCode()
            );
        }

        return ResponseEntity.ok(deletedTask);
    }
}
