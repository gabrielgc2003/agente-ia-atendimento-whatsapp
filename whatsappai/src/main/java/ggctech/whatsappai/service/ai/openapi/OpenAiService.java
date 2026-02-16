package ggctech.whatsappai.service.ai.openapi;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final RestTemplate restTemplate;

    private static final String CHAT_URL =
            "https://api.openai.com/v1/chat/completions";

    private static final String AUDIO_URL =
            "https://api.openai.com/v1/audio/transcriptions";

    public OpenAiService(RestTemplate openAiRestTemplate) {
        this.restTemplate = openAiRestTemplate;
    }

    /* =========================
       TEXTO → TEXTO
       ========================= */
    public String chat(List<Map<String, String>> messages) {

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1",
                "messages", messages,
                "temperature", 0.2
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(CHAT_URL, body, Map.class);

        return extractAndCleanContent(response);
    }

    /* =========================
       FORMATADOR
       ========================= */
    public String formatter(String userMessage, String systemPrompt) {

        List<Map<String, Object>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-nano-2025-04-14",
                "messages", messages,
                "temperature", 0.2
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(CHAT_URL, body, Map.class);

        return extractAndCleanContent(response);
    }

    /* =========================
       IMAGEM → TEXTO
       ========================= */
    public String readImage(File imageFile, String prompt) {

        try {

            String base64 = Base64.getEncoder().encodeToString(
                    Files.readAllBytes(imageFile.toPath())
            );

            String mimeType = "image/jpeg";

            Map<String, Object> message = Map.of(
                    "role", "user",
                    "content", List.of(
                            Map.of("type", "text", "text", prompt),
                            Map.of("type", "image_url",
                                    "image_url", Map.of(
                                            "url", "data:" + mimeType + ";base64," + base64
                                    ))
                    )
            );

            Map<String, Object> body = Map.of(
                    "model", "gpt-4.1-mini",
                    "messages", List.of(message)
            );

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(CHAT_URL, body, Map.class);

            return extractAndCleanContent(response);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler imagem", e);
        }
    }

    /* =========================
       ÁUDIO → TEXTO
       ========================= */
    public String transcribeAudio(File audioFile) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        body.add("model", "whisper-1");

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(AUDIO_URL, request, Map.class);

        Object text = response.getBody().get("text");

        return text != null ? text.toString() : "";
    }

    /* =========================
       EXTRAÇÃO SEGURA
       ========================= */
    private String extractAndCleanContent(ResponseEntity<Map> response) {

        if (response == null || response.getBody() == null) {
            throw new RuntimeException("Resposta nula da OpenAI");
        }

        Object choicesObj = response.getBody().get("choices");

        if (!(choicesObj instanceof List<?> choices) || choices.isEmpty()) {
            throw new RuntimeException("Resposta inválida da OpenAI");
        }

        Object first = choices.get(0);

        if (!(first instanceof Map<?, ?> firstMap)) {
            throw new RuntimeException("Estrutura inesperada da OpenAI");
        }

        Object messageObj = firstMap.get("message");

        if (!(messageObj instanceof Map<?, ?> messageMap)) {
            throw new RuntimeException("Mensagem inválida da OpenAI");
        }

        Object contentObj = messageMap.get("content");

        if (contentObj == null) {
            return "";
        }

        String content = contentObj.toString();

        return cleanMarkdownJson(content);
    }

    /* =========================
       REMOVE ```json
       ========================= */
    private String cleanMarkdownJson(String raw) {

        if (raw == null) return null;

        if (raw.startsWith("```")) {
            raw = raw.replace("```json", "")
                    .replace("```", "")
                    .trim();
        }

        return raw;
    }
}
