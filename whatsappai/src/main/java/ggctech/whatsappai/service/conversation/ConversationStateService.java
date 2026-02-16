package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.memory.ConversationState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationStateService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String key(String conversationKey) {
        return "state:" + conversationKey;
    }

    public ConversationState getOrCreate(String conversationKey) {

        ConversationState state =
                (ConversationState) redisTemplate.opsForValue().get(key(conversationKey));

        if (state == null) {
            state = ConversationState.builder()
                    .stage("discovery")
                    .build();
            save(conversationKey, state);
        }

        return state;
    }

    public void save(String conversationKey, ConversationState state) {
        redisTemplate.opsForValue().set(key(conversationKey), state);
    }
}
