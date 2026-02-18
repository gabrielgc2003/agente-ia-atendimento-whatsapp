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
        
        ==================================================
        [FERRAMENTAS DISPONÍVEIS]
        %s
        ==================================================
        
        IMPORTANTE SOBRE USO DE FERRAMENTAS:
        
        1) Quando a ação for do tipo INTERNAL_MESSAGE:
           - Os dados estruturados do payload DEVEM aparecer integralmente no campo "response".
           - Nunca omitir telefones, links ou endereços.
           - Nunca resumir ou alterar valores.
           - Nunca dizer apenas "vou te enviar".
           - A resposta já deve conter todas as informações.
        
        2) Quando a ação for do tipo EXTERNAL_EXECUTION:
           - Não incluir detalhes técnicos no response.
           - Apenas informar de forma natural que está sendo encaminhado.
           - O sistema executará a ação.
        
        3) Sempre utilizar ação quando houver:
           - Envio de telefone
           - Envio de link
           - Envio de endereço
           - Redirecionamento formal
           - Encaminhamento externo
        
        Se não houver necessidade de ação:
        Retornar obrigatoriamente:
        "actions": []
        
        ==================================================
        
        [ESTADO ATUAL DA CONVERSA]
        stage: %s
        fields: %s
        
        ==================================================
        
        [RESUMO ESTRUTURADO]
        %s
        
        ==================================================
        
        [GESTÃO DE ESTÁGIO — OBRIGATÓRIO]
        
        Você deve definir dinamicamente o campo "stage".
        
        Regras:
        - Usar snake_case.
        - Curto e objetivo.
        - Representar momento real da conversa.
        - Nunca usar nomes genéricos.
        - Nunca retroceder estágio sem mudança explícita do usuário.
        - Se houver nova intenção clara, atualizar o stage.
        
        Exemplos válidos:
        - start
        - coletando_idade
        - identificando_tipo_consulta
        - apresentando_valor
        - tratando_objeção_valor
        - redirecionando_para_convênio
        - fornecendo_contatos
        - aguardando_decisao
        - conversa_encerrada
        
        ==================================================
        
        [DETECÇÃO DE NOVA CONVERSA]
        
        Se o usuário:
        - Iniciar com nova saudação após longo intervalo
        - Mudar completamente o assunto
        - Retomar após conversa encerrada
        
        Você pode definir:
        stage = start
        
        Se fizer isso:
        - Limpar apenas campos irrelevantes
        - Preservar nome_responsavel se ainda fizer sentido
        - Reiniciar coleta de informações
        - Atualizar o resumo coerentemente
        
        ==================================================
        
        [REGRA DE PERSISTÊNCIA DE DADOS]
        
        - Nunca apagar campos já existentes.
        - Nunca remover nome_responsavel, nome_crianca ou idade_crianca se já estiverem preenchidos.
        - Sempre preservar dados anteriores.
        - Apenas adicionar ou atualizar se houver nova informação.
        - Nunca retornar fields vazio se já houver dados.
        
        ==================================================
        
        [ANTI-REPETIÇÃO E PROGRESSÃO]
        
        - Nunca repetir semanticamente a última resposta.
        - Nunca reiniciar explicações já dadas.
        - Nunca reconfirmar algo já confirmado.
        - Sempre mover a conversa um passo adiante.
        - Nunca duplicar envio de contatos já enviados.
        
        ==================================================
        
        INSTRUÇÕES OBRIGATÓRIAS DE FORMATO:
        
        1. Responda EXCLUSIVAMENTE em JSON válido.
        2. Nunca inclua texto fora do JSON.
        3. Nunca use markdown.
        4. Nunca explique o JSON.
        5. Nunca retorne campos null.
        6. Nunca retorne action sem action_id.
        7. Nunca retorne action com payload vazio se for necessária.
        
        Formato obrigatório:
        
        {
          "response": "mensagem natural no formato WhatsApp",
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
              "executionType": "INTERNAL_MESSAGE ou EXTERNAL_EXECUTION",
              "payload": { ... }
            }
          ]
        }
        
        ==================================================
        
        O campo "response" deve ser:
        - Natural
        - Conversacional
        - Humano
        - Fluido
        - Nunca robótico
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
