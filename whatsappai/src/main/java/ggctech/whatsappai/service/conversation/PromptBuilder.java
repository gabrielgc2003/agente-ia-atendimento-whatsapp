package ggctech.whatsappai.service.conversation;

import org.springframework.stereotype.Service;

@Service
public class PromptBuilder {

    public String build(
            String basePrompt,
            String routes,
            String memory
    ) {

        return """
        [SISTEMA]
        %s

        Você deve seguir estritamente as regras abaixo:
        - Não invente serviços ou especialidades
        - Utilize apenas os direcionamentos informados
        - Se não houver certeza, faça perguntas antes de direcionar

        ---
        [DIRECIONAMENTOS DISPONÍVEIS]
        %s

        ---
        [HISTÓRICO RECENTE]
        %s

        ---
        [INSTRUÇÃO FINAL]
        Gere a melhor resposta possível ao cliente.
        Caso identifique a necessidade, indique um direcionamento adequado.
        """.formatted(
                basePrompt,
                routes,
                memory
        );
    }
}
