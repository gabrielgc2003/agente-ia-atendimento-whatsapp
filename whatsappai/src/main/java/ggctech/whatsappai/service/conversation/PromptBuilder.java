package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.memory.ConversationState;
import ggctech.whatsappai.domain.memory.ConversationSummary;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class PromptBuilder {

    private final tools.jackson.databind.ObjectMapper objectMapper = new ObjectMapper();

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
        
        [FERRAMENTAS - DIFERENCIAÇÃO]
        Existem dois tipos de ferramentas:
        
        1. INTERNAL_MESSAGE
           → A informação deve ser enviada ao usuário na própria conversa.
           → A mensagem natural deve introduzir a informação.
        
        2. EXTERNAL_EXECUTION
           → A ação será executada pelo sistema.
           → Não incluir detalhes técnicos na mensagem.
           → Apenas informar que está sendo encaminhado.
        
        Se a ferramenta for EXTERNAL_EXECUTION,
        não escrever manualmente o link ou telefone.
        Utilizar a action correspondente.
        
        ==============================

        [ESTADO ATUAL DA CONVERSA]
        stage: %s
        fields: %s

        ==============================

        [RESUMO ESTRUTURADO]
        %s

        ==============================

        [GESTÃO DE ESTÁGIO - OBRIGATÓRIO]

        Você deve definir dinamicamente o campo "stage"
        representando claramente o momento atual da conversa.

        Regras obrigatórias:

        - O stage deve ser curto.
        - Usar snake_case.
        - Não conter espaços.
        - Representar intenção ou fase real da conversa.
        - Nunca usar nomes genéricos como "etapa1" ou "fase_final".
        - Nunca retornar ao stage anterior,
          exceto se o usuário mudar explicitamente a intenção.
        - O stage deve refletir progressão real.
        - Se a conversa evoluir semanticamente,
          o stage deve evoluir também.

        Exemplos válidos:
        - coletando_idade
        - identificando_tipo_consulta
        - tratando_objeção_valor
        - redirecionando_para_convênio
        - aguardando_decisao
        - conversa_encerrada

        ==============================
        [DETECÇÃO DE NOVA CONVERSA]
        
        Se o usuário:
        - Iniciar com nova saudação após longo intervalo
        - Mudar completamente o assunto
        - Iniciar novo pedido diferente do anterior
        - Retomar após conversa encerrada
        
        Você pode alterar o stage para: start
        
        Se fizer isso:
        - Limpe fields irrelevantes
        - Reinicie coleta de informações
        - Atualize o resumo coerentemente
        
        ==============================
        [ANTI-REPETIÇÃO E PROGRESSÃO]

        - Nunca repetir semanticamente a última resposta enviada.
        - Nunca reiniciar a conversa se já houver contexto.
        - Nunca repetir explicações já dadas.
        - Sempre mover a conversa um passo adiante.
        - Se o usuário já confirmou algo, não reconfirmar.
        - Evitar duplicação de informação.
        
        ==============================

        INSTRUÇÕES OBRIGATÓRIAS:

        1. Responda EXCLUSIVAMENTE em JSON válido.
        2. Nunca inclua texto fora do JSON.
        3. Nunca use markdown.
        4. Nunca explique o JSON.
        5. Nunca quebre o formato.
        6. Nunca retorne campos null.

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
              "executionType": "INTERNAL_MESSAGE ou EXTERNAL_EXECUTION"
              "payload": { ... }
            }
          ]
        }

        Regras adicionais:

        - Se não houver ação necessária, retornar obrigatoriamente:
          "actions": []
        - Nunca retornar array com item vazio.
        - Nunca retornar action sem action_id.
        - Nunca retornar action com payload vazio se a ação exigir dados.
        - Atualizar o estado apenas se houver mudança real.
        - Atualizar o resumo apenas se houver informação nova relevante.
        - O campo response deve ser natural e conversacional (formato WhatsApp).
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
