package com.grocery.app.integrationTests.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.entities.Device;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.security.JWTUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
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
import java.util.Set;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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




    @BeforeAll
    public void setup(){
        // Given
        this.user=addUser("test");

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
    protected HttpHeaders getFormDataHeader(){
        HttpHeaders headers = new HttpHeaders();
        String token= jwtUtil.generateToken("test", AppConstants.ACCESS_TOKEN_LIFETIME);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    protected  User addUser(String username){
        User user = User.builder()
                .firstName("test")
                .lastName("test")
                .username(username)
                .password(passwordEncoder.encode("123456789"))
                .email(username)
                .role(Role.builder().id(102L).name("USER").build())
                .devices(Set.of(Device.builder().deviceId(username).build(),Device.builder().deviceId(username+'1').build()))
                .build();
        user=userRepo.save(user);
        return user;

    }
}
