package com.grocery.app.unitTests.service;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationServiceTests {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        UserInfoConfig userInfoConfig = new UserInfoConfig(
                1L, "test","test",null, Boolean.TRUE,null
        );
        Authentication auth= new UsernamePasswordAuthenticationToken(userInfoConfig,null,null);

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testGetAuthenticatedUser() {
        UserInfoConfig userInfoConfig = authenticationService.getCurrentUser();
        assert userInfoConfig.getId().equals(1L);
        assert userInfoConfig.getUsername().equals("test");
    }

}
