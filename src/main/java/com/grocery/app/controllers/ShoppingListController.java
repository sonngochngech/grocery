package com.grocery.app.controllers;

import com.grocery.app.config.constant.StatusConfig;
import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.ShoppingListDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.request.createRequest.CreateShoppingListRequest;
import com.grocery.app.dto.request.updateRequest.UpdateShoppingListRequest;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

@RestController
@RequestMapping("api/shopping")
@Slf4j
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final AuthenticationService authenticationService;
    private final FamilyService familyService;
    private final UserService userService;

    public ShoppingListController(ShoppingListService shoppingListService,
                                  AuthenticationService authenticationService,
                                  FamilyService familyService,
                                  UserService userService) {
        this.shoppingListService = shoppingListService;
        this.authenticationService = authenticationService;
        this.familyService = familyService;
        this.userService = userService;
    }

    // Tạo một danh sách mua sắm mới
    @PostMapping("/add")
    public ResponseEntity<BaseResponse<ShoppingListDTO>> createShoppingList(@RequestBody CreateShoppingListRequest createShoppingListRequest) {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser(); // Xác thực người dùng hiện tại
        UserDTO owner = userService.getUserById(userInfoConfig.getId()); // Lấy thông tin chi tiết của chủ sở hữu

        FamilyDTO familyDTO = familyService.getFamilyInformation(createShoppingListRequest.getFamilyId()).getBasicInfo();
        if (familyDTO == null) {
            throw new ServiceException(
                    ResCode.FAMILY_NOT_FOUND.getMessage(),
                    ResCode.FAMILY_NOT_FOUND.getCode()
            );
        }

        // Kiểm tra nếu người dùng là chủ sở hữu của gia đình
        boolean isOwner = familyService.verifyOwner(
                familyDTO.getId(),
                owner.getId()
        );
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_OWNER_OF_FAMILY.getMessage(),
                    ResCode.NOT_OWNER_OF_FAMILY.getCode()
            );
        }

        // Tạo đối tượng ShoppingListDTO từ yêu cầu
        ShoppingListDTO shoppingListDTO = ShoppingListDTO.builder()
                .ownerDTO(owner)
                .familyDTO(familyDTO)
                .name(createShoppingListRequest.getName())
                .description(createShoppingListRequest.getDescription())
                .taskArrayList(new ArrayList<>())
                .createdAt(Date.valueOf(LocalDate.now()))
                .updatedAt(Date.valueOf(LocalDate.now()))
                .status(StatusConfig.AVAILABLE.getStatus())
                .build();

        // Lưu danh sách mua sắm đã tạo
        ShoppingListDTO createdShoppingList = shoppingListService.createShoppingList(shoppingListDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseFactory.createResponse(
                                createdShoppingList,
                                ResCode.CREATE_SHOPPING_LIST_SUCCESSFULLY.getMessage(),
                                ResCode.CREATE_SHOPPING_LIST_SUCCESSFULLY.getCode()
                        )
                );// Trả về danh sách đã tạo
    }

    // Lấy danh sách mua sắm theo ID
    @GetMapping("/get/{shoppingListId}")
    public ResponseEntity<BaseResponse<ShoppingListDTO>> getShoppingListById(@PathVariable long shoppingListId) {
        UserInfoConfig user = authenticationService.getCurrentUser();

        ShoppingListDTO shoppingListDTO = shoppingListService.getShoppingListById(user.getId(), shoppingListId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                shoppingListDTO,
                                ResCode.GET_SHOPPING_LIST_SUCCESSFULLY.getMessage(),
                                ResCode.GET_SHOPPING_LIST_SUCCESSFULLY.getCode()
                        )
                ); // Trả về danh sách mua sắm được tìm thấy
    }

    // Lấy tất cả danh sách mua sắm trong một khoảng từ chỉ số from đến to
    @GetMapping("/getAll")
    public ResponseEntity<BaseResponse<ArrayList<ShoppingListDTO>>> getAllShoppingList(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int to) {

        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();
        ArrayList<ShoppingListDTO> shoppingLists = shoppingListService.getAllShoppingList(
                userInfoConfig.getId(),
                from,
                to
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                shoppingLists,
                                ResCode.GET_SHOPPING_LISTS_SUCCESSFULLY.getMessage(),
                                ResCode.GET_SHOPPING_LISTS_SUCCESSFULLY.getCode()
                        )
                ); // Trả về danh sách mua sắm trong khoảng
    }

    // Cập nhật thông tin của một danh sách mua sắm
    @PostMapping("/update")
    public ResponseEntity<BaseResponse<ShoppingListDTO>> updateShoppingList(@RequestBody UpdateShoppingListRequest updateShoppingListRequest) {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();

        // Kiểm tra nếu danh sách mua sắm tồn tại
        ShoppingListDTO shoppingListDTO = shoppingListService.getShoppingListById(
                userInfoConfig.getId(),
                updateShoppingListRequest.getShoppingListId()
        );

        // Kiểm tra nếu người dùng là chủ sở hữu
        boolean isOwner = Objects.equals(userInfoConfig.getId(), shoppingListDTO.getOwnerDTO().getId());
        if (!isOwner) {
            throw new ServiceException(
                    ResCode.NOT_SHOPPING_LIST_OWNER.getMessage(),
                    ResCode.NOT_SHOPPING_LIST_OWNER.getCode()
            );
        }

        // Cập nhật thông tin danh sách mua sắm
        shoppingListDTO.setName(updateShoppingListRequest.getName());
        shoppingListDTO.setDescription(updateShoppingListRequest.getDescription());
        shoppingListDTO.setUpdatedAt(Date.valueOf(LocalDate.now()));

        // Lưu lại danh sách mua sắm đã cập nhật
        ShoppingListDTO updatedShoppingList = shoppingListService.updateShoppingList(shoppingListDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                updatedShoppingList,
                                ResCode.UPDATE_SHOPPING_LIST_SUCCESSFULLY.getMessage(),
                                ResCode.UPDATE_SHOPPING_LIST_SUCCESSFULLY.getCode()
                        )
                ); // Trả về danh sách mua sắm đã cập nhật
    }

    // Xoá danh sách mua sắm dựa vào ID
    @DeleteMapping("/delete/{shoppingListId}")
    public ResponseEntity<BaseResponse<ShoppingListDTO>> deleteShoppingList(@PathVariable long shoppingListId) {
        UserInfoConfig currentUser = authenticationService.getCurrentUser();

        ShoppingListDTO deletedShoppingList = shoppingListService.deleteShoppingList(currentUser.getId(), shoppingListId);

        if (deletedShoppingList == null) {
            throw new ServiceException(
                    ResCode.SHOPPING_LIST_NOT_DELETED.getMessage(),
                    ResCode.SHOPPING_LIST_NOT_DELETED.getCode()
            );
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ResponseFactory.createResponse(
                                deletedShoppingList,
                                ResCode.DELETE_SHOPPING_LIST_SUCCESSFULLY.getMessage(),
                                ResCode.DELETE_SHOPPING_LIST_SUCCESSFULLY.getCode()
                        )
                ); // Trả về danh sách mua sắm đã xoá
    }
}
