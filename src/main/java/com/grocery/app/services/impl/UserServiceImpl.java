package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.config.constant.Language;
import com.grocery.app.entities.Device;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.exceptions.ResourceException;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.payloads.loginCredentials.DefaultCredentials;
import com.grocery.app.repositories.DeviceRepo;
import com.grocery.app.repositories.RoleRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.UserService;
import com.grocery.app.utils.Validate;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private DeviceRepo deviceRepo;

    @Autowired
    private ModelMapper modelMapper;




    @Override
    public UserDetailDTO registerUser(UserDetailDTO userDTO) {
            Validate(userDTO);
            User user = modelMapper.map(userDTO, User.class);
            User userExists = userRepo.findByUsername(user.getUsername()).orElse(null);
            if (userExists != null) {
                throw new ResourceException(ResourceException.EXISTED, Language.USER, Language.USERNAME, user.getUsername());
            }
            User registerUser = userRepo.save(user);
            return modelMapper.map(registerUser, UserDetailDTO.class);
    }

    @Override
    public UserDetailDTO loginUser(DefaultCredentials credentials) {
        User user = userRepo.findByUsername(credentials.getUsername()).orElse(null);
        if(user==null || !user.getPassword().equals(credentials.getPassword())){
            throw new ServiceException(ResCode.WRONG_LOGIN_CREDENTIAL.getCode(), ResCode.WRONG_LOGIN_CREDENTIAL.getMessage());
        }
        if(user.getIsActivated().equals(false)){
            throw new ServiceException(ResCode.INACTIVATED_ACCOUNT.getCode(), ResCode.INACTIVATED_ACCOUNT.getMessage());
        }
        Device device = modelMapper.map(credentials.getDevice(), Device.class);

        if(deviceRepo.findByDeviceId(device.getDeviceId()).isEmpty()){
            user.addDevice(device);
            user= userRepo.save(user);
        }

        return modelMapper.map(user, UserDetailDTO.class);
    }

    private void  Validate(UserDetailDTO userDTO) throws  ServiceException{
        Validate.stateNot(userDTO.getUsername().isEmpty(), ResCode.USERNAME_NOT_FOUND.getCode(), ResCode.USERNAME_NOT_FOUND.getMessage());
        Role userRole = roleRepo.findByName(userDTO.getRole().getName()).orElse(null);
        Validate.stateNot(userRole == null || !Objects.equals(userRole.getId(), userDTO.getRole().getId()),ResCode.ROLE_NOT_FOUND.getCode(), ResCode.ROLE_NOT_FOUND.getMessage());
    }


}
