package com.nasim.chat.client.websocket;

import com.nasim.chat.client.websocket.channelInterceptor.OutboundMessageInterceptor;
import com.nasim.chat.client.websocket.channelInterceptor.RoomSubscriptionAuthorizationInterceptor;
import com.nasim.chat.client.websocket.channelInterceptor.StompAuthenticationChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer  {
    public static final String ROOM_TOPIC_PREFIX = "/topic/room/";
    private final StompAuthenticationChannelInterceptor authenticationInterceptor;
    private final RoomSubscriptionAuthorizationInterceptor roomSubscriptionAuthorizationInterceptor;
    private final OutboundMessageInterceptor outboundMessageInterceptor;
    public WebSocketConfig(StompAuthenticationChannelInterceptor authenticationInterceptor, RoomSubscriptionAuthorizationInterceptor roomSubscriptionAuthorizationInterceptor, OutboundMessageInterceptor outboundMessageInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.roomSubscriptionAuthorizationInterceptor = roomSubscriptionAuthorizationInterceptor;
        this.outboundMessageInterceptor = outboundMessageInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
       registration.interceptors(authenticationInterceptor,roomSubscriptionAuthorizationInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(outboundMessageInterceptor);
    }
}
