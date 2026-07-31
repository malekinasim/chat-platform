package com.nasim.chat.auth_service.config;


import com.nasim.chat.auth_service.handler.OidcLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Component
@EnableWebSecurity
public class SecurityConfig {

    private final OidcLoginSuccessHandler oidcLoginSuccessHandler;

    public SecurityConfig(OidcLoginSuccessHandler oidcLoginSuccessHandler) {
        this.oidcLoginSuccessHandler = oidcLoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        return http
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/login",
                                "/login/**",
                                "/oauth2/**",
                                "/api/auth/token/**",
                                "/api/auth/onboarding/complete",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/api/auth/token/exchange",
                                "/api/auth/token/refresh",
                                "/api/auth/token/logout",
                                "/api/auth/onboarding/complete"
                        )
                )
                .oauth2Login(oauth2 ->
                        oauth2.successHandler(oidcLoginSuccessHandler)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8082"));
        configuration.setAllowedMethods(List.of("POST", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/auth/**", configuration);
        return source;
    }
}
