package ggctech.whatsappai.service.conversation.message;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import org.springframework.stereotype.Component;

@Component
public class ConversationMessageProcessor implements MessageProcessor {

    @Override
    public String supports() {
        return "conversation";
    }

    @Override
    public String process(IncomingMessageDTO message) {
        return message.getMessageContent();
    }
}

