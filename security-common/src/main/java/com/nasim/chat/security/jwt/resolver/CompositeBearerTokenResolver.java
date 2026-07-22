package com.nasim.chat.security.jwt.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

public class CompositeBearerTokenResolver implements BearerTokenResolver{
    private final BearerTokenResolver headerResolver;
    private final BearerTokenResolver cookieResolver;

    public CompositeBearerTokenResolver(BearerTokenResolver headerResolver, BearerTokenResolver cookieResolver) {
        this.headerResolver = headerResolver;
        this.cookieResolver = cookieResolver;
    }
    @Override
    public String resolve(HttpServletRequest request) {
        String headerToken = headerResolver.resolve(request);

        if (headerToken != null) {
            return headerToken;
        }

        return cookieResolver.resolve(request);
    }
}
