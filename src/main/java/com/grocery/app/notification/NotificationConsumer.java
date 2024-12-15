package com.grocery.app.notification;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

public interface NotificationConsumer {

    public void receiveEmailMessage(String message, Channel channel, Message amqpMessage) throws Exception;

    public void receiveFcmMessage(String message);
}
