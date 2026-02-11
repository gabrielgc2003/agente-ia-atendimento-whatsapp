package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.domain.lead.ChatHistory;
import ggctech.whatsappai.enums.Sender;
import ggctech.whatsappai.service.ai.openapi.OpenAiService;
import ggctech.whatsappai.service.lead.ChatHistoryService;
import ggctech.whatsappai.service.lead.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationFinalizer {

    private final ConversationBufferService bufferService;
    private final ConversationLockService lockService;
    private final ChatHistoryService chatHistoryService;
    private final LeadService leadService;
    private final PromptBuilder promptBuilder;
    private final MessageService messageService;

    public void finalizeConversation(String conversationKey, IncomingMessageDTO dto) {

        String finalBuffer = bufferService.get(conversationKey);

        if (finalBuffer == null || finalBuffer.isBlank()) {
            return;
        }

        bufferService.delete(conversationKey);
        lockService.delete(conversationKey);

        // 1️⃣ Persistir USER primeiro
        chatHistoryService.saveMessage(
                dto.getInstanceId(),
                dto.getRemoteJid(),
                finalBuffer,
                Sender.USER
        );

        // 2️⃣ Buscar histórico estruturado
        List<Map<String, String>> history =
                chatHistoryService.lastMessages(
                        dto.getInstanceId(),
                        dto.getRemoteJid()
                );

        // 3️⃣ Base prompt + rotas
        String basePrompt = dto.getMessageConfig().getBasePrompt();
        String routes = leadService.getRoutes(
                dto.getInstanceId(),
                dto.getRemoteJid()
        );

        String systemPrompt = promptBuilder.build(basePrompt, routes);

        // 4️⃣ Montar lista final igual n8n
        List<Map<String, String>> messages = new ArrayList<>();

        messages.add(Map.of(
                "role", "system",
                "content", systemPrompt
        ));

        messages.addAll(history);

        // 5️⃣ Chamar OpenAI passando array real
        String response = messageService.sendMessageToAgent(messages);

        // 6️⃣ Persistir BOT
        chatHistoryService.saveMessage(
                dto.getInstanceId(),
                dto.getRemoteJid(),
                response,
                Sender.BOT
        );

        messageService.sendMessageToUser(response, dto);
    }
}


