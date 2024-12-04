package com.grocery.app.integrationTests.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ServicesTestSupport {

    @Autowired
    protected UserRepo userRepo;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Autowired
    protected JWTUtil jwtUtil;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected ObjectMapper objectMapper;

    protected User user;




    @BeforeEach
    public void setup(){
        // Given
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .username("test")
                .password(passwordEncoder.encode("123456789"))
                .email("")
                .role(Role.builder().id(102L).name("USER").build())
                .devices(null)
                .build();
        // When
        userRepo.save(user);
        this.user=userRepo.findById(1L).orElse(null);

    }


    protected HttpHeaders getHeader(){
        HttpHeaders headers = new HttpHeaders();
        String token= jwtUtil.generateToken("test", AppConstants.ACCESS_TOKEN_LIFETIME);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }
    protected  HttpHeaders unAuthHeader(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
