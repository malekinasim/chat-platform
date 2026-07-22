package com.nasim.chat.auth_service.filter;

import org.springframework.http.server.reactive.HttpHandler;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;

public class BearerTokenResolver  {

    public String resolveToken(HttpHeaders httpHeaders){
        return null;
    }
}
