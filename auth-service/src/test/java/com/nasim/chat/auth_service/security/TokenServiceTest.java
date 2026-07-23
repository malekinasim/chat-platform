package com.nasim.chat.auth_service.security;

import com.nasim.chat.auth_service.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.*;
import com.nasim.chat.security.jwt.decoder.JwtDecoders;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private RSAPublicKey jwtPublicKey;
    @Autowired
    private JwtEncoder jwtEncoder;

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
       JwtDecoder decoder= JwtDecoders.create(
                jwtPublicKey,
                "http://auth-service:8083",
                "chat-client"
        );
        Jwt jwt = decoder.decode(token);

        assertEquals( "http://auth-service:8083", String.valueOf(jwt.getIssuer()));
        assertEquals("test-user-123", jwt.getSubject());
        assertTrue(Objects.requireNonNull(jwt.getAudience()).contains("chat-client"));
        assertEquals(List.of("USER"), jwt.getClaimAsStringList("roles"));
    }

    @Test
    void shouldRejectTokenWithWrongAudience() {
        String token = tokenService.generateAccessToken(
                "test-user-123",
                List.of("USER"),
                List.of("notification-service")
        );

        JwtDecoder decoder = JwtDecoders.create(
                jwtPublicKey,
                "http://auth-service:8083",
                "chat-client"
        );

        JwtValidationException exception = assertThrows(
                JwtValidationException.class,
                () -> decoder.decode(token)
        );

        System.out.println(exception.getMessage());
    }
    @Test
    void shouldRejectTokenWithWrongIssuer() {
        String token = tokenService.generateAccessToken(
                "test-user-123",
                List.of("USER"),
                List.of("chat-client")
        );

        JwtDecoder decoder = JwtDecoders.create(
                jwtPublicKey,
                "http://wrong-auth-service:8083",
                "chat-client"
        );

        JwtValidationException exception = assertThrows(
                JwtValidationException.class,
                () -> decoder.decode(token)
        );

        System.out.println(exception.getMessage());
    }

    @Test
    void shouldRejectExpiredToken() {
         Instant now=Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://auth-service:8083")
                .subject("test-user-123")
                .audience(List.of("chat-client"))
                .issuedAt(now.minusSeconds(200))
                .expiresAt(now.minusSeconds(100))
                .claim("roles", List.of("USER"))
                .build();
        String expiredToken = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        JwtDecoder decoder = JwtDecoders.create(
                jwtPublicKey,
                "http://auth-service:8083",
                "chat-client"
        );
        JwtValidationException exception= assertThrows(
                JwtValidationException.class,
                () -> decoder.decode(expiredToken)
        );
        System.out.println(exception.getMessage()+" now "+Instant.now());
    }
}