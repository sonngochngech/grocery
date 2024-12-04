package com.grocery.app.notification;

import com.grocery.app.dto.NotiDTO;

public interface NotificationFactory {
    public NotiDTO VerifyCodeNoti(String email, String code);
}
