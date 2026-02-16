package ggctech.whatsappai.domain.memory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContextResult {

    private ConversationState state;
    private ConversationSummary summary;
}
