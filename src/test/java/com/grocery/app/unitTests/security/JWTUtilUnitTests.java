package com.grocery.app.unitTests.security;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.security.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

public class JWTUtilUnitTests {


    private JWTUtil jwtUtil;

    private String token;

    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JWTUtil();
        token = jwtUtil.generateToken("test", 1000L);
        userDetails=new UserInfoConfig(User.builder().username("test").role(Role.builder().name("USER").build()).build());
    }

    @Test
    public void testisExpired() {
        boolean value = jwtUtil.isExpired(this.token);
        assert value==false;
    }

    @Test
    public void testisTokenValid() {
        boolean value = jwtUtil.isTokenValid(this.token, userDetails);
        assert value==true;
    }

    @Test void getUsername() {
        String username = jwtUtil.getUsername(this.token);
        assert username.equals("test");
    }
}
