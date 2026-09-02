package com.nasim.chat.client.storages.impl;

import com.nasim.chat.client.storages.RoomPresenceStore;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("redis-presence")
public class RedisRoomPresenceStore
        implements RoomPresenceStore {
    private static final String ROOM_KEY_PREFIX = "presence:room:";
    private static final String SESSION_KEY_PREFIX = "presence:session:";
    private final StringRedisTemplate redisTemplate;

    public RedisRoomPresenceStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void subscribe(String sessionId, String subscriptionId, String userId, String roomCode) {
        //add room online user
        redisTemplate.opsForHash().put(
                this.getRoomKey(roomCode),
        this.getConnectionField(sessionId,subscriptionId),
                userId
        );
        //add session subscriptions online room
        redisTemplate.opsForHash().put(
                this.getSessionKey(sessionId),
                subscriptionId,
                roomCode
        );
    }

    @Override
    public Optional<String> unsubscribe(String sessionId, String subscriptionId) {
        Object roomCode=redisTemplate.opsForHash().get(
                this.getSessionKey(sessionId),
                subscriptionId
        );
        if(roomCode==null || !StringUtils.hasText(roomCode.toString()))
            return Optional.empty();

        this.deletePresence(roomCode.toString(),sessionId,subscriptionId);

        return Optional.of(roomCode.toString());
    }

    @Override
    public Set<String> disconnect(String sessionId) {
        Map<Object, Object> entries=redisTemplate.opsForHash().entries(
                this.getSessionKey(sessionId)
        );
        Set<String > affectedRoomCodes=new HashSet<>();
       for(Map.Entry<Object,Object> entry:entries.entrySet()){
           Object subscriptionId=entry.getKey().toString();
           Object roomCode=entry.getValue().toString();

           this.deletePresence(roomCode.toString(),sessionId,subscriptionId.toString());
           affectedRoomCodes.add(roomCode.toString());
       }
      return  affectedRoomCodes;
    }

    @Override
    public Set<String> onlineUsers(String roomCode) {
      List<Object> users=  redisTemplate.opsForHash().values(this.getRoomKey(roomCode));
       return  users.stream().map(String::valueOf).collect(Collectors.toSet());
    }

    @Override
    public int onlineUserCount(String roomCode) {
        return this.onlineUsers(roomCode).size();
    }


    private String getRoomKey(String roomCode){
        return ROOM_KEY_PREFIX+roomCode;
    }
    private String getSessionKey(String sessionId){
        return SESSION_KEY_PREFIX+sessionId;
    }

    private String getConnectionField(String sessionId,String subscriptionId){
        return sessionId+":"+subscriptionId;
    }

    private void deletePresence(String roomCode,String sessionId,String subscriptionId){
        redisTemplate.opsForHash().delete(
                this.getRoomKey(roomCode),
                this.getConnectionField(sessionId,subscriptionId)
        );

        redisTemplate.opsForHash().delete(
                this.getSessionKey(sessionId),
                subscriptionId
        );
    }


}