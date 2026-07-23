package com.nasim.chat.auth_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class OidcLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String issuer = oidcUser.getIssuer().toString();
        String externalSubject = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        // 1. Find/create internal user using issuer + externalSubject
        // 2. Load the internal user's roles
        // 3. Generate our internal access/refresh tokens
        // 4. Return them safely
    }
}
