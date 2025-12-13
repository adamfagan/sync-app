package com.playersync.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playersync.dto.PlayerSyncDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class RedisCacheService {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private static final String KEY_PREFIX = "player:%d:missions";
    private static final Duration TTL = Duration.ofMinutes(10);

    public void cachePlayerMissions(Long playerId, PlayerSyncDTO data, Instant lastSqlUpdate) {
        String key = String.format(KEY_PREFIX, playerId);

        // Watermark check: Get existing last_sync_at
        Object existingSyncTime = redisTemplate.opsForHash().get(key, "last_sync_at");

        if (existingSyncTime != null) {
            try {
                Instant cachedTime = Instant.parse(existingSyncTime.toString());
                if (!lastSqlUpdate.isAfter(cachedTime)) {
                    log.info("Data not changed since last sync for player {}", playerId);
                    return;
                }
            } catch (Exception e) {
                log.warn("Could not parse existing sync time, proceeding with update");
            }
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(data);

            Map<String, String> hash = new HashMap<>();
            hash.put("payload", jsonPayload);
            hash.put("last_sync_at", Instant.now().toString());

            redisTemplate.opsForHash().putAll(key, hash);
            redisTemplate.expire(key, TTL);

            log.info("Synced player {} to Redis", playerId);
        } catch (Exception e) {
            log.error("Failed to serialize/cache data", e);
        }
    }

    public void invalidateCache(Long playerId) {
        String key = String.format(KEY_PREFIX, playerId);
        redisTemplate.delete(key);
        log.info("Invalidated cache for player {}", playerId);
    }
}