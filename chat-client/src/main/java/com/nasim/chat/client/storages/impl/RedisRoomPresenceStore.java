package com.nasim.chat.client.storages.impl;


import com.nasim.chat.client.storages.RoomPresenceStore;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("redis-presence")
public class RedisRoomPresenceStore implements RoomPresenceStore {

    private static final String ROOM_KEY_PREFIX =  "presence:room:";

    private static final String INSTANCE_KEY_PREFIX = "presence:instance:";

    private static final String SESSION_KEY_PART =":session:";

    private static final RedisScript<Long> SUBSCRIBE_SCRIPT = RedisScript.of(
                    """
                    redis.call( 'HSET',KEYS[1],ARGV[1],ARGV[2])
                    redis.call( 'HSET',KEYS[2],ARGV[3],ARGV[4])
                    return 1
                    """,
                    Long.class
            );

    private static final RedisScript<String> UNSUBSCRIBE_SCRIPT =  RedisScript.of(
                    """
                    local roomCode =  redis.call(  'HGET', KEYS[1], ARGV[1] )

                    if not roomCode then
                        return nil
                    end

                    local roomKey =  ARGV[2] .. roomCode
                    redis.call( 'HDEL',  roomKey, ARGV[3])
                    redis.call(  'HDEL', KEYS[1], ARGV[1])
                  
                
                    return roomCode
                    """,
                    String.class
            );

    private static final RedisScript<List> DISCONNECT_SCRIPT = RedisScript.of(
                    """
                    local subscriptions =redis.call('HGETALL', KEYS[1])
                    local affectedRooms = {}

                    for index = 1, #subscriptions, 2 do
                        local subscriptionId =subscriptions[index]
                        local roomCode = subscriptions[index + 1]
                        local roomKey =ARGV[1] .. roomCode
                        local connectionField =ARGV[2] .. ':' .. subscriptionId
                        redis.call( 'HDEL', roomKey,connectionField)
                        affectedRooms[#affectedRooms + 1] = roomCode
                    end
                    redis.call('DEL', KEYS[1])
                    return affectedRooms
                    """,
                    List.class
            );

    private final StringRedisTemplate redisTemplate;
    private final String instanceId;

    public RedisRoomPresenceStore(
            StringRedisTemplate redisTemplate,
            PresenceInstanceIdentity identity
    ) {
        this.redisTemplate = redisTemplate;
        this.instanceId = identity.getInstanceId();
    }

    @Override
    public void subscribe( String sessionId, String subscriptionId, String userId, String roomCode) {
        redisTemplate.execute(
                SUBSCRIBE_SCRIPT,
                List.of(getRoomKey(roomCode), getSessionKey(sessionId)),
                getConnectionField(   sessionId, subscriptionId),
                userId,
                subscriptionId,
                roomCode
        );
    }

    @Override
    public Optional<String> unsubscribe(String sessionId, String subscriptionId) {
        String roomCode = redisTemplate.execute(
                UNSUBSCRIBE_SCRIPT,
                List.of(getSessionKey(sessionId)),
                subscriptionId,
                ROOM_KEY_PREFIX,
                getConnectionField(sessionId, subscriptionId)
        );

        return Optional.ofNullable(roomCode);
    }
    @Override
    public Set<String> disconnect(String sessionId) {
        return cleanupSession(instanceId, sessionId);
    }

    @Override
    public Set<String> cleanupSession(
            String ownerInstanceId,
            String sessionId
    ) {
        List<?> affectedRooms = redisTemplate.execute(
                DISCONNECT_SCRIPT,
                List.of(
                        getSessionKey(ownerInstanceId, sessionId)
                ),
                ROOM_KEY_PREFIX,
                getConnectionPrefix(
                        ownerInstanceId,
                        sessionId
                )
        );

        if (affectedRooms == null) {
            return Set.of();
        }

        return affectedRooms.stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> onlineUsers(String roomCode) {
        List<Object> users = redisTemplate.opsForHash().values(getRoomKey(roomCode));
        return users.stream().map(String::valueOf).collect(Collectors.toSet());
    }

    @Override
    public int onlineUserCount(String roomCode) {
        return onlineUsers(roomCode).size();
    }

    private String getRoomKey(String roomCode) {
        return ROOM_KEY_PREFIX + roomCode;
    }

    private String getSessionKey(String sessionId) {
        return getSessionKey(instanceId, sessionId);
    }

    private String getSessionKey(
            String ownerInstanceId,
            String sessionId
    ) {
        return INSTANCE_KEY_PREFIX
                + ownerInstanceId
                + SESSION_KEY_PART
                + sessionId;
    }

    private String getConnectionPrefix(String sessionId) {
        return getConnectionPrefix(instanceId, sessionId);
    }

    private String getConnectionPrefix(
            String ownerInstanceId,
            String sessionId
    ) {
        return ownerInstanceId
                + ":"
                + sessionId;
    }

    private String getConnectionField(
            String sessionId,
            String subscriptionId
    ) {
        return getConnectionPrefix(sessionId) + ":" + subscriptionId;
    }

}