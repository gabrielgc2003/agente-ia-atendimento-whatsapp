package ggctech.whatsappai.service.conversation;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ConversationLockService {

    private final StringRedisTemplate redis;

    public ConversationLockService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public boolean exists(String conversationKey) {
        return Boolean.TRUE.equals(
                redis.hasKey("lock:" + conversationKey)
        );
    }

    public boolean create(String conversationKey) {
        return Boolean.TRUE.equals(
                redis.opsForValue().setIfAbsent(
                        "lock:" + conversationKey,
                        "true",
                        Duration.ofSeconds(45)
                )
        );
    }

    public void delete(String conversationKey) {
        redis.delete("lock:" + conversationKey);
    }
}

