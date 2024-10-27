package com.grocery.app.config.externalConfig;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue fcmQueue() {
        return new Queue("fcm.queue", false);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("email.queue", false);
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
