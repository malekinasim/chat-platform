package com.nasim.chat.auth_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http){
       return http.authorizeHttpRequests(
                athurize->
                        athurize.requestMatchers("/login/**",
                                "/oauth2/**").permitAll()
                                .anyRequest().authenticated()

        ).oauth2Login(Customizer.withDefaults())
                .build();
    }
}
