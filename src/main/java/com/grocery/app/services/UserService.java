package com.grocery.app.services;

import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.payloads.loginCredentials.DefaultCredentials;

public interface UserService {
    UserDetailDTO registerUser(UserDetailDTO userDTO);
    UserDetailDTO loginUser(DefaultCredentials credentials);
    UserDetailDTO getUser(Long id);
    UserDetailDTO updateUser(UserDetailDTO userDTO);
    UserDTO getUserById(Long userId);
    boolean verifyUser(Long userId);




}
