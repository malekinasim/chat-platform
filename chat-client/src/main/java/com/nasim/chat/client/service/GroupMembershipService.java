package com.nasim.chat.client.service;

public interface GroupMembershipService {
    boolean hasActiveMembership(String userId, String roomCode);
}
