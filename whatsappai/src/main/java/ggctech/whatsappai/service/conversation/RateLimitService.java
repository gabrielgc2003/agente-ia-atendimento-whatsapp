package ggctech.whatsappai.service.conversation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redis;

    public boolean isSpam(String companyNumber, String clientNumber) {

        String key = "rate_limit:" + companyNumber + ":" + clientNumber;

        Long count = redis.opsForValue().increment(key);

        if (count == 1) {
            redis.expire(key, Duration.ofMinutes(1));
        }

        return count != null && count > 10;
    }
}
