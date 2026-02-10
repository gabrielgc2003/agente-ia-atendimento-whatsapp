package ggctech.whatsappai.controller.webhook;

import ggctech.whatsappai.enums.SourceType;
import ggctech.whatsappai.service.conversation.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
public class WebhookController {

    private final ConversationService conversationService;

    @PostMapping("/message-meta")
    public ResponseEntity<Void> receiveMeta(@RequestBody JsonNode template) {
        conversationService.handleIncomingMessage(template,SourceType.META_API);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/message-evolution")
    public ResponseEntity<Void> receiveEvolution(@RequestBody JsonNode template) {
        conversationService.handleIncomingMessage(template, SourceType.EVOLUTION_API);
        return ResponseEntity.ok().build();
    }
}
