package com.grocery.app.notification;

import com.grocery.app.dto.NotiDTO;
import com.grocery.app.dto.NotificationDTO;

public interface NotificationFactory {
    public NotiDTO VerifyCodeNoti(String email, String code);

    public NotiDTO sendExpriedNoti(String email, String content);
    public NotiDTO sendTaskAlert(String email, String content);

    public NotiDTO sendInvitationNoti(String email, String content);

    public NotiDTO sendNotification(NotificationDTO notificationDTO);
}
