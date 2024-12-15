package com.grocery.app.notification.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.notification.NotificationConsumer;
import com.grocery.app.dto.MailDetailsDTO;
import com.grocery.app.services.MailService;
import com.rabbitmq.client.Channel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class NotificationConsumerImpl implements NotificationConsumer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @RabbitListener(queues = "email.queue",ackMode = "MANUAL")
    public void receiveEmailMessage(String message, Channel channel, Message amqpMessage) throws Exception {
        log.info("Email message received: {}", message);
        try{
            MailDetailsDTO mailDetailsDTO = objectMapper.readValue(message, MailDetailsDTO.class);
            mailService.sendEmail(mailDetailsDTO);
            channel.basicAck(amqpMessage.getMessageProperties().getDeliveryTag(), false);
        }catch (JsonProcessingException e){
            System.out.println("Error parsing email message: " + e.getMessage());
            channel.basicAck(amqpMessage.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error acknowledging message: " + e.getMessage());
        }

    }

    @Override
    @RabbitListener(queues = "fcm.queue")
    public void receiveFcmMessage(String message) {
        System.out.println("Fcm message received: " + message);
    }
}
