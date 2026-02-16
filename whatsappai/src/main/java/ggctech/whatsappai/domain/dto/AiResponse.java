package ggctech.whatsappai.domain.dto;

import ggctech.whatsappai.domain.memory.ConversationState;
import ggctech.whatsappai.domain.memory.ConversationSummary;
import lombok.Data;

import java.util.List;

@Data
public class AiResponse {

    private String response;

    private ConversationState updatedState;

    private ConversationSummary updatedSummary;

    private List<AiAction> actions;
}
