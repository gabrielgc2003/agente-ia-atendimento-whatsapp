package ggctech.whatsappai.service.conversation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ConversationLockService {

    @Value("${config.lock.timer}")
    private int lockSeconds;

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
                        Duration.ofSeconds(lockSeconds)
                )
        );
    }

    public void delete(String conversationKey) {
        redis.delete("lock:" + conversationKey);
    }
}

