package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.dto.AiAction;
import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ActionExecutor {

    public void execute(List<AiAction> actions, IncomingMessageDTO dto) {

        if (actions == null) return;

        for (AiAction action : actions) {

            switch (action.getType()) {

                case "SEND_LINK" -> {
                    log.info("Enviar link: {}", action.getPayload());
                }

                case "FORWARD_WHATSAPP" -> {
                    log.info("Encaminhar: {}", action.getPayload());
                }

                case "SEND_EMAIL" -> {
                    log.info("Enviar email: {}", action.getPayload());
                }

                default -> log.warn("Ação desconhecida: {}", action.getType());
            }
        }
    }
}
