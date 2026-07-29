package com.nasim.chat.auth_service.service.impl;


import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.dto.AuthenticationTokens;
import com.nasim.chat.auth_service.model.dto.GeneratedRefreshToken;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;
import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.service.RefreshTokenSessionService;
import com.nasim.chat.auth_service.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
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
            String userId, List<String> roles
            , List<String> allowedAudiences) {
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


    public GeneratedRefreshToken generateRefreshToken() throws NoSuchAlgorithmException {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(7));
        String rawRefreshToken = secureRandomToken();
        String refreshTokenHash = bytesToHex(getSHA256(rawRefreshToken));
        return new GeneratedRefreshToken(
                rawRefreshToken,
                refreshTokenHash,
                expiresAt
        );
    }

    private byte[] getSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String secureRandomToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Override
    public AuthenticationTokens generatesAuthenticationTokens(LoginExchangeCode loginData) {
        try {
            String accessToken = this.generateAccessToken(
                    loginData.userId(),
                    loginData.roles(),
                    loginData.allowedAudiences()
            );
            GeneratedRefreshToken refreshToken = this.generateRefreshToken();

            refreshTokenSessionService.createAndRevokeRefreshToken(
                    loginData.userId(),
                    refreshToken.hashRefreshToken(),
                    loginData.clientId(),
                    null,
                    refreshToken.expiresAt());
            return new AuthenticationTokens(accessToken, refreshToken.rawRefreshToken());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthenticationTokens generatesAuthenticationTokens(String rawRefreshToken) {
        try{
             RefreshTokenSession oldRefreshToken = refreshTokenSessionService.findByTokenHash(
                bytesToHex(getSHA256(rawRefreshToken)))
                .orElseThrow(
                        () -> new CustomException("can nor find valid refreshToken",
                                "INVALID_TOKEN_HASH")
                );
            if (!oldRefreshToken.getUser().isActive() ) {
                throw new CustomException(
                        "user is not active",
                        "INACTIVE_USER"
                );
            }
            String userId = oldRefreshToken.getUser().getId().toString();
            String clientId = oldRefreshToken.getClient().getClientId();
            List<String> roles = oldRefreshToken.getUser().getRoles().stream().map(Role::getName).toList();
            List<String> allowedAudiences = List.of(oldRefreshToken.getClient().getAudience());
            String accessToken = this.generateAccessToken(
                    userId, roles, allowedAudiences
            );
            GeneratedRefreshToken newRefreshToken = this.generateRefreshToken();
            refreshTokenSessionService.createAndRevokeRefreshToken(
                    userId,
                    newRefreshToken.hashRefreshToken(),
                    clientId,
                    oldRefreshToken,
                    newRefreshToken.expiresAt());
            return new AuthenticationTokens(accessToken, newRefreshToken.rawRefreshToken());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void revokeRefreshToken(String rawRefreshToken) {
        try {
            refreshTokenSessionService.revokeRefreshToken(bytesToHex(getSHA256(rawRefreshToken)));

        } catch (NoSuchAlgorithmException e) {
            throw  new CustomException("can nor find valid refreshToken",
                    "INVALID_TOKEN_HASH");
        }
    }
}
