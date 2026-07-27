package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.dto.AccessTokenResponse;
import com.nasim.chat.auth_service.model.dto.AuthenticationTokens;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import com.nasim.chat.auth_service.service.TokenService;
import com.nasim.chat.auth_service.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginExchangeCodeService loginExchangeCodeService;
    private final TokenService tokenService;

    public AuthController(LoginExchangeCodeService loginExchangeCodeService, TokenService tokenService) {
        this.loginExchangeCodeService = loginExchangeCodeService;
        this.tokenService = tokenService;
    }


    @PostMapping("/token/exchange")
    public ResponseEntity<AccessTokenResponse> exchange(
            @CookieValue("LOGIN_EXCHANGE_CODE") String code,
            HttpServletResponse response
    ) {
        LoginExchangeCode loginData =
                loginExchangeCodeService.consume(code);


        AuthenticationTokens tokens = tokenService.generatesAuthenticationTokens(loginData);
        CookieUtils.removedCookie(
                response,
                "LOGIN_EXCHANGE_CODE",
                "/api/auth/token/exchange"
        );
        return this.generateTokenResponse(tokens, response);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue("REFRESH_TOKEN") String rawRefreshToken,
            HttpServletResponse response
    ) {
        try {
            AuthenticationTokens tokens = tokenService.generatesAuthenticationTokens(rawRefreshToken);
            return this.generateTokenResponse(tokens, response);
        } catch (CustomException e) {
            CookieUtils.removedCookie(
                    response,
                    "REFRESH_TOKEN",
                    "/api/auth/token"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    private ResponseEntity<AccessTokenResponse> generateTokenResponse(AuthenticationTokens tokens,
                                                                      HttpServletResponse response) {
        AccessTokenResponse body = new AccessTokenResponse(
                tokens.accessToken(),
                "Bearer",
                300
        );

        ResponseCookie refreshCookie = CookieUtils.CreateCookie(
                "REFRESH_TOKEN", tokens.refreshToken(),
                "/api/auth/token", Duration.ofDays(7),
                true, false, "Lax");

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