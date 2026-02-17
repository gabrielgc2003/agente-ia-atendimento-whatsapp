package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.AiAction;
import ggctech.whatsappai.domain.dto.AiResponse;
import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.domain.memory.ConversationState;
import ggctech.whatsappai.domain.memory.ConversationSummary;
import ggctech.whatsappai.enums.Sender;
import ggctech.whatsappai.service.action.ActionExecutor;
import ggctech.whatsappai.service.action.CompanyActionService;
import ggctech.whatsappai.service.lead.ChatHistoryService;
import ggctech.whatsappai.service.lead.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationFinalizer {

    private final ConversationBufferService bufferService;
    private final ConversationLockService lockService;
    private final ChatHistoryService chatHistoryService;
    private final LeadService leadService;
    private final PromptBuilder promptBuilder;
    private final MessageService messageService;

    private final ConversationStateService stateService;
    private final ConversationSummaryService summaryService;
    private final ContextService contextService;
    private final ActionExecutor actionExecutor;

    private final CompanyActionService companyActionService;

    public void finalizeConversation(String conversationKey, IncomingMessageDTO dto) {

        String finalBuffer = bufferService.get(conversationKey);

        if (finalBuffer == null || finalBuffer.isBlank()) {
            return;
        }

        // Limpa buffer e lock
        bufferService.delete(conversationKey);
        lockService.delete(conversationKey);

        // 1️⃣ Persistir mensagem USER (apenas auditoria)
        chatHistoryService.saveMessage(
                dto.getInstanceId(),
                dto.getRemoteJid(),
                finalBuffer,
                Sender.USER
        );

        // 2️⃣ Buscar ou criar memória estruturada
        ConversationState state =
                stateService.getOrCreate(conversationKey);

        ConversationSummary summary =
                summaryService.getOrCreate(conversationKey);

        // 3️⃣ Atualizar memória com IA barata (context update)
        var contextResult =
                contextService.update(finalBuffer, state, summary);

        state = contextResult.getState();
        summary = contextResult.getSummary();

        stateService.save(conversationKey, state);
        summaryService.save(conversationKey, summary);

        // 4️⃣ Montar prompt estruturado
        String basePrompt = dto.getMessageConfig().getBasePrompt();

        String routes = companyActionService.getActionsForAi(dto.getInstanceId());

        String systemPrompt = promptBuilder.build(
                basePrompt,
                routes,
                state,
                summary
        );

        // 5️⃣ Chamar IA principal (resposta + ações + memória)
        AiResponse aiResponse =
                messageService.sendStructuredMessage(
                        systemPrompt,
                        finalBuffer
                );

        if (aiResponse == null) {
            return;
        }

        // 9️⃣ Persistir BOT
        chatHistoryService.saveMessage(
                dto.getInstanceId(),
                dto.getRemoteJid(),
                aiResponse.toString(),
                Sender.BOT
        );

        // 6️⃣ Atualizar estado se IA retornou
        if (aiResponse.getUpdatedState() != null) {
            stateService.save(conversationKey, aiResponse.getUpdatedState());
        }

        // 7️⃣ Atualizar resumo se IA retornou
        if (aiResponse.getUpdatedSummary() != null) {
            summaryService.save(conversationKey, aiResponse.getUpdatedSummary());
        }

        // 8️⃣ Executar ações estruturadas
        if (aiResponse.getActions() != null &&
                !aiResponse.getActions().isEmpty()) {
            List<AiAction> validActions = aiResponse.getActions()
                    .stream()
                    .filter(a -> a != null)
                    .filter(a -> a.getType() != null && !a.getType().isBlank())
                    .toList();

            actionExecutor.execute(validActions, dto);
        }

        // 🔟 Enviar resposta ao usuário
        messageService.sendMessageToUser(
                aiResponse.getResponse(),
                dto
        );
    }
}
