package com.grocery.app.services.impl;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.services.AuthenticationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public UserInfoConfig getCurrentUser() {
        return (UserInfoConfig) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
