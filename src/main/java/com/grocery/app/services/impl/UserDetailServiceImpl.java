package com.grocery.app.services.impl;

import com.grocery.app.config.UserInfoConfig;
import com.grocery.app.entities.User;
import com.grocery.app.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
   private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user=userRepo.findByUsername(username);
        return user.map(UserInfoConfig::new).orElseThrow(()->new UsernameNotFoundException("User not found"));

    }
}
