package com.nasim.chat.security.config;



import com.nasim.chat.security.jwt.resolver.CompositeBearerTokenResolver;
import com.nasim.chat.security.jwt.convertor.JwtRoleConverter;
import com.nasim.chat.security.jwt.decoder.JwtDecoders;
import com.nasim.chat.security.jwt.resolver.CookieBearerTokenResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;

@Configuration(proxyBeanMethods = false)
public class JwtResourceServerConfiguration {

    @Bean
    public JwtRoleConverter jwtRoleConverter(){
        return  new JwtRoleConverter();
    }


    @Bean
    BearerTokenResolver bearerTokenResolver() {
        BearerTokenResolver headerResolver =
                new DefaultBearerTokenResolver();

        BearerTokenResolver cookieResolver =
                new CookieBearerTokenResolver("access_token");

        return new CompositeBearerTokenResolver(
                headerResolver,
                cookieResolver
        );
    }

    @Bean
    public JwtDecoder jwtDecoder(
            RSAPublicKey jwtPublicKey,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience) {

        return JwtDecoders.create(
                jwtPublicKey,
                issuer,
                audience
        );
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtAuthenticationConverter converter=new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this.jwtRoleConverter());
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            BearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            ResourceServerAuthorizationRules authorizationRules)
            throws Exception {

         http.authorizeHttpRequests(authorize -> {
            authorizationRules.configure(authorize);
            authorize.anyRequest().authenticated();
        })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(bearerTokenResolver)
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }


}
