package com.grocery.app.integrationTests.admin;

import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class AdminServicesTestSupport extends ServicesTestSupport {
    protected User admin;

    @BeforeEach
    public void setup() {
        // Given
        this.admin = User.builder()
                .firstName("admin")
                .lastName("admin")
                .username("admin")
                .password(passwordEncoder.encode("123456789"))
                .email("")
                .role(Role.builder().id(101L).name("ADMIN").build())
                .devices(null)
                .build();
        // When
        userRepo.save(admin);
        this.admin = userRepo.findById(2L).orElse(null);
    }

    protected HttpHeaders adminHeader() {
        HttpHeaders headers = new HttpHeaders();
        String token= jwtUtil.generateToken("admin", AppConstants.ACCESS_TOKEN_LIFETIME);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }
}
