package com.nasim.chat.auth_service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
}