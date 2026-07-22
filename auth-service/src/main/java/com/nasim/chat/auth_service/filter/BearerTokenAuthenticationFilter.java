package com.nasim.chat.auth_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpHeaders;

public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {
    private final BearerTokenResolver bearerTokenResolver;

    public BearerTokenAuthenticationFilter(BearerTokenResolver bearerTokenResolver) {
        this.bearerTokenResolver = bearerTokenResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    }

    private String BearerTokenResolver(HttpHeaders httpHeaders){
          return  null;
    }
}
