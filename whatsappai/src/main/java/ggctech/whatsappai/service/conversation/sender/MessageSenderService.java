package ggctech.whatsappai.service.conversation.sender;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;

public interface MessageSenderService {
    void sendMessage(String message, IncomingMessageDTO incomingMessageDTO);
}
