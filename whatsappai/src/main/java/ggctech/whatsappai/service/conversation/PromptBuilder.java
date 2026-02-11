package ggctech.whatsappai.service.conversation;

import org.springframework.stereotype.Service;

@Service
public class PromptBuilder {

    public String build(String basePrompt, String routes) {

        return """
        %s

        ---
        [DIRECIONAMENTOS DISPONÍVEIS]
        %s

        ---
        Siga rigorosamente as regras acima.
        """.formatted(basePrompt, routes);
    }
}

