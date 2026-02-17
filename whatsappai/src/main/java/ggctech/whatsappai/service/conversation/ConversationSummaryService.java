package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.memory.ConversationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ConversationSummaryService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private String key(String conversationKey) {
        return "summary:" + conversationKey;
    }

    public ConversationSummary getOrCreate(String conversationKey) {

        String json = redisTemplate.opsForValue().get(key(conversationKey));

        if (json == null) {
            ConversationSummary summary = ConversationSummary.builder()
                    .bullets(new ArrayList<>())
                    .build();

            save(conversationKey, summary);
            return summary;
        }

        try {
            return objectMapper.readValue(json, ConversationSummary.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter summary", e);
        }
    }

    public void save(String conversationKey, ConversationSummary summary) {
        try {
            String json = objectMapper.writeValueAsString(summary);
            redisTemplate.opsForValue().set(key(conversationKey), json);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar summary", e);
        }
    }
}

