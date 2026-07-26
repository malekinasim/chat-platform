package com.nasim.chat.auth_service.config;


import com.nasim.chat.auth_service.handler.OidcLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
public class SecurityConfig {

    private final OidcLoginSuccessHandler oidcLoginSuccessHandler;

    public SecurityConfig(OidcLoginSuccessHandler oidcLoginSuccessHandler) {
        this.oidcLoginSuccessHandler = oidcLoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        return http.authorizeHttpRequests(
                        athurize ->
                                athurize.requestMatchers(
                                                "/auth/login",
                                                "/login/**",
                                                "/oauth2/**"
                                        ).permitAll()
                                        .anyRequest().authenticated()

                ).oauth2Login(oauth2 -> oauth2
                        .successHandler(oidcLoginSuccessHandler)
                ).sessionManagement(session -> session
                        // 1. Define Creation Policy
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)

                        // 2. Prevent Session Fixation Attacks
                        .sessionFixation(sessionFixation -> sessionFixation.newSession())

                        // 3. Control Concurrent Sessions
                        .maximumSessions(1)

                        .maxSessionsPreventsLogin(true)
                )
                .build();
    }
}
