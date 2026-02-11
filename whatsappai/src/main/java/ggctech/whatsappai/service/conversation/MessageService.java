package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.enums.SourceType;
import ggctech.whatsappai.service.ai.openapi.OpenAiService;
import ggctech.whatsappai.service.conversation.sender.EvolutionApiSenderService;
import ggctech.whatsappai.service.conversation.sender.MetaApiSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final OpenAiService openAiService;
    private final EvolutionApiSenderService evolutionApiService;
    private final MetaApiSenderService metaApiService;

    public String sendMessageToAgent(List<Map<String, String>> messages) {

        String response = openAiService.chat(messages);

        String responseFormatted = openAiService.formatter(
                response,
                """
                Prompt do Agente Formatador
                Você é um agente formatador de mensagens para WhatsApp.
                Sua única função é dividir a mensagem recebida em múltiplas mensagens curtas,
                naturais e bem estruturadas.
    
                Regras:
                1 - Divida em 2 ou mais mensagens quando possível.
                2 - Aplique exatamente duas quebras de linha reais (\\n\\n) ao final de cada bloco.
                3 - Nunca quebre frases no meio.
                4 - Nunca quebre listas.
                5 - NUNCA altere o conteúdo original.
                """
        );

        return responseFormatted;
    }

    public void sendMessageToUser(String message, IncomingMessageDTO incomingMessageDTO) {
        if(incomingMessageDTO.getSourceType() == SourceType.EVOLUTION_API) {
            evolutionApiService.sendMessage(message, incomingMessageDTO);
        }
        if (incomingMessageDTO.getSourceType()  == SourceType.META_API) {
            metaApiService.sendMessage(message, incomingMessageDTO);
        }

    }
}
