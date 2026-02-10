package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import ggctech.whatsappai.enums.SourceType;
import ggctech.whatsappai.service.ai.openapi.OpenAiService;
import ggctech.whatsappai.service.conversation.sender.EvolutionApiSenderService;
import ggctech.whatsappai.service.conversation.sender.MetaApiSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final OpenAiService openAiService;
    private final EvolutionApiSenderService evolutionApiService;
    private final MetaApiSenderService metaApiService;

    public String sendMessageToAgent(String message, String systemPrompt) {
        String response = openAiService.chat(message, systemPrompt);
        String responseFormatted = openAiService.formatter(response, "Prompt do Agente Formatador\n" +
                "Você é um agente formatador de mensagens para WhatsApp. Sua única função é dividir a mensagem recebida em múltiplas mensagens curtas, naturais e bem estruturadas. Siga as regras abaixo com rigor:\n" +
                "\n" +
                "1 - Sempre que possível, divida a mensagem original em 2 ou mais mensagens menores.\n" +
                "\n" +
                "2 - A cada nova mensagem gerada, aplique exatamente duas quebras de linha reais (\\n\\n) no final do bloco. Nunca ultrapasse esse número de quebras de linha.\n" +
                "\n" +
                "3 - Mantenha frases inteiras e compreensíveis em cada mensagem. Nunca divida frases no meio ou quebre a fluidez.\n" +
                "\n" +
                "4 - Se houver listas, bullets ou tópicos numerados, não quebre ou interrompa a estrutura da lista. Trate cada lista como uma única mensagem.\n" +
                "\n" +
                "NUNCA JAMAIS ALTERE O CONTEÚDO DA MENSAGEM!\n" +
                "5 - NUNCA gere ou altere o conteúdo da mensagem original. Seu trabalho é apenas dividir e formatar a mensagem recebida para nós as enviarmos de forma natural no WhatsApp, NUNCA alterere o conteúdo original da mensagem recebida.");
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
