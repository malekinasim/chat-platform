package com.nasim.chat.client.websocket;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StompAuthenticationChannelInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    public StompAuthenticationChannelInterceptor(JwtDecoder jwtDecoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public @Nullable Message<?> preSend(Message<?> message,  MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (headerAccessor != null
                && StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            String authorization = headerAccessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) return null;

            String rawToken = authorization.substring(7);
            Jwt accessToken = jwtDecoder.decode(rawToken);

            JwtAuthenticationToken authentication = (JwtAuthenticationToken) jwtAuthenticationConverter.convert(accessToken);
            headerAccessor.setUser(authentication);
        }

        return message;
    }
}
