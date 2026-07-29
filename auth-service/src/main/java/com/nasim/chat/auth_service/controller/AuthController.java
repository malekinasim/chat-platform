package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.dto.*;
import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.service.AppRegisterClientService;
import com.nasim.chat.auth_service.service.AppUserService;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import com.nasim.chat.auth_service.service.TokenService;
import com.nasim.chat.auth_service.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private final LoginExchangeCodeService loginExchangeCodeService;
    private final TokenService tokenService;
    private final AppUserService appUserService;
    private final boolean secureCookie;
    private final AppRegisterClientService appRegisterClientService;
    public AuthController(
            LoginExchangeCodeService loginExchangeCodeService,
            TokenService tokenService, AppUserService appUserService,
            @Value("${security.cookie.secure:false}") boolean secureCookie,
            AppRegisterClientService appRegisterClientService
    ) {
        this.loginExchangeCodeService = loginExchangeCodeService;
        this.tokenService = tokenService;
        this.appUserService = appUserService;
        this.secureCookie = secureCookie;
        this.appRegisterClientService = appRegisterClientService;
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
    @PostMapping("/onboarding/complete")
    public void completeRegistration(@RequestParam(name = "phone" ) @Pattern(regexp = "^\\+[1-9]\\d{7,14}$",
                                                   message = "Phone number must use international format, such as +46701234567") String phoneNumber,
                                       @SessionAttribute(name = "PENDING_REGISTRATION" ,required = false) PendingRegistration userInfo,
                                       @SessionAttribute(name = "APP_CLIENT_ID" ,required = false) String clientId,
                                      HttpServletResponse response,HttpServletRequest request) throws IOException {

        if (userInfo == null || clientId == null) {
            throw new CustomException(
                    "The registration session has expired",
                    "REGISTRATION_SESSION_EXPIRED"
            );
        }

        if (userInfo.expiresAt().isBefore(Instant.now())) {
            throw new CustomException(
                    "The registration session has expired",
                    "REGISTRATION_SESSION_EXPIRED"
            );
        }
        AppUser user= appUserService.completeRegistration(phoneNumber,userInfo,clientId);

        AppRegisteredClient client= appRegisterClientService.findActiveClient(clientId)
                .orElseThrow(
                        ()-> new CustomException("invalid client id ","INVALID_CLIENT_ID")
                );


        String exchangeCode = loginExchangeCodeService.create(
                user.getId().toString(),
                client.getClientId(),
                user.getRoles().stream().map(Role::getName).toList(),
                List.of( client.getAudience())
        );
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        CookieUtils.removedCookie(response, "JSESSIONID", "/");

        ResponseCookie cookie = CookieUtils.CreateCookie("LOGIN_EXCHANGE_CODE", exchangeCode,
                "/api/auth/token/exchange",Duration.ofSeconds(60),true,false,"Lax");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(client.getCallbackUrl());

    }


}