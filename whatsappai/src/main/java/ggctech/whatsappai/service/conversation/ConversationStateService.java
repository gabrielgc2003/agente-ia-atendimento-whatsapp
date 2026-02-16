package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.memory.ConversationState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ConversationStateService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private String key(String conversationKey) {
        return "state:" + conversationKey;
    }

    public ConversationState getOrCreate(String conversationKey) {

        String json = redisTemplate.opsForValue().get(key(conversationKey));

        if (json == null) {
            ConversationState state = ConversationState.builder()
                    .stage("discovery")
                    .build();

            save(conversationKey, state);
            return state;
        }

        try {
            return objectMapper.readValue(json, ConversationState.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter state", e);
        }
    }

    public void save(String conversationKey, ConversationState state) {
        try {
            String json = objectMapper.writeValueAsString(state);
            redisTemplate.opsForValue().set(key(conversationKey), json);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar state", e);
        }
    }
}

