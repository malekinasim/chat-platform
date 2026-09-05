package com.nasim.chat.client.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;

public class SecurityUtils {

    public static String authenticatedUsername() {
        SecurityContext securityContext=SecurityContextHolder.getContext();
        Principal principal=securityContext.getAuthentication();
        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "An authenticated WebSocket user is required"
            );
        }

        return principal.getName();
    }
    public static Authentication authenticatedUerInfo() {
        SecurityContext securityContext=SecurityContextHolder.getContext();
        Authentication principal=securityContext.getAuthentication();
        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "An authenticated WebSocket user is required"
            );
        }

        return principal;
    }
    public static String authenticatedUsername(Principal principal) {
        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "An authenticated WebSocket user is required"
            );
        }

        return principal.getName();
    }

    public static String authenticatedAccessToken() {
        Authentication authentication = authenticatedUerInfo();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new AuthenticationCredentialsNotFoundException(
                    "A JWT-authenticated user is required"
            );
        }
        return jwtAuthentication.getToken().getTokenValue();
    }

}
