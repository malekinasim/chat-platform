package com.nasim.chat.client.storages.impl;

import com.nasim.chat.client.storages.InstanceSessionStore;
import com.nasim.chat.client.storages.RoomPresenceStore;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Component
@Profile("redis-presence")
public class RedisPresenceLifecycleManager {

    private static final String INSTANCES_KEY =   "presence:instances";

    private static final String INSTANCE_KEY_PREFIX = "presence:instance:";

    private static final String LEASE_SUFFIX = ":lease";

    private static final RedisScript<Long> REFRESH_LEASE_SCRIPT =
            RedisScript.of(
                    """
                    redis.call(
                        'SADD',
                        KEYS[1],
                        ARGV[1]
                    )

                    redis.call(
                        'SET',
                        KEYS[2],
                        'alive',
                        'PX',
                        ARGV[2]
                    )

                    return 1
                    """,
                    Long.class
            );

    private final StringRedisTemplate redisTemplate;
    private final InstanceSessionStore instanceSessionStore;
    private final RoomPresenceStore roomPresenceStore;
    private final String instanceId;
    private final Duration leaseTtl;

    public RedisPresenceLifecycleManager(
            StringRedisTemplate redisTemplate,
            InstanceSessionStore instanceSessionStore,
            RoomPresenceStore roomPresenceStore,
            PresenceInstanceIdentity identity,
            @Value("${chat.presence.lease.ttl:60s}")
            Duration leaseTtl
    ) {
        this.redisTemplate = redisTemplate;
        this.instanceSessionStore = instanceSessionStore;
        this.roomPresenceStore = roomPresenceStore;
        this.instanceId = identity.getInstanceId();
        this.leaseTtl = leaseTtl;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        refreshLease();
        cleanupDeadInstances();
    }

    @Scheduled(
            fixedDelayString =
                    "${chat.presence.lease.refresh-interval:20s}"
    )
    public void refreshLease() {
        redisTemplate.execute(
                REFRESH_LEASE_SCRIPT,
                List.of(
                        INSTANCES_KEY,
                        getLeaseKey(instanceId)
                ),
                instanceId,
                String.valueOf(leaseTtl.toMillis())
        );
    }

    @Scheduled(
            fixedDelayString =
                    "${chat.presence.lease.cleanup-interval:30s}"
    )
    public void cleanupDeadInstances() {
        Set<String> instances =
                redisTemplate.opsForSet()
                        .members(INSTANCES_KEY);

        if (instances == null) {
            return;
        }

        for (String candidateInstanceId : instances) {
            if (candidateInstanceId.equals(instanceId)) {
                continue;
            }

            if (leaseExists(candidateInstanceId)) {
                continue;
            }

            cleanupInstance(candidateInstanceId);
        }
    }

    private boolean leaseExists(String candidateInstanceId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(
                        getLeaseKey(candidateInstanceId)
                )
        );
    }

    private void cleanupInstance(String deadInstanceId) {
        Set<String> sessions =
                instanceSessionStore.findSessions(deadInstanceId);

        for (String sessionId : sessions) {
            roomPresenceStore.cleanupSession(
                    deadInstanceId,
                    sessionId
            );

            instanceSessionStore.removeSession(
                    deadInstanceId,
                    sessionId
            );
        }

        if (!instanceSessionStore
                .findSessions(deadInstanceId)
                .isEmpty()) {
            return;
        }

        redisTemplate.delete(
                getLeaseKey(deadInstanceId)
        );

        redisTemplate.opsForSet().remove(
                INSTANCES_KEY,
                deadInstanceId
        );
    }
    @PreDestroy
    public void shutdown() {
        cleanupInstance(instanceId);
    }

    private String getLeaseKey(String ownerInstanceId) {
        return INSTANCE_KEY_PREFIX
                + ownerInstanceId
                + LEASE_SUFFIX;
    }
}