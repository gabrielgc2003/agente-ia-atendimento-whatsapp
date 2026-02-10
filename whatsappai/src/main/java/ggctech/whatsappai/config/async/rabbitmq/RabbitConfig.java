package ggctech.whatsappai.config.async.rabbitmq;

import org.springframework.amqp.core.*;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange conversationExchange() {
        return new DirectExchange(RabbitConstants.CONVERSATION_EXCHANGE);
    }

    @Bean
    public Queue conversationQueue() {
        return QueueBuilder.durable(RabbitConstants.CONVERSATION_QUEUE).build();
    }

    @Bean
    public Binding conversationBinding() {
        return BindingBuilder
                .bind(conversationQueue())
                .to(conversationExchange())
                .with(RabbitConstants.CONVERSATION_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}

