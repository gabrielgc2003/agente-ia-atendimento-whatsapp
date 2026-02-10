package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.domain.lead.ChatHistory;
import ggctech.whatsappai.enums.Sender;
import ggctech.whatsappai.service.ai.openapi.OpenAiService;
import ggctech.whatsappai.service.lead.ChatHistoryService;
import ggctech.whatsappai.service.lead.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        // Limpeza de estado
        bufferService.delete(conversationKey);
        lockService.delete(conversationKey);

        // Busca memória
        String memory = chatHistoryService.lastMessages(
                dto.getInstanceId(),
                dto.getRemoteJid()
        );

        // Persistência de histórico USER
        chatHistoryService.saveMessage(
                dto.getInstanceId(),
                dto.getRemoteJid(),
                finalBuffer,
                Sender.USER
        );

        // Dados dinâmicos
        String routes = leadService.getRoutes(
                dto.getInstanceId(),
                dto.getRemoteJid()
        );

        String basePrompt = dto.getMessageConfig().getBasePrompt();

        // Prompt final
        String prompt = promptBuilder.build(
                basePrompt,
                routes,
                memory
        );

        // Chamada IA e espera o retorno para enviar a resposta
        String response = messageService.sendMessageToAgent(finalBuffer, prompt);

        // Persistência de histórico IA
        chatHistoryService.saveMessage(
                dto.getInstanceId(),
                dto.getRemoteJid(),
                response,
                Sender.BOT
        );

        messageService.sendMessageToUser(response, dto);


    }
}


