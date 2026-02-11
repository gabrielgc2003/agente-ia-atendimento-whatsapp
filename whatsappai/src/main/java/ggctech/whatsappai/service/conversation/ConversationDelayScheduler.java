package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConversationDelayScheduler {

    @Qualifier("taskScheduler")
    private final TaskScheduler scheduler;
    private final ConversationFinalizer finalizer;

    @Value("${config.finalize.timer}")
    private int finalizeSeconds;

    public void schedule(String conversationKey, IncomingMessageDTO incomingMessageDTO) {
        scheduler.schedule(
                () -> finalizer.finalizeConversation(conversationKey, incomingMessageDTO),
                Instant.now().plusSeconds(finalizeSeconds)
        );
    }
}

