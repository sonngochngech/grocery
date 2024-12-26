package com.grocery.app.controllers;


import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.exceptions.ControllerException;
import com.grocery.app.exceptions.ResourceException;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.notification.NotificationConsumer;
import com.grocery.app.notification.NotificationFactory;
import com.grocery.app.notification.NotificationProducer;
import com.grocery.app.payloads.loginCredentials.DefaultCredentials;
import com.grocery.app.payloads.loginCredentials.SocialCredentials;
import com.grocery.app.payloads.responses.AuthResponse;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ResponseFactory;
import com.grocery.app.payloads.users.GetVerifyCodeDTO;
import com.grocery.app.payloads.users.RegisterUserDTO;
import com.grocery.app.payloads.users.VerifyCodeDTO;
import com.grocery.app.security.JWTUtil;
import com.grocery.app.services.UserService;
import com.grocery.app.utils.RedisService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NotificationFactory notificationFactory;

    @Autowired
    private NotificationProducer notificationProducer;




    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@RequestBody  @Valid RegisterUserDTO userDTO ) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserDetailDTO userDetailDTO=modelMapper.map(userDTO,UserDetailDTO.class);

        UserDetailDTO user = userService.registerUser(userDetailDTO);
        String accessToken = jwtUtil.generateToken(user.getUsername(), AppConstants.ACCESS_TOKEN_LIFETIME);
        String userHash=jwtUtil.generateToken(user.getUsername(),AppConstants.REFRESH_TOKEN_LIFETIME);
        String refreshToken = UUID.randomUUID().toString();

        redisService.saveData(refreshToken,userHash);
        AuthResponse authResponse = new AuthResponse(
                ResCode.REGISTER_SUCCESSFULLY.getMessage(),
                ResCode.REGISTER_SUCCESSFULLY.getCode(),
                user,
                accessToken,
                refreshToken);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("default-login")
    public ResponseEntity<AuthResponse> defaultLogin(@RequestBody @Valid DefaultCredentials loginCredentials){
        log.info("Login request received: {}",loginCredentials);
        UserDetailDTO user=userService.loginUser(loginCredentials);
        log.info("User logged in successfully:{} ",user);
        String accessToken = jwtUtil.generateToken(user.getUsername(),AppConstants.ACCESS_TOKEN_LIFETIME);
        String userHash=jwtUtil.generateToken(user.getUsername(),AppConstants.REFRESH_TOKEN_LIFETIME);

        String refreshToken = UUID.randomUUID().toString();
        redisService.saveData(refreshToken,userHash);
        AuthResponse authResponse = new AuthResponse(
                ResCode.LOGIN_SUCCESSFULLY.getMessage(),
                ResCode.LOGIN_SUCCESSFULLY.getCode(),
                user,
                accessToken,
                refreshToken);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }

    @PostMapping("social-login")
    public String socialLogin(@RequestBody  @Valid SocialCredentials socialCredentials){

        return "Social Login";
    }


    @PostMapping("get-access-token")
    public ResponseEntity<AuthResponse> getAccessToken(@RequestBody String token){
        Object data;
        try{
             data = redisService.getData(token);

             if(jwtUtil.isExpired((String) data)){
                 throw new ResourceException();
             }
             String access_token=jwtUtil.generateToken(jwtUtil.getUsername((String) data),AppConstants.ACCESS_TOKEN_LIFETIME);
             return new ResponseEntity<>(new AuthResponse(ResCode.GET_ACCESS_TOKEN_SUCCESSFULLY.getMessage(),ResCode.GET_ACCESS_TOKEN_SUCCESSFULLY.getCode(),null,access_token,null),HttpStatus.OK);
        }catch (ResourceException e){
            throw new ServiceException(ResCode.INVALID_REFRESH_TOKEN.getCode(),ResCode.INVALID_REFRESH_TOKEN.getMessage());
        }
    }


    @PostMapping("get-verify-code")
    public ResponseEntity<BaseResponse<String>> getVerifyingCode(@Valid @RequestBody GetVerifyCodeDTO dto){
        String email=dto.getEmail();
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
        redisService.saveData(email,code);
        notificationProducer.sendMessage(notificationFactory.VerifyCodeNoti(email,code));
        BaseResponse<String> res=ResponseFactory.createResponse(code,ResCode.GET_VERIFY_CODE_SUCCESSFULLY.getMessage(),ResCode.GET_VERIFY_CODE_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PostMapping("verify-code")
    public ResponseEntity<BaseResponse<String>> verifyingCode( @Valid @RequestBody VerifyCodeDTO verifyCodeDTO){
        String email=verifyCodeDTO.getEmail();
        String code=verifyCodeDTO.getCode();
        Object storedCode=redisService.getData(email);
        if(storedCode==null){
            throw new ServiceException(ResCode.INVALID_VERIFY_CODE.getCode(),ResCode.INVALID_VERIFY_CODE.getMessage());
        }
        if(!((String) storedCode).equals(code)){
            throw new ControllerException(ResCode.WRONG_VERIFY_CODE.getCode(),ResCode.WRONG_VERIFY_CODE.getMessage());
        };
        redisService.removeData(email);
        BaseResponse<String> res=ResponseFactory.createResponse(email,ResCode.VERIFY_CODE_SUCCESSFULLY.getMessage(),ResCode.VERIFY_CODE_SUCCESSFULLY.getCode());
        return new ResponseEntity<>(res,HttpStatus.OK);

    }










    
}
