package com.grocery.app.controllers;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.services.AuthenticationService;
import com.grocery.app.services.FileService;
import com.grocery.app.services.UserService;
import com.grocery.app.utils.ImagePathUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.grocery.app.config.constant.AppConstants.AVATAR_PATH;

@RestController
@RequestMapping("/api/upload")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;


    @PostMapping("/avatar")
    public ResponseEntity<BaseResponse<String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        Long userId=authenticationService.getCurrentUser().getId();

        String fileName= ImagePathUtil.setImagePath(AVATAR_PATH,file.getOriginalFilename(), System.currentTimeMillis());
        String url=fileService.uploadImage(fileName,file.getBytes());
        UserDetailDTO userDetailDTO=UserDetailDTO.builder().avatar(url).id(userId).build();
        userService.updateUser(userDetailDTO);
        BaseResponse<String> response= ResponseFactory.createResponse(url, ResCode.UPLOAD_AVATAR_SUCCESSFULLY.getMessage(),ResCode.UPLOAD_AVATAR_SUCCESSFULLY.getCode());
        return  new ResponseEntity<>(response, HttpStatus.OK);


    }
}
