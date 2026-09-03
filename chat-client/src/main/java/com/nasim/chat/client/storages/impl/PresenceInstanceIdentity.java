package com.nasim.chat.client.storages.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("redis-presence")
public class PresenceInstanceIdentity {

    private final String instanceId;

    public PresenceInstanceIdentity(
            @Value("${HOSTNAME:chat-client}") String hostname
    ) {
        this.instanceId =
                hostname + ":" + UUID.randomUUID();
    }

    public String getInstanceId() {
        return instanceId;
    }
}