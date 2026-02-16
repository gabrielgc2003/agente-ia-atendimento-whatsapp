package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.memory.ContextResult;
import ggctech.whatsappai.domain.memory.ConversationState;
import ggctech.whatsappai.domain.memory.ConversationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContextService {

    public ContextResult update(
            String userMessage,
            ConversationState state,
            ConversationSummary summary
    ) {

        // Estratégia simples inicial:
        // adiciona bullet simples e mantém estado

        summary.getBullets().add("Usuário disse: " + userMessage);

        if (summary.getBullets().size() > 10) {
            summary.getBullets().remove(0);
        }

        return new ContextResult(state, summary);
    }
}
