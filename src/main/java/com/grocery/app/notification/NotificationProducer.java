package com.grocery.app.notification;

import com.grocery.app.dto.NotiDTO;

public interface NotificationProducer {

    public void sendMessage(NotiDTO notiDTO);



}
