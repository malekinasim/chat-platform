package com.nasim.chat.client.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;

public class securityUtils {

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

}
