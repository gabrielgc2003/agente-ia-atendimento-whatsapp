package ggctech.whatsappai.service.conversation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationBufferService {

    private final StringRedisTemplate redis;
    private final RedisScript<String> appendScript;

    public ConversationBufferService(StringRedisTemplate redis) {
        this.redis = redis;
        this.appendScript = RedisScript.of(
                """
                local key = KEYS[1]
                local newMessage = ARGV[1]
    
                local current = redis.call("GET", key)
    
                if not current or current == "" then
                    redis.call("SET", key, newMessage)
                    return newMessage
                else
                    local updated = current .. "\\n" .. newMessage
                    redis.call("SET", key, updated)
                    return updated
                end
                """,
                String.class
        );
    }

    public String append(String conversationKey, String newMessage) {
        return redis.execute(
                appendScript,
                List.of("buffer:" + conversationKey),
                newMessage
        );
    }

    public String get(String conversationKey) {
        return redis.opsForValue().get("buffer:" + conversationKey);
    }

    public void delete(String conversationKey) {
        redis.delete("buffer:" + conversationKey);
    }
}
