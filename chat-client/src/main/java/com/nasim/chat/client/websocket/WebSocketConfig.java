package com.nasim.chat.client.websocket;

import com.nasim.chat.client.websocket.channelInterceptor.OutboundMessageInterceptor;
import com.nasim.chat.client.websocket.channelInterceptor.RoomSubscriptionAuthorizationInterceptor;
import com.nasim.chat.client.websocket.channelInterceptor.StompAuthenticationChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    public static final String ROOM_TOPIC_PREFIX = "/topic/room/";
    public static final String PRIVATE_TOPIC_PREFIX = "/user/queue/private";
    private  final String rabbitMQHost;
    private  final int rabbitMQPort;
    private  final String rabbitMQUsername;
    private  final String rabbitMQPassword;
    private final StompAuthenticationChannelInterceptor authenticationInterceptor;
    private final RoomSubscriptionAuthorizationInterceptor roomSubscriptionAuthorizationInterceptor;
    private final OutboundMessageInterceptor outboundMessageInterceptor;
    private TaskScheduler messageBrokerTaskScheduler;

    public WebSocketConfig(StompAuthenticationChannelInterceptor authenticationInterceptor,
                           RoomSubscriptionAuthorizationInterceptor roomSubscriptionAuthorizationInterceptor,
                           OutboundMessageInterceptor outboundMessageInterceptor,
                           @Value("${chat.broker-relay.host}")
                           String rabbitMQHost,
                           @Value("${chat.broker-relay.port}")
                           int rabbitMQPort,
                           @Value("${chat.broker-relay.username}")
                           String rabbitMQUsername,
                           @Value("${chat.broker-relay.password}")
                           String rabbitMQPassword
                           ) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.roomSubscriptionAuthorizationInterceptor = roomSubscriptionAuthorizationInterceptor;
        this.outboundMessageInterceptor = outboundMessageInterceptor;
        this.rabbitMQHost =rabbitMQHost;
        this.rabbitMQPort =rabbitMQPort;
        this.rabbitMQUsername =rabbitMQUsername;
        this.rabbitMQPassword =rabbitMQPassword;
    }

    @Autowired
    public void setMessageBrokerTaskScheduler(
            @Lazy TaskScheduler taskScheduler
    ) {
        this.messageBrokerTaskScheduler = taskScheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry
    ) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(rabbitMQHost)
                .setRelayPort(rabbitMQPort)
                .setVirtualHost("/")
                .setClientLogin(rabbitMQUsername)
                .setClientPasscode(rabbitMQPassword)
                .setSystemLogin(rabbitMQUsername)
                .setSystemPasscode(rabbitMQPassword)
                .setSystemHeartbeatReceiveInterval(10_000)
                .setSystemHeartbeatSendInterval(10_000)
                .setUserRegistryBroadcast( "/topic/simp-user-registry")
                .setUserDestinationBroadcast(  "/topic/unresolved-user-destination")
                .setTaskScheduler(messageBrokerTaskScheduler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authenticationInterceptor,
                new SecurityContextChannelInterceptor(),
                roomSubscriptionAuthorizationInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(outboundMessageInterceptor);
    }
}
