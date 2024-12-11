package com.grocery.app.controllers;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.*;
import com.grocery.app.dto.request.createRequest.CreateTaskRequest;
import com.grocery.app.dto.request.updateRequest.UpdateTaskRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

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
    public ResponseEntity<BaseResponse<TaskDTO>> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        // Lấy người dùng hiện tại đang được xác thực để gán nhiệm vụ
        UserInfoConfig assigner = authenticationService.getCurrentUser();

        System.out.println("assignee");
        // Kiểm tra sự tồn tại của người được giao (người dùng)
        UserDTO assignee = userService.getUserById(createTaskRequest.getAssignee());
        if (assignee == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }
        System.out.println(assignee.getId());

        // Kiểm tra sự tồn tại của thực phẩm
        System.out.println("food");
        FoodDTO foodDTO = foodService.getFoodById(assigner.getId(), createTaskRequest.getFoodId());
        if (foodDTO == null) {
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }
        System.out.println(foodDTO.getId());

        // Kiểm tra sự tồn tại của danh sách mua sắm
        System.out.println("shopping list");
        ShoppingListDTO shoppingListDTO = shoppingListService.getShoppingListById(assigner.getId(), createTaskRequest.getShoppingListId())
                .orElseThrow(() -> new ServiceException(
                        ResCode.SHOPPING_LIST_NOT_FOUND.getMessage(),
                        ResCode.SHOPPING_LIST_NOT_FOUND.getCode()
                ));
        System.out.println(shoppingListDTO.getId());

        // Kiểm tra quyền sở hữu của người giao trong gia đình
        boolean isOwner = familyService.verifyOwner(shoppingListDTO.getFamilyDTO().getId(), assigner.getId());
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Kiểm tra thành viên của người được giao trong gia đình
        boolean isMember = familyService.verifyMember(shoppingListDTO.getFamilyDTO().getId(), assignee.getId());
        if (!isMember) {
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }

        // Xây dựng TaskDTO với các thực thể đã được xác minh và các chi tiết khác
        System.out.println("task dto");
        TaskDTO taskDTO = TaskDTO.builder()
                .assignee(assignee)
                .foodDTO(foodDTO)
                .shoppingListId(shoppingListDTO.getId())
                .quantity(createTaskRequest.getQuantity())
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())  // Đặt trạng thái mặc định nếu cần
                .build();

        System.out.println(taskDTO.getFoodDTO().getId());
        System.out.println(taskDTO.getAssignee().getId());
        System.out.println(taskDTO.getShoppingListId());

        // Lưu nhiệm vụ và trả về phản hồi với nhiệm vụ đã tạo
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseFactory.createResponse(
                        createdTask,
                        ResCode.CREATE_TASK_SUCCESSFULLY.getMessage(),
                        ResCode.CREATE_TASK_SUCCESSFULLY.getCode()
                )
        );
    }

    @GetMapping("/get/{taskId}")
    public ResponseEntity<BaseResponse<TaskDTO>> getTaskById(@PathVariable long taskId) {
        // Lấy người dùng được ủy quyền hiện tại
        UserInfoConfig user = authenticationService.getCurrentUser();

        // Lấy nhiệm vụ dựa trên người dùng và taskId
        TaskDTO taskDTO = taskService.getTaskById(user.getId(), taskId);

        // Trả về nhiệm vụ đã tìm thấy
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                taskDTO,
                                ResCode.GET_TASK_SUCCESSFULLY.getMessage(),
                                ResCode.GET_TASK_SUCCESSFULLY.getCode()
                        )
                );
    }

    @GetMapping("/getAll")
    public ResponseEntity<BaseResponse<ArrayList<TaskDTO>>> getAllTask(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int to) {

        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        ArrayList<TaskDTO> tasks = taskService.getAllTask(userInfoConfig.getId(), from, to);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                tasks,
                                ResCode.GET_TASKS_SUCCESSFULLY.getMessage(),
                                ResCode.GET_TASKS_SUCCESSFULLY.getCode()
                        )
                );
    }

    @PostMapping("/update")
    public ResponseEntity<BaseResponse<TaskDTO>> updateTask(@RequestBody UpdateTaskRequest updateTaskRequest) {
        UserInfoConfig assigner = authenticationService.getCurrentUser();

        // Lấy nhiệm vụ bằng ID và xác minh sự tồn tại
        TaskDTO taskDTO = taskService.getTaskById(
                assigner.getId(),
                updateTaskRequest.getTaskId()
        );

        // Kiểm tra mối liên kết của danh sách mua sắm
        if (!Objects.equals(taskDTO.getShoppingListId(), updateTaskRequest.getShoppingListId())) {
            throw new ServiceException(
                    ResCode.TASK_NOT_IN_SHOPPING_LIST.getMessage(),
                    ResCode.TASK_NOT_IN_SHOPPING_LIST.getCode()
            );
        }

        // Cập nhật thực phẩm nếu đã thay đổi
        FoodDTO foodDTO = foodService.getFoodById(assigner.getId(), updateTaskRequest.getFoodId());
        if (foodDTO == null) {
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }
        taskDTO.setFoodDTO(foodDTO);

        // Cập nhật người nhận nếu đã thay đổi
        UserDTO newAssignee = userService.getUserById(updateTaskRequest.getUserId());
        if (newAssignee == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }
        taskDTO.setAssignee(newAssignee);

        // Cập nhật số lượng nếu đã thay đổi
        taskDTO.setQuantity(updateTaskRequest.getQuantity());

        // Đảm bảo người giao là chủ sở hữu của gia đình trong danh sách mua sắm
        boolean isOwner = familyService.verifyOwner(
                taskDTO.getShoppingListId(),
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
                taskDTO.getShoppingListId(),
                updateTaskRequest.getUserId()
        );
        if (!isMember) {
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }

        // Cập nhật ngày sửa đổi
        taskDTO.setUpdatedAt(Date.valueOf(LocalDate.now()));

        // Lưu nhiệm vụ đã cập nhật và trả về phản hồi
        TaskDTO updatedTaskDTO = taskService.updateTask(taskDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                updatedTaskDTO,
                                ResCode.UPDATE_TASK_SUCCESSFULLY.getMessage(),
                                ResCode.UPDATE_TASK_SUCCESSFULLY.getCode()
                        )
                );
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<BaseResponse<TaskDTO>> deleteTask(@PathVariable long taskId) {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        TaskDTO deletedTask = taskService.deleteTask(userInfoConfig.getId(), taskId);

        // Kiểm tra xem nhiệm vụ đã bị xóa chưa
        if (deletedTask == null) {
            throw new ServiceException(
                    ResCode.TASK_NOT_DELETED.getMessage(),
                    ResCode.TASK_NOT_DELETED.getCode()
            );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                deletedTask,
                                ResCode.DELETE_TASK_SUCCESSFULLY.getMessage(),
                                ResCode.DELETE_TASK_SUCCESSFULLY.getCode()
                        )
                );
    }
}
