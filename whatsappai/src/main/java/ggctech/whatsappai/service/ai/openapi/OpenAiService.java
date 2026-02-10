package ggctech.whatsappai.service.ai.openapi;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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
    public String chat(String userMessage, String systemPrompt) {

        List<Map<String, Object>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-mini",
                "messages", messages
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(CHAT_URL, body, Map.class);

        return extractText(response);
    }

    /* =========================
       FORMATADOR (TEXTO → TEXTO)
       ========================= */
    public String formatter(String userMessage, String systemPrompt) {

        List<Map<String, Object>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-nano-2025-04-14",
                "messages", messages
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(CHAT_URL, body, Map.class);

        return extractText(response);
    }

    /* =========================
       IMAGEM → TEXTO
       ========================= */
    public String readImage(File imageFile, String prompt) {

        String base64;
        try {
            base64 = Base64.getEncoder().encodeToString(
                    Files.readAllBytes(imageFile.toPath())
            );
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler imagem", e);
        }

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

        return extractText(response);
    }

    /* =========================
       ÁUDIO → TEXTO (WHISPER)
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

        return response.getBody().get("text").toString();
    }

    /* =========================
       UTIL
       ========================= */
    private String extractText(ResponseEntity<Map> response) {
        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");
        return message.get("content").toString();
    }
}
