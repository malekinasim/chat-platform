package com.nasim.chat.client.storages;

import java.util.Set;


public interface InstanceSessionStore {

    void addSession(String instanceId, String sessionId);

    void removeSession(String instanceId, String sessionId);

    Set<String> findSessions(String instanceId);

    void deleteSessions(String instanceId);
}