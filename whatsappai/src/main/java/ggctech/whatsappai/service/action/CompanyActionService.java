package ggctech.whatsappai.service.action;

import ggctech.whatsappai.domain.destination.CompanyAction;
import ggctech.whatsappai.repository.CompanyActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
@Service
@RequiredArgsConstructor
public class CompanyActionService {

    private final CompanyActionRepository repository;
    private final ObjectMapper objectMapper;

    public String getActionsForAi(String instanceId) {

        List<CompanyAction> actions =
                repository.findActiveAiActionsByInstanceId(instanceId);

        if (actions.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (CompanyAction action : actions) {

            sb.append("ID: ").append(action.getId()).append("\n");
            sb.append("Nome: ").append(action.getName()).append("\n");
            sb.append("Tipo: ").append(action.getType()).append("\n");
            sb.append("Descrição: ").append(action.getDescription()).append("\n");

            if (action.getConfigJson() != null && !action.getConfigJson().isBlank()) {
                sb.append("Configuração:\n");
                sb.append(action.getConfigJson()).append("\n");
            }

            sb.append("\n---\n\n");
        }

        sb.append("""
        Se for necessário executar uma ferramenta, responda exclusivamente em JSON:

        {
          "action_id": "UUID",
          "payload": { ... }
        }

        Caso não seja necessário executar ferramenta,
        apenas responda normalmente.
        """);

        return sb.toString();
    }
}

