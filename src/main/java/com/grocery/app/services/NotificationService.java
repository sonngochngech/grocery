package com.grocery.app.services;

import com.grocery.app.dto.NotiContentDTO;
import com.grocery.app.dto.NotificationDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.entities.Notification;

import java.util.List;

public interface NotificationService {

    NotificationDTO saveNotification(Long sender, List<Long> recipients, NotiContentDTO notiContentDTO);
    Long deleteNotification(Long id);
    List<NotificationDTO> getNotifications(Long userId);

}
