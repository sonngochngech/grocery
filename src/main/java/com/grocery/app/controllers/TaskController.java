package com.grocery.app.controllers;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.*;
import com.grocery.app.dto.request.createRequest.CreateTaskRequest;
import com.grocery.app.dto.request.updateRequest.UpdateTaskRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.notification.NotificationFactory;
import com.grocery.app.notification.NotificationProducer;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/task")
@Slf4j
@SecurityRequirement(name = "bearerAuth")
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

    @Autowired
    private NotificationFactory notificationFactory;

    @Autowired
    private NotificationProducer notificationProducer;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/add")
    public ResponseEntity<BaseResponse<TaskDTO>> createTask(@RequestBody CreateTaskRequest createTaskRequest) {
        // Tracing log: Nhận request tạo task
        System.out.println("Received request to create task with details: " + createTaskRequest);

        // Lấy người dùng hiện tại đang được xác thực để gán nhiệm vụ
        UserInfoConfig assigner = authenticationService.getCurrentUser();
        System.out.println("Assigning task to user: " + assigner);

        // Kiểm tra sự tồn tại của người được giao (người dùng)
        UserDTO assignee = userService.getUserById(createTaskRequest.getAssignee());
        UserDetailDTO assigneeInfo = userService.getUser(createTaskRequest.getAssignee());
        if (assignee == null || assigneeInfo == null) {
            System.out.println("Assignee not found: " + createTaskRequest.getAssignee());
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }
        System.out.println("Assignee found: " + assignee);

        // Kiểm tra sự tồn tại của thực phẩm
        FoodDTO foodDTO = foodService.getFoodById(assigner.getId(), createTaskRequest.getFoodId());
        if (foodDTO == null) {
            System.out.println("Food item not found: " + createTaskRequest.getFoodId());
            throw new ServiceException(
                    ResCode.FOOD_NOT_FOUND.getMessage(),
                    ResCode.FOOD_NOT_FOUND.getCode()
            );
        }
        System.out.println("Food item found: " + foodDTO);

        // Kiểm tra sự tồn tại của danh sách mua sắm
        ShoppingListDTO shoppingListDTO = shoppingListService.getShoppingListById(assigner.getId(), createTaskRequest.getShoppingListId());
        System.out.println("Shopping list found: " + shoppingListDTO);

        // Kiểm tra quyền sở hữu của người giao trong gia đình
        boolean isOwner = familyService.verifyOwner(shoppingListDTO.getFamilyDTO().getId(), assigner.getId());
        if (!isOwner) {
            System.out.println("User is not the owner of the family: " + assigner.getId());
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }
        System.out.println("User is the owner of the family.");

        // Kiểm tra thành viên của người được giao trong gia đình
        boolean isMember = familyService.verifyMember(shoppingListDTO.getFamilyDTO().getId(), assignee.getId());
        if (!isMember) {
            System.out.println("Assignee is not a member of the family: " + assignee.getId());
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }
        System.out.println("Assignee is a member of the family.");

        // Lấy thời gian hiện tại
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Lấy thời gian tác vụ từ yêu cầu
        Timestamp taskTimestamp = createTaskRequest.getTimestamp();

        // Tính toán độ dài thời gian giữa hiện tại và thời gian tác vụ
        long durationInMillis = taskTimestamp.getTime() - now.getTime();
        long durationInMinutes = durationInMillis / 1000 / 60;  // Chuyển đổi từ mili giây sang phút

        System.out.println("Thời gian hết hạn tác vụ: " + taskTimestamp + ", thời gian hiện tại: " + now);

        // Kiểm tra nếu thời gian còn lại ít hơn 60 phút
        if (durationInMinutes < 60) {
            System.out.println("Thời gian hết hạn không hợp lệ, ít hơn 60 phút: " + durationInMinutes);
            throw new ServiceException(
                    ResCode.DUE_DATE_TIME_NOT_VALID.getMessage(),
                    ResCode.DUE_DATE_TIME_NOT_VALID.getCode()
            );
        }

        System.out.println("Thời gian hết hạn hợp lệ.");

        // Xây dựng TaskDTO với các thực thể đã được xác minh và các chi tiết khác
        TaskDTO taskDTO = TaskDTO.builder()
                .assignee(assignee)
                .foodDTO(foodDTO)
                .shoppingListId(shoppingListDTO.getId())
                .quantity(createTaskRequest.getQuantity())
                .timestamp(createTaskRequest.getTimestamp())
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())  // Đặt trạng thái mặc định nếu cần
                .build();
        System.out.println("TaskDTO created: " + taskDTO);

        // Lưu nhiệm vụ và trả về phản hồi với nhiệm vụ đã tạo
        TaskDTO createdTask = taskService.createTask(taskDTO);
        System.out.println("Task created successfully: " + createdTask);

        String content = "Bạn có một nhiệm vụ mới, mua "
                + foodDTO.getName()
                + " với số lượng "
                + taskDTO.getQuantity()
                + " thời hạn chót "
                + taskDTO.getTimestamp();

        NotiContentDTO notiContentDTO = NotiContentDTO.builder()
                .title("Nhiệm vụ mới")
                .type("task")
                .message(content)
                .externalData("nothing else")
                .build();

        NotificationDTO notificationDTO = notificationService.saveNotification(
                assigner.getId(),
                List.of(assignee.getId()),
                notiContentDTO
        );

        NotiDTO noti = notificationFactory.sendNotification(notificationDTO);
        notificationProducer.sendMessage(noti);

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
        UserDetailDTO newAssigneeDTO = userService.getUser(updateTaskRequest.getUserId());
        if (newAssignee == null || newAssigneeDTO == null) {
            throw new ServiceException(
                    ResCode.USER_NOT_FOUND.getMessage(),
                    ResCode.USER_NOT_FOUND.getCode()
            );
        }
        taskDTO.setAssignee(newAssignee);

        // Cập nhật số lượng nếu đã thay đổi
        taskDTO.setQuantity(updateTaskRequest.getQuantity());
        ShoppingListDTO shoppingListDTO = shoppingListService.getShoppingListById(assigner.getId(), updateTaskRequest.getShoppingListId());

        // Kiểm tra quyền sở hữu của người giao trong gia đình
        boolean isOwner = familyService.verifyOwner(shoppingListDTO.getFamilyDTO().getId(), assigner.getId());
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Kiểm tra thành viên của người được giao trong gia đình
        boolean isMember = familyService.verifyMember(shoppingListDTO.getFamilyDTO().getId(), updateTaskRequest.getUserId());

        if (!isMember) {
            throw new ServiceException(
                    ResCode.NOT_BELONG_TO_FAMILY.getMessage(),
                    ResCode.NOT_BELONG_TO_FAMILY.getCode()
            );
        }

        // Lấy thời gian hiện tại
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Lấy thời gian tác vụ từ yêu cầu
        Timestamp taskTimestamp = updateTaskRequest.getTimestamp();

        // Tính toán độ dài thời gian giữa hiện tại và thời gian tác vụ
        long durationInMillis = taskTimestamp.getTime() - now.getTime();
        long durationInMinutes = durationInMillis / 1000 / 60;  // Chuyển đổi từ mili giây sang phút

        System.out.println("Thời gian hết hạn tác vụ: " + taskTimestamp + ", thời gian hiện tại: " + now);

        // Kiểm tra nếu thời gian còn lại ít hơn 5 phút
        if (durationInMinutes < 5) {
            System.out.println("Thời gian hết hạn không hợp lệ, ít hơn 5 phút: " + durationInMinutes);
            throw new ServiceException(
                    ResCode.DUE_DATE_TIME_NOT_VALID.getMessage(),
                    ResCode.DUE_DATE_TIME_NOT_VALID.getCode()
            );
        }

        System.out.println("Thời gian hết hạn hợp lệ.");

        // Cập nhật ngày sửa đổi
        taskDTO.setUpdatedAt(Date.valueOf(LocalDate.now()));
        taskDTO.setTimestamp(updateTaskRequest.getTimestamp());

        // Lưu nhiệm vụ đã cập nhật và trả về phản hồi
        TaskDTO updatedTaskDTO = taskService.updateTask(taskDTO);

        String content = "Bạn có một nhiệm vụ mới, mua "
                + foodDTO.getName()
                + " với số lượng "
                + taskDTO.getQuantity()
                + " thời hạn chót "
                + taskDTO.getTimestamp();

        NotiContentDTO notiContentDTO = NotiContentDTO.builder()
                .title("Nhiệm vụ mới")
                .type("task")
                .message(content)
                .externalData("nothing else")
                .build();

        NotificationDTO notificationDTO = notificationService.saveNotification(
                assigner.getId(),
                List.of(newAssignee.getId()),
                notiContentDTO
        );

        NotiDTO noti = notificationFactory.sendNotification(notificationDTO);
        notificationProducer.sendMessage(noti);

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
