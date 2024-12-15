package com.grocery.app.config.externalConfig;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue fcmQueue() {
        return new Queue("fcm.queue", true);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable("email.queue")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "email")
                .withArgument("x-message-ttl", 5000)
                .build();
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
