package ggctech.whatsappai.service.conversation;

import ggctech.whatsappai.domain.memory.ConversationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationSummaryService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String key(String conversationKey) {
        return "summary:" + conversationKey;
    }

    public ConversationSummary getOrCreate(String conversationKey) {

        ConversationSummary summary =
                (ConversationSummary) redisTemplate.opsForValue().get(key(conversationKey));

        if (summary == null) {
            summary = ConversationSummary.builder().build();
            save(conversationKey, summary);
        }

        return summary;
    }

    public void save(String conversationKey, ConversationSummary summary) {
        redisTemplate.opsForValue().set(key(conversationKey), summary);
    }
}
