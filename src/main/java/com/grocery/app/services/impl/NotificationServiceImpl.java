package com.grocery.app.services.impl;


import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.NotiContentDTO;
import com.grocery.app.dto.NotificationDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.entities.Device;
import com.grocery.app.entities.Notification;

import com.grocery.app.entities.User;
import com.grocery.app.entities.UserNoti;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.repositories.NotiRepo;
import com.grocery.app.repositories.UserNotiRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.NotificationService;
import com.grocery.app.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {


    @Autowired
    private NotiRepo notiRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserNotiRepo userNotiRepo;


    @Override
    public NotificationDTO saveNotification(Long sender, List<Long> recipients, NotiContentDTO notiContentDTO) {
        System.out.println("zooo");
        System.out.println(sender);

        User senderEntity=userRepo.findById(sender).orElseThrow(()-> new ServiceException(ResCode.USER_NOT_FOUND.getMessage(), ResCode.USER_NOT_FOUND.getCode()));
        System.out.println("zooo1");
        List<User> recipientsEntity=userRepo.findAllById(recipients);
        System.out.println("zooo2");
        if(recipientsEntity.size()!=recipients.size()){
            throw new ServiceException(ResCode.USER_NOT_FOUND.getMessage(), ResCode.USER_NOT_FOUND.getCode());
        }

        List<UserNoti> userNotis=recipientsEntity.stream().map(user -> UserNoti.builder().user(user).build()).toList();

        Notification notification=Notification.builder().title(notiContentDTO.getTitle())
                                                .message(notiContentDTO.getMessage())
                                                .type(notiContentDTO.getType())
                                                .externalData(notiContentDTO.getExternalData())
                                                .sender(senderEntity)
                                                .build();
        notification=notiRepo.save(notification);
        final Notification finalNotification = notification;
        userNotis.forEach(userNoti -> userNoti.setNotification(finalNotification));
        userNotiRepo.saveAll(userNotis);
        NotificationDTO notificationDTO=modelMapper.map(notification,NotificationDTO.class);
        notificationDTO.setDevices(
                recipientsEntity.stream()
                        .flatMap(user -> user.getDevices().stream().map(Device::getDeviceId))
                        .collect(Collectors.toSet())
        );


        return notificationDTO;
    }

    @Override
    public Long deleteNotification(Long id) {
        Notification notification=notiRepo.findById(id).orElseThrow(()-> new ServiceException(ResCode.NOTIFICATION_NOT_FOUND.getMessage(), ResCode.NOTIFICATION_NOT_FOUND.getCode()));
        notiRepo.delete(notification);
        return id;

    }

    public List<NotificationDTO> getNotifications(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ServiceException(ResCode.USER_NOT_FOUND.getMessage(), ResCode.USER_NOT_FOUND.getCode()));
        return user.getNotifications().stream()
                .map(notification -> modelMapper.map(notification.getNotification(), NotificationDTO.class))
                .toList();
    }
}
