package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.service.AppRegisterClientService;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import com.nasim.chat.auth_service.service.impl.TokenService;
import com.nimbusds.oauth2.sdk.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
                "idToken",oidcUser.getIdToken()
        );
    }


    @PostMapping("/api/auth/token/exchange")
    public TokenResponse exchange(
            @CookieValue("LOGIN_EXCHANGE_CODE") String code,
            HttpServletResponse response
    ) {
        LoginExchangeCode exchangeCode = loginExchangeCodeService.consume(code);
        if(exchangeCode!=null && exchangeCode.expiresAt().isAfter(Instant.now())){
            LoginExchangeCode loginData =
                    loginExchangeCodeService.consume(code);
//TODO
           tokenService.generateAccessToken(
                    loginData.userId(),
                    loginData.roles(),
                    loginData.allowedAudiences()
            );
        }else{

        }
        return null;
    }


}