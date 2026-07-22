package com.nasim.chat.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.net.URL;
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
}
