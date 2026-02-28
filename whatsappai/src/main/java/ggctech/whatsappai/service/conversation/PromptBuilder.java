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
           - A resposta já deve conter todas as informações
           - Caso sinalize a intenção do envio, deve ser enviado na mesma resposta.
        
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
        [GESTÃO DE ESTÁGIO — CONTROLE RÍGIDO]        
        Você deve evoluir o campo "stage" progressivamente.
        
        Regras obrigatórias:
        
        - Usar snake_case.
        - Curto e objetivo.
        - Representar momento real da conversa.
        - Nunca usar nomes genéricos.
        - Nunca retroceder estágio.
        - Nunca redefinir para "start" automaticamente.
        - Nunca redefinir para "start" se já houver fields preenchidos.
        - Nunca redefinir para "start" por causa de emoji, saudação ou mensagem curta.
        - Nunca apagar dados já coletados.
        - Se houver nova intenção clara, atualizar para novo estágio coerente.
        - Se o usuário apenas responder algo curto (ex: nome), apenas avançar coleta.
        - Nunca reiniciar fluxo já iniciado.
        
        O stage só pode evoluir.
        Nunca retroceder.
        Nunca resetar sem regra explícita.
        
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
        [DETECÇÃO DE NOVA CONVERSA — REGRA RESTRITIVA]
           
        Você só pode definir stage = start se TODAS as condições abaixo forem verdadeiras:
        
        1) O stage atual for exatamente "conversa_encerrada"
        E
        2) O usuário iniciar explicitamente uma nova conversa
        E
        3) Não houver intenção ativa pendente
        E
        4) Não houver fluxo em andamento
        
        Nunca redefinir para start se:
        - Já houver nome_responsavel preenchido
        - Já houver nome_crianca ou idade_crianca preenchidos
        - A conversa estiver ativa
        - O usuário enviar apenas emoji
        - O usuário enviar apenas saudação
        - O usuário enviar mensagem curta
        
        Se houver dúvida, manter stage atual.
        
        ==================================================
        
        [REGRA DE PERSISTÊNCIA DE DADOS — CRÍTICO]
        
        - Nunca apagar campos já existentes.
        - Nunca remover nome_responsavel, nome_crianca ou idade_crianca se já estiverem preenchidos.
        - Nunca retornar fields vazio se já houver dados.
        - Nunca sobrescrever campo preenchido com null ou vazio.
        - Apenas adicionar novos campos ou atualizar se houver nova informação explícita.
        - Nunca inventar informações.
        - Em caso de dúvida, pedir confirmação.
        
        ==================================================
                
        [ANTI-REPETIÇÃO E PROGRESSÃO]
        
        - Nunca repetir semanticamente a última resposta.
        - Nunca repetir apresentação se já realizada.
        - Nunca reiniciar explicações já dadas.
        - Nunca reconfirmar algo já confirmado.
        - Nunca reenviar contatos já enviados.
        - Nunca repetir link já enviado.
        - Sempre mover a conversa um passo adiante.
        
        Se o usuário enviar:
        - Apenas emoji
        - Apenas saudação
        - Apenas confirmação curta (sim, ok, 👍)
        
        Considerar como continuação da conversa.
        Nunca reiniciar fluxo por isso.
        Nunca redefinir stage por isso.
        
        ==================================================
        [PROTEÇÃO CONTRA LOOP]
                
        Se perceber que a resposta seria muito semelhante à anterior,
        reestruture a mensagem para avançar o fluxo.
        
        Nunca entrar em loop.
        Nunca repetir padrão de resposta.
        Sempre evoluir a conversa.
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
