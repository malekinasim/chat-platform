package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.dto.AccessTokenResponse;
import com.nasim.chat.auth_service.model.dto.AuthenticationTokens;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import com.nasim.chat.auth_service.service.TokenService;
import com.nasim.chat.auth_service.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginExchangeCodeService loginExchangeCodeService;
    private final TokenService tokenService;

    private final boolean secureCookie;

    public AuthController(
            LoginExchangeCodeService loginExchangeCodeService,
            TokenService tokenService,
            @Value("${security.cookie.secure:false}") boolean secureCookie
    ) {
        this.loginExchangeCodeService = loginExchangeCodeService;
        this.tokenService = tokenService;
        this.secureCookie = secureCookie;
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

    @PostMapping("/token/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String rawRefreshToken,
            HttpServletResponse response
    ) {
        if (StringUtils.hasText(rawRefreshToken)) {
            try {
               tokenService.revokeRefreshToken(rawRefreshToken);

            } catch (CustomException  e) {
                // An invalid or already-revoked token is treated as logged out
            }
        }
        CookieUtils.removedCookie(response,
                "REFRESH_TOKEN",
                "/api/auth/token");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
    @PostMapping("/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String rawRefreshToken,
            HttpServletResponse response
    ) {
        if (!StringUtils.hasText(rawRefreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
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
                true, secureCookie, "Lax");

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