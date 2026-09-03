package com.nasim.chat.client.storages.impl;

import com.nasim.chat.client.storages.InstanceSessionStore;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@Profile("redis-presence")
public class RedisInstanceSessionStore
        implements InstanceSessionStore {

    private static final String INSTANCE_KEY_PREFIX = "presence:instance:";

    private static final String SESSIONS_SUFFIX =":sessions";

    private final StringRedisTemplate redisTemplate;

    public RedisInstanceSessionStore(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addSession(String instanceId, String sessionId) {
        redisTemplate.opsForValue().set(
                getSessionsKey(instanceId),
                sessionId
        );
    }

    @Override
    public void removeSession(String instanceId, String sessionId) {
        redisTemplate.opsForSet().remove(
                getSessionsKey(instanceId),
                sessionId
        );
    }

    @Override
    public Set<String> findSessions(String instanceId) {
        Set<String> sessions =
                redisTemplate.opsForSet()
                        .members(getSessionsKey(instanceId));

        if (sessions == null) {
            return Set.of();
        }
        return Set.copyOf(sessions);
    }

    @Override
    public void deleteSessions(String instanceId) {
        redisTemplate.delete(getSessionsKey(instanceId));
    }

    private String getSessionsKey(String instanceId) {
        return INSTANCE_KEY_PREFIX + instanceId + SESSIONS_SUFFIX;
    }
}