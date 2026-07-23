package com.nasim.chat.auth_service.security;

import com.nasim.chat.auth_service.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private RSAPublicKey jwtPublicKey;

    @Test
    void shouldGenerateAccessToken() {
        String token = tokenService.generateAccessToken(
                "test-user-123",
                List.of("USER"),
                List.of("chat-client")
        );

        System.out.println(token);
    }

    @Test
    void shouldGenerateAndDecodeAccessToken() {
        String token = tokenService.generateAccessToken(
                "test-user-123",
                List.of("USER"),
                List.of("chat-client")
        );

        JwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey(jwtPublicKey)
                .build();

        Jwt jwt = decoder.decode(token);

        assertEquals( "http://auth-service:8083", String.valueOf(jwt.getIssuer()));
        assertEquals("test-user-123", jwt.getSubject());
        assertTrue(Objects.requireNonNull(jwt.getAudience()).contains("chat-client"));
        assertEquals(List.of("USER"), jwt.getClaimAsStringList("roles"));
    }
}