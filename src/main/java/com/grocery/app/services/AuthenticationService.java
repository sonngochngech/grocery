package com.grocery.app.services;

import com.grocery.app.config.UserInfoConfig;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {

    public UserInfoConfig getCurrentUser();
}
