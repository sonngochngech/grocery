package com.grocery.app.integrationTests;

import com.grocery.app.AppApplication;
import com.grocery.app.dto.DeviceDTO;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.entities.Role;
import com.grocery.app.integrationTests.base.AbstractIntegrationTest;
import com.grocery.app.payloads.responses.AuthResponse;
import com.grocery.app.security.JWTUtil;
import com.grocery.app.services.UserService;
import com.grocery.app.utils.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;


@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableConfigurationProperties
@ActiveProfiles("test")
public class AuthControllerIntegrationTest  extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RedisService redisService;


   @Test
   @DisplayName("Happy Path Test: Register User")
    void givenValidUserDTO_whenRegisterUser_thenSaveUserAndReturnUserDTO() throws Exception {
       // Given
       DeviceDTO deviceDTO=DeviceDTO.builder()
               .deviceId("test")
               .deviceType("test")
               .build();
       UserDetailDTO userDetailDTO=UserDetailDTO.builder()
               .firstName("test")
                .lastName("test")
               .username("test")
                .password("123456789")
                .email("")
               .role(Role.builder().id(102L).name("USER").build())
               .devices(Set.of(deviceDTO))
               .build();
       // When
       AuthResponse authResponse=performPostRequestExpectedSuccess("/api/auth/register?verifyCode=123456",userDetailDTO, AuthResponse.class);
       System.out.println(authResponse);



        // Then
    }


}
