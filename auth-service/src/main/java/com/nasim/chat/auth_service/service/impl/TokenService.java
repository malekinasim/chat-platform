package com.nasim.chat.auth_service.service.impl;


import com.nasim.chat.auth_service.model.dto.GeneratedRefreshToken;
import com.nasim.chat.auth_service.service.RefreshTokenSessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;

    private final String issuer;

    public TokenService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.issuer}") String issuer) {

        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
    }
    public String generateAccessToken(
            String userId , List<String> roles
    , List<String> allowedAudiences){
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer.toString())
                .subject(userId)
                .audience(allowedAudiences)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300))
                .id(UUID.randomUUID().toString())
                .claim("roles", roles)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    public GeneratedRefreshToken generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(7));
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userId)
                .audience(List.of("auth-service"))
                .issuedAt(now)
                .expiresAt(expiresAt)
                .id(tokenId)
                .claim("type", "refresh")
                .build();

        String tokenValue = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
        return new GeneratedRefreshToken(
                tokenValue,
                tokenId,
                expiresAt
        );
    }
}
