package ggctech.whatsappai.event.producer;

import ggctech.whatsappai.config.async.rabbitmq.RabbitConstants;
import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConversationProducer {

    private final RabbitTemplate rabbitTemplate;

    public ConversationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(IncomingMessageDTO dto) {
        rabbitTemplate.convertAndSend(
                RabbitConstants.CONVERSATION_EXCHANGE,
                RabbitConstants.CONVERSATION_ROUTING_KEY,
                dto
        );
    }
}

