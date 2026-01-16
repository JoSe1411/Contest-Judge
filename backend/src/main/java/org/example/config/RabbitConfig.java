package org.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue submissionQueue() {
        return new Queue("submission.queue", true);
    }

    @Bean
    public DirectExchange submissionExchange() {
        return new DirectExchange("submission.exchange", true, false);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        Binding bind = BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("submission.routing.key");

        return bind;
    }
}
