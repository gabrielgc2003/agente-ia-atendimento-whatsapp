package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.memory.ConversationState;
import ggctech.whatsappai.domain.memory.ConversationSummary;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class PromptBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String build(
            String basePrompt,
            String routes,
            ConversationState state,
            ConversationSummary summary
    ) {

        String stateJson = toJson(state.getFields());
        String summaryJson = toJson(summary.getBullets());

        return """
        %s

        ==============================
        [FERRAMENTAS DISPONÍVEIS]
        %s
        ==============================

        [ESTADO ATUAL DA CONVERSA]
        stage: %s
        fields: %s

        ==============================

        [RESUMO ESTRUTURADO]
        %s

        ==============================

        INSTRUÇÕES OBRIGATÓRIAS:

        1. Responda EXCLUSIVAMENTE em JSON válido.
        2. Nunca inclua texto fora do JSON.
        3. Nunca use markdown.
        4. Nunca explique o JSON.
        5. Nunca quebre o formato.

        Formato obrigatório:

        {
          "response": "mensagem natural para o usuário",
          "updatedState": {
            "stage": "...",
            "fields": { ... }
          },
          "updatedSummary": {
            "bullets": [ ... ]
          },
          "actions": [
            {
              "action_id": "UUID",
              "payload": { ... }
            }
          ]
        }

        Regras:
        - Se não houver ação necessária, retornar obrigatoriamente:
          "actions": []
        - Nunca retorne objetos com type null.
        - Nunca retorne array com item vazio.
        - Nunca retorne action sem type.
        - Atualize o estado apenas se houver mudança real
        - Atualize o resumo apenas se houver informação nova
        """.formatted(
                basePrompt,
                routes,
                state.getStage(),
                stateJson,
                summaryJson
        );
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "{}";
        }
    }
}

