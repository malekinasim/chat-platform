package com.nasim.chat.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@FunctionalInterface
public interface ResourceServerSecurityMatcher {

    void configure(HttpSecurity http) throws Exception;
}