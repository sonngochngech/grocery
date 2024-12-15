package com.grocery.app.config.externalConfig;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true); // Ensures the admin starts automatically
        return rabbitAdmin;
    }

    @Bean
    public Queue fcmQueue() {
        return new Queue("fcm.queue", true);
    }

    @Bean
    public Queue emailQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 5000); // Match the existing queue settings
        return new Queue("email.queue", true, false, false, args);
    }


    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("app.exchange");
    }

    @Bean
    public Binding fcmBinding(Queue fcmQueue, DirectExchange exchange) {
        return BindingBuilder.bind(fcmQueue).to(exchange).with("fcm");
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange exchange) {
        return BindingBuilder.bind(emailQueue).to(exchange).with("email");
    }
}
