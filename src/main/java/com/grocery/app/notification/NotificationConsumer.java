package com.grocery.app.notification;

public interface NotificationConsumer {

    public void receiveEmailMessage(String message);

    public void receiveFcmMessage(String message);
}
