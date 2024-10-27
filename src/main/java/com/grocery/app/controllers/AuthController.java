package com.grocery.app.controllers;


import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.exceptions.ResourceException;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.payloads.loginCredentials.DefaultCredentials;
import com.grocery.app.payloads.loginCredentials.SocialCredentials;
import com.grocery.app.payloads.responses.AuthResponse;
import com.grocery.app.security.JWTUtil;
import com.grocery.app.services.UserService;
import com.grocery.app.utils.RedisService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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



    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@RequestBody  @Valid UserDetailDTO userDTO , @RequestParam String verifyCode) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserDetailDTO user = userService.registerUser(userDTO);
        log.info("User registered successfully");
        String accessToken = jwtUtil.generateToken(user.getUsername(), AppConstants.ACCESS_TOKEN_LIFETIME);
        String userHash=jwtUtil.generateToken(user.getUsername(),AppConstants.REFRESH_TOKEN_LIFETIME);
        log.info("Access token generated successfully:{}",userHash);
        String refreshToken = UUID.randomUUID().toString();
        redisService.saveData(refreshToken,userHash);
        AuthResponse authResponse = new AuthResponse(
                ResCode.REGISTER_SUCCESSFULLY.getMessage(),
                ResCode.REGISTER_SUCCESSFULLY.getCode(),
                user,
                accessToken,
                refreshToken);
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("default-login")
    public ResponseEntity<AuthResponse> defaultLogin(@RequestBody @Valid DefaultCredentials loginCredentials){
        loginCredentials.setPassword(passwordEncoder.encode(loginCredentials.getPassword()));
        UserDetailDTO user=userService.loginUser(loginCredentials);
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


    @PostMapping("verify-code")
    public ResponseEntity<AuthResponse> verifyCode(@RequestBody String token){
        log.info("Verifying code: "+token);
        Object data;
        try{
             data = redisService.getData(token);

             if(jwtUtil.isExpired((String) data)){
                 throw new ResourceException();
             }
             log.info("Code verified successfully");
             String access_token=jwtUtil.generateToken(jwtUtil.getUsername((String) data),AppConstants.ACCESS_TOKEN_LIFETIME);
             return new ResponseEntity<>(new AuthResponse(ResCode.GET_ACCESS_TOKEN_SUCCESSFULLY.getMessage(),ResCode.GET_ACCESS_TOKEN_SUCCESSFULLY.getCode(),null,access_token,null),HttpStatus.OK);
        }catch (ResourceException e){
            throw new ServiceException(ResCode.INVALID_REFRESH_TOKEN.getCode(),ResCode.INVALID_REFRESH_TOKEN.getMessage());
        }
    }







    
}
