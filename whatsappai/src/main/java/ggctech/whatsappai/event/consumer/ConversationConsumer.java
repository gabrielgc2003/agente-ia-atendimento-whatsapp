package ggctech.whatsappai.event.consumer;

import ggctech.whatsappai.config.async.rabbitmq.RabbitConstants;
import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.service.conversation.ConversationProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConversationConsumer {

    private final ConversationProcessor processor;

    public ConversationConsumer(ConversationProcessor processor) {
        this.processor = processor;
    }

    @RabbitListener(queues = RabbitConstants.CONVERSATION_QUEUE)
    public void consume(IncomingMessageDTO message) {
        processor.process(message);
    }
}

