package com.grocery.app.controllers;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.payloads.users.UpdateUserDTO;
import com.grocery.app.services.AuthenticationService;
import com.grocery.app.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;


    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<UserDetailDTO>> getUser(){
        Long   id=authenticationService.getCurrentUser().getId();
        UserDetailDTO user=userService.getUser(id);
        BaseResponse<UserDetailDTO> response = ResponseFactory.createResponse(user, ResCode.GET_USER_SUCCESSFULLY.getMessage(),ResCode.GET_USER_SUCCESSFULLY.getCode());
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<BaseResponse<UserDetailDTO>> updateUser( @RequestBody @Valid UpdateUserDTO userDTO){
        Long id =authenticationService.getCurrentUser().getId();
        UserDetailDTO userDetailDTO=modelMapper.map(userDTO,UserDetailDTO.class);
        userDetailDTO.setId(id);
        UserDetailDTO user=userService.updateUser(userDetailDTO);
        BaseResponse<UserDetailDTO> response = ResponseFactory.createResponse(user, ResCode.UPDATE_USER_SUCCESSFULLY.getMessage(),ResCode.UPDATE_USER_SUCCESSFULLY.getCode());
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/profile/update-avatar")
    public ResponseEntity<BaseResponse<UserDetailDTO>> updateUserAvatar( @RequestBody  MultipartFile file){
        return null;
    }

    @PutMapping("/profile/lock")
    public ResponseEntity<BaseResponse<UserDetailDTO>> lockAccount(){
        Long id =authenticationService.getCurrentUser().getId();
        UserDetailDTO userDetailDTO=UserDetailDTO.builder().id(id).isActivated(false).build();
        UserDetailDTO user=userService.updateUser(userDetailDTO);
        BaseResponse<UserDetailDTO> response = ResponseFactory.createResponse(user, ResCode.LOCK_USER_SUCCESSFULLY.getMessage(),ResCode.LOCK_USER_SUCCESSFULLY.getCode());
        return new  ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/profile/update-invitation")
    public ResponseEntity<BaseResponse<Void>> updateUserInvitation(@RequestParam String code){
        return null;
    }

}
