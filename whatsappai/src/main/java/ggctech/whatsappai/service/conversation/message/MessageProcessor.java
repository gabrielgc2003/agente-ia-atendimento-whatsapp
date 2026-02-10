package ggctech.whatsappai.service.conversation.message;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;

public interface MessageProcessor {
    String supports();

    String process(IncomingMessageDTO message);
}
