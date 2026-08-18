package com.nasim.chat.client.websocket.eventListener;

public record PrivateSubscriptionReadyEvent(String sessionId,String userId) {
}
