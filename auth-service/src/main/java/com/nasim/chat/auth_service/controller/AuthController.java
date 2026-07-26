package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.model.dto.AccessTokenResponse;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import com.nasim.chat.auth_service.service.impl.TokenService;
import com.nasim.chat.auth_service.utils.CookieUtils;
import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginExchangeCodeService loginExchangeCodeService;
    private final TokenService tokenService;

    public AuthController(LoginExchangeCodeService loginExchangeCodeService, TokenService tokenService) {
        this.loginExchangeCodeService = loginExchangeCodeService;
        this.tokenService = tokenService;
    }

    @GetMapping("/me")
    public Map<String, Object> currentUser(
            @AuthenticationPrincipal OidcUser oidcUser) {

        return Map.of(
                "subject", oidcUser.getSubject(),
                "email", oidcUser.getEmail(),
                "name", oidcUser.getFullName(),
                "issuer", oidcUser.getIssuer().toString(),
                "idToken", oidcUser.getIdToken()
        );
    }

    @PostMapping("/token/exchange")
    public ResponseEntity<AccessTokenResponse> exchange(
            @CookieValue("LOGIN_EXCHANGE_CODE") String code,
            HttpServletResponse response
    ) {
        LoginExchangeCode loginData =
                loginExchangeCodeService.consume(code);

        String accessToken = tokenService.generateAccessToken(
                loginData.userId(),
                loginData.roles(),
                loginData.allowedAudiences()
        );
        String refreshToken = tokenService.generateRefreshToken(
                loginData.userId(),
                loginData.roles()
        );
        CookieUtils.removedCookie(
                response,
                "LOGIN_EXCHANGE_CODE",
                "/api/auth/token/exchange"
        );

        AccessTokenResponse body = new AccessTokenResponse(
                accessToken,
                "Bearer",
                300
        );

        ResponseCookie refreshCookie = CookieUtils.CreateCookie("REFRESH_TOKEN", refreshToken,
                "/api/auth/token", Duration.ofDays(7),true,false,"Lax");

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshCookie.toString()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(body);
    }


}