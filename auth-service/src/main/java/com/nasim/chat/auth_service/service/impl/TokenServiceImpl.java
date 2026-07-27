package com.nasim.chat.auth_service.service.impl;


import com.nasim.chat.auth_service.model.dto.AuthenticationTokens;
import com.nasim.chat.auth_service.model.dto.GeneratedRefreshToken;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.service.RefreshTokenSessionService;
import com.nasim.chat.auth_service.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final RefreshTokenSessionService refreshTokenSessionService;

    public TokenServiceImpl(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.issuer}") String issuer, RefreshTokenSessionService refreshTokenSessionService) {

        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.refreshTokenSessionService = refreshTokenSessionService;
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

    @Override
    public AuthenticationTokens generatesAuthenticationTokens(LoginExchangeCode loginData) {
        String accessToken = this.generateAccessToken(
                loginData.userId(),
                loginData.roles(),
                loginData.allowedAudiences()
        );
        GeneratedRefreshToken refreshToken = this.generateRefreshToken(loginData.userId());
        refreshTokenSessionService.createAndRevokeRefreshToken(
                loginData.userId(),
                refreshToken.tokenId(),
                loginData.clientId(),
                refreshToken.expiresAt());
        return new AuthenticationTokens(accessToken,refreshToken.tokenValue());

    }

    @Override
    public boolean isValidRefreshToken(String refreshToken) {
        return  refreshTokenSessionService.isValidToken(refreshToken);
    }
}
