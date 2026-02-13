package ggctech.whatsappai.service.conversation.sender;

import ggctech.whatsappai.domain.dto.EvolutionChatPresenceRequest;
import ggctech.whatsappai.domain.dto.EvolutionTextMessageRequest;
import ggctech.whatsappai.domain.dto.IncomingMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EvolutionApiSenderService implements MessageSenderService {

    private final InstanceExecutionRegistry  executionRegistry;
    private final RestTemplate evolutionRestTemplate;

    @Value("${evolution.api.base-url}")
    private String baseUrl;

    @Override
    public void sendMessage(String message, IncomingMessageDTO dto) {

        ScheduledExecutorService executor =
                executionRegistry.getExecutor(dto.getInstanceId(), dto.getRemoteJid());

        executor.execute(() -> {

            List<String> parts = splitMessage(message);

            try {

                Thread.sleep(initialDelay());

                for (String part : parts) {

                    int typingTime = (int) calculateDelayBySize(part);

                    // 1️⃣ Simula digitando
                    typingSimulation(dto, typingTime);

                    // 2️⃣ Espera o tempo da digitação
                    Thread.sleep(typingTime);

                    // 3️⃣ Envia mensagem
                    sendPart(part, dto);

                    // Pequena pausa natural entre mensagens
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        });
    }


    private void sendPart(String part, IncomingMessageDTO dto) {

        String url = baseUrl + "/message/sendText/" + dto.getInstanceName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", dto.getInstanceId());

        EvolutionTextMessageRequest body =
                new EvolutionTextMessageRequest(
                        dto.getRemoteJid(),
                        sanitizeText(part)
                );

        HttpEntity<EvolutionTextMessageRequest> request =
                new HttpEntity<>(body, headers);

        evolutionRestTemplate.postForEntity(
                url,
                request,
                Void.class
        );
    }

    private void typingSimulation(IncomingMessageDTO dto, int delay) {
        String url = baseUrl + "/chat/sendPresence/" + dto.getInstanceName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", dto.getInstanceId());

        EvolutionChatPresenceRequest body =
                new EvolutionChatPresenceRequest(
                        dto.getRemoteJid(),
                        "composing",
                         delay
                );

        HttpEntity<EvolutionChatPresenceRequest> request =
                new HttpEntity<>(body, headers);

        evolutionRestTemplate.postForEntity(
                url,
                request,
                Void.class
        );
    }

    private String sanitizeText(String text) {
        return text.replace("\"", "'"); // mesmo replace que você fazia no n8n
    }

    private List<String> splitMessage(String message) {
        return List.of(message.split("\n\n"));
    }

    private long initialDelay() {
        return 1200;
    }

    private long calculateDelayBySize(String text) {

        return Math.max(800, (text.length() * 1000L) / 20);
    }
}