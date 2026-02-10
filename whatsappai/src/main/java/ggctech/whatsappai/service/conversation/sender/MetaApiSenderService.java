package ggctech.whatsappai.service.conversation.sender;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import org.springframework.stereotype.Service;

@Service
public class MetaApiSenderService implements MessageSenderService{
    @Override
    public void sendMessage(String message, IncomingMessageDTO incomingMessageDTO) {
        String i ="";
    }
}
