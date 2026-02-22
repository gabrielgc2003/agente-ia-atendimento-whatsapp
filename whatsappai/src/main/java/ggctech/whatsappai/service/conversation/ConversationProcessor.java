package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.service.conversation.message.MessageProcessorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationProcessor {

    private final MessageProcessorRegistry messageProcessorRegistry;
    private final ConversationBufferService bufferService;
    private final ConversationLockService lockService;
    private final ConversationDelayScheduler scheduler;

    public void process(IncomingMessageDTO message) {

        String conversationKey =
                message.getInstanceId() + ":" + message.getRemoteJid();

        // 1️⃣ Sempre transforma em String
        String text = messageProcessorRegistry.process(message);
        if (text == null || text.isEmpty() || text.equals("")) {
            return;
        }        // 2️⃣ Concatena no buffer
        bufferService.append(conversationKey, text);

        // 3️⃣ Se lock existe, não faz mais nada
        if (lockService.exists(conversationKey)) {
            return;
        }

        // 4️⃣ Se não existe, cria lock
        if (lockService.create(conversationKey)) {
            // 5️⃣ Agenda finalização em 30s
            scheduler.schedule(conversationKey, message);
        }
    }
}
